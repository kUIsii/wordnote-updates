package com.wordnote.app.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.wordnote.app.R
import com.wordnote.app.data.QuizMode
import com.wordnote.app.data.Word
import com.wordnote.app.databinding.ActivityQuizBinding
import com.wordnote.app.util.compatOverridePendingTransition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var binding: ActivityQuizBinding

    private var quizWords = emptyList<Word>()
    private var currentIndex = 0
    private var correctCount = 0
    private val forgottenWords = mutableListOf<Word>()

    private var wordCount = 35
    private var categoryIds: List<Long>? = null
    private var useForgetCount = false
    private var quizMode: QuizMode = QuizMode.EN_TO_CN

    private var hasRemembered = false
    private var isShowingMeaning = false

    companion object {
        private const val EXTRA_WORD_COUNT = "word_count"
        private const val EXTRA_CATEGORY_IDS = "category_ids"
        private const val EXTRA_USE_FORGET_COUNT = "use_forget_count"
        private const val EXTRA_QUIZ_MODE = "quiz_mode"

        fun launch(context: Context, wordCount: Int, categoryIds: List<Long>?, useForgetCount: Boolean, quizMode: QuizMode = QuizMode.EN_TO_CN) {
            val intent = Intent(context, QuizActivity::class.java).apply {
                putExtra(EXTRA_WORD_COUNT, wordCount)
                putExtra(EXTRA_CATEGORY_IDS, categoryIds?.toLongArray())
                putExtra(EXTRA_USE_FORGET_COUNT, useForgetCount)
                putExtra(EXTRA_QUIZ_MODE, quizMode.name)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        wordCount = intent.getIntExtra(EXTRA_WORD_COUNT, 35)
        categoryIds = intent.getLongArrayExtra(EXTRA_CATEGORY_IDS)?.toList()
        useForgetCount = intent.getBooleanExtra(EXTRA_USE_FORGET_COUNT, false)
        quizMode = intent.getStringExtra(EXTRA_QUIZ_MODE)?.let {
            try { QuizMode.valueOf(it) } catch (e: Exception) { QuizMode.EN_TO_CN }
        } ?: QuizMode.EN_TO_CN

        initViews()
        viewModel.allCategories.observe(this) { }
        loadWords()
    }

    private fun initViews() {
        binding.backButton.setOnClickListener {
            finish()
            compatOverridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        binding.forgetButton.setOnClickListener { onWordForgotten() }
        binding.rememberButton.setOnClickListener { onWordRememberedOrUnremember() }
        binding.nextButton.setOnClickListener { advanceToNext() }

        binding.spellingInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkSpelling()
                true
            } else {
                false
            }
        }

        binding.checkSpellingButton.setOnClickListener {
            checkSpelling()
        }
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
            finishQuiz()
            return
        }

        isShowingMeaning = false
        hasRemembered = false
        val word = quizWords[currentIndex]

        binding.progressText.text = "${currentIndex + 1} / ${quizWords.size}"
        binding.progressBar.max = quizWords.size
        binding.progressBar.progress = currentIndex + 1

        // Determine effective mode for MIXED
        val effectiveMode = if (quizMode == QuizMode.MIXED) {
            QuizMode.entries.toTypedArray().filter { it != QuizMode.MIXED }.random()
        } else {
            quizMode
        }

        if (effectiveMode == QuizMode.SPELLING) {
            showSpellingTest()
            return
        }

        binding.quizWordText.text = if (effectiveMode == QuizMode.CN_TO_EN) word.meaning else word.word
        binding.quizWordText.setTextColor(getColor(R.color.text_primary))
        val category = word.categoryId?.let { viewModel.getCategoryById(it) }
        binding.quizCategoryText.text = category?.name ?: ""

        binding.meaningCard.visibility = View.GONE
        binding.spellingInputContainer.visibility = View.GONE

        // Both buttons visible, remember button in default state
        binding.forgetButton.visibility = View.VISIBLE
        binding.forgetButton.text = "不记得"
        binding.rememberButton.visibility = View.VISIBLE
        binding.rememberButton.text = "记得"
        binding.nextButton.visibility = View.GONE

        binding.wordDisplayContainer.let {
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

        // If previously remembered, undo the correctCount
        if (hasRemembered) {
            correctCount--
        }
        forgottenWords.add(word)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val updatedWord = word.copy(forgetCount = word.forgetCount + 1)
                viewModel.updateWord(updatedWord)
            }
        }

        // Show word in red + meaning
        binding.quizWordText.setTextColor(getColor(R.color.cat_hard))
        showMeaningWithHighlight(word)

        binding.forgetButton.visibility = View.GONE
        binding.rememberButton.visibility = View.GONE
        binding.nextButton.visibility = View.VISIBLE
    }

    private fun onWordRememberedOrUnremember() {
        if (quizWords.isEmpty() || currentIndex < 0 || currentIndex >= quizWords.size) return

        if (hasRemembered) {
            // Undo: change back to forgotten
            onWordForgotten()
        } else {
            // Mark as remembered
            hasRemembered = true
            correctCount++

            val word = quizWords[currentIndex]
            showMeaningWithHighlight(word)

            // Change button to "不记得" (undo)
            binding.rememberButton.text = "不记得"
            binding.rememberButton.visibility = View.VISIBLE
            binding.forgetButton.visibility = View.GONE
            binding.nextButton.visibility = View.VISIBLE
        }
    }

    private fun showMeaningWithHighlight(word: Word) {
        binding.meaningCard.visibility = View.VISIBLE

        // Check if meaning has highlighted parts
        lifecycleScope.launch {
            val meanings = withContext(Dispatchers.IO) {
                viewModel.getMeaningsForWordSync(word.id)
            }
            val highlightedTexts = meanings.filter { it.isHighlighted }.map { it.meaningText }

            if (highlightedTexts.isNotEmpty()) {
                val spannable = SpannableString(word.meaning)
                highlightedTexts.forEach { hm ->
                    val idx = word.meaning.indexOf(hm)
                    if (idx >= 0) {
                        val highlightColor = getColor(R.color.primary)
                        spannable.setSpan(
                            ForegroundColorSpan(highlightColor),
                            idx, idx + hm.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            idx, idx + hm.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
                binding.quizMeaningText.text = spannable
            } else {
                binding.quizMeaningText.text = word.meaning
            }
        }
    }

    private fun showSpellingTest() {
        val word = quizWords[currentIndex]

        // Display meaning, ask user to type the word
        binding.quizWordText.text = word.meaning
        binding.quizWordText.setTextColor(getColor(R.color.text_primary))
        binding.quizCategoryText.text = "请拼写单词"

        // Hide remember/forget buttons
        binding.forgetButton.visibility = View.GONE
        binding.rememberButton.visibility = View.GONE
        binding.nextButton.visibility = View.GONE
        binding.meaningCard.visibility = View.GONE

        // Show spelling input
        binding.spellingInputContainer.visibility = View.VISIBLE
        binding.spellingInput.text?.clear()
        binding.spellingInput.requestFocus()

        // Show keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.spellingInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun checkSpelling() {
        val word = quizWords[currentIndex]
        val userInput = binding.spellingInput.text.toString().trim()

        if (userInput.isEmpty()) return

        if (userInput.equals(word.word, ignoreCase = true)) {
            onWordRememberedOrUnremember()
            showSpellingResult(true, word.word)
        } else {
            onWordForgotten()
            showSpellingResult(false, word.word)
        }
    }

    private fun showSpellingResult(isCorrect: Boolean, correctWord: String) {
        binding.spellingInputContainer.visibility = View.GONE

        // Hide keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.spellingInput.windowToken, 0)

        if (isCorrect) {
            binding.quizWordText.setTextColor(getColor(R.color.cat_easy))
            Toast.makeText(this, "正确！", Toast.LENGTH_SHORT).show()
        } else {
            binding.quizWordText.setTextColor(getColor(R.color.cat_hard))
            binding.quizWordText.text = correctWord
            Toast.makeText(this, "正确答案: $correctWord", Toast.LENGTH_LONG).show()
        }

        // Show next button
        binding.nextButton.visibility = View.VISIBLE
    }

    private fun advanceToNext() {
        currentIndex++
        showWord()
    }

    private var isFinishingQuiz = false

    private fun finishQuiz() {
        if (isFinishingQuiz) return
        isFinishingQuiz = true

        val totalWords = quizWords.size
        val correctCountVal = correctCount
        val forgottenIds = forgottenWords.map { it.id }.toLongArray()
        val forgottenIdSet = forgottenIds.toSet()
        val correctIds = quizWords.filter { it.id !in forgottenIdSet }.map { it.id }.toLongArray()

        Log.d("QuizActivity", "Finishing quiz: total=$totalWords, correct=$correctCountVal, forgotten=${forgottenWords.size}")

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val history = com.wordnote.app.data.QuizHistory(
                        totalWords = totalWords,
                        correctCount = correctCountVal,
                        categoryIds = categoryIds?.joinToString(",") ?: "",
                        forgottenWordIds = forgottenWords.joinToString(",") { it.id.toString() },
                        forgottenWordTexts = forgottenWords.joinToString("||") { "${it.word}=${it.meaning}" },
                        correctWordIds = correctIds.joinToString(",")
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
                    putExtra(QuizResultActivity.EXTRA_CORRECT_IDS, correctIds)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
