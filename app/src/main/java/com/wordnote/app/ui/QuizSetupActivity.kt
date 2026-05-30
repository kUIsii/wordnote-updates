package com.wordnote.app.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wordnote.app.R
import com.wordnote.app.data.Category
import com.wordnote.app.data.QuizHistory
import com.wordnote.app.data.QuizMode
import com.wordnote.app.databinding.ActivityQuizSetupBinding
import com.wordnote.app.util.compatOverridePendingTransition

class QuizSetupActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var binding: ActivityQuizSetupBinding

    private var selectedCategories = mutableSetOf<Long>()
    private var allCategories = emptyList<Category>()
    private var categoryCheckBoxes = mutableListOf<Pair<Long, CheckBox>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        initViews()
        observeData()
    }

    private fun initViews() {
        binding.backButton.setOnClickListener {
            finish()
            compatOverridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        binding.wordCountSeekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                binding.wordCountText.text = "$progress 个单词"
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.randomCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) binding.forgetCountCheckBox.isChecked = false
            updateMethodCards()
        }
        binding.forgetCountCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) binding.randomCheckBox.isChecked = false
            updateMethodCards()
        }

        binding.randomCard.setOnClickListener { binding.randomCheckBox.isChecked = true }
        binding.forgetCountCard.setOnClickListener { binding.forgetCountCheckBox.isChecked = true }
        updateMethodCards()

        binding.startQuizButton.setOnClickListener {
            it.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                    startQuiz()
                }
                .start()
        }

        binding.viewAllHistoryButton.setOnClickListener {
            startActivity(Intent(this, QuizHistoryActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun observeData() {
        viewModel.allCategories.observe(this) { categories ->
            allCategories = categories
            setupCategorySelection(categories)
        }

        viewModel.allQuizHistory.observe(this) { history ->
            if (history.isNullOrEmpty()) {
                binding.historySection.visibility = View.GONE
            } else {
                binding.historySection.visibility = View.VISIBLE
                displayHistory(history)
            }
        }
    }

    private fun updateMethodCards() {
        val primaryColor = getColor(R.color.primary)
        val cardBgColor = getColor(R.color.card_background)
        val dividerColor = getColor(R.color.divider)

        if (binding.randomCheckBox.isChecked) {
            (binding.randomCard as? com.google.android.material.card.MaterialCardView)?.apply {
                setCardBackgroundColor(primaryColor)
                setStrokeColor(android.content.res.ColorStateList.valueOf(primaryColor))
            }
            updateCardTextColors(binding.randomCard, isLight = true)

            (binding.forgetCountCard as? com.google.android.material.card.MaterialCardView)?.apply {
                setCardBackgroundColor(cardBgColor)
                setStrokeColor(android.content.res.ColorStateList.valueOf(dividerColor))
            }
            updateCardTextColors(binding.forgetCountCard, isLight = false)
        } else {
            (binding.forgetCountCard as? com.google.android.material.card.MaterialCardView)?.apply {
                setCardBackgroundColor(primaryColor)
                setStrokeColor(android.content.res.ColorStateList.valueOf(primaryColor))
            }
            updateCardTextColors(binding.forgetCountCard, isLight = true)

            (binding.randomCard as? com.google.android.material.card.MaterialCardView)?.apply {
                setCardBackgroundColor(cardBgColor)
                setStrokeColor(android.content.res.ColorStateList.valueOf(dividerColor))
            }
            updateCardTextColors(binding.randomCard, isLight = false)
        }
    }

    private fun updateCardTextColors(card: View, isLight: Boolean) {
        val innerLayout = (card as? android.view.ViewGroup)?.let { vg ->
            for (i in 0 until vg.childCount) {
                val child = vg.getChildAt(i)
                if (child is LinearLayout) return@let child
            }
            null
        } ?: return

        for (j in 0 until innerLayout.childCount) {
            val child = innerLayout.getChildAt(j)
            when (child) {
                is TextView -> {
                    if (child.textSize > 13f) {
                        child.setTextColor(if (isLight) android.graphics.Color.WHITE else getColor(com.wordnote.app.R.color.text_primary))
                    } else {
                        child.setTextColor(if (isLight) android.graphics.Color.argb(179, 255, 255, 255) else getColor(com.wordnote.app.R.color.text_hint))
                    }
                }
                is ImageView -> {
                    child.setColorFilter(
                        if (isLight) android.graphics.Color.WHITE else getColor(com.wordnote.app.R.color.text_hint),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }
    }

    private fun setupCategorySelection(categories: List<Category>) {
        categoryCheckBoxes.clear()
        selectedCategories.clear()
        categories.forEach { selectedCategories.add(it.id) }

        binding.allCheckBox.isChecked = true
        styleCheckBox(binding.allCheckBox, getColor(R.color.primary))
        binding.allCheckBox.setOnCheckedChangeListener { _, isChecked ->
            toggleAllCategories(isChecked)
        }

        categories.forEach { category ->
            val color = try {
                Color.parseColor(category.color)
            } catch (e: Exception) {
                Color.parseColor("#757575")
            }

            val checkBox = CheckBox(this).apply {
                text = category.name
                textSize = 15f
                buttonTintList = android.content.res.ColorStateList.valueOf(color)
                isChecked = true
                tag = category.id
                setPadding(dpToPx(4), dpToPx(6), dpToPx(8), dpToPx(6))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            styleCheckBox(checkBox, color)
            setCheckBoxCheckedState(checkBox, true)

            categoryCheckBoxes.add(category.id to checkBox)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCategoryToggled(category.id, isChecked)
            }

            binding.categorySelectionContainer.addView(checkBox)
        }
    }

    private fun styleCheckBox(checkBox: CheckBox, color: Int) {
        val bg = GradientDrawable().apply {
            cornerRadius = 20f * resources.displayMetrics.density
            setColor(Color.TRANSPARENT)
            setStroke(0, Color.TRANSPARENT)
        }
        checkBox.background = bg
        checkBox.setTextColor(getColor(R.color.text_primary))

        val originalColor = color
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            val tag = buttonView.tag
            val itemColor = if (tag is Long) {
                allCategories.find { it.id == tag }?.let {
                    try { Color.parseColor(it.color) } catch (e: Exception) { Color.parseColor("#757575") }
                } ?: Color.parseColor("#757575")
            } else {
                originalColor
            }

            val targetAlpha = if (isChecked) 30 else 0
            val animator = ValueAnimator.ofArgb(
                Color.argb(0, Color.red(itemColor), Color.green(itemColor), Color.blue(itemColor)),
                Color.argb(targetAlpha, Color.red(itemColor), Color.green(itemColor), Color.blue(itemColor))
            )
            animator.duration = 200
            animator.interpolator = DecelerateInterpolator()
            animator.addUpdateListener { animator ->
                bg.setColor(animator.animatedValue as Int)
            }
            animator.start()

            val scale = if (isChecked) 1.05f else 1.0f
            buttonView.animate()
                .scaleX(scale).scaleY(scale)
                .setDuration(150)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    private fun onCategoryToggled(categoryId: Long, isChecked: Boolean) {
        if (isChecked) {
            selectedCategories.add(categoryId)
        } else {
            selectedCategories.remove(categoryId)
        }
        // Sync "全部" checkbox state
        binding.allCheckBox.setOnCheckedChangeListener(null)
        binding.allCheckBox.isChecked = selectedCategories.size == allCategories.size && allCategories.isNotEmpty()
        binding.allCheckBox.setOnCheckedChangeListener { _, isChecked2 -> toggleAllCategories(isChecked2) }
    }

    private fun toggleAllCategories(selectAll: Boolean) {
        if (selectAll) {
            selectedCategories.clear()
            allCategories.forEach { selectedCategories.add(it.id) }
        } else {
            selectedCategories.clear()
        }
        categoryCheckBoxes.forEach { (_, cb) ->
            setCheckBoxCheckedState(cb, selectAll)
        }
    }

    private fun setCheckBoxCheckedState(checkBox: CheckBox, checked: Boolean) {
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = checked
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            val id = buttonView.tag as? Long ?: return@setOnCheckedChangeListener
            onCategoryToggled(id, isChecked)
        }
        val bg = checkBox.background as? GradientDrawable
        if (bg != null) {
            val tag = checkBox.tag
            val itemColor = if (tag is Long) {
                allCategories.find { it.id == tag }?.let {
                    try { Color.parseColor(it.color) } catch (e: Exception) { Color.parseColor("#757575") }
                } ?: Color.parseColor("#757575")
            } else {
                getColor(R.color.primary)
            }
            val targetAlpha = if (checked) 30 else 0
            bg.setColor(Color.argb(targetAlpha, Color.red(itemColor), Color.green(itemColor), Color.blue(itemColor)))
        }
        val scale = if (checked) 1.05f else 1.0f
        checkBox.scaleX = scale
        checkBox.scaleY = scale
    }

    private fun displayHistory(history: List<QuizHistory>) {
        binding.historyContainer.removeAllViews()

        history.take(10).forEachIndexed { index, record ->
            val percentage = if (record.totalWords > 0) (record.correctCount * 100 / record.totalWords) else 0
            val dateStr = java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(record.createdAt))

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dpToPx(4), dpToPx(10), dpToPx(4), dpToPx(10))
                isClickable = true
                isFocusable = true
                val tv = android.util.TypedValue()
                context.theme.resolveAttribute(android.R.attr.selectableItemBackground, tv, true)
                setBackgroundResource(tv.resourceId)
            }

            row.setOnClickListener { showQuizDetail(record) }

            row.setOnLongClickListener {
                MaterialAlertDialogBuilder(this, R.style.Theme_WordNoteApp_Dialog)
                    .setTitle("删除记录")
                    .setMessage("确定要删除这次测验记录吗？")
                    .setPositiveButton("删除") { _, _ ->
                        viewModel.deleteQuizHistory(record)
                        Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("取消", null)
                    .show()
                true
            }

            // Date
            val dateText = TextView(this).apply {
                text = dateStr
                setTextColor(getColor(R.color.text_hint))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            row.addView(dateText)

            // Score
            val scoreColor = when {
                percentage >= 80 -> getColor(R.color.primary)
                percentage >= 50 -> Color.parseColor("#FB8C00")
                else -> Color.parseColor("#E07A5F")
            }
            val scoreText = TextView(this).apply {
                text = "$percentage%"
                setTextColor(scoreColor)
                textSize = 15f
                paint.isFakeBoldText = true
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginStart = dpToPx(16)
                }
                gravity = Gravity.END
            }
            row.addView(scoreText)

            // Detail
            val detailText = TextView(this).apply {
                text = "${record.correctCount}/${record.totalWords}"
                setTextColor(getColor(R.color.text_secondary))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = dpToPx(12)
                }
            }
            row.addView(detailText)

            binding.historyContainer.addView(row)

            if (index < history.size - 1 && index < 9) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    )
                    setBackgroundColor(getColor(R.color.divider))
                }
                binding.historyContainer.addView(divider)
            }
        }
    }

    private fun showQuizDetail(record: QuizHistory) {
        val forgottenIds = record.forgottenWordIds
            .split(",")
            .filter { it.isNotBlank() }
            .map { it.trim().toLong() }
            .toLongArray()

        val correctIds = record.correctWordIds
            .split(",")
            .filter { it.isNotBlank() }
            .map { it.trim().toLong() }
            .toLongArray()

        val intent = Intent(this, QuizResultActivity::class.java).apply {
            putExtra(QuizResultActivity.EXTRA_TOTAL, record.totalWords)
            putExtra(QuizResultActivity.EXTRA_CORRECT, record.correctCount)
            putExtra(QuizResultActivity.EXTRA_FORGOTTEN_IDS, forgottenIds)
            putExtra(QuizResultActivity.EXTRA_CORRECT_IDS, correctIds)
        }
        startActivity(intent)
        compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun startQuiz() {
        val wordCount = binding.wordCountSeekBar.progress
        if (wordCount <= 0) {
            Toast.makeText(this, "请选择测验数量", Toast.LENGTH_SHORT).show()
            return
        }
        val useForgetCount = binding.forgetCountCheckBox.isChecked && !binding.randomCheckBox.isChecked

        val quizMode = when (binding.quizModeChipGroup.checkedChipId) {
            R.id.chipCnToEn -> QuizMode.CN_TO_EN
            R.id.chipSpelling -> QuizMode.SPELLING
            R.id.chipMixed -> QuizMode.MIXED
            else -> QuizMode.EN_TO_CN
        }

        try {
            if (selectedCategories.size == allCategories.size) {
                QuizActivity.launch(this, wordCount, null, useForgetCount, quizMode)
            } else {
                QuizActivity.launch(this, wordCount, selectedCategories.toList(), useForgetCount, quizMode)
            }
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "启动测验失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
