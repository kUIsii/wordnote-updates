package com.wordnote.app.ui

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.wordnote.app.R
import com.wordnote.app.data.Category
import com.wordnote.app.util.compatOverridePendingTransition

class QuizSetupActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var wordCountText: TextView
    private lateinit var wordCountSeekBar: SeekBar
    private lateinit var categorySelectionContainer: LinearLayout
    private lateinit var allCheckBox: CheckBox
    private lateinit var randomCheckBox: CheckBox
    private lateinit var forgetCountCheckBox: CheckBox

    private var selectedCategories = mutableSetOf<Long>()
    private var allCategories = emptyList<Category>()
    private var categoryCheckBoxes = mutableListOf<Pair<Long, CheckBox>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_setup)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        initViews()
        observeData()
    }

    private fun initViews() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            compatOverridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        wordCountText = findViewById(R.id.wordCountText)
        wordCountSeekBar = findViewById(R.id.wordCountSeekBar)
        categorySelectionContainer = findViewById(R.id.categorySelectionContainer)
        allCheckBox = findViewById(R.id.allCheckBox)
        randomCheckBox = findViewById(R.id.randomCheckBox)
        forgetCountCheckBox = findViewById(R.id.forgetCountCheckBox)

        wordCountSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                wordCountText.text = "$progress 个单词"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        allCheckBox.setOnCheckedChangeListener { _, isChecked ->
            toggleAllCategories(isChecked)
        }

        randomCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) forgetCountCheckBox.isChecked = false
        }
        forgetCountCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) randomCheckBox.isChecked = false
        }

        findViewById<MaterialButton>(R.id.startQuizButton).setOnClickListener {
            startQuiz()
        }
    }

    private fun observeData() {
        viewModel.allCategories.observe(this) { categories ->
            allCategories = categories
            setupCategorySelection(categories)
        }
    }

    private fun setupCategorySelection(categories: List<Category>) {
        categoryCheckBoxes.clear()
        selectedCategories.clear()
        selectedCategories.add(-1)

        allCheckBox.isChecked = true
        styleCheckBox(allCheckBox, getColor(R.color.primary))

        categories.forEach { category ->
            val color = try {
                Color.parseColor(category.color)
            } catch (e: Exception) {
                Color.parseColor("#757575")
            }

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dpToPx(4), dpToPx(6), dpToPx(4), dpToPx(6))
                tag = category.id
            }

            val colorDot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(dpToPx(12), dpToPx(12)).apply {
                    marginEnd = dpToPx(12)
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                }
            }
            row.addView(colorDot)

            val checkBox = CheckBox(this).apply {
                text = category.name
                textSize = 15f
                buttonTintList = android.content.res.ColorStateList.valueOf(color)
                isChecked = true
                tag = category.id
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            styleCheckBox(checkBox, color)
            row.addView(checkBox)

            categoryCheckBoxes.add(category.id to checkBox)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCategoryToggled(category.id, isChecked)
            }

            categorySelectionContainer.addView(row)
        }
    }

    private fun styleCheckBox(checkBox: CheckBox, color: Int) {
        val bg = GradientDrawable().apply {
            cornerRadius = 20f * resources.displayMetrics.density
            setColor(Color.TRANSPARENT)
            setStroke(0, Color.TRANSPARENT)
        }
        checkBox.background = bg
        checkBox.setPadding(dpToPx(4), dpToPx(2), dpToPx(8), dpToPx(2))
        checkBox.setTextColor(getColor(R.color.text_primary))

        val originalColor = color
        checkBox.tag = checkBox.tag // preserve original tag
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
            selectedCategories.remove(-1)
            selectedCategories.add(categoryId)
            allCheckBox.isChecked = false
        } else {
            selectedCategories.remove(categoryId)
            if (selectedCategories.isEmpty()) {
                selectedCategories.add(-1)
                allCheckBox.isChecked = true
            }
        }
    }

    private fun toggleAllCategories(selectAll: Boolean) {
        if (selectAll) {
            selectedCategories.clear()
            selectedCategories.add(-1)
            categoryCheckBoxes.forEach { (_, cb) ->
                cb.setOnCheckedChangeListener(null)
                cb.isChecked = true
                cb.setOnCheckedChangeListener { buttonView, isChecked ->
                    val id = buttonView.tag as Long
                    onCategoryToggled(id, isChecked)
                }
            }
        } else {
            selectedCategories.clear()
            categoryCheckBoxes.forEach { (_, cb) ->
                cb.setOnCheckedChangeListener(null)
                cb.isChecked = false
                cb.setOnCheckedChangeListener { buttonView, isChecked ->
                    val id = buttonView.tag as Long
                    onCategoryToggled(id, isChecked)
                }
            }
            selectedCategories.add(-1)
            allCheckBox.isChecked = true
        }
    }

    private fun startQuiz() {
        val wordCount = wordCountSeekBar.progress
        val useForgetCount = forgetCountCheckBox.isChecked && !randomCheckBox.isChecked

        if (selectedCategories.isEmpty() || selectedCategories.contains(-1)) {
            QuizActivity.launch(this, wordCount, null, useForgetCount)
        } else {
            QuizActivity.launch(this, wordCount, selectedCategories.toList(), useForgetCount)
        }

        compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
