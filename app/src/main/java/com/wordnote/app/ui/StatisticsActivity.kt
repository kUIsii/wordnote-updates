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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wordnote.app.R
import com.wordnote.app.data.Category
import com.wordnote.app.data.QuizHistory
import com.wordnote.app.data.Word
import com.wordnote.app.util.compatOverridePendingTransitionClose
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticsActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var streakContainer: LinearLayout
    private lateinit var trendStatsRow: LinearLayout
    private lateinit var trendChartContainer: LinearLayout
    private lateinit var pieChartView: PieChartView
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
        trendStatsRow = findViewById(R.id.trendStatsRow)
        trendChartContainer = findViewById(R.id.trendChartContainer)
        pieChartView = findViewById(R.id.pieChartView)
        quizStatsContainer = findViewById(R.id.quizStatsContainer)
        forgottenWordsContainer = findViewById(R.id.forgottenWordsContainer)
    }

    private fun observeData() {
        viewModel.allWords.observe(this) { words ->
            allWords = words
            updateStats()
            updateStreak()
            updateLearningTrend()
            updateForgottenWords()
        }

        viewModel.allCategories.observe(this) { categories ->
            categoriesList = categories
            updateCategoryDistribution()
            updateLearningTrend()
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

    private fun updateLearningTrend() {
        trendStatsRow.removeAllViews()
        trendChartContainer.removeAllViews()

        val density = resources.displayMetrics.density
        val cal = Calendar.getInstance()
        val now = System.currentTimeMillis()
        val categoryMap = categoriesList.associateBy { it.id }

        // === Stats Row ===
        // This month
        val monthCal = Calendar.getInstance()
        val year = monthCal.get(Calendar.YEAR)
        val month = monthCal.get(Calendar.MONTH)
        val monthWords = allWords.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.createdAt }
            c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) == month
        }

        // Last 7 days
        val sevenDaysAgo = now - 7L * 24 * 60 * 60 * 1000
        val last7Words = allWords.filter { it.createdAt >= sevenDaysAgo }

        val stats = listOf(
            Triple("${monthWords.size}", "本月新增", getColor(R.color.primary)),
            Triple("${last7Words.size}", "近7天", Color.parseColor("#FB8C00")),
            Triple("${allWords.size}", "总词汇量", getColor(R.color.text_primary))
        )

        stats.forEach { (value, label, color) ->
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val valueText = TextView(this).apply {
                text = value
                setTextColor(color)
                textSize = 24f
                paint.isFakeBoldText = true
                gravity = Gravity.CENTER
            }
            item.addView(valueText)

            val labelText = TextView(this).apply {
                text = label
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 12f
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(2), 0, 0)
            }
            item.addView(labelText)

            trendStatsRow.addView(item)
        }

        // === 7-Day Stacked Bar Chart by Category ===
        val days = mutableListOf<DayData>()
        val dayFormatter = SimpleDateFormat("MM/dd", Locale.getDefault())
        val weekdayFormatter = SimpleDateFormat("EEE", Locale.CHINESE)

        for (i in 6 downTo 0) {
            cal.timeInMillis = now
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val dk = getDayKey(cal.timeInMillis)
            val dayWords = allWords.filter { getDayKey(it.createdAt) == dk }
            val label = weekdayFormatter.format(cal.time)
            val catCounts = dayWords.filter { it.categoryId != null }
                .groupBy { it.categoryId!! }
                .mapValues { it.value.size }
            days.add(DayData(label, dk, catCounts))
        }

        // Find max total per day for scaling
        val maxDayTotal = days.maxOfOrNull { day ->
            day.categoryCounts.values.sum()
        }?.coerceAtLeast(1) ?: 1

        if (days.all { day -> day.categoryCounts.values.sum() == 0 }) {
            val emptyText = TextView(this).apply {
                text = "近7天暂无学习记录"
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 13f
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(16), 0, dpToPx(8))
            }
            trendChartContainer.addView(emptyText)
            return
        }

        // Build stacked bars
        val chartRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.BOTTOM
            setPadding(0, dpToPx(4), 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (120 * density).toInt()
            )
        }

        val barWidth = (32 * density).toInt()
        val maxBarHeight = (70 * density).toInt()

        days.forEach { day ->
            val total = day.categoryCounts.values.sum()

            val col = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                isClickable = total > 0
                isFocusable = total > 0
                if (total > 0) {
                    setOnClickListener { showDayCategoryDetail(day, categoryMap) }
                }
            }

            // Count on top
            val countText = TextView(this).apply {
                text = if (total > 0) "$total" else ""
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 12f
                gravity = Gravity.CENTER
            }
            col.addView(countText)

            // Stacked bar container
            val barHeight = if (total > 0) {
                ((total.toFloat() / maxDayTotal) * maxBarHeight).toInt().coerceAtLeast((6 * density).toInt())
            } else {
                (6 * density).toInt()
            }

            val barFrame = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(barWidth, barHeight).apply {
                    topMargin = dpToPx(4)
                }
            }

            if (total > 0) {
                // Draw category segments from bottom up
                var yOffset = barHeight
                val sortedCats = day.categoryCounts.entries.sortedByDescending { it.value }
                sortedCats.forEach { (catId, count) ->
                    val segHeight = ((count.toFloat() / total) * barHeight).toInt().coerceAtLeast(1)
                    yOffset -= segHeight

                    val catColor = categoryMap[catId]?.let {
                        try { Color.parseColor(it.color) } catch (e: Exception) { Color.parseColor("#757575") }
                    } ?: Color.parseColor("#757575")

                    val segment = View(this).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            segHeight
                        ).apply {
                            topMargin = yOffset
                        }
                        background = GradientDrawable().apply {
                            setColor(catColor)
                            if (yOffset == 0) cornerRadius = 4f * density
                            if (yOffset + segHeight >= barHeight - 1) {
                                // bottom segment gets bottom corners
                            }
                        }
                    }
                    barFrame.addView(segment)
                }

                // Round corners via outer background
                barFrame.clipToOutline = true
                barFrame.outlineProvider = object : android.view.ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: android.graphics.Outline) {
                        outline.setRoundRect(0, 0, view.width, view.height, 4f * density)
                    }
                }
            } else {
                barFrame.background = GradientDrawable().apply {
                    setColor(resources.getColor(R.color.divider, null))
                    cornerRadius = 4f * density
                }
            }

            col.addView(barFrame)

            // Day label
            val dayLabel = TextView(this).apply {
                text = day.label
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 11f
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(6), 0, 0)
            }
            col.addView(dayLabel)

            chartRow.addView(col)
        }

        trendChartContainer.addView(chartRow)
    }

    private fun showDayCategoryDetail(day: DayData, categoryMap: Map<Long, Category>) {
        val density = resources.displayMetrics.density

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(20), dpToPx(8), dpToPx(20), 0)
        }

        val total = day.categoryCounts.values.sum()
        val summary = TextView(this).apply {
            text = "共 $total 个单词"
            setTextColor(resources.getColor(R.color.text_hint, null))
            textSize = 13f
            setPadding(0, 0, 0, dpToPx(8))
        }
        container.addView(summary)

        val sortedCats = day.categoryCounts.entries.sortedByDescending { it.value }
        sortedCats.forEachIndexed { index, (catId, count) ->
            val category = categoryMap[catId]
            val catName = category?.name ?: "未分类"
            val catColor = category?.let {
                try { Color.parseColor(it.color) } catch (e: Exception) { Color.parseColor("#757575") }
            } ?: Color.parseColor("#757575")

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dpToPx(10), 0, dpToPx(10))
            }

            val dot = View(this).apply {
                val size = (10 * density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(catColor)
                }
            }
            row.addView(dot)

            val nameText = TextView(this).apply {
                text = catName
                setTextColor(resources.getColor(R.color.text_primary, null))
                textSize = 15f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginStart = dpToPx(10)
                }
            }
            row.addView(nameText)

            val countText = TextView(this).apply {
                text = "$count"
                setTextColor(catColor)
                textSize = 15f
                paint.isFakeBoldText = true
            }
            row.addView(countText)

            container.addView(row)

            if (index < sortedCats.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                    setBackgroundColor(resources.getColor(R.color.divider, null))
                }
                container.addView(divider)
            }
        }

        MaterialAlertDialogBuilder(this, R.style.Theme_WordNoteApp_Dialog)
            .setTitle(day.label)
            .setView(container)
            .setPositiveButton("确定", null)
            .show()
    }

    private data class DayData(val label: String, val dayKey: Long, val categoryCounts: Map<Long, Int>)



    private fun updateCategoryDistribution() {
        if (allWords.isEmpty() || categoriesList.isEmpty()) {
            pieChartView.setData(emptyList())
            return
        }

        val wordsByCategory = allWords.groupBy { it.categoryId }
        val categoryMap = categoriesList.associateBy { it.id }

        val slices = wordsByCategory.entries
            .sortedByDescending { it.value.size }
            .mapNotNull { (catId, words) ->
                val cat = categoryMap[catId] ?: return@mapNotNull null
                val color = try { Color.parseColor(cat.color) } catch (e: Exception) { Color.parseColor("#757575") }
                PieChartView.Slice(cat.name, words.size, color)
            }

        pieChartView.setData(slices)
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
