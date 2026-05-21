package com.wordnote.app.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.wordnote.app.R
import com.wordnote.app.data.DiaryEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATE = "extra_date"
    }

    private lateinit var viewModel: DiaryViewModel
    private lateinit var dateTextView: TextView
    private lateinit var contentEditText: EditText

    private var entryId: Long = -1
    private var entryDate: Long = 0
    private var currentEntry: DiaryEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_detail)

        viewModel = ViewModelProvider(this)[DiaryViewModel::class.java]

        entryDate = intent.getLongExtra(EXTRA_DATE, System.currentTimeMillis())

        initViews()
        setupListeners()
        loadEntry()
    }

    private fun initViews() {
        dateTextView = findViewById(R.id.dateTextView)
        contentEditText = findViewById(R.id.contentEditText)

        val dateFormat = SimpleDateFormat("M月d日 EEEE", Locale.CHINA)
        dateTextView.text = dateFormat.format(Date(entryDate))

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            saveAndFinish()
        }
    }

    private fun setupListeners() {
        findViewById<TextView>(R.id.saveButton).setOnClickListener {
            saveAndFinish()
        }
    }

    private fun loadEntry() {
        val startOfDay = viewModel.getStartOfDay(entryDate)
        viewModel.getDiaryEntryByDate(startOfDay).observe(this) { entry ->
            if (entry != null && currentEntry == null) {
                currentEntry = entry
                entryId = entry.id
                contentEditText.setText(entry.content)
                contentEditText.setSelection(entry.content.length)
            }
        }
    }

    private fun saveAndFinish() {
        val content = contentEditText.text.toString().trim()
        val now = System.currentTimeMillis()

        if (content.isEmpty() && currentEntry == null) {
            finish()
            return
        }

        if (currentEntry != null) {
            if (content.isEmpty()) {
                viewModel.deleteDiaryEntry(currentEntry!!)
            } else {
                val updated = currentEntry!!.copy(
                    content = content,
                    updatedAt = now
                )
                viewModel.updateDiaryEntry(updated)
            }
        } else {
            if (content.isNotEmpty()) {
                val entry = DiaryEntry(
                    entryDate = entryDate,
                    content = content,
                    createdAt = now,
                    updatedAt = now
                )
                viewModel.insertDiaryEntry(entry) { }
            }
        }

        finish()
    }

    override fun onBackPressed() {
        saveAndFinish()
    }
}
