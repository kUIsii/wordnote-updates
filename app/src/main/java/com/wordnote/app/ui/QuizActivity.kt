package com.wordnote.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.wordnote.app.R
import com.wordnote.app.data.Word
import com.wordnote.app.util.compatOverridePendingTransition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var progressText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var quizWordText: TextView
    private lateinit var quizCategoryText: TextView
    private lateinit var meaningCard: MaterialCardView
    private lateinit var quizMeaningText: TextView
    private lateinit var forgetButton: MaterialButton
    private lateinit var rememberButton: MaterialButton
    private lateinit var nextButton: MaterialButton
    private lateinit var wordDisplayContainer: LinearLayout

    private var quizWords = emptyList<Word>()
    private var currentIndex = 0
    private var correctCount = 0
    private var forgottenWords = mutableListOf<Word>()

    private var wordCount = 35
    private var categoryIds: List<Long>? = null
    private var useForgetCount = false

    private val autoAdvanceHandler = Handler(Looper.getMainLooper())
    private var isShowingMeaning = false

    companion object {
        private const val EXTRA_WORD_COUNT = "word_count"
        private const val EXTRA_CATEGORY_IDS = "category_ids"
        private const val EXTRA_USE_FORGET_COUNT = "use_forget_count"
        private const val AUTO_ADVANCE_DELAY = 2000L

        fun launch(context: Context, wordCount: Int, categoryIds: List<Long>?, useForgetCount: Boolean) {
            val intent = Intent(context, QuizActivity::class.java).apply {
                putExtra(EXTRA_WORD_COUNT, wordCount)
                putExtra(EXTRA_CATEGORY_IDS, categoryIds?.toLongArray())
                putExtra(EXTRA_USE_FORGET_COUNT, useForgetCount)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        wordCount = intent.getIntExtra(EXTRA_WORD_COUNT, 35)
        categoryIds = intent.getLongArrayExtra(EXTRA_CATEGORY_IDS)?.toList()
        useForgetCount = intent.getBooleanExtra(EXTRA_USE_FORGET_COUNT, false)

        initViews()
        // Observe categories so getCategoryById() works
        viewModel.allCategories.observe(this) { }
        loadWords()
    }

    private fun initViews() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            autoAdvanceHandler.removeCallbacksAndMessages(null)
            finish()
            compatOverridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        progressText = findViewById(R.id.progressText)
        progressBar = findViewById(R.id.progressBar)
        quizWordText = findViewById(R.id.quizWordText)
        quizCategoryText = findViewById(R.id.quizCategoryText)
        meaningCard = findViewById(R.id.meaningCard)
        quizMeaningText = findViewById(R.id.quizMeaningText)
        forgetButton = findViewById(R.id.forgetButton)
        rememberButton = findViewById(R.id.rememberButton)
        nextButton = findViewById(R.id.nextButton)
        wordDisplayContainer = findViewById(R.id.wordDisplayContainer)

        forgetButton.setOnClickListener { onWordForgotten() }
        rememberButton.setOnClickListener { onWordRemembered() }
        nextButton.setOnClickListener { advanceToNext() }
    }

    private fun loadWords() {
        lifecycleScope.launch {
            try {
                val allWords = withContext(Dispatchers.IO) {
                    val words = viewModel.getAllActiveWordsSync()
                    Log.d("QuizActivity", "Loaded ${words.size} active words, categoryIds=$categoryIds")
                    if (categoryIds.isNullOrEmpty()) {
                        words
                    } else {
                        words.filter { word ->
                            word.categoryId?.let { categoryIds!!.contains(it) } ?: false
                        }
                    }
                }

                Log.d("QuizActivity", "Filtered to ${allWords.size} words")

                if (allWords.isEmpty()) {
                    Toast.makeText(this@QuizActivity, "没有可测验的单词", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                quizWords = selectQuizWords(allWords, wordCount, useForgetCount)
                currentIndex = 0
                correctCount = 0
                forgottenWords.clear()

                Log.d("QuizActivity", "Selected ${quizWords.size} words for quiz")

                if (quizWords.isEmpty()) {
                    Toast.makeText(this@QuizActivity, "没有可测验的单词", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                showWord()
            } catch (e: Exception) {
                Log.e("QuizActivity", "Failed to load words", e)
                Toast.makeText(this@QuizActivity, "加载单词失败: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun selectQuizWords(words: List<Word>, count: Int, useForgetCount: Boolean): List<Word> {
        if (words.size <= count) return words.shuffled()

        return if (useForgetCount) {
            val sorted = words.sortedByDescending { it.forgetCount }
            val topCount = (count * 0.7).toInt().coerceAtMost(count)
            val topWords = sorted.take(topCount).shuffled()
            val remaining = sorted.drop(topCount).shuffled().take(count - topCount)
            (topWords + remaining).shuffled()
        } else {
            words.shuffled().take(count)
        }
    }

    private fun showWord() {
        if (quizWords.isEmpty() || currentIndex >= quizWords.size) {
            if (!isFinishingQuiz) finishQuiz()
            return
        }

        isShowingMeaning = false
        val word = quizWords[currentIndex]

        progressText.text = "${currentIndex + 1} / ${quizWords.size}"
        progressBar.max = quizWords.size
        progressBar.progress = currentIndex + 1

        quizWordText.text = word.word
        val category = word.categoryId?.let { viewModel.getCategoryById(it) }
        quizCategoryText.text = category?.name ?: ""

        meaningCard.visibility = View.GONE
        forgetButton.visibility = View.VISIBLE
        rememberButton.visibility = View.VISIBLE
        nextButton.visibility = View.GONE

        wordDisplayContainer.let {
            it.alpha = 0f
            it.translationY = 20f
            it.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(200)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
        }
    }

    private fun onWordForgotten() {
        if (quizWords.isEmpty() || currentIndex < 0 || currentIndex >= quizWords.size) return
        if (isShowingMeaning) return

        isShowingMeaning = true
        val word = quizWords[currentIndex]
        forgottenWords.add(word)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val updatedWord = word.copy(forgetCount = word.forgetCount + 1)
                viewModel.updateWord(updatedWord)
            }
        }

        meaningCard.visibility = View.VISIBLE
        quizMeaningText.text = word.meaning

        forgetButton.visibility = View.GONE
        rememberButton.visibility = View.GONE
        nextButton.visibility = View.VISIBLE
    }

    private fun onWordRemembered() {
        correctCount++
        currentIndex++
        showWord()
    }

    private fun advanceToNext() {
        autoAdvanceHandler.removeCallbacksAndMessages(null)
        currentIndex++
        showWord()
    }

    private var isFinishingQuiz = false

    private fun finishQuiz() {
        if (isFinishingQuiz) return
        isFinishingQuiz = true
        autoAdvanceHandler.removeCallbacksAndMessages(null)

        val totalWords = quizWords.size
        val correctCountVal = correctCount
        val forgottenIds = forgottenWords.map { it.id }.toLongArray()

        Log.d("QuizActivity", "Finishing quiz: total=$totalWords, correct=$correctCountVal, forgotten=${forgottenWords.size}")

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val history = com.wordnote.app.data.QuizHistory(
                        totalWords = totalWords,
                        correctCount = correctCountVal,
                        categoryIds = categoryIds?.joinToString(",") ?: "",
                        forgottenWordIds = forgottenWords.joinToString(",") { it.id.toString() },
                        forgottenWordTexts = forgottenWords.joinToString("||") { "${it.word}=${it.meaning}" }
                    )
                    viewModel.insertQuizHistorySync(history)
                    Log.d("QuizActivity", "Quiz history saved successfully")
                }
            } catch (e: Exception) {
                Log.e("QuizActivity", "Failed to save quiz history", e)
            }

            if (!isFinishing && !isDestroyed) {
                val intent = Intent(this@QuizActivity, QuizResultActivity::class.java).apply {
                    putExtra(QuizResultActivity.EXTRA_TOTAL, totalWords)
                    putExtra(QuizResultActivity.EXTRA_CORRECT, correctCountVal)
                    putExtra(QuizResultActivity.EXTRA_FORGOTTEN_IDS, forgottenIds)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        autoAdvanceHandler.removeCallbacksAndMessages(null)
    }
}
