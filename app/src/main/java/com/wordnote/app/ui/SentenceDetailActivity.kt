package com.wordnote.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wordnote.app.R
import com.wordnote.app.data.SentenceWithWords
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
    private var sentenceId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sentence_detail)

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
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupButtons() {
        findViewById<ImageView>(R.id.editButton).setOnClickListener {
            val intent = Intent(this, SentenceEditActivity::class.java).apply {
                putExtra(SentenceEditActivity.EXTRA_SENTENCE_ID, sentenceId)
            }
            startActivity(intent)
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        findViewById<ImageView>(R.id.deleteButton).setOnClickListener {
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

        findViewById<TextView>(R.id.sentenceText).text = sentence.originalText

        // Translation
        val translationCard = findViewById<View>(R.id.translationCard)
        if (!sentence.translation.isNullOrBlank()) {
            translationCard.visibility = View.VISIBLE
            findViewById<TextView>(R.id.translationText).text = sentence.translation
        } else {
            translationCard.visibility = View.GONE
        }

        // Note
        val noteCard = findViewById<View>(R.id.noteCard)
        if (!sentence.note.isNullOrBlank()) {
            noteCard.visibility = View.VISIBLE
            findViewById<TextView>(R.id.noteText).text = sentence.note
        } else {
            noteCard.visibility = View.GONE
        }

        // Words
        val wordsSection = findViewById<View>(R.id.wordsSection)
        val wordsContainer = findViewById<LinearLayout>(R.id.wordsContainer)
        if (item.words.isNotEmpty()) {
            wordsSection.visibility = View.VISIBLE
            wordsContainer.removeAllViews()

            item.words.sortedBy { it.sortOrder }.forEachIndexed { index, word ->
                val wordView = createWordView(word.wordText, word.meaning)
                wordsContainer.addView(wordView)

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
                    wordsContainer.addView(divider)
                }
            }
        } else {
            wordsSection.visibility = View.GONE
        }

        // Date
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        findViewById<TextView>(R.id.dateText).text = sdf.format(Date(sentence.createdAt))
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
