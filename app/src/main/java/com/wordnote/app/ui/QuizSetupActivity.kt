package com.wordnote.app.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.card.MaterialCardView
import com.wordnote.app.R
import com.wordnote.app.data.Category
import com.wordnote.app.util.compatOverridePendingTransition

class QuizSetupActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var wordCountText: TextView
    private lateinit var wordCountSeekBar: SeekBar
    private lateinit var categorySelectionContainer: LinearLayout
    private lateinit var randomRadio: RadioButton
    private lateinit var forgetCountRadio: RadioButton

    private var selectedCategories = mutableSetOf<Long>()
    private var allCategories = emptyList<Category>()
    private var allCategoryViews = mutableListOf<View>()

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
        randomRadio = findViewById(R.id.randomRadio)
        forgetCountRadio = findViewById(R.id.forgetCountRadio)

        wordCountSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                wordCountText.text = "$progress 个单词"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        findViewById<com.google.android.material.button.MaterialButton>(R.id.startQuizButton).setOnClickListener {
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
        categorySelectionContainer.removeAllViews()
        allCategoryViews.clear()

        // Add "All" option
        val allRow = createCategoryRow(null, "全部分类", 0)
        categorySelectionContainer.addView(allRow)
        allCategoryViews.add(allRow)
        selectedCategories.add(-1) // -1 means all

        // Add dividers and category rows
        categories.forEach { category ->
            val divider = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                ).apply {
                    topMargin = dpToPx(8)
                    bottomMargin = dpToPx(8)
                }
                setBackgroundColor(getColor(R.color.divider))
            }
            categorySelectionContainer.addView(divider)

            val color = try {
                Color.parseColor(category.color)
            } catch (e: Exception) {
                Color.parseColor("#757575")
            }

            val row = createCategoryRow(category.id, category.name, color)
            categorySelectionContainer.addView(row)
            allCategoryViews.add(row)
        }
    }

    private fun createCategoryRow(categoryId: Long?, name: String, color: Int): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dpToPx(4), dpToPx(8), dpToPx(4), dpToPx(8))
            background = android.util.TypedValue().let {
                theme.resolveAttribute(android.R.attr.selectableItemBackground, it, true)
                resources.getDrawable(it.resourceId, null)
            }
            tag = categoryId
        }

        // Checkbox
        val checkbox = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(dpToPx(24), dpToPx(24))
            if (categoryId == null) {
                setImageResource(android.R.drawable.ic_menu_add)
                setColorFilter(getColor(R.color.primary))
            } else {
                setImageResource(android.R.drawable.ic_menu_add)
                setColorFilter(color)
            }
            tag = "checkbox"
        }
        row.addView(checkbox)

        // Color dot (for categories)
        if (categoryId != null) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(dpToPx(12), dpToPx(12)).apply {
                    marginStart = dpToPx(12)
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                }
            }
            row.addView(dot)
        }

        // Name
        val nameText = TextView(this).apply {
            text = name
            setTextColor(getColor(R.color.text_primary))
            textSize = 15f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = if (categoryId != null) dpToPx(8) else dpToPx(12)
            }
        }
        row.addView(nameText)

        row.setOnClickListener {
            toggleCategory(categoryId, row)
        }

        return row
    }

    private fun toggleCategory(categoryId: Long?, row: View) {
        val checkbox = row.findViewWithTag<ImageView>("checkbox")

        if (categoryId == null) {
            // Toggle all
            if (selectedCategories.contains(-1)) {
                selectedCategories.clear()
                allCategoryViews.forEach { view ->
                    val cb = view.findViewWithTag<ImageView>("checkbox")
                    cb?.setImageResource(android.R.drawable.ic_menu_add)
                }
            } else {
                selectedCategories.clear()
                selectedCategories.add(-1)
                allCategoryViews.forEach { view ->
                    val cb = view.findViewWithTag<ImageView>("checkbox")
                    cb?.setImageResource(android.R.drawable.btn_minus)
                    cb?.setColorFilter(getColor(R.color.primary))
                }
            }
        } else {
            // Remove "all" if specific category selected
            selectedCategories.remove(-1)
            val allRow = allCategoryViews.firstOrNull { it.tag == null }
            allRow?.findViewWithTag<ImageView>("checkbox")?.setImageResource(android.R.drawable.ic_menu_add)

            if (selectedCategories.contains(categoryId)) {
                selectedCategories.remove(categoryId)
                checkbox?.setImageResource(android.R.drawable.ic_menu_add)
            } else {
                selectedCategories.add(categoryId)
                checkbox?.setImageResource(android.R.drawable.btn_minus)
                val color = try {
                    Color.parseColor(allCategories.find { it.id == categoryId }?.color ?: "#757575")
                } catch (e: Exception) {
                    Color.parseColor("#757575")
                }
                checkbox?.setColorFilter(color)
            }

            // If no categories selected, select all
            if (selectedCategories.isEmpty()) {
                selectedCategories.add(-1)
                allRow?.findViewWithTag<ImageView>("checkbox")?.setImageResource(android.R.drawable.btn_minus)
                allRow?.findViewWithTag<ImageView>("checkbox")?.setColorFilter(getColor(R.color.primary))
            }
        }
    }

    private fun startQuiz() {
        val wordCount = wordCountSeekBar.progress
        val useForgetCount = forgetCountRadio.isChecked

        if (selectedCategories.isEmpty() || selectedCategories.contains(-1)) {
            // All categories
            QuizActivity.launch(this, wordCount, null, useForgetCount)
        } else {
            // Specific categories
            QuizActivity.launch(this, wordCount, selectedCategories.toList(), useForgetCount)
        }

        compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
