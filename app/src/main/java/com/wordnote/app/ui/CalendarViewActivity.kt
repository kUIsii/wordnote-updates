package com.wordnote.app.ui

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import java.util.concurrent.ConcurrentHashMap
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.wordnote.app.R
import com.wordnote.app.databinding.ActivityCalendarViewBinding
import com.wordnote.app.databinding.ItemCalendarSentenceBinding
import com.wordnote.app.databinding.ItemCalendarCategoryHeaderBinding
import com.wordnote.app.databinding.ItemCalendarWordBinding
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

    private lateinit var binding: ActivityCalendarViewBinding
    private lateinit var viewModel: WordViewModel
    private var sentenceViewModel: SentenceViewModel? = null

    private var selectedDate: Long = System.currentTimeMillis()
    private var allWords: List<Word> = emptyList()
    private var categoriesList: List<Category> = emptyList()
    private val collapsedCategories = mutableSetOf<Long>()
    private val highlightedMeaningsMap = ConcurrentHashMap<Long, List<String>>()
    private val wordMeaningsMap = ConcurrentHashMap<Long, List<WordMeaning>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.backButton.setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        updateSelectedDateText()
    }

    private fun setupDatePicker() {
        binding.datePickerButton.setOnClickListener {
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

        binding.selectedDateText.text = dateStr
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : android.text.TextWatcher {
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

            val searchQuery = binding.searchEditText.text.toString().trim()

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
            binding.sentencesContainer.removeAllViews()

            if (sentences.isEmpty()) {
                binding.sentencesSection.visibility = View.GONE
                if (binding.categoryContainer.childCount == 0) {
                    binding.emptyView.visibility = View.VISIBLE
                }
                return
            }

            binding.sentencesSection.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE

            sentences.forEach { item ->
                val itemBinding = ItemCalendarSentenceBinding.inflate(layoutInflater, binding.sentencesContainer, false)

                itemBinding.sentenceText.text = item.sentence.originalText

                if (!item.sentence.translation.isNullOrBlank()) {
                    itemBinding.translationText.text = item.sentence.translation
                    itemBinding.translationText.visibility = View.VISIBLE
                }

                if (item.words.isNotEmpty()) {
                    itemBinding.wordCountText.text = "${item.words.size}个生词"
                    itemBinding.wordCountText.setTextColor(getColor(R.color.primary))
                    itemBinding.wordCountText.visibility = View.VISIBLE
                }

                binding.sentencesContainer.addView(itemBinding.root)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun displayWordsByCategory(words: List<Word>) {
        try {
            binding.categoryContainer.removeAllViews()

            val hasSentences = binding.sentencesSection.visibility == View.VISIBLE

            if (words.isEmpty() && !hasSentences) {
                binding.emptyView.visibility = View.VISIBLE
                binding.categoryContainer.visibility = View.GONE
                return
            }

            binding.emptyView.visibility = View.GONE
            binding.categoryContainer.visibility = if (words.isEmpty()) View.GONE else View.VISIBLE

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

        val isCollapsed = collapsedCategories.contains(category.id)

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

        // Category header from XML layout
        val headerBinding = ItemCalendarCategoryHeaderBinding.inflate(layoutInflater, container, false)
        headerBinding.categoryDot.background = android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.OVAL
            setColor(categoryColor)
        }
        headerBinding.categoryNameText.text = category.name
        headerBinding.categoryCountText.text = "${words.size}个"
        headerBinding.expandIcon.rotation = if (isCollapsed) 0f else 180f

        headerBinding.root.setOnClickListener {
            if (collapsedCategories.contains(category.id)) {
                collapsedCategories.remove(category.id)
            } else {
                collapsedCategories.add(category.id)
            }
            filterWordsByDate()
        }

        container.addView(headerBinding.root)

        // Words container (collapsible)
        if (!isCollapsed) {
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
                    createBatchGroup(wordsContainer, batchWords, categoryColor, wordIndex, density)
                    wordIndex += batchWords.size
                } else {
                    batchWords.forEach { word ->
                        createWordItem(wordsContainer, word, wordIndex, density)
                        wordIndex++
                    }
                }
            }

            container.addView(wordsContainer)
        }

        binding.categoryContainer.addView(container)
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
        val itemBinding = ItemCalendarWordBinding.inflate(layoutInflater, container, false)

        itemBinding.indexText.text = "$index."
        itemBinding.wordText.text = word.word

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
            itemBinding.meaningText.text = spannable
        } else {
            itemBinding.meaningText.text = word.meaning
        }

        container.addView(itemBinding.root)
    }
}
