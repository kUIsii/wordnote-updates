package com.wordnote.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var buttonContainer: LinearLayout

    private var allWords = emptyList<Word>()
    private var quizWords = emptyList<Word>()
    private var currentIndex = 0
    private var correctCount = 0
    private var forgottenWords = mutableListOf<Word>()
    private var showingMeaning = false

    private var wordCount = 35
    private var categoryIds: List<Long>? = null
    private var useForgetCount = false

    companion object {
        private const val EXTRA_WORD_COUNT = "word_count"
        private const val EXTRA_CATEGORY_IDS = "category_ids"
        private const val EXTRA_USE_FORGET_COUNT = "use_forget_count"

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
        loadWords()
    }

    private fun initViews() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
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
        buttonContainer = findViewById(R.id.buttonContainer)

        forgetButton.setOnClickListener {
            onWordForgotten()
        }

        rememberButton.setOnClickListener {
            onWordRemembered()
        }

        nextButton.setOnClickListener {
            showNextWord()
        }
    }

    private fun loadWords() {
        CoroutineScope(Dispatchers.IO).launch {
            val words = if (categoryIds == null || categoryIds!!.isEmpty()) {
                viewModel.allWords.value ?: emptyList()
            } else {
                viewModel.allWords.value?.filter { word ->
                    word.categoryId?.let { categoryIds!!.contains(it) } ?: false
                } ?: emptyList()
            }

            allWords = words

            withContext(Dispatchers.Main) {
                if (allWords.isEmpty()) {
                    Toast.makeText(this@QuizActivity, "没有可测验的单词", Toast.LENGTH_SHORT).show()
                    finish()
                    return@withContext
                }

                quizWords = selectQuizWords(allWords, wordCount, useForgetCount)
                currentIndex = 0
                correctCount = 0
                forgottenWords.clear()

                showNextWord()
            }
        }
    }

    private fun selectQuizWords(words: List<Word>, count: Int, useForgetCount: Boolean): List<Word> {
        if (words.size <= count) return words.shuffled()

        return if (useForgetCount) {
            // Sort by forgetCount descending, then take top words with some randomness
            val sorted = words.sortedByDescending { it.forgetCount }
            val topCount = (count * 0.7).toInt().coerceAtLeast(count)
            val topWords = sorted.take(topCount).shuffled()
            val remaining = sorted.drop(topCount).shuffled().take(count - topCount)
            (topWords + remaining).shuffled()
        } else {
            words.shuffled().take(count)
        }
    }

    private fun showNextWord() {
        if (currentIndex >= quizWords.size) {
            showResults()
            return
        }

        val word = quizWords[currentIndex]
        showingMeaning = false

        // Update UI
        progressText.text = "${currentIndex + 1} / ${quizWords.size}"
        progressBar.max = quizWords.size
        progressBar.progress = currentIndex + 1

        quizWordText.text = word.word

        // Get category name
        val category = word.categoryId?.let { viewModel.getCategoryById(it) }
        quizCategoryText.text = category?.name ?: ""

        // Hide meaning and buttons
        meaningCard.visibility = View.GONE
        forgetButton.visibility = View.VISIBLE
        rememberButton.visibility = View.GONE
        nextButton.visibility = View.GONE
    }

    private fun onWordForgotten() {
        val word = quizWords[currentIndex]
        forgottenWords.add(word)

        // Increment forgetCount
        CoroutineScope(Dispatchers.IO).launch {
            val updatedWord = word.copy(forgetCount = word.forgetCount + 1)
            viewModel.updateWord(updatedWord)
        }

        showMeaning()
    }

    private fun onWordRemembered() {
        correctCount++
        showNextWord()
    }

    private fun showMeaning() {
        showingMeaning = true
        val word = quizWords[currentIndex]

        // Show meaning
        meaningCard.visibility = View.VISIBLE
        quizMeaningText.text = word.meaning

        // Update buttons
        forgetButton.visibility = View.GONE
        rememberButton.visibility = View.GONE
        nextButton.visibility = View.VISIBLE
    }

    private fun showResults() {
        val intent = Intent(this, QuizResultActivity::class.java).apply {
            putExtra(QuizResultActivity.EXTRA_TOTAL, quizWords.size)
            putExtra(QuizResultActivity.EXTRA_CORRECT, correctCount)
            putExtra(QuizResultActivity.EXTRA_FORGOTTEN_IDS, forgottenWords.map { it.id }.toLongArray())
        }
        startActivity(intent)
        finish()
    }
}
