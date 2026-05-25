package com.wordnote.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.wordnote.app.R
import com.wordnote.app.data.Sentence
import com.wordnote.app.data.SentenceWord
import com.wordnote.app.util.compatOverridePendingTransitionClose
import kotlinx.coroutines.launch

class SentenceEditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SENTENCE_ID = "extra_sentence_id"
    }

    private lateinit var viewModel: SentenceViewModel
    private lateinit var sentenceWordsContainer: LinearLayout
    private var editingSentenceId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sentence_edit)

        viewModel = ViewModelProvider(this)[SentenceViewModel::class.java]

        editingSentenceId = intent.getLongExtra(EXTRA_SENTENCE_ID, -1L)

        setupToolbar()
        setupViews()

        if (editingSentenceId != -1L) {
            loadSentence()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = if (editingSentenceId == -1L) "添加句子" else "编辑句子"
        toolbar.setNavigationOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupViews() {
        sentenceWordsContainer = findViewById(R.id.sentenceWordsContainer)

        findViewById<View>(R.id.addWordButton).setOnClickListener {
            addSentenceWordRow()
        }

        findViewById<View>(R.id.saveButton).setOnClickListener {
            saveSentence()
        }
    }

    private fun loadSentence() {
        lifecycleScope.launch {
            val item = viewModel.getSentenceWithWords(editingSentenceId) ?: return@launch
            val sentence = item.sentence

            findViewById<TextInputEditText>(R.id.originalTextEditText).setText(sentence.originalText)
            findViewById<TextInputEditText>(R.id.translationEditText).setText(sentence.translation ?: "")
            findViewById<TextInputEditText>(R.id.noteEditText).setText(sentence.note ?: "")

            sentenceWordsContainer.removeAllViews()
            item.words.sortedBy { it.sortOrder }.forEach { sw ->
                addSentenceWordRow(sw.wordText, sw.meaning)
            }
        }
    }

    private fun addSentenceWordRow(wordText: String = "", meaning: String = "") {
        val row = LayoutInflater.from(this).inflate(R.layout.item_sentence_word, sentenceWordsContainer, false)

        val wordEditText = row.findViewById<TextInputEditText>(R.id.wordEditText)
        val meaningEditText = row.findViewById<TextInputEditText>(R.id.meaningEditText)
        val removeButton = row.findViewById<ImageView>(R.id.removeButton)

        wordEditText.setText(wordText)
        meaningEditText.setText(meaning)

        removeButton.setOnClickListener {
            sentenceWordsContainer.removeView(row)
        }

        sentenceWordsContainer.addView(row)
    }

    private fun saveSentence() {
        val originalText = findViewById<TextInputEditText>(R.id.originalTextEditText).text.toString().trim()
        val translation = findViewById<TextInputEditText>(R.id.translationEditText).text.toString().trim()
        val note = findViewById<TextInputEditText>(R.id.noteEditText).text.toString().trim()

        if (originalText.isEmpty()) {
            findViewById<TextInputEditText>(R.id.originalTextEditText).error = "请输入句子"
            return
        }

        val words = mutableListOf<SentenceWord>()
        for (i in 0 until sentenceWordsContainer.childCount) {
            val row = sentenceWordsContainer.getChildAt(i)
            val wordText = row.findViewById<TextInputEditText>(R.id.wordEditText).text.toString().trim()
            val wordMeaning = row.findViewById<TextInputEditText>(R.id.meaningEditText).text.toString().trim()
            if (wordText.isNotEmpty() && wordMeaning.isNotEmpty()) {
                words.add(SentenceWord(wordText = wordText, meaning = wordMeaning, sortOrder = i))
            }
        }

        if (editingSentenceId != -1L) {
            val sentence = Sentence(
                id = editingSentenceId,
                originalText = originalText,
                translation = translation.ifBlank { null },
                note = note.ifBlank { null }
            )
            viewModel.updateSentence(sentence, words)
            Toast.makeText(this, "已更新", Toast.LENGTH_SHORT).show()
        } else {
            val sentence = Sentence(
                originalText = originalText,
                translation = translation.ifBlank { null },
                note = note.ifBlank { null }
            )
            viewModel.insertSentence(sentence, words)
            Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}
