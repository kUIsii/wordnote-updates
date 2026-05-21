package com.wordnote.app.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wordnote.app.R
import com.wordnote.app.ui.adapter.DiaryAdapter
import java.util.Calendar

class DiaryActivity : AppCompatActivity() {

    private lateinit var viewModel: DiaryViewModel
    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var emptyView: LinearLayout
    private lateinit var diaryRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        viewModel = ViewModelProvider(this)[DiaryViewModel::class.java]

        initViews()
        setupRecyclerView()
        setupSearch()
        observeDiaryEntries()
    }

    private fun initViews() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        emptyView = findViewById(R.id.emptyView)
        diaryRecyclerView = findViewById(R.id.diaryRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            openDiaryDetail(System.currentTimeMillis())
        }
    }

    private fun setupRecyclerView() {
        diaryAdapter = DiaryAdapter { entry ->
            openDiaryDetail(entry.entryDate)
        }
        diaryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DiaryActivity)
            adapter = diaryAdapter
        }
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                if (query.isEmpty()) {
                    observeDiaryEntries()
                } else {
                    viewModel.searchDiaryEntries(query).observe(this@DiaryActivity) { entries ->
                        updateUI(entries)
                    }
                }
            }
        })
    }

    private fun observeDiaryEntries() {
        viewModel.allDiaryEntries.observe(this) { entries ->
            updateUI(entries)
        }
    }

    private fun updateUI(entries: List<com.wordnote.app.data.DiaryEntry>?) {
        if (entries.isNullOrEmpty()) {
            emptyView.visibility = View.VISIBLE
            diaryRecyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            diaryRecyclerView.visibility = View.VISIBLE
            diaryAdapter.submitList(entries)
        }
    }

    private fun openDiaryDetail(date: Long) {
        val intent = Intent(this, DiaryDetailActivity::class.java).apply {
            putExtra(DiaryDetailActivity.EXTRA_DATE, date)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onResume() {
        super.onResume()
        searchEditText.text.clear()
        observeDiaryEntries()
    }
}
