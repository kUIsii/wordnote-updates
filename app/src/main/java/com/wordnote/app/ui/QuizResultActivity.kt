package com.wordnote.app.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.wordnote.app.R
import com.wordnote.app.data.Word
import com.wordnote.app.util.compatOverridePendingTransition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizResultActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var scoreText: TextView
    private lateinit var scoreDetailText: TextView
    private lateinit var correctTitleText: TextView
    private lateinit var incorrectTitleText: TextView
    private lateinit var correctWordsContainer: LinearLayout
    private lateinit var incorrectWordsContainer: LinearLayout

    private var total = 0
    private var correct = 0
    private var forgottenIds = longArrayOf()
    private var correctIds = longArrayOf()

    companion object {
        const val EXTRA_TOTAL = "total"
        const val EXTRA_CORRECT = "correct"
        const val EXTRA_FORGOTTEN_IDS = "forgotten_ids"
        const val EXTRA_CORRECT_IDS = "correct_ids"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        total = intent.getIntExtra(EXTRA_TOTAL, 0)
        correct = intent.getIntExtra(EXTRA_CORRECT, 0)
        forgottenIds = intent.getLongArrayExtra(EXTRA_FORGOTTEN_IDS) ?: longArrayOf()
        correctIds = intent.getLongArrayExtra(EXTRA_CORRECT_IDS) ?: longArrayOf()

        initViews()
        loadWords()
    }

    private fun initViews() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            compatOverridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        scoreText = findViewById(R.id.scoreText)
        scoreDetailText = findViewById(R.id.scoreDetailText)
        correctTitleText = findViewById(R.id.correctTitleText)
        incorrectTitleText = findViewById(R.id.incorrectTitleText)
        correctWordsContainer = findViewById(R.id.correctWordsContainer)
        incorrectWordsContainer = findViewById(R.id.incorrectWordsContainer)

        val percentage = if (total > 0) (correct * 100 / total) else 0
        scoreText.text = "$percentage%"
        scoreDetailText.text = "$correct / $total 正确"

        val isOldRecord = correctIds.isEmpty() && forgottenIds.isNotEmpty()
        correctTitleText.text = if (isOldRecord) "正确" else "正确 (${correctIds.size})"
        incorrectTitleText.text = "不熟悉 (${forgottenIds.size})"

        findViewById<MaterialButton>(R.id.retakeButton).setOnClickListener {
            startActivity(Intent(this, QuizSetupActivity::class.java))
            finish()
        }

        findViewById<MaterialButton>(R.id.finishButton).setOnClickListener {
            finish()
        }
    }

    private fun loadWords() {
        lifecycleScope.launch {
            val isOldRecord = correctIds.isEmpty() && forgottenIds.isNotEmpty()

            val correctWords = if (isOldRecord) {
                emptyList()
            } else {
                withContext(Dispatchers.IO) {
                    correctIds.toList().mapNotNull { id ->
                        try { viewModel.getWordByIdSync(id) } catch (e: Exception) { null }
                    }
                }
            }
            val incorrectWords = withContext(Dispatchers.IO) {
                forgottenIds.toList().mapNotNull { id ->
                    try { viewModel.getWordByIdSync(id) } catch (e: Exception) { null }
                }
            }

            withContext(Dispatchers.Main) {
                if (isOldRecord) {
                    correctWordsContainer.removeAllViews()
                    val hint = TextView(this@QuizResultActivity).apply {
                        text = "旧记录无法显示正确单词"
                        setTextColor(getColor(R.color.text_hint))
                        textSize = 13f
                        gravity = Gravity.CENTER
                        setPadding(0, dpToPx(12), 0, dpToPx(12))
                    }
                    correctWordsContainer.addView(hint)
                } else {
                    displayWords(correctWords, correctWordsContainer, isCorrect = true)
                }
                displayWords(incorrectWords, incorrectWordsContainer, isCorrect = false)
            }
        }
    }

    private fun displayWords(words: List<Word>, container: LinearLayout, isCorrect: Boolean) {
        container.removeAllViews()

        if (words.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "无"
                setTextColor(getColor(R.color.text_hint))
                textSize = 13f
                gravity = Gravity.CENTER
                setPadding(0, dpToPx(8), 0, dpToPx(8))
            }
            container.addView(emptyText)
            return
        }

        val density = resources.displayMetrics.density
        val dotColor = if (isCorrect) getColor(R.color.primary) else Color.parseColor("#E07A5F")

        words.forEachIndexed { index, word ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dpToPx(6), 0, dpToPx(6))
            }

            // Colored dot
            val dot = View(this).apply {
                val size = (6 * density).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = dpToPx(6)
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(dotColor)
                }
            }
            row.addView(dot)

            // Word
            val wordText = TextView(this).apply {
                text = word.word
                setTextColor(getColor(R.color.text_primary))
                textSize = 14f
                paint.isFakeBoldText = true
                maxLines = 1
            }
            row.addView(wordText)

            // Meaning
            val meaningText = TextView(this).apply {
                text = word.meaning
                setTextColor(getColor(R.color.text_hint))
                textSize = 12f
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    marginStart = dpToPx(8)
                }
            }
            row.addView(meaningText)

            container.addView(row)

            if (index < words.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    )
                    setBackgroundColor(getColor(R.color.divider))
                }
                container.addView(divider)
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
