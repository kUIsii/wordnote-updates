package com.wordnote.app.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.wordnote.app.R
import com.wordnote.app.data.Category
import com.wordnote.app.data.QuizHistory
import com.wordnote.app.data.Word
import com.wordnote.app.util.compatOverridePendingTransitionClose
import java.util.Calendar

class StatisticsActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var heatmapView: HeatmapView
    private lateinit var categoryContainer: LinearLayout
    private lateinit var quizStatsContainer: LinearLayout
    private lateinit var forgottenWordsContainer: LinearLayout

    private var allWords: List<Word> = emptyList()
    private var categoriesList: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        initViews()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        // Force redraw heatmap when returning to this screen
        if (::heatmapView.isInitialized) {
            heatmapView.invalidate()
        }
    }

    private fun initViews() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        heatmapView = findViewById(R.id.heatmapView)
        categoryContainer = findViewById(R.id.categoryDistributionContainer)
        quizStatsContainer = findViewById(R.id.quizStatsContainer)
        forgottenWordsContainer = findViewById(R.id.forgottenWordsContainer)

        val yearText = findViewById<TextView>(R.id.heatmapYearText)
        yearText.text = "${Calendar.getInstance().get(Calendar.YEAR)} 年"

        // Set up heatmap click listener
        heatmapView.setOnDayClickListener(object : HeatmapView.OnDayClickListener {
            override fun onDayClick(date: String, count: Int) {
                val message = if (count > 0) {
                    "在 $date 学习了 $count 个单词"
                } else {
                    "在 $date 没有学习记录"
                }
                AlertDialog.Builder(this@StatisticsActivity)
                    .setTitle("学习记录")
                    .setMessage(message)
                    .setPositiveButton("确定", null)
                    .show()
            }
        })

        val legendColors = intArrayOf(
            Color.parseColor("#EBEDF0"),
            Color.parseColor("#9BE9A8"),
            Color.parseColor("#40C463"),
            Color.parseColor("#30A14E"),
            Color.parseColor("#216E39")
        )
        listOf(R.id.legend0, R.id.legend1, R.id.legend2, R.id.legend3, R.id.legend4)
            .forEachIndexed { index, id ->
                findViewById<View>(id).background = GradientDrawable().apply {
                    setColor(legendColors[index])
                    cornerRadius = 2f * resources.displayMetrics.density
                }
            }
    }

    private fun observeData() {
        viewModel.allWords.observe(this) { words ->
            allWords = words
            updateStats()
            updateHeatmap()
            updateForgottenWords()
        }

        viewModel.allCategories.observe(this) { categories ->
            categoriesList = categories
            updateCategoryDistribution()
        }

        viewModel.allQuizHistory.observe(this) { history ->
            updateQuizStats(history)
        }
    }

    private fun updateStats() {
        // Total words
        findViewById<TextView>(R.id.totalWordsText).text = "${allWords.size}"

        // This month's new words
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val monthWords = allWords.filter { word ->
            val wordCal = Calendar.getInstance().apply { timeInMillis = word.createdAt }
            wordCal.get(Calendar.YEAR) == year && wordCal.get(Calendar.MONTH) == month
        }
        findViewById<TextView>(R.id.monthWordsText).text = "${monthWords.size}"

        // Category count
        val uniqueCategories = allWords.mapNotNull { it.categoryId }.distinct()
        findViewById<TextView>(R.id.categoryCountText).text = "${uniqueCategories.size}"
    }

    private fun updateHeatmap() {
        // Group words by day
        val wordsByDay = mutableMapOf<Long, Int>()
        allWords.forEach { word ->
            val dayKey = getDayKey(word.createdAt)
            wordsByDay[dayKey] = (wordsByDay[dayKey] ?: 0) + 1
        }
        heatmapView.setData(wordsByDay)
        // Ensure redraw after layout is complete
        heatmapView.post { heatmapView.invalidate() }
    }

    private fun updateCategoryDistribution() {
        categoryContainer.removeAllViews()

        if (allWords.isEmpty() || categoriesList.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "暂无数据"
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(20), 0, dpToPx(20))
            }
            categoryContainer.addView(emptyText)
            return
        }

        // Group words by category
        val wordsByCategory = allWords.groupBy { it.categoryId }
        val categoryMap = categoriesList.associateBy { it.id }
        val maxCount = wordsByCategory.values.maxOfOrNull { it.size } ?: 1

        // Sort by count descending
        val sortedCategories = wordsByCategory.entries.sortedByDescending { it.value.size }

        sortedCategories.forEach { (categoryId, words) ->
            val category = categoryMap[categoryId] ?: return@forEach
            createCategoryBar(category, words.size, maxCount)
        }
    }

    private fun createCategoryBar(category: Category, count: Int, maxCount: Int) {
        val density = resources.displayMetrics.density

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = (12 * density).toInt()
            }
            layoutParams = params
        }

        // Category color dot
        val colorDot = View(this).apply {
            val dotSize = (10 * density).toInt()
            val params = LinearLayout.LayoutParams(dotSize, dotSize)
            layoutParams = params
            val categoryColor = try {
                Color.parseColor(category.color)
            } catch (e: Exception) {
                Color.parseColor("#757575")
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(categoryColor)
            }
        }
        row.addView(colorDot)

        // Category name
        val nameText = TextView(this).apply {
            text = category.name
            setTextColor(resources.getColor(R.color.text_primary, null))
            textSize = 13f
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = (8 * density).toInt()
            }
            layoutParams = params
            maxLines = 1
        }
        row.addView(nameText)

        // Progress bar background
        val barBg = GradientDrawable().apply {
            setColor(resources.getColor(R.color.divider, null))
            cornerRadius = 4f * density
        }

        val barContainer = FrameLayout(this).apply {
            val params = LinearLayout.LayoutParams(
                0,
                (12 * density).toInt(),
                1f
            ).apply {
                marginStart = (8 * density).toInt()
                marginEnd = (8 * density).toInt()
            }
            layoutParams = params
            background = barBg
        }

        // Progress bar fill
        val barFill = View(this).apply {
            val fillWidth = if (maxCount > 0) {
                (count.toFloat() / maxCount * 100).toInt()
            } else 0
            val params = FrameLayout.LayoutParams(
                0,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                width = (fillWidth * density).toInt()
            }
            layoutParams = params

            val categoryColor = try {
                Color.parseColor(category.color)
            } catch (e: Exception) {
                Color.parseColor("#757575")
            }

            background = GradientDrawable().apply {
                setColor(categoryColor)
                cornerRadius = 4f * density
            }
        }
        barContainer.addView(barFill)

        row.addView(barContainer)

        // Count text
        val countText = TextView(this).apply {
            text = "$count"
            setTextColor(resources.getColor(R.color.text_secondary, null))
            textSize = 13f
            val params = LinearLayout.LayoutParams(
                (32 * density).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = (4 * density).toInt()
            }
            layoutParams = params
            gravity = Gravity.END
        }
        row.addView(countText)

        categoryContainer.addView(row)
    }

    private fun getDayKey(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun updateQuizStats(history: List<QuizHistory>) {
        quizStatsContainer.removeAllViews()

        if (history.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "暂无测验记录"
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(12), 0, dpToPx(12))
            }
            quizStatsContainer.addView(emptyText)
            return
        }

        val totalQuizzes = history.size
        val totalWordsTested = history.sumOf { it.totalWords }
        val avgScore = if (totalWordsTested > 0) {
            history.sumOf { it.correctCount } * 100 / totalWordsTested
        } else 0
        val bestScore = history.maxOfOrNull {
            if (it.totalWords > 0) it.correctCount * 100 / it.totalWords else 0
        } ?: 0

        val stats = listOf(
            "测验次数" to "$totalQuizzes",
            "累计测验单词" to "$totalWordsTested",
            "平均正确率" to "$avgScore%",
            "最高正确率" to "$bestScore%"
        )

        stats.forEach { (label, value) ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dpToPx(8), 0, dpToPx(8))
            }

            val labelText = TextView(this).apply {
                text = label
                setTextColor(resources.getColor(R.color.text_secondary, null))
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            row.addView(labelText)

            val valueText = TextView(this).apply {
                text = value
                setTextColor(resources.getColor(R.color.text_primary, null))
                textSize = 15f
                paint.isFakeBoldText = true
            }
            row.addView(valueText)

            quizStatsContainer.addView(row)

            if (label != stats.last().first) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                    setBackgroundColor(resources.getColor(R.color.divider, null))
                }
                quizStatsContainer.addView(divider)
            }
        }
    }

    private fun updateForgottenWords() {
        forgottenWordsContainer.removeAllViews()

        val forgottenWords = allWords
            .filter { it.forgetCount > 0 }
            .sortedByDescending { it.forgetCount }
            .take(15)

        if (forgottenWords.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "暂无数据"
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(12), 0, dpToPx(12))
            }
            forgottenWordsContainer.addView(emptyText)
            return
        }

        val maxForgetCount = forgottenWords.first().forgetCount.coerceAtLeast(1)

        forgottenWords.forEachIndexed { index, word ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dpToPx(8), 0, dpToPx(8))
            }

            // Word name
            val wordText = TextView(this).apply {
                text = word.word
                setTextColor(resources.getColor(R.color.text_primary, null))
                textSize = 14f
                paint.isFakeBoldText = true
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            row.addView(wordText)

            // Meaning
            val meaningText = TextView(this).apply {
                text = word.meaning
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 12f
                maxLines = 1
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    marginStart = dpToPx(8)
                }
            }
            row.addView(meaningText)

            // Forget count badge
            val badge = TextView(this).apply {
                text = "${word.forgetCount}次"
                setTextColor(Color.WHITE)
                textSize = 11f
                val bg = GradientDrawable().apply {
                    setColor(getColor(R.color.cat_hard))
                    cornerRadius = 10f * resources.displayMetrics.density
                }
                background = bg
                setPadding(dpToPx(8), dpToPx(2), dpToPx(8), dpToPx(2))
            }
            row.addView(badge)

            forgottenWordsContainer.addView(row)

            if (index < forgottenWords.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                    setBackgroundColor(resources.getColor(R.color.divider, null))
                }
                forgottenWordsContainer.addView(divider)
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
