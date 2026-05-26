package com.wordnote.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.wordnote.app.R
import com.wordnote.app.data.Sentence
import com.wordnote.app.data.SentenceWord
import com.wordnote.app.databinding.ActivitySentenceEditBinding
import com.wordnote.app.util.compatOverridePendingTransitionClose
import kotlinx.coroutines.launch

class SentenceEditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SENTENCE_ID = "extra_sentence_id"
    }

    private lateinit var viewModel: SentenceViewModel
    private lateinit var binding: ActivitySentenceEditBinding
    private var editingSentenceId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySentenceEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SentenceViewModel::class.java]

        editingSentenceId = intent.getLongExtra(EXTRA_SENTENCE_ID, -1L)

        setupToolbar()
        setupViews()

        if (editingSentenceId != -1L) {
            loadSentence()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = if (editingSentenceId == -1L) "添加句子" else "编辑句子"
        binding.toolbar.setNavigationOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupViews() {
        binding.addWordButton.setOnClickListener {
            addSentenceWordRow()
        }

        binding.saveButton.setOnClickListener {
            saveSentence()
        }
    }

    private fun loadSentence() {
        lifecycleScope.launch {
            val item = viewModel.getSentenceWithWords(editingSentenceId) ?: return@launch
            val sentence = item.sentence

            binding.originalTextEditText.setText(sentence.originalText)
            binding.translationEditText.setText(sentence.translation ?: "")
            binding.noteEditText.setText(sentence.note ?: "")

            binding.sentenceWordsContainer.removeAllViews()
            item.words.sortedBy { it.sortOrder }.forEach { sw ->
                addSentenceWordRow(sw.wordText, sw.meaning)
            }
        }
    }

    private fun addSentenceWordRow(wordText: String = "", meaning: String = "") {
        val row = LayoutInflater.from(this).inflate(R.layout.item_sentence_word, binding.sentenceWordsContainer, false)

        val wordEditText = row.findViewById<TextInputEditText>(R.id.wordEditText)
        val meaningEditText = row.findViewById<TextInputEditText>(R.id.meaningEditText)
        val removeButton = row.findViewById<ImageView>(R.id.removeButton)

        wordEditText.setText(wordText)
        meaningEditText.setText(meaning)

        removeButton.setOnClickListener {
            binding.sentenceWordsContainer.removeView(row)
        }

        binding.sentenceWordsContainer.addView(row)
    }

    private fun saveSentence() {
        val originalText = binding.originalTextEditText.text.toString().trim()
        val translation = binding.translationEditText.text.toString().trim()
        val note = binding.noteEditText.text.toString().trim()

        if (originalText.isEmpty()) {
            binding.originalTextEditText.error = "请输入句子"
            return
        }

        val words = mutableListOf<SentenceWord>()
        for (i in 0 until binding.sentenceWordsContainer.childCount) {
            val row = binding.sentenceWordsContainer.getChildAt(i)
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
