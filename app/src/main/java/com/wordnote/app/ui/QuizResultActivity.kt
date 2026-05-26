package com.wordnote.app.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wordnote.app.R
import com.wordnote.app.databinding.ActivityQuizResultBinding
import com.wordnote.app.databinding.ItemQuizResultWordBinding
import com.wordnote.app.data.Word
import com.wordnote.app.util.compatOverridePendingTransition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizResultBinding
    private lateinit var viewModel: WordViewModel

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
        binding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        total = intent.getIntExtra(EXTRA_TOTAL, 0)
        correct = intent.getIntExtra(EXTRA_CORRECT, 0)
        forgottenIds = intent.getLongArrayExtra(EXTRA_FORGOTTEN_IDS) ?: longArrayOf()
        correctIds = intent.getLongArrayExtra(EXTRA_CORRECT_IDS) ?: longArrayOf()

        initViews()
        loadWords()
    }

    private fun initViews() {
        binding.backButton.setOnClickListener {
            finish()
            compatOverridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val percentage = if (total > 0) (correct * 100 / total) else 0
        binding.scoreText.text = "$percentage%"
        binding.scoreDetailText.text = "$correct / $total 正确"

        val isOldRecord = correctIds.isEmpty() && forgottenIds.isNotEmpty()
        binding.correctTitleText.text = if (isOldRecord) "正确" else "正确 (${correctIds.size})"
        binding.incorrectTitleText.text = "不熟悉 (${forgottenIds.size})"

        binding.retakeButton.setOnClickListener {
            startActivity(Intent(this, QuizSetupActivity::class.java))
            finish()
        }

        binding.finishButton.setOnClickListener {
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
                    binding.correctWordsContainer.removeAllViews()
                    val hint = TextView(this@QuizResultActivity).apply {
                        text = "旧记录无法显示正确单词"
                        setTextColor(getColor(R.color.text_hint))
                        textSize = 13f
                        gravity = Gravity.CENTER
                        setPadding(0, dpToPx(12), 0, dpToPx(12))
                    }
                    binding.correctWordsContainer.addView(hint)
                } else {
                    displayWords(correctWords, binding.correctWordsContainer, isCorrect = true)
                }
                displayWords(incorrectWords, binding.incorrectWordsContainer, isCorrect = false)
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

        val dotColor = if (isCorrect) getColor(R.color.primary) else Color.parseColor("#E07A5F")

        words.forEachIndexed { index, word ->
            val itemBinding = ItemQuizResultWordBinding.inflate(layoutInflater, container, false)

            itemBinding.colorDot.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(dotColor)
            }
            itemBinding.wordText.text = word.word
            itemBinding.wordText.paint.isFakeBoldText = true
            itemBinding.meaningText.text = word.meaning

            // Hide divider after last item
            if (index >= words.size - 1) {
                itemBinding.divider.visibility = View.GONE
            }

            container.addView(itemBinding.root)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
