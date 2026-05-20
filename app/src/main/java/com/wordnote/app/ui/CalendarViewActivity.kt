package com.wordnote.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.wordnote.app.R
import com.wordnote.app.data.Category
import com.wordnote.app.data.Word
import com.wordnote.app.util.compatOverridePendingTransitionClose
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var backButton: ImageView
    private lateinit var datePickerButton: TextView
    private lateinit var selectedDateText: TextView
    private lateinit var searchEditText: EditText
    private lateinit var categoryContainer: LinearLayout
    private lateinit var emptyView: LinearLayout

    private var selectedDate: Long = System.currentTimeMillis()
    private var allWords: List<Word> = emptyList()
    private var categoriesList: List<Category> = emptyList()
    private val collapsedCategories = mutableSetOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

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
        viewModel.allWords.observe(this) { words ->
            allWords = words
            filterWordsByDate()
        }

        viewModel.allCategories.observe(this) { categories ->
            categoriesList = categories
            filterWordsByDate()
        }
    }

    private fun filterWordsByDate() {
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
    }

    private fun displayWordsByCategory(words: List<Word>) {
        categoryContainer.removeAllViews()

        if (words.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            categoryContainer.visibility = View.GONE
            return
        }

        emptyView.visibility = View.GONE
        categoryContainer.visibility = View.VISIBLE

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
            text = word.meaning
            textSize = 14f
            setTextColor(resources.getColor(R.color.text_secondary, null))
        }
        row.addView(meaningText)

        container.addView(row)
    }
}
