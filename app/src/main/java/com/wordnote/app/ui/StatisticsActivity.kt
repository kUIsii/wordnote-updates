package com.wordnote.app.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wordnote.app.R
import com.wordnote.app.databinding.ActivityStatisticsBinding
import com.wordnote.app.databinding.ItemDayCategoryDetailBinding
import com.wordnote.app.databinding.ItemForgottenWordBinding
import com.wordnote.app.databinding.ItemStatRowBinding
import com.wordnote.app.databinding.ItemTrendStatBinding
import com.wordnote.app.databinding.LayoutStreakDisplayBinding
import com.wordnote.app.data.Category
import com.wordnote.app.data.QuizHistory
import com.wordnote.app.data.Word
import com.wordnote.app.util.compatOverridePendingTransitionClose
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var viewModel: WordViewModel

    private var allWords: List<Word> = emptyList()
    private var categoriesList: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        initViews()
        observeData()
    }

    private fun initViews() {
        binding.backButton.setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }
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
        binding.totalWordsText.text = "${allWords.size}"

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val monthWords = allWords.filter { word ->
            val wordCal = Calendar.getInstance().apply { timeInMillis = word.createdAt }
            wordCal.get(Calendar.YEAR) == year && wordCal.get(Calendar.MONTH) == month
        }
        binding.monthWordsText.text = "${monthWords.size}"

        val uniqueCategories = allWords.mapNotNull { it.categoryId }.distinct()
        binding.categoryCountText.text = "${uniqueCategories.size}"
    }

    private fun updateStreak() {
        binding.streakContainer.removeAllViews()

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

        val streakBinding = LayoutStreakDisplayBinding.inflate(layoutInflater, binding.streakContainer, true)
        streakBinding.streakCount.text = "$streak"
        streakBinding.streakLabel.text = "  天连续学习"
        streakBinding.longestStreakValue.text = "$longestStreak 天"
        streakBinding.longestStreakLabel.text = "最长连续"
        streakBinding.totalDaysValue.text = "$totalDays 天"
        streakBinding.totalDaysLabel.text = "累计学习"
    }

    private fun updateLearningTrend() {
        binding.trendStatsRow.removeAllViews()
        binding.trendChartContainer.removeAllViews()

        val density = resources.displayMetrics.density
        val cal = Calendar.getInstance()
        val now = System.currentTimeMillis()
        val categoryMap = categoriesList.associateBy { it.id }

        // === Stats Row ===
        val monthCal = Calendar.getInstance()
        val year = monthCal.get(Calendar.YEAR)
        val month = monthCal.get(Calendar.MONTH)
        val monthWords = allWords.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.createdAt }
            c.get(Calendar.YEAR) == year && c.get(Calendar.MONTH) == month
        }

        val sevenDaysAgo = now - 7L * 24 * 60 * 60 * 1000
        val last7Words = allWords.filter { it.createdAt >= sevenDaysAgo }

        val stats = listOf(
            Triple("${monthWords.size}", "本月新增", getColor(R.color.primary)),
            Triple("${last7Words.size}", "近7天", Color.parseColor("#FB8C00")),
            Triple("${allWords.size}", "总词汇量", getColor(R.color.text_primary))
        )

        stats.forEach { (value, label, color) ->
            val itemBinding = ItemTrendStatBinding.inflate(layoutInflater, binding.trendStatsRow, true)
            itemBinding.trendStatValue.text = value
            itemBinding.trendStatValue.setTextColor(color)
            itemBinding.trendStatLabel.text = label
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
            binding.trendChartContainer.addView(emptyText)
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

        binding.trendChartContainer.addView(chartRow)
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
        sortedCats.forEach { (catId, count) ->
            val category = categoryMap[catId]
            val catName = category?.name ?: "未分类"
            val catColor = category?.let {
                try { Color.parseColor(it.color) } catch (e: Exception) { Color.parseColor("#757575") }
            } ?: Color.parseColor("#757575")

            val itemBinding = ItemDayCategoryDetailBinding.inflate(layoutInflater, container, true)
            itemBinding.categoryDot.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(catColor)
            }
            itemBinding.categoryNameText.text = catName
            itemBinding.categoryCountText.text = "$count"
            itemBinding.categoryCountText.setTextColor(catColor)
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
            binding.pieChartView.setData(emptyList())
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

        binding.pieChartView.setData(slices)
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
        binding.quizStatsContainer.removeAllViews()

        if (history.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "暂无测验记录"
                setTextColor(resources.getColor(R.color.text_hint, null))
                textSize = 14f
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(12), 0, dpToPx(12))
            }
            binding.quizStatsContainer.addView(emptyText)
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
            val rowBinding = ItemStatRowBinding.inflate(layoutInflater, binding.quizStatsContainer, true)
            rowBinding.statLabel.text = label
            rowBinding.statValue.text = value
        }
    }

    private fun updateForgottenWords() {
        binding.forgottenWordsContainer.removeAllViews()

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
            binding.forgottenWordsContainer.addView(emptyText)
            return
        }

        val density = resources.displayMetrics.density
        forgottenWords.forEach { word ->
            val itemBinding = ItemForgottenWordBinding.inflate(layoutInflater, binding.forgottenWordsContainer, true)
            itemBinding.forgottenWordText.text = word.word
            itemBinding.forgottenMeaningText.text = word.meaning
            itemBinding.forgetCountBadge.text = "${word.forgetCount}次"
            itemBinding.forgetCountBadge.background = GradientDrawable().apply {
                setColor(getColor(R.color.cat_hard))
                cornerRadius = 10f * density
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
