package com.wordnote.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wordnote.app.R
import com.wordnote.app.data.SentenceWithWords
import com.wordnote.app.databinding.ActivitySentenceDetailBinding
import com.wordnote.app.util.compatOverridePendingTransition
import com.wordnote.app.util.compatOverridePendingTransitionClose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SentenceDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SENTENCE_ID = "sentence_id"
    }

    private lateinit var viewModel: SentenceViewModel
    private lateinit var binding: ActivitySentenceDetailBinding
    private var sentenceId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySentenceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sentenceId = intent.getLongExtra(EXTRA_SENTENCE_ID, -1L)
        if (sentenceId == -1L) {
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[SentenceViewModel::class.java]

        setupToolbar()
        setupButtons()
        observeData()
    }

    private fun setupToolbar() {
        binding.backButton.setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupButtons() {
        binding.editButton.setOnClickListener {
            val intent = Intent(this, SentenceEditActivity::class.java).apply {
                putExtra(SentenceEditActivity.EXTRA_SENTENCE_ID, sentenceId)
            }
            startActivity(intent)
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("删除句子")
                .setMessage("确定要删除这个句子吗？")
                .setPositiveButton("删除") { _, _ ->
                    viewModel.allSentencesWithWords.observe(this) { sentences ->
                        val sentence = sentences.find { it.sentence.id == sentenceId }
                        if (sentence != null) {
                            viewModel.deleteSentence(sentence.sentence)
                        }
                        finish()
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun observeData() {
        viewModel.allSentencesWithWords.observe(this) { sentences ->
            val item = sentences.find { it.sentence.id == sentenceId }
            if (item != null) {
                displaySentence(item)
            }
        }
    }

    private fun displaySentence(item: SentenceWithWords) {
        val sentence = item.sentence

        binding.sentenceText.text = sentence.originalText

        // Translation
        if (!sentence.translation.isNullOrBlank()) {
            binding.translationCard.visibility = View.VISIBLE
            binding.translationText.text = sentence.translation
        } else {
            binding.translationCard.visibility = View.GONE
        }

        // Note
        if (!sentence.note.isNullOrBlank()) {
            binding.noteCard.visibility = View.VISIBLE
            binding.noteText.text = sentence.note
        } else {
            binding.noteCard.visibility = View.GONE
        }

        // Words
        if (item.words.isNotEmpty()) {
            binding.wordsSection.visibility = View.VISIBLE
            binding.wordsContainer.removeAllViews()

            item.words.sortedBy { it.sortOrder }.forEachIndexed { index, word ->
                val wordView = createWordView(word.wordText, word.meaning)
                binding.wordsContainer.addView(wordView)

                if (index < item.words.size - 1) {
                    val divider = View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1
                        ).apply {
                            marginStart = dpToPx(16)
                            marginEnd = dpToPx(16)
                        }
                        setBackgroundColor(getColor(R.color.divider))
                    }
                    binding.wordsContainer.addView(divider)
                }
            }
        } else {
            binding.wordsSection.visibility = View.GONE
        }

        // Date
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        binding.dateText.text = sdf.format(Date(sentence.createdAt))
    }

    private fun createWordView(word: String, meaning: String): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
        }

        val wordText = TextView(this).apply {
            text = word
            textSize = 15f
            setTextColor(getColor(R.color.text_primary))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        layout.addView(wordText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginEnd = dpToPx(12)
        })

        val meaningText = TextView(this).apply {
            text = meaning
            textSize = 14f
            setTextColor(getColor(R.color.text_secondary))
        }
        layout.addView(meaningText)

        return layout
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
