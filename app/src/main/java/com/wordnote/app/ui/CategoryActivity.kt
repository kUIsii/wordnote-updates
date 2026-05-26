package com.wordnote.app.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wordnote.app.R
import com.wordnote.app.databinding.ActivityCategoryBinding
import com.wordnote.app.data.Category
import com.wordnote.app.ui.adapter.CategoryAdapter
import com.wordnote.app.util.compatOverridePendingTransitionClose

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var viewModel: WordViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    private var selectedColor: Int = Color.parseColor("#8E24AA")

    // 按色相顺序排列: 红→粉→橙→黄→黄绿→绿→青→蓝→靛→紫→棕→灰
    private val presetColors = intArrayOf(
        Color.parseColor("#E53935"),  // 红
        Color.parseColor("#D81B60"),  // 粉
        Color.parseColor("#F4511E"),  // 深橙
        Color.parseColor("#FB8C00"),  // 橙
        Color.parseColor("#FDD835"),  // 黄
        Color.parseColor("#7CB342"),  // 黄绿
        Color.parseColor("#43A047"),  // 绿
        Color.parseColor("#00ACC1"),  // 青
        Color.parseColor("#1E88E5"),  // 蓝
        Color.parseColor("#5C6BC0"),  // 靛
        Color.parseColor("#8E24AA"),  // 紫
        Color.parseColor("#6D4C41"),  // 棕
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupAddButton()
        observeData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onEditClick = { category -> showEditCategoryDialog(category) },
            onDeleteClick = { category -> showDeleteConfirmation(category) }
        )
        binding.categoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CategoryActivity)
            adapter = categoryAdapter
        }
    }

    private fun setupAddButton() {
        binding.addCategoryButton.setOnClickListener {
            selectedColor = Color.parseColor("#8E24AA")
            showAddCategoryDialog()
        }
    }

    private fun observeData() {
        viewModel.allCategories.observe(this) { categories ->
            categoryAdapter.submitList(categories)
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_category, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.categoryNameEditText)
        val colorPreview = dialogView.findViewById<View>(R.id.colorPreview)
        val changeColorBtn = dialogView.findViewById<TextView>(R.id.changeColorButton)

        (colorPreview.background as? GradientDrawable)?.setColor(selectedColor)
        changeColorBtn.setOnClickListener {
            try {
                showColorPickerDialog { color ->
                    selectedColor = color
                    (colorPreview.background as? GradientDrawable)?.setColor(color)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_category)
            .setView(dialogView)
            .setPositiveButton(R.string.yes) { _, _ ->
                val name = nameInput.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(this, R.string.error_category_empty, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val hex = String.format("#%06X", 0xFFFFFF and selectedColor)
                viewModel.insertCategory(Category(name = name, color = hex))
                Toast.makeText(this, R.string.category_added, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun showColorPickerDialog(initialColor: Int = selectedColor, onColorSelected: (Int) -> Unit) {
        var currentColor = initialColor
        val density = resources.displayMetrics.density

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val pad = (24 * density).toInt()
            setPadding(pad, pad, pad, (12 * density).toInt())
        }

        // Preview bar
        val preview = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (56 * density).toInt()
            ).apply {
                bottomMargin = (20 * density).toInt()
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 16f * density
                setColor(currentColor)
            }
        }
        container.addView(preview)

        // Color label
        val label = TextView(this).apply {
            text = "选择分类颜色"
            setTextColor(getColor(R.color.text_primary))
            textSize = 15f
            setPadding(0, 0, 0, (12 * density).toInt())
        }
        container.addView(label)

        // Preset color grid
        val grid = GridLayout(this).apply {
            columnCount = 4
            rowCount = 3
            useDefaultMargins = true
        }
        val cellSize = (64 * density).toInt()
        val checkSize = (20 * density).toInt()

        fun refreshGrid() {
            for (i in 0 until grid.childCount) {
                val cell = grid.getChildAt(i) as FrameLayout
                val bg = cell.background as? GradientDrawable
                val cellColor = presetColors[i]
                if (cellColor == currentColor) {
                    bg?.setStroke((3 * density).toInt(), Color.WHITE)
                    cell.getChildAt(1)?.visibility = View.VISIBLE
                } else {
                    bg?.setStroke(0, Color.WHITE)
                    cell.getChildAt(1)?.visibility = View.GONE
                }
            }
        }

        presetColors.forEach { color ->
            val cellContainer = FrameLayout(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = cellSize
                    height = cellSize
                    setMargins(6, 6, 6, 6)
                }
            }

            val colorBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 14f * density
                setColor(color)
                if (color == currentColor) {
                    setStroke((3 * density).toInt(), Color.WHITE)
                }
            }
            cellContainer.background = colorBg

            val checkIcon = ImageView(this).apply {
                setImageResource(android.R.drawable.ic_menu_add)
                setColorFilter(Color.WHITE)
                layoutParams = FrameLayout.LayoutParams(checkSize, checkSize).apply {
                    gravity = Gravity.CENTER
                }
                visibility = if (color == currentColor) View.VISIBLE else View.GONE
                alpha = 0.9f
            }
            cellContainer.addView(checkIcon)

            cellContainer.setOnClickListener {
                currentColor = color
                preview.background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 16f * density
                    setColor(color)
                }
                refreshGrid()
            }

            grid.addView(cellContainer)
        }
        container.addView(grid)

        // Custom color button
        val customBtn = TextView(this).apply {
            text = "自定义颜色"
            setTextColor(getColor(R.color.primary))
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, (12 * density).toInt(), 0, (8 * density).toInt())
            val tv = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, tv, true)
            setBackgroundResource(tv.resourceId)
        }
        container.addView(customBtn)

        customBtn.setOnClickListener {
            try {
                showHslColorPicker(currentColor) { color ->
                    currentColor = color
                    preview.background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 16f * density
                        setColor(color)
                    }
                    refreshGrid()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(null)
            .setView(container)
            .setPositiveButton("确定") { _, _ -> onColorSelected(currentColor) }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showHslColorPicker(initialColor: Int, onColorSelected: (Int) -> Unit) {
        val hsv = FloatArray(3)
        Color.colorToHSV(initialColor, hsv)
        var hue = hsv[0]
        var saturation = hsv[1] * 100f
        var lightness = hsv[2] * 100f
        val density = resources.displayMetrics.density

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val pad = (24 * density).toInt()
            setPadding(pad, pad, pad, (12 * density).toInt())
        }

        // Color preview circle
        val previewSize = (48 * density).toInt()
        val preview = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(previewSize, previewSize).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = (16 * density).toInt()
            }
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(initialColor)
            }
        }
        container.addView(preview)

        // Helper to create a seek bar row
        fun createSeekBarRow(labelText: String, value: Int, maxValue: Int, onProgressChanged: (Int) -> Unit): LinearLayout {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, (6 * density).toInt(), 0, (6 * density).toInt())
            }

            val header = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val label = TextView(this).apply {
                text = labelText
                setTextColor(getColor(R.color.text_secondary))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            header.addView(label)

            val valueText = TextView(this).apply {
                text = "$value"
                setTextColor(getColor(R.color.text_primary))
                textSize = 13f
                paint.isFakeBoldText = true
            }
            header.addView(valueText)

            row.addView(header)

            val seekBar = SeekBar(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                max = maxValue
                progress = value
            }
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        valueText.text = "$progress"
                        onProgressChanged(progress)
                        // Update preview
                        val color = Color.HSVToColor(floatArrayOf(hue, saturation / 100f, lightness / 100f))
                        preview.background = GradientDrawable().apply {
                            shape = GradientDrawable.OVAL
                            setColor(color)
                        }
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            row.addView(seekBar)

            return row
        }

        // Hue (0-360)
        container.addView(createSeekBarRow("色相", hue.toInt(), 360) { hue = it.toFloat() })

        // Saturation (0-100)
        container.addView(createSeekBarRow("饱和度", saturation.toInt(), 100) { saturation = it.toFloat() })

        // Lightness (0-100)
        container.addView(createSeekBarRow("明度", lightness.toInt(), 100) { lightness = it.toFloat() })

        // Hex value display
        val hexText = TextView(this).apply {
            val color = Color.HSVToColor(floatArrayOf(hue, saturation / 100f, lightness / 100f))
            text = String.format("#%06X", 0xFFFFFF and color)
            setTextColor(getColor(R.color.text_hint))
            textSize = 12f
            gravity = Gravity.CENTER
            setPadding(0, (8 * density).toInt(), 0, 0)
        }
        container.addView(hexText)

        MaterialAlertDialogBuilder(this)
            .setTitle("自定义颜色")
            .setView(container)
            .setPositiveButton("确定") { _, _ ->
                val color = Color.HSVToColor(floatArrayOf(hue, saturation / 100f, lightness / 100f))
                onColorSelected(color)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showEditCategoryDialog(category: Category) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_category, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.categoryNameEditText)
        val colorPreview = dialogView.findViewById<View>(R.id.colorPreview)
        val changeColorBtn = dialogView.findViewById<TextView>(R.id.changeColorButton)

        nameInput.setText(category.name)
        var editColor = try { Color.parseColor(category.color) } catch (e: Exception) { Color.parseColor("#8E24AA") }
        (colorPreview.background as? GradientDrawable)?.setColor(editColor)

        changeColorBtn.setOnClickListener {
            try {
                showColorPickerDialog(editColor) { color ->
                    editColor = color
                    (colorPreview.background as? GradientDrawable)?.setColor(color)
                }
            } catch (e: Exception) {
                android.util.Log.e("CategoryActivity", "Error showing color picker", e)
                Toast.makeText(this, "颜色选择器打开失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("编辑分类")
            .setView(dialogView)
            .setPositiveButton("保存") { _, _ ->
                val name = nameInput.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(this, R.string.error_category_empty, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val hex = String.format("#%06X", 0xFFFFFF and editColor)
                viewModel.updateCategory(category.copy(name = name, color = hex))
                Toast.makeText(this, "已更新", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showDeleteConfirmation(category: Category) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_category)
            .setMessage(R.string.delete_category_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteCategory(category)
                Toast.makeText(this, R.string.category_deleted, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
}
