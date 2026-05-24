package com.wordnote.app.ui.adapter

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.wordnote.app.R
import com.wordnote.app.data.Category
import com.wordnote.app.data.HighlightedMeaning
import com.wordnote.app.data.Word
import com.wordnote.app.data.WordGroup
import com.wordnote.app.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

sealed class ListItem {
    data class DateHeader(val date: String, val dateLabel: String) : ListItem()
    data class WordItem(val word: Word, val index: Int, val isFirstInBatch: Boolean = false, val isLastInBatch: Boolean = false) : ListItem()
    data class MonthHeader(val yearMonth: String, val label: String, val wordCount: Int) : ListItem()
    data class WeekHeader(val yearWeek: String, val label: String, val wordCount: Int) : ListItem()
    data class DayHeader(val date: String, val label: String) : ListItem()
}

class WordAdapter(
    private val onWordClick: (Word) -> Unit,
    private val onEditClick: (Word) -> Unit = {},
    private val onDeleteClick: (Word) -> Unit = {},
    private val onSelectionChanged: (Set<Long>) -> Unit = {}
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    private var categories: Map<Long, Category> = emptyMap()
    private var groups: Map<Long, WordGroup> = emptyMap()
    private var highlightedMeaningsMap: Map<Long, List<String>> = emptyMap()
    private var isSelectionMode = false
    private val selectedWordIds = mutableSetOf<Long>()

    // Date grouping mode
    private var isDateGroupingMode = false
    private val collapsedMonths = mutableSetOf<String>()
    private val collapsedWeeks = mutableSetOf<String>()
    private var allWords: List<Word> = emptyList()

    fun setCategories(categoryList: List<Category>) {
        categories = categoryList.associateBy { it.id }
        notifyDataSetChanged()
    }

    fun setGroups(groupList: List<WordGroup>) {
        groups = groupList.associateBy { it.id }
        notifyDataSetChanged()
    }

    fun setHighlightedMeanings(meanings: List<HighlightedMeaning>) {
        highlightedMeaningsMap = meanings.groupBy { it.wordId }.mapValues { it.value.map { m -> m.meaningText } }
        notifyDataSetChanged()
    }

    fun enterSelectionMode() {
        isSelectionMode = true
        selectedWordIds.clear()
        notifyDataSetChanged()
    }

    fun exitSelectionMode() {
        isSelectionMode = false
        selectedWordIds.clear()
        notifyDataSetChanged()
        onSelectionChanged(emptySet())
    }

    fun getSelectedWords(): Set<Long> = selectedWordIds.toSet()

    fun deleteSelectedWords() {
        selectedWordIds.clear()
        isSelectionMode = false
        notifyDataSetChanged()
    }

    private fun toggleSelection(wordId: Long) {
        if (selectedWordIds.contains(wordId)) {
            selectedWordIds.remove(wordId)
        } else {
            selectedWordIds.add(wordId)
        }
        onSelectionChanged(selectedWordIds)
        notifyDataSetChanged()
    }

    fun setDateGroupingMode(enabled: Boolean) {
        if (isDateGroupingMode != enabled) {
            isDateGroupingMode = enabled
            collapsedMonths.clear()
            collapsedWeeks.clear()
            submitWordList(allWords)
        }
    }

    fun isDateGroupingMode(): Boolean = isDateGroupingMode

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_WORD = 1
        private const val TYPE_MONTH_HEADER = 2
        private const val TYPE_WEEK_HEADER = 3
        private const val TYPE_DAY_HEADER = 4
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.DateHeader -> TYPE_DATE_HEADER
            is ListItem.WordItem -> TYPE_WORD
            is ListItem.MonthHeader -> TYPE_MONTH_HEADER
            is ListItem.WeekHeader -> TYPE_WEEK_HEADER
            is ListItem.DayHeader -> TYPE_DAY_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            TYPE_MONTH_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_month_header, parent, false)
                MonthHeaderViewHolder(view)
            }
            TYPE_WEEK_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_week_header, parent, false)
                WeekHeaderViewHolder(view)
            }
            TYPE_DAY_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day_header, parent, false)
                DayHeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word_compact, parent, false)
                WordViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ListItem.DateHeader -> (holder as DateHeaderViewHolder).bind(item.dateLabel)
            is ListItem.WordItem -> (holder as WordViewHolder).bind(item.word, item.index, item.isFirstInBatch, item.isLastInBatch)
            is ListItem.MonthHeader -> (holder as MonthHeaderViewHolder).bind(item)
            is ListItem.WeekHeader -> (holder as WeekHeaderViewHolder).bind(item)
            is ListItem.DayHeader -> (holder as DayHeaderViewHolder).bind(item.label)
        }
    }

    fun submitWordList(words: List<Word>) {
        allWords = words
        val items = mutableListOf<ListItem>()

        if (!isDateGroupingMode) {
            // Original flat date grouping mode
            submitFlatList(words, items)
        } else {
            // Hierarchical date grouping: Month -> Week -> Day
            submitGroupedList(words, items)
        }

        submitList(items)
    }

    private fun submitFlatList(words: List<Word>, items: MutableList<ListItem>) {
        var currentDate = ""
        var index = 1
        var currentBatchId: Long? = null

        words.forEach { word ->
            val date = DateUtils.formatDate(word.createdAt)
            if (date != currentDate) {
                currentDate = date
                val label = formatDateLabel(word.createdAt)
                items.add(ListItem.DateHeader(date, label))
                currentBatchId = null
            }

            if (word.batchId != null) {
                if (word.batchId != currentBatchId) {
                    currentBatchId = word.batchId
                    items.add(ListItem.WordItem(word, index, isFirstInBatch = true, isLastInBatch = false))
                } else {
                    val nextWordIndex = words.indexOf(word) + 1
                    val isLast = nextWordIndex >= words.size ||
                            words[nextWordIndex].batchId != currentBatchId ||
                            DateUtils.formatDate(words[nextWordIndex].createdAt) != currentDate
                    items.add(ListItem.WordItem(word, index, isFirstInBatch = false, isLastInBatch = isLast))
                    if (isLast) index++
                }
            } else {
                currentBatchId = null
                items.add(ListItem.WordItem(word, index))
                index++
            }
        }
    }

    private fun submitGroupedList(words: List<Word>, items: MutableList<ListItem>) {
        val cal = Calendar.getInstance()
        val weekCal = Calendar.getInstance()

        // Group by month -> week -> day
        val monthMap = linkedMapOf<String, LinkedHashMap<String, LinkedHashMap<String, MutableList<Word>>>>()

        words.forEach { word ->
            cal.timeInMillis = word.createdAt

            // Month key: "2026-05"
            val monthKey = "${cal.get(Calendar.YEAR)}-${String.format("%02d", cal.get(Calendar.MONTH) + 1)}"

            // Week key: "2026-W20" (week of year)
            weekCal.timeInMillis = word.createdAt
            val weekOfYear = weekCal.get(Calendar.WEEK_OF_YEAR)
            val weekKey = "${cal.get(Calendar.YEAR)}-W${String.format("%02d", weekOfYear)}"

            // Day key: "2026-05-24"
            val dayKey = DateUtils.formatDate(word.createdAt)

            monthMap.getOrPut(monthKey) { linkedMapOf() }
                .getOrPut(weekKey) { linkedMapOf() }
                .getOrPut(dayKey) { mutableListOf() }
                .add(word)
        }

        var globalIndex = 1

        // Build items in order: Month -> Week -> Day -> Words
        monthMap.forEach { (monthKey, weeks) ->
            val monthWords = weeks.values.flatMap { it.values.flatten() }
            val monthLabel = formatMonthLabel(monthKey)
            items.add(ListItem.MonthHeader(monthKey, monthLabel, monthWords.size))

            if (!collapsedMonths.contains(monthKey)) {
                weeks.forEach { (weekKey, days) ->
                    val weekWords = days.values.flatten()
                    val weekLabel = formatWeekLabel(weekKey)
                    items.add(ListItem.WeekHeader(weekKey, weekLabel, weekWords.size))

                    if (!collapsedWeeks.contains(weekKey)) {
                        days.forEach { (dayKey, dayWords) ->
                            val dayLabel = formatDayLabel(dayWords.first().createdAt)
                            items.add(ListItem.DayHeader(dayKey, dayLabel))

                            var currentBatchId: Long? = null
                            dayWords.forEach { word ->
                                if (word.batchId != null) {
                                    if (word.batchId != currentBatchId) {
                                        currentBatchId = word.batchId
                                        items.add(ListItem.WordItem(word, globalIndex, isFirstInBatch = true, isLastInBatch = false))
                                    } else {
                                        val nextIdx = dayWords.indexOf(word) + 1
                                        val isLast = nextIdx >= dayWords.size || dayWords[nextIdx].batchId != currentBatchId
                                        items.add(ListItem.WordItem(word, globalIndex, isFirstInBatch = false, isLastInBatch = isLast))
                                        if (isLast) globalIndex++
                                    }
                                } else {
                                    currentBatchId = null
                                    items.add(ListItem.WordItem(word, globalIndex))
                                    globalIndex++
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun formatMonthLabel(monthKey: String): String {
        val parts = monthKey.split("-")
        if (parts.size != 2) return monthKey
        val year = parts[0].toIntOrNull() ?: return monthKey
        val month = parts[1].toIntOrNull() ?: return monthKey
        return "${year}年${month}月"
    }

    private fun formatWeekLabel(weekKey: String): String {
        val parts = weekKey.split("-W")
        if (parts.size != 2) return weekKey
        val weekNum = parts[1].toIntOrNull() ?: return weekKey
        return "第${weekNum}周"
    }

    private fun formatDayLabel(timestamp: Long): String {
        val cal = Calendar.getInstance()
        val now = Calendar.getInstance()
        cal.timeInMillis = timestamp

        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "周一"
            Calendar.TUESDAY -> "周二"
            Calendar.WEDNESDAY -> "周三"
            Calendar.THURSDAY -> "周四"
            Calendar.FRIDAY -> "周五"
            Calendar.SATURDAY -> "周六"
            Calendar.SUNDAY -> "周日"
            else -> ""
        }

        val isToday = cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

        val yesterday = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        val isYesterday = cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)

        return when {
            isToday -> "今天 ${month}月${day}日 $dayOfWeek"
            isYesterday -> "昨天 ${month}月${day}日 $dayOfWeek"
            else -> "${month}月${day}日 $dayOfWeek"
        }
    }

    private fun formatDateLabel(timestamp: Long): String {
        val cal = java.util.Calendar.getInstance()
        val now = java.util.Calendar.getInstance()
        cal.timeInMillis = timestamp

        val month = cal.get(java.util.Calendar.MONTH) + 1
        val day = cal.get(java.util.Calendar.DAY_OF_MONTH)

        val isToday = cal.get(java.util.Calendar.YEAR) == now.get(java.util.Calendar.YEAR) &&
                cal.get(java.util.Calendar.DAY_OF_YEAR) == now.get(java.util.Calendar.DAY_OF_YEAR)

        val isYesterday = now.apply { add(java.util.Calendar.DAY_OF_YEAR, -1) }.let {
            cal.get(java.util.Calendar.YEAR) == it.get(java.util.Calendar.YEAR) &&
                    cal.get(java.util.Calendar.DAY_OF_YEAR) == it.get(java.util.Calendar.DAY_OF_YEAR)
        }

        return when {
            isToday -> "今天 · ${month}月${day}日"
            isYesterday -> "昨天 · ${month}月${day}日"
            else -> "${month}月${day}日"
        }
    }

    inner class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        fun bind(dateLabel: String) {
            dateTextView.text = dateLabel
        }
    }

    inner class MonthHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val expandIcon: ImageView = itemView.findViewById(R.id.expandIcon)
        private val monthTextView: TextView = itemView.findViewById(R.id.monthTextView)
        private val wordCountTextView: TextView = itemView.findViewById(R.id.wordCountTextView)

        fun bind(item: ListItem.MonthHeader) {
            monthTextView.text = item.label
            wordCountTextView.text = "${item.wordCount}个单词"

            val isCollapsed = collapsedMonths.contains(item.yearMonth)
            expandIcon.rotation = if (isCollapsed) -90f else 0f

            itemView.setOnClickListener {
                if (collapsedMonths.contains(item.yearMonth)) {
                    collapsedMonths.remove(item.yearMonth)
                } else {
                    collapsedMonths.add(item.yearMonth)
                }
                submitWordList(allWords)
            }
        }
    }

    inner class WeekHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val expandIcon: ImageView = itemView.findViewById(R.id.expandIcon)
        private val weekTextView: TextView = itemView.findViewById(R.id.weekTextView)
        private val wordCountTextView: TextView = itemView.findViewById(R.id.wordCountTextView)

        fun bind(item: ListItem.WeekHeader) {
            weekTextView.text = item.label
            wordCountTextView.text = "${item.wordCount}个"

            val isCollapsed = collapsedWeeks.contains(item.yearWeek)
            expandIcon.rotation = if (isCollapsed) -90f else 0f

            itemView.setOnClickListener {
                if (collapsedWeeks.contains(item.yearWeek)) {
                    collapsedWeeks.remove(item.yearWeek)
                } else {
                    collapsedWeeks.add(item.yearWeek)
                }
                submitWordList(allWords)
            }
        }
    }

    inner class DayHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)

        fun bind(label: String) {
            dayTextView.text = label
        }
    }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = (itemView as MaterialCardView).apply {
            clipToOutline = true
        }
        private val indexTextView: TextView = itemView.findViewById(R.id.indexTextView)
        private val wordTextView: TextView = itemView.findViewById(R.id.wordTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        private val categoryStrip: View = itemView.findViewById(R.id.categoryStrip)
        private val groupBadge: TextView = itemView.findViewById(R.id.groupBadge)
        private val editButton: ImageView = itemView.findViewById(R.id.editWordButton)
        private val deleteButton: ImageView = itemView.findViewById(R.id.deleteWordButton)

        fun bind(word: Word, index: Int, isFirstInBatch: Boolean = false, isLastInBatch: Boolean = false) {
            val density = itemView.resources.displayMetrics.density

            // Only show index on first word of batch, or on non-batch words
            if (word.batchId != null && !isFirstInBatch) {
                indexTextView.visibility = View.GONE
                // Measure indexTextView width dynamically for accurate alignment
                indexTextView.text = "$index."
                indexTextView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                val indexWidth = indexTextView.measuredWidth
                val indexMarginEnd = (indexTextView.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
                val indentWidth = indexWidth + indexMarginEnd

                // Indent word and meaning to align with first row
                val wordLp = wordTextView.layoutParams as ViewGroup.MarginLayoutParams
                wordLp.marginStart = indentWidth
                wordTextView.layoutParams = wordLp
                val contentLp = contentTextView.layoutParams as ViewGroup.MarginLayoutParams
                contentLp.marginStart = 0
                contentTextView.layoutParams = contentLp
            } else {
                indexTextView.visibility = View.VISIBLE
                indexTextView.text = "$index."
                // Reset margins for first row or non-batch words
                val wordLp = wordTextView.layoutParams as ViewGroup.MarginLayoutParams
                wordLp.marginStart = 0
                wordTextView.layoutParams = wordLp
                val contentLp = contentTextView.layoutParams as ViewGroup.MarginLayoutParams
                contentLp.marginStart = 0
                contentTextView.layoutParams = contentLp
            }
            wordTextView.text = word.word

            // Apply color to highlighted meanings
            val highlighted = highlightedMeaningsMap[word.id]
            if (!highlighted.isNullOrEmpty()) {
                val spannable = SpannableString(word.meaning)
                highlighted.forEach { hm ->
                    val idx = word.meaning.indexOf(hm)
                    if (idx >= 0) {
                        val highlightColor = itemView.context.getColor(R.color.primary)
                        spannable.setSpan(
                            ForegroundColorSpan(highlightColor),
                            idx, idx + hm.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannable.setSpan(
                            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                            idx, idx + hm.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
                contentTextView.text = spannable
            } else {
                contentTextView.text = word.meaning
            }

            val categoryColor = try {
                val cat = categories[word.categoryId]
                if (cat != null) Color.parseColor(cat.color) else Color.parseColor("#757575")
            } catch (e: Exception) {
                Color.parseColor("#757575")
            }
            categoryStrip.setBackgroundColor(categoryColor)

            if (word.groupId != null) {
                val group = groups[word.groupId]
                if (group != null) {
                    groupBadge.text = group.name
                    groupBadge.visibility = View.VISIBLE
                } else {
                    groupBadge.visibility = View.GONE
                }
            } else {
                groupBadge.visibility = View.GONE
            }

            // Batch: merge same-batch items into one visual card
            val borderWidth = (1 * density).toInt()
            val cr = 12f * density

            val batchGroupDivider = itemView.findViewById<View>(R.id.batchGroupDivider)
            batchGroupDivider?.visibility = View.GONE

            cardView.cardElevation = 0f
            updateBatchCorners(cardView, cr, isFirstInBatch, isLastInBatch, word.batchId != null)

            if (word.batchId != null) {
                cardView.setCardBackgroundColor(itemView.context.getColor(R.color.batch_group_bg))
                // Add subtle border for batch groups
                cardView.strokeWidth = borderWidth
                cardView.strokeColor = itemView.context.getColor(R.color.divider)
                val lp = cardView.layoutParams as ViewGroup.MarginLayoutParams
                lp.topMargin = if (isFirstInBatch) (12 * density).toInt() else 0
                lp.bottomMargin = if (isLastInBatch) (12 * density).toInt() else (1 * density).toInt()
                cardView.layoutParams = lp
            } else {
                cardView.strokeWidth = 0
                val lp = cardView.layoutParams as ViewGroup.MarginLayoutParams
                lp.topMargin = (2 * density).toInt()
                lp.bottomMargin = (2 * density).toInt()
                cardView.layoutParams = lp
            }

            // Selection mode
            if (isSelectionMode) {
                val isSelected = selectedWordIds.contains(word.id)
                if (isSelected) {
                    cardView.strokeWidth = borderWidth
                    cardView.strokeColor = categoryColor
                    cardView.setCardBackgroundColor(Color.parseColor("#1A1E88E5"))
                } else {
                    cardView.strokeWidth = 0
                    cardView.setCardBackgroundColor(itemView.context.getColor(R.color.card_background))
                }

                itemView.setOnClickListener {
                    toggleSelection(word.id)
                }
                editButton.visibility = View.GONE
                deleteButton.visibility = View.GONE
            } else {
                cardView.setCardBackgroundColor(itemView.context.getColor(R.color.card_background))
                itemView.setOnClickListener { onWordClick(word) }
                editButton.setOnClickListener { onEditClick(word) }
                deleteButton.setOnClickListener { onDeleteClick(word) }
                editButton.visibility = View.VISIBLE
                deleteButton.visibility = View.VISIBLE
            }

            // Long click to enter selection mode
            itemView.setOnLongClickListener {
                if (!isSelectionMode) {
                    enterSelectionMode()
                    toggleSelection(word.id)
                    true
                } else {
                    false
                }
            }
        }

        private fun updateBatchCorners(card: MaterialCardView, cr: Float, isFirst: Boolean, isLast: Boolean, isBatch: Boolean) {
            if (!isBatch) {
                card.radius = cr
                return
            }
            val shape = ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, if (isFirst) cr else 0f)
                .setTopRightCorner(CornerFamily.ROUNDED, if (isFirst) cr else 0f)
                .setBottomLeftCorner(CornerFamily.ROUNDED, if (isLast) cr else 0f)
                .setBottomRightCorner(CornerFamily.ROUNDED, if (isLast) cr else 0f)
                .build()
            card.shapeAppearanceModel = shape
        }
    }

    class ListItemDiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return when {
                oldItem is ListItem.DateHeader && newItem is ListItem.DateHeader -> oldItem.date == newItem.date
                oldItem is ListItem.WordItem && newItem is ListItem.WordItem -> oldItem.word.id == newItem.word.id
                oldItem is ListItem.MonthHeader && newItem is ListItem.MonthHeader -> oldItem.yearMonth == newItem.yearMonth
                oldItem is ListItem.WeekHeader && newItem is ListItem.WeekHeader -> oldItem.yearWeek == newItem.yearWeek
                oldItem is ListItem.DayHeader && newItem is ListItem.DayHeader -> oldItem.date == newItem.date
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }
}
