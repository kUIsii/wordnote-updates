package com.wordnote.app.ui

import android.content.Context
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.wordnote.app.R
import com.wordnote.app.data.Word
import com.wordnote.app.util.compatOverridePendingTransition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizResultActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var scoreText: TextView
    private lateinit var scoreDetailText: TextView
    private lateinit var forgottenTitleText: TextView
    private lateinit var forgottenWordsContainer: LinearLayout

    private var total = 0
    private var correct = 0
    private var forgottenIds = longArrayOf()
    private var forgottenWords = emptyList<Word>()

    companion object {
        const val EXTRA_TOTAL = "total"
        const val EXTRA_CORRECT = "correct"
        const val EXTRA_FORGOTTEN_IDS = "forgotten_ids"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        total = intent.getIntExtra(EXTRA_TOTAL, 0)
        correct = intent.getIntExtra(EXTRA_CORRECT, 0)
        forgottenIds = intent.getLongArrayExtra(EXTRA_FORGOTTEN_IDS) ?: longArrayOf()

        initViews()
        loadForgottenWords()
    }

    private fun initViews() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            compatOverridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        scoreText = findViewById(R.id.scoreText)
        scoreDetailText = findViewById(R.id.scoreDetailText)
        forgottenTitleText = findViewById(R.id.forgottenTitleText)
        forgottenWordsContainer = findViewById(R.id.forgottenWordsContainer)

        val percentage = if (total > 0) (correct * 100 / total) else 0
        scoreText.text = "$percentage%"
        scoreDetailText.text = "$correct / $total 正确"

        forgottenTitleText.text = "不熟悉的单词 (${forgottenIds.size})"

        findViewById<MaterialButton>(R.id.retakeButton).setOnClickListener {
            startActivity(Intent(this, QuizSetupActivity::class.java))
            finish()
        }

        findViewById<MaterialButton>(R.id.finishButton).setOnClickListener {
            finish()
        }
    }

    private fun loadForgottenWords() {
        if (forgottenIds.isEmpty()) {
            forgottenWordsContainer.visibility = View.GONE
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val words = mutableListOf<Word>()
            forgottenIds.forEach { id ->
                viewModel.getWordByIdSync(id)?.let { words.add(it) }
            }
            forgottenWords = words

            withContext(Dispatchers.Main) {
                displayForgottenWords()
            }
        }
    }

    private fun displayForgottenWords() {
        forgottenWordsContainer.removeAllViews()

        forgottenWords.forEach { word ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dpToPx(12), 0, dpToPx(12))
            }

            // Word
            val wordText = TextView(this).apply {
                text = word.word
                setTextColor(getColor(R.color.text_primary))
                textSize = 16f
                paint.isFakeBoldText = true
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = dpToPx(12)
                }
            }
            row.addView(wordText)

            // Meaning
            val meaningText = TextView(this).apply {
                text = word.meaning
                setTextColor(getColor(R.color.text_secondary))
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                maxLines = 2
            }
            row.addView(meaningText)

            forgottenWordsContainer.addView(row)

            // Divider
            if (word != forgottenWords.last()) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    )
                    setBackgroundColor(getColor(R.color.divider))
                }
                forgottenWordsContainer.addView(divider)
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
