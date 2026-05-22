package com.wordnote.app.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.wordnote.app.R
import com.wordnote.app.data.Word
import com.wordnote.app.data.WordMeaning
import com.wordnote.app.util.compatOverridePendingTransition
import com.wordnote.app.util.compatOverridePendingTransitionClose

class WordDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_WORD_ID = "extra_word_id"
    }

    private lateinit var viewModel: WordViewModel
    private lateinit var backButton: ImageView
    private lateinit var wordTextView: TextView
    private lateinit var meaningTextView: TextView
    private lateinit var forgetCountTextView: TextView
    private lateinit var nextReviewTextView: TextView
    private lateinit var forgottenButton: MaterialButton
    private lateinit var rememberedButton: MaterialButton
    private lateinit var editButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var meaningsContainer: LinearLayout
    private lateinit var meaningsCard: MaterialCardView

    private var wordId: Long = -1
    private var currentWord: Word? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_detail)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        wordId = intent.getLongExtra(EXTRA_WORD_ID, -1)
        if (wordId == -1L) {
            finish()
            return
        }

        initViews()
        setupListeners()
        loadWord()
        loadMeanings()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        wordTextView = findViewById(R.id.wordTextView)
        meaningTextView = findViewById(R.id.meaningTextView)
        forgetCountTextView = findViewById(R.id.forgetCountTextView)
        nextReviewTextView = findViewById(R.id.nextReviewTextView)
        forgottenButton = findViewById(R.id.forgottenButton)
        rememberedButton = findViewById(R.id.rememberedButton)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)
        meaningsContainer = findViewById(R.id.meaningsContainer)
        meaningsCard = findViewById(R.id.meaningsCard)
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        forgottenButton.setOnClickListener {
            currentWord?.let { word ->
                viewModel.markAsForgotten(word)
                Toast.makeText(this, "已标记，稍后复习", Toast.LENGTH_SHORT).show()
                loadWord()
            }
        }

        rememberedButton.setOnClickListener {
            currentWord?.let { word ->
                viewModel.markAsReviewed(word)
                Toast.makeText(this, "太棒了！", Toast.LENGTH_SHORT).show()
                loadWord()
            }
        }

        editButton.setOnClickListener {
            val intent = Intent(this, AddWordActivity::class.java).apply {
                putExtra(AddWordActivity.EXTRA_WORD_ID, wordId)
            }
            startActivity(intent)
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.yes) { _, _ ->
                    currentWord?.let { word ->
                        viewModel.deleteWord(word)
                        Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }
    }

    private fun loadWord() {
        viewModel.getWordById(wordId).observe(this) { word ->
            word?.let { displayWord(it) }
        }
    }

    private fun loadMeanings() {
        viewModel.getMeaningsForWord(wordId).observe(this) { meanings ->
            displayMeanings(meanings)
        }
    }

    private fun displayWord(word: Word) {
        currentWord = word
        wordTextView.text = word.word
        meaningTextView.text = word.meaning
        forgetCountTextView.text = "${word.forgetCount}"

        if (word.nextReviewAt > 0) {
            val now = System.currentTimeMillis()
            if (word.nextReviewAt <= now) {
                nextReviewTextView.text = "该复习了"
                nextReviewTextView.setTextColor(getColor(R.color.cat_hard))
            } else {
                val diff = word.nextReviewAt - now
                val minutes = diff / (1000 * 60)
                val hours = minutes / 60
                val days = hours / 24

                nextReviewTextView.text = when {
                    days > 0 -> "${days}天后"
                    hours > 0 -> "${hours}小时后"
                    minutes > 0 -> "${minutes}分钟后"
                    else -> "马上"
                }
                nextReviewTextView.setTextColor(getColor(R.color.primary))
            }
        } else {
            nextReviewTextView.text = "未设置"
            nextReviewTextView.setTextColor(getColor(R.color.text_hint))
        }
    }

    private fun displayMeanings(meanings: List<WordMeaning>) {
        meaningsContainer.removeAllViews()
        if (meanings.isEmpty()) {
            val word = currentWord
            if (word != null) {
                val parts = word.meaning.split("，", ",").map { it.trim() }.filter { it.isNotBlank() }
                if (parts.size > 1) {
                    // Multiple meanings - show split button
                    meaningsCard.visibility = View.VISIBLE
                    meaningTextView.visibility = View.GONE

                    val label = TextView(this).apply {
                        text = "各释义标注"
                        textSize = 15f
                        setTextColor(getColor(R.color.text_primary))
                        setPadding(0, 0, 0, dpToPx(8))
                    }
                    meaningsContainer.addView(label)

                    val hint = TextView(this).apply {
                        text = "点击下方按钮拆分释义，即可对每个释义单独标记和排序"
                        textSize = 13f
                        setTextColor(getColor(R.color.text_secondary))
                        setPadding(0, 0, 0, dpToPx(12))
                    }
                    meaningsContainer.addView(hint)

                    val splitButton = TextView(this).apply {
                        text = "拆分释义"
                        textSize = 14f
                        setPadding(dpToPx(16), dpToPx(10), dpToPx(16), dpToPx(10))
                        val bg = GradientDrawable().apply {
                            setColor(getColor(R.color.primary))
                            cornerRadius = 20f * resources.displayMetrics.density
                        }
                        background = bg
                        setTextColor(getColor(R.color.text_on_primary))
                        setOnClickListener {
                            viewModel.saveMeanings(wordId, parts)
                            Toast.makeText(this@WordDetailActivity, "已拆分 ${parts.size} 个释义", Toast.LENGTH_SHORT).show()
                        }
                    }
                    meaningsContainer.addView(splitButton)
                    return
                } else if (parts.size == 1) {
                    // Single meaning - auto-create and show marking UI
                    viewModel.createMeaningsIfEmpty(wordId, parts)
                    return  // Will re-trigger via LiveData observer
                }
            }
            meaningsCard.visibility = View.GONE
            meaningTextView.visibility = View.VISIBLE
            return
        }
        meaningsCard.visibility = View.VISIBLE
        meaningTextView.visibility = View.GONE

        val label = TextView(this).apply {
            text = "各释义标注"
            textSize = 15f
            setTextColor(getColor(R.color.text_primary))
            setPadding(0, 0, 0, dpToPx(8))
        }
        meaningsContainer.addView(label)

        // Highlight color is always blue for all categories
        val highlightColor = Color.parseColor("#5B9BD5")

        meanings.forEachIndexed { index, meaning ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(dpToPx(4), dpToPx(10), dpToPx(4), dpToPx(10))
                gravity = android.view.Gravity.CENTER_VERTICAL
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dpToPx(2)
                }
                layoutParams = params
            }

            // Sequence number
            val seqText = TextView(this).apply {
                text = "${index + 1}."
                textSize = 13f
                setTextColor(getColor(R.color.text_secondary))
                setPadding(dpToPx(2), 0, dpToPx(6), 0)
                minWidth = dpToPx(24)
            }
            row.addView(seqText)

            // Up/Down buttons for reorder
            val upButton = TextView(this).apply {
                text = "▲"
                textSize = 10f
                setPadding(dpToPx(4), dpToPx(2), dpToPx(4), dpToPx(2))
                setTextColor(if (index > 0) getColor(R.color.text_secondary) else getColor(R.color.text_hint))
                alpha = if (index > 0) 1f else 0.3f
                if (index > 0) {
                    setOnClickListener {
                        val mutableMeanings = meanings.toMutableList()
                        val item = mutableMeanings.removeAt(index)
                        mutableMeanings.add(index - 1, item)
                        viewModel.reorderMeanings(mutableMeanings)
                    }
                }
            }
            row.addView(upButton)

            val downButton = TextView(this).apply {
                text = "▼"
                textSize = 10f
                setPadding(dpToPx(4), dpToPx(2), dpToPx(4), dpToPx(2))
                setTextColor(if (index < meanings.size - 1) getColor(R.color.text_secondary) else getColor(R.color.text_hint))
                alpha = if (index < meanings.size - 1) 1f else 0.3f
                if (index < meanings.size - 1) {
                    setOnClickListener {
                        val mutableMeanings = meanings.toMutableList()
                        val item = mutableMeanings.removeAt(index)
                        mutableMeanings.add(index + 1, item)
                        viewModel.reorderMeanings(mutableMeanings)
                    }
                }
            }
            row.addView(downButton)

            // Meaning text styling
            val meaningText = TextView(this).apply {
                text = meaning.meaningText
                textSize = 15f
                val density = resources.displayMetrics.density
                if (meaning.isProblematic) {
                    setTextColor(getColor(R.color.cat_hard))
                    val bg = GradientDrawable().apply {
                        setColor(Color.parseColor("#1AE8636A"))
                        cornerRadius = 6f * density
                    }
                    background = bg
                    setPadding(dpToPx(8), dpToPx(2), dpToPx(8), dpToPx(2))
                    paint.isFakeBoldText = true
                } else if (meaning.isHighlighted) {
                    setTextColor(highlightColor)
                    val bg = GradientDrawable().apply {
                        setColor(Color.argb(35, Color.red(highlightColor), Color.green(highlightColor), Color.blue(highlightColor)))
                        cornerRadius = 6f * density
                    }
                    background = bg
                    setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4))
                    paint.isFakeBoldText = true
                } else {
                    setTextColor(getColor(R.color.text_primary))
                }
                val params = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                layoutParams = params
            }
            row.addView(meaningText)

            // "标记" button - for ALL categories
            val highlightButton = TextView(this).apply {
                text = if (meaning.isHighlighted) "已标记" else "标记"
                textSize = 11f
                setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4))
                if (meaning.isHighlighted) {
                    setTextColor(highlightColor)
                } else {
                    val bg = GradientDrawable().apply {
                        setStroke(dpToPx(1), getColor(R.color.divider))
                        cornerRadius = 16f * resources.displayMetrics.density
                        setColor(Color.TRANSPARENT)
                    }
                    background = bg
                    setTextColor(getColor(R.color.text_secondary))
                }
                setOnClickListener {
                    viewModel.toggleMeaningHighlighted(meaning)
                }
            }
            row.addView(highlightButton)

            // Note button
            val noteButton = TextView(this).apply {
                text = if (meaning.note != null) "有备注" else "备注"
                textSize = 12f
                setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4))
                setTextColor(getColor(R.color.text_secondary))
                setOnClickListener {
                    showNoteDialog(meaning)
                }
            }
            row.addView(noteButton)

            meaningsContainer.addView(row)

            if (meaning.note != null && meaning.note.isNotBlank()) {
                val noteText = TextView(this).apply {
                    text = "备注: ${meaning.note}"
                    textSize = 12f
                    setTextColor(getColor(R.color.text_hint))
                    setPadding(dpToPx(8), 0, dpToPx(8), dpToPx(6))
                }
                meaningsContainer.addView(noteText)
            }
        }
    }

    private fun showNoteDialog(meaning: WordMeaning) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_note_input, null)
        val input = dialogView.findViewById<EditText>(R.id.noteEditText)
        input.setText(meaning.note ?: "")

        MaterialAlertDialogBuilder(this)
            .setTitle("添加备注")
            .setView(dialogView)
            .setPositiveButton("保存") { _, _ ->
                val note = input.text.toString().trim().ifBlank { null }
                viewModel.updateMeaningNote(meaning, note)
                Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onResume() {
        super.onResume()
        loadWord()
        loadMeanings()
    }
}
