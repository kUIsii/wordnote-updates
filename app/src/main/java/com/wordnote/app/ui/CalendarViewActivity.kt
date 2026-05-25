package com.wordnote.app.ui

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import java.util.concurrent.ConcurrentHashMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.wordnote.app.R
import com.wordnote.app.data.Category
import com.wordnote.app.data.SentenceWithWords
import com.wordnote.app.data.Word
import com.wordnote.app.data.WordMeaning
import com.wordnote.app.util.compatOverridePendingTransitionClose
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private var sentenceViewModel: SentenceViewModel? = null
    private lateinit var backButton: ImageView
    private lateinit var datePickerButton: TextView
    private lateinit var selectedDateText: TextView
    private lateinit var searchEditText: EditText
    private lateinit var categoryContainer: LinearLayout
    private lateinit var emptyView: LinearLayout
    private lateinit var sentencesSection: LinearLayout
    private lateinit var sentencesContainer: LinearLayout

    private var selectedDate: Long = System.currentTimeMillis()
    private var allWords: List<Word> = emptyList()
    private var categoriesList: List<Category> = emptyList()
    private val collapsedCategories = mutableSetOf<Long>()
    private val highlightedMeaningsMap = ConcurrentHashMap<Long, List<String>>()
    private val wordMeaningsMap = ConcurrentHashMap<Long, List<WordMeaning>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)

        try {
            viewModel = ViewModelProvider(this)[WordViewModel::class.java]
        } catch (e: Exception) {
            android.util.Log.e("CalendarView", "Failed to init WordViewModel", e)
            finish()
            return
        }
        try {
            sentenceViewModel = ViewModelProvider(this)[SentenceViewModel::class.java]
        } catch (e: Exception) {
            android.util.Log.e("CalendarView", "Failed to init SentenceViewModel, continuing without sentences", e)
            sentenceViewModel = null
        }

        initViews()
        setupDatePicker()
        setupSearch()
        observeData()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        datePickerButton = findViewById(R.id.datePickerButton)
        selectedDateText = findViewById(R.id.selectedDateText)
        searchEditText = findViewById(R.id.searchEditText)
        categoryContainer = findViewById(R.id.categoryContainer)
        emptyView = findViewById(R.id.emptyView)
        sentencesSection = findViewById(R.id.sentencesSection)
        sentencesContainer = findViewById(R.id.sentencesContainer)

        backButton.setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        updateSelectedDateText()
    }

    private fun setupDatePicker() {
        datePickerButton.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("选择日期")
            .setSelection(selectedDate)
            .build()

        datePicker.addOnPositiveButtonClickListener { date ->
            selectedDate = date
            updateSelectedDateText()
            filterWordsByDate()
        }

        datePicker.show(supportFragmentManager, "date_picker")
    }

    private fun updateSelectedDateText() {
        val sdf = SimpleDateFormat("yyyy年M月d日", Locale.getDefault())
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }

        val now = Calendar.getInstance()
        val isToday = cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

        val isYesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.let {
            cal.get(Calendar.YEAR) == it.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == it.get(Calendar.DAY_OF_YEAR)
        }

        val dateStr = when {
            isToday -> "今天 · ${sdf.format(Date(selectedDate))}"
            isYesterday -> "昨天 · ${sdf.format(Date(selectedDate))}"
            else -> sdf.format(Date(selectedDate))
        }

        selectedDateText.text = dateStr
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                filterWordsByDate()
            }
        })
    }

    private fun observeData() {
        try {
            viewModel.allWords.observe(this) { words ->
                try {
                    allWords = words
                    filterWordsByDate()
                    fetchHighlightedMeanings(words)
                } catch (e: Exception) {
                    android.util.Log.e("CalendarView", "Error in allWords observer", e)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("CalendarView", "Failed to observe allWords", e)
        }

        try {
            viewModel.allCategories.observe(this) { categories ->
                try {
                    categoriesList = categories
                    filterWordsByDate()
                } catch (e: Exception) {
                    android.util.Log.e("CalendarView", "Error in allCategories observer", e)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("CalendarView", "Failed to observe allCategories", e)
        }

        try {
            sentenceViewModel?.allSentencesWithWords?.observe(this) { sentences ->
                try {
                    filterSentencesByDate(sentences)
                } catch (e: Exception) {
                    android.util.Log.e("CalendarView", "Error in allSentences observer", e)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("CalendarView", "Failed to observe allSentences", e)
        }
    }

    private fun fetchHighlightedMeanings(words: List<Word>) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    highlightedMeaningsMap.clear()
                    wordMeaningsMap.clear()
                    words.forEach { word ->
                        val meanings = viewModel.getMeaningsForWordSync(word.id)
                        if (meanings.isNotEmpty()) {
                            wordMeaningsMap[word.id] = meanings
                            val highlighted = meanings.filter { it.isHighlighted }.map { it.meaningText }
                            if (highlighted.isNotEmpty()) {
                                highlightedMeaningsMap[word.id] = highlighted
                            }
                        }
                    }
                }
                filterWordsByDate()
            } catch (e: Exception) {
                e.printStackTrace()
                filterWordsByDate()
            }
        }
    }

    private fun filterWordsByDate() {
        try {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
            val targetYear = cal.get(Calendar.YEAR)
            val targetMonth = cal.get(Calendar.MONTH)
            val targetDay = cal.get(Calendar.DAY_OF_MONTH)

            val searchQuery = searchEditText.text.toString().trim()

            val filteredWords = allWords.filter { word ->
                val wordCal = Calendar.getInstance().apply { timeInMillis = word.createdAt }
                val isSameDate = wordCal.get(Calendar.YEAR) == targetYear &&
                        wordCal.get(Calendar.MONTH) == targetMonth &&
                        wordCal.get(Calendar.DAY_OF_MONTH) == targetDay

                if (searchQuery.isEmpty()) {
                    isSameDate
                } else {
                    isSameDate && (word.word.contains(searchQuery, ignoreCase = true) ||
                            word.meaning.contains(searchQuery, ignoreCase = true))
                }
            }

            displayWordsByCategory(filteredWords)

            // Also filter sentences
            try {
                sentenceViewModel?.allSentencesWithWords?.value?.let { filterSentencesByDate(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun filterSentencesByDate(sentences: List<SentenceWithWords>) {
        try {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
            val targetYear = cal.get(Calendar.YEAR)
            val targetMonth = cal.get(Calendar.MONTH)
            val targetDay = cal.get(Calendar.DAY_OF_MONTH)

            val filteredSentences = sentences.filter { item ->
                val sentenceCal = Calendar.getInstance().apply { timeInMillis = item.sentence.createdAt }
                sentenceCal.get(Calendar.YEAR) == targetYear &&
                        sentenceCal.get(Calendar.MONTH) == targetMonth &&
                        sentenceCal.get(Calendar.DAY_OF_MONTH) == targetDay
            }

            displaySentences(filteredSentences)
        } catch (e: Exception) {
            android.util.Log.e("CalendarView", "Error filtering sentences", e)
        }
    }

    private fun displaySentences(sentences: List<SentenceWithWords>) {
        try {
            sentencesContainer.removeAllViews()

            if (sentences.isEmpty()) {
                sentencesSection.visibility = View.GONE
                // Check if words are also empty
                if (categoryContainer.childCount == 0) {
                    emptyView.visibility = View.VISIBLE
                }
                return
            }

            sentencesSection.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            val density = resources.displayMetrics.density

            sentences.forEach { item ->
                val card = com.google.android.material.card.MaterialCardView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = (8 * density).toInt()
                    }
                    setCardBackgroundColor(getColor(R.color.card_background))
                    radius = 12f * density
                    cardElevation = 0f
                }

                val content = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding((14 * density).toInt(), (12 * density).toInt(), (14 * density).toInt(), (12 * density).toInt())
                }

                val sentenceText = TextView(this).apply {
                    text = item.sentence.originalText
                    textSize = 15f
                    setTextColor(getColor(R.color.text_primary))
                    maxLines = 3
                    ellipsize = android.text.TextUtils.TruncateAt.END
                }
                content.addView(sentenceText)

                if (!item.sentence.translation.isNullOrBlank()) {
                    val translationText = TextView(this).apply {
                        text = item.sentence.translation
                        textSize = 13f
                        setTextColor(getColor(R.color.text_secondary))
                        maxLines = 2
                        setPadding(0, (4 * density).toInt(), 0, 0)
                        ellipsize = android.text.TextUtils.TruncateAt.END
                    }
                    content.addView(translationText)
                }

                if (item.words.isNotEmpty()) {
                    val wordCount = TextView(this).apply {
                        text = "${item.words.size}个生词"
                        textSize = 11f
                        setTextColor(getColor(R.color.primary))
                        setPadding(0, (6 * density).toInt(), 0, 0)
                    }
                    content.addView(wordCount)
                }

                card.addView(content)
                sentencesContainer.addView(card)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun displayWordsByCategory(words: List<Word>) {
        try {
            categoryContainer.removeAllViews()

            val hasSentences = sentencesSection.visibility == View.VISIBLE

            if (words.isEmpty() && !hasSentences) {
                emptyView.visibility = View.VISIBLE
                categoryContainer.visibility = View.GONE
                return
            }

            emptyView.visibility = View.GONE
            categoryContainer.visibility = if (words.isEmpty()) View.GONE else View.VISIBLE

            // Group words by category
            val wordsByCategory = words.groupBy { it.categoryId }

            // Create a map of category id to category for quick lookup
            val categoryMap = categoriesList.associateBy { it.id }

            // Display each category
            wordsByCategory.forEach { (categoryId, categoryWords) ->
                val category = categoryMap[categoryId]
                if (category != null) {
                    createCategorySection(category, categoryWords)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createCategorySection(category: Category, words: List<Word>) {
        val density = resources.displayMetrics.density
        val borderWidth = (2 * density).toInt()
        val cornerRadius = 12f * density

        val categoryColor = try {
            Color.parseColor(category.color)
        } catch (e: Exception) {
            Color.parseColor("#757575")
        }

        // Category container with border
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val borderDrawable = android.graphics.drawable.GradientDrawable().apply {
                setStroke(borderWidth, categoryColor)
                setColor(Color.TRANSPARENT)
                this.cornerRadius = cornerRadius
            }
            background = borderDrawable
            val padding = (12 * density).toInt()
            setPadding(padding, padding, padding, padding)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = (12 * density).toInt()
            }
            layoutParams = params
        }

        // Category header with collapse button
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
        }

        // Category color dot
        val colorDot = View(this).apply {
            val dotSize = (12 * density).toInt()
            val params = LinearLayout.LayoutParams(dotSize, dotSize)
            layoutParams = params
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(categoryColor)
            }
        }
        headerLayout.addView(colorDot)

        // Category name
        val nameText = TextView(this).apply {
            text = category.name
            textSize = 16f
            setTextColor(resources.getColor(R.color.text_primary, null))
            setPadding((8 * density).toInt(), 0, 0, 0)
            val params = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            layoutParams = params
        }
        headerLayout.addView(nameText)

        // Word count
        val countText = TextView(this).apply {
            text = "${words.size}个"
            textSize = 13f
            setTextColor(resources.getColor(R.color.text_secondary, null))
            setPadding(0, 0, (8 * density).toInt(), 0)
        }
        headerLayout.addView(countText)

        // Collapse/expand arrow
        val arrowText = TextView(this).apply {
            text = if (collapsedCategories.contains(category.id)) "▶" else "▼"
            textSize = 12f
            setTextColor(resources.getColor(R.color.text_secondary, null))
        }
        headerLayout.addView(arrowText)

        container.addView(headerLayout)

        // Toggle collapse on header click
        headerLayout.setOnClickListener {
            if (collapsedCategories.contains(category.id)) {
                collapsedCategories.remove(category.id)
            } else {
                collapsedCategories.add(category.id)
            }
            filterWordsByDate()
        }

        // Words container (collapsible)
        if (!collapsedCategories.contains(category.id)) {
            val wordsContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (8 * density).toInt()
                }
                layoutParams = params
            }

            // Group words by batchId
            val wordsByBatch = words.groupBy { it.batchId }
            var wordIndex = 1

            wordsByBatch.forEach { (batchId, batchWords) ->
                if (batchId != null && batchWords.size > 1) {
                    // Multiple words in batch - show with thin border
                    createBatchGroup(wordsContainer, batchWords, categoryColor, wordIndex, density)
                    wordIndex += batchWords.size
                } else {
                    // Single word or no batch - show individually
                    batchWords.forEach { word ->
                        createWordItem(wordsContainer, word, wordIndex, density)
                        wordIndex++
                    }
                }
            }

            container.addView(wordsContainer)
        }

        categoryContainer.addView(container)
    }

    private fun createBatchGroup(container: LinearLayout, words: List<Word>, categoryColor: Int, startIndex: Int, density: Float) {
        val borderWidth = (1 * density).toInt()
        val cornerRadius = 8f * density

        // Batch container with thin border
        val batchContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val borderDrawable = android.graphics.drawable.GradientDrawable().apply {
                setStroke(borderWidth, categoryColor)
                setColor(Color.TRANSPARENT)
                this.cornerRadius = cornerRadius
            }
            background = borderDrawable
            val padding = (8 * density).toInt()
            setPadding(padding, padding, padding, padding)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (4 * density).toInt()
            }
            layoutParams = params
        }

        words.forEachIndexed { index, word ->
            createWordItem(batchContainer, word, startIndex + index, density, showBorder = false)
        }

        container.addView(batchContainer)
    }

    private fun createWordItem(container: LinearLayout, word: Word, index: Int, density: Float, showBorder: Boolean = true) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (4 * density).toInt()
            }
            layoutParams = params
        }

        // Index number
        val indexText = TextView(this).apply {
            text = "$index."
            textSize = 13f
            setTextColor(resources.getColor(R.color.text_secondary, null))
            setPadding(0, 0, (8 * density).toInt(), 0)
        }
        row.addView(indexText)

        // Word
        val wordText = TextView(this).apply {
            text = word.word
            textSize = 15f
            setTextColor(resources.getColor(R.color.text_primary, null))
            setPadding(0, 0, (12 * density).toInt(), 0)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
        }
        row.addView(wordText)

        // Meaning
        val meaningText = TextView(this).apply {
            textSize = 14f
            val highlighted = highlightedMeaningsMap[word.id]
            if (!highlighted.isNullOrEmpty()) {
                val spannable = SpannableString(word.meaning)
                val highlightColor = Color.parseColor("#5B9BD5")
                highlighted.forEach { hm ->
                    val idx = word.meaning.indexOf(hm)
                    if (idx >= 0) {
                        spannable.setSpan(
                            ForegroundColorSpan(highlightColor),
                            idx, idx + hm.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannable.setSpan(
                            StyleSpan(android.graphics.Typeface.BOLD),
                            idx, idx + hm.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
                text = spannable
            } else {
                text = word.meaning
                setTextColor(resources.getColor(R.color.text_secondary, null))
            }
        }
        row.addView(meaningText)

        container.addView(row)
    }
}
