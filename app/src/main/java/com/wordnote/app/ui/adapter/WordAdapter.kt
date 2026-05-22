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

sealed class ListItem {
    data class DateHeader(val date: String, val dateLabel: String) : ListItem()
    data class WordItem(val word: Word, val index: Int, val isFirstInBatch: Boolean = false, val isLastInBatch: Boolean = false) : ListItem()
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

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_WORD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.DateHeader -> TYPE_DATE_HEADER
            is ListItem.WordItem -> TYPE_WORD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
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
        }
    }

    fun submitWordList(words: List<Word>) {
        val items = mutableListOf<ListItem>()
        var currentDate = ""
        var index = 1
        var currentBatchId: Long? = null
        var batchIndex = 0

        words.forEach { word ->
            val date = DateUtils.formatDate(word.createdAt)
            if (date != currentDate) {
                currentDate = date
                val label = formatDateLabel(word.createdAt)
                items.add(ListItem.DateHeader(date, label))
                index = 1
                currentBatchId = null
                batchIndex = 0
            }

            // Check if this word is part of a batch
            if (word.batchId != null) {
                if (word.batchId != currentBatchId) {
                    // New batch started
                    currentBatchId = word.batchId
                    batchIndex = 0
                    items.add(ListItem.WordItem(word, index, isFirstInBatch = true, isLastInBatch = false))
                } else {
                    // Same batch continued
                    val nextWordIndex = words.indexOf(word) + 1
                    val isLast = nextWordIndex >= words.size ||
                            words[nextWordIndex].batchId != currentBatchId ||
                            DateUtils.formatDate(words[nextWordIndex].createdAt) != currentDate
                    items.add(ListItem.WordItem(word, index, isFirstInBatch = false, isLastInBatch = isLast))
                }
                batchIndex++
            } else {
                // Not in a batch
                currentBatchId = null
                batchIndex = 0
                items.add(ListItem.WordItem(word, index))
            }
            index++
        }

        submitList(items)
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
            indexTextView.text = "$index."
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
            val density = itemView.resources.displayMetrics.density
            val borderWidth = (2 * density).toInt()
            val cr = 12f * density

            val batchGroupDivider = itemView.findViewById<View>(R.id.batchGroupDivider)
            batchGroupDivider?.visibility = View.GONE

            cardView.strokeWidth = 0
            cardView.cardElevation = 0f
            updateBatchCorners(cardView, cr, isFirstInBatch, isLastInBatch, word.batchId != null)

            if (word.batchId != null) {
                cardView.setCardBackgroundColor(itemView.context.getColor(R.color.batch_group_bg))
                val lp = cardView.layoutParams as ViewGroup.MarginLayoutParams
                lp.topMargin = if (isFirstInBatch) (10 * density).toInt() else 0
                lp.bottomMargin = if (isLastInBatch) (10 * density).toInt() else (1 * density).toInt()
                cardView.layoutParams = lp
            } else {
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
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }
}
