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
    private lateinit var streakContainer: LinearLayout
    private lateinit var weeklyActivityContainer: LinearLayout
    private lateinit var reviewScheduleContainer: LinearLayout
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

    private fun initViews() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        streakContainer = findViewById(R.id.streakContainer)
        weeklyActivityContainer = findViewById(R.id.weeklyActivityContainer)
        reviewScheduleContainer = findViewById(R.id.reviewScheduleContainer)
        categoryContainer = findViewById(R.id.categoryDistributionContainer)
        quizStatsContainer = findViewById(R.id.quizStatsContainer)
        forgottenWordsContainer = findViewById(R.id.forgottenWordsContainer)
    }

    private fun observeData() {
        viewModel.allWords.observe(this) { words ->
            allWords = words
            updateStats()
            updateStreak()
            updateWeeklyActivity()
            updateReviewSchedule()
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
        findViewById<TextView>(R.id.totalWordsText).text = "${allWords.size}"

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val monthWords = allWords.filter { word ->
            val wordCal = Calendar.getInstance().apply { timeInMillis = word.createdAt }
            wordCal.get(Calendar.YEAR) == year && wordCal.get(Calendar.MONTH) == month
        }
        findViewById<TextView>(R.id.monthWordsText).text = "${monthWords.size}"

        val uniqueCategories = allWords.mapNotNull { it.categoryId }.distinct()
        findViewById<TextView>(R.id.categoryCountText).text = "${uniqueCategories.size}"
    }

    private fun updateStreak() {
        streakContainer.removeAllViews()

        // Calculate consecutive learning days ending today
        val wordsByDay = allWords.groupBy { getDayKey(it.createdAt) }
        val today = getDayKey(System.currentTimeMillis())

        var streak = 0
        var checkTime = today
        val cal = Calendar.getInstance()

        while (wordsByDay.containsKey(checkTime)) {
            streak++
            cal.timeInMillis = checkTime
            cal.add(Calendar.DAY_OF_YEAR, -1)
            checkTime = cal.timeInMillis
        }

        // Calculate longest streak ever
        val allDays = wordsByDay.keys.sorted()
        var longestStreak = 0
        var currentRun = 0
        var prevDay: Long? = null

        for (day in allDays) {
            if (prevDay != null) {
                cal.timeInMillis = prevDay
                cal.add(Calendar.DAY_OF_YEAR, 1)
                if (cal.timeInMillis == day) {
                    currentRun++
                } else {
                    currentRun = 1
                }
            } else {
                currentRun = 1
            }
            longestStreak = maxOf(longestStreak, currentRun)
            prevDay = day
        }

        val totalDays = wordsByDay.size

        val density = resources.displayMetrics.density

        // Current streak
        val streakRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dpToPx(6), 0, dpToPx(6))
        }

        val streakValue = TextView(this).apply {
            text = "$streak"
            setTextColor(getColor(R.color.primary))
            textSize = 28f
            paint.isFakeBoldText = true
        }
        streakRow.addView(streakValue)

        val streakLabel = TextView(this).apply {
            text = "  天连续学习"
            setTextColor(resources.getColor(R.color.text_secondary, null))
            textSize = 14f
        }
        streakRow.addView(streakLabel)

        streakContainer.addView(streakRow)

        // Stats row: longest streak + total days
        val statsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dpToPx(8), 0, dpToPx(4))
        }

        val statsItems = listOf(
            "最长连续" to "$longestStreak 天",
            "累计学习" to "$totalDays 天"
        )

        statsItems.forEachIndexed { index, (label, value) ->
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val valueText = TextView(this).apply {
                text = value
                setTextColor(resources.getColor(R.color.text_primary, null))
                textSize = 15f
                paint.isFakeBoldText = true
            }
            item.addView(valueText)

            val labelText = TextView(this).apply {
                text = label
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 12f
                setPadding(0, dpToPx(2), 0, 0)
            }
            item.addView(labelText)

            statsRow.addView(item)
        }

        streakContainer.addView(statsRow)
    }

    private fun updateWeeklyActivity() {
        weeklyActivityContainer.removeAllViews()

        val density = resources.displayMetrics.density
        val cal = Calendar.getInstance()
        val today = cal.timeInMillis

        // Get last 7 days
        data class DayData(val label: String, val count: Int)

        val days = mutableListOf<DayData>()
        val dayNames = arrayOf("日", "一", "二", "三", "四", "五", "六")

        for (i in 6 downTo 0) {
            cal.timeInMillis = today
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val dayKey = getDayKey(cal.timeInMillis)
            val count = allWords.count { getDayKey(it.createdAt) == dayKey }
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            val label = if (i == 0) "今" else dayNames[dayOfWeek - 1]
            days.add(DayData(label, count))
        }

        val maxCount = days.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

        // Bar chart
        val chartRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.BOTTOM
            setPadding(0, dpToPx(8), 0, dpToPx(4))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (100 * density).toInt()
            )
        }

        days.forEach { day ->
            val barCol = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            }

            // Count text above bar
            val countText = TextView(this).apply {
                text = if (day.count > 0) "${day.count}" else ""
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 11f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            barCol.addView(countText)

            // Bar
            val barHeight = if (day.count > 0) {
                ((day.count.toFloat() / maxCount) * 60 * density).toInt().coerceAtLeast((4 * density).toInt())
            } else {
                (4 * density).toInt()
            }

            val bar = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (16 * density).toInt(),
                    barHeight
                ).apply {
                    topMargin = dpToPx(4)
                }
                background = GradientDrawable().apply {
                    setColor(if (day.count > 0) getColor(R.color.primary) else resources.getColor(R.color.divider, null))
                    cornerRadius = 4f * density
                }
            }
            barCol.addView(bar)

            // Day label
            val dayLabel = TextView(this).apply {
                text = day.label
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 11f
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(6), 0, 0)
            }
            barCol.addView(dayLabel)

            chartRow.addView(barCol)
        }

        weeklyActivityContainer.addView(chartRow)
    }

    private fun updateReviewSchedule() {
        reviewScheduleContainer.removeAllViews()

        val now = System.currentTimeMillis()
        val wordsToReview = allWords.filter { it.nextReviewAt > 0 && it.nextReviewAt <= now }
        val neverReviewed = allWords.filter { it.nextReviewAt == 0L && it.forgetCount == 0 }
        val wellKnown = allWords.filter { it.forgetCount == 0 && it.nextReviewAt > 0 }

        val density = resources.displayMetrics.density

        if (allWords.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "暂无数据"
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(12), 0, dpToPx(12))
            }
            reviewScheduleContainer.addView(emptyText)
            return
        }

        // Progress bar showing review status
        val total = allWords.size.toFloat()

        val segments = listOf(
            Triple("待复习", wordsToReview.size, getColor(R.color.cat_hard)),
            Triple("已掌握", wellKnown.size, getColor(R.color.primary)),
            Triple("未复习", neverReviewed.size, resources.getColor(R.color.divider, null))
        )

        // Stacked progress bar
        val barBg = GradientDrawable().apply {
            setColor(resources.getColor(R.color.divider, null))
            cornerRadius = 6f * density
        }
        val barContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (14 * density).toInt()
            )
            background = barBg
        }

        var offsetX = 0
        segments.filter { it.second > 0 }.forEach { (label, count, color) ->
            val segmentWidth = ((count / total) * 100).toInt()
            if (segmentWidth > 0) {
                val segment = View(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        0,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                        width = (segmentWidth * density).toInt()
                        marginStart = offsetX
                    }
                    background = GradientDrawable().apply {
                        setColor(color)
                        cornerRadius = 6f * density
                    }
                }
                barContainer.addView(segment)
                offsetX += (segmentWidth * density).toInt()
            }
        }

        reviewScheduleContainer.addView(barContainer)

        // Legend
        val legendRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dpToPx(12), 0, dpToPx(4))
        }

        segments.forEach { (label, count, color) ->
            val legendItem = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = dpToPx(16)
                }
            }

            val dot = View(this).apply {
                val size = (8 * density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                }
            }
            legendItem.addView(dot)

            val text = TextView(this).apply {
                text = "$label $count"
                setTextColor(resources.getColor(R.color.text_secondary, null))
                textSize = 12f
                setPadding(dpToPx(6), 0, 0, 0)
            }
            legendItem.addView(text)

            legendRow.addView(legendItem)
        }

        reviewScheduleContainer.addView(legendRow)

        // Upcoming reviews
        if (wordsToReview.isNotEmpty()) {
            val upcomingTitle = TextView(this).apply {
                text = "需要复习的单词"
                setTextColor(resources.getColor(R.color.text_primary, null))
                textSize = 13f
                paint.isFakeBoldText = true
                setPadding(0, dpToPx(8), 0, dpToPx(4))
            }
            reviewScheduleContainer.addView(upcomingTitle)

            wordsToReview.sortedBy { it.nextReviewAt }.take(10).forEachIndexed { index, word ->
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(0, dpToPx(6), 0, dpToPx(6))
                }

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

                val timeLeft = word.nextReviewAt - now
                val timeText = when {
                    timeLeft < 3600000 -> "${timeLeft / 60000}分钟后"
                    timeLeft < 86400000 -> "${timeLeft / 3600000}小时后"
                    else -> "${timeLeft / 86400000}天后"
                }
                val badge = TextView(this).apply {
                    text = timeText
                    setTextColor(resources.getColor(R.color.text_hint, null))
                    textSize = 11f
                    setPadding(dpToPx(8), dpToPx(2), dpToPx(8), dpToPx(2))
                    background = GradientDrawable().apply {
                        setColor(resources.getColor(R.color.divider, null))
                        cornerRadius = 10f * density
                    }
                }
                row.addView(badge)

                reviewScheduleContainer.addView(row)

                if (index < minOf(wordsToReview.size, 10) - 1) {
                    val divider = View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                        setBackgroundColor(resources.getColor(R.color.divider, null))
                    }
                    reviewScheduleContainer.addView(divider)
                }
            }
        }
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

        val wordsByCategory = allWords.groupBy { it.categoryId }
        val categoryMap = categoriesList.associateBy { it.id }
        val maxCount = wordsByCategory.values.maxOfOrNull { it.size } ?: 1

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

        forgottenWords.forEachIndexed { index, word ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dpToPx(8), 0, dpToPx(8))
            }

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
