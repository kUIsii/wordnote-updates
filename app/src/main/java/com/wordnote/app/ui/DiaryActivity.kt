package com.wordnote.app.ui

import android.content.Intent
import android.os.Bundle
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
    private lateinit var monthTextView: TextView

    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        viewModel = ViewModelProvider(this)[DiaryViewModel::class.java]

        initViews()
        setupRecyclerView()
        setupMonthSelector()
        observeDiaryEntries()
    }

    private fun initViews() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        emptyView = findViewById(R.id.emptyView)
        diaryRecyclerView = findViewById(R.id.diaryRecyclerView)
        monthTextView = findViewById(R.id.monthTextView)

        findViewById<ImageView>(R.id.searchButton).setOnClickListener {
            showSearchDialog()
        }

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            openDiaryDetail(System.currentTimeMillis())
        }
    }

    private fun setupRecyclerView() {
        diaryAdapter = DiaryAdapter(
            onClick = { entry ->
                openDiaryDetail(entry.entryDate)
            },
            onLongClick = { entry ->
                showDeleteDialog(entry)
            }
        )
        diaryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DiaryActivity)
            adapter = diaryAdapter
        }
    }

    private fun setupMonthSelector() {
        updateMonthDisplay()

        findViewById<ImageView>(R.id.prevMonthButton).setOnClickListener {
            currentMonth--
            if (currentMonth < 0) {
                currentMonth = 11
                currentYear--
            }
            updateMonthDisplay()
            observeDiaryEntries()
        }

        findViewById<ImageView>(R.id.nextMonthButton).setOnClickListener {
            currentMonth++
            if (currentMonth > 11) {
                currentMonth = 0
                currentYear++
            }
            updateMonthDisplay()
            observeDiaryEntries()
        }
    }

    private fun updateMonthDisplay() {
        monthTextView.text = "${currentYear}年${currentMonth + 1}月"
    }

    private fun observeDiaryEntries() {
        val start = viewModel.getStartOfMonth(currentYear, currentMonth)
        val end = viewModel.getEndOfMonth(currentYear, currentMonth)

        viewModel.getDiaryEntriesBetween(start, end).observe(this) { entries ->
            if (entries.isNullOrEmpty()) {
                emptyView.visibility = View.VISIBLE
                diaryRecyclerView.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                diaryRecyclerView.visibility = View.VISIBLE
                diaryAdapter.submitList(entries)
            }
        }
    }

    private fun openDiaryDetail(date: Long) {
        val intent = Intent(this, DiaryDetailActivity::class.java).apply {
            putExtra(DiaryDetailActivity.EXTRA_DATE, date)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun showDeleteDialog(entry: com.wordnote.app.data.DiaryEntry) {
        val dateStr = java.text.SimpleDateFormat("M月d日", java.util.Locale.CHINA).format(java.util.Date(entry.entryDate))
        MaterialAlertDialogBuilder(this)
            .setTitle("删除日记")
            .setMessage("确定要删除 ${dateStr} 的日记吗？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteDiaryEntry(entry)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showSearchDialog() {
        val input = EditText(this).apply {
            hint = "搜索日记内容..."
            setPadding(48, 32, 48, 32)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("搜索日记")
            .setView(input)
            .setPositiveButton("搜索") { _, _ ->
                val query = input.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchDiary(query)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun searchDiary(query: String) {
        viewModel.searchDiaryEntries(query).observe(this) { entries ->
            if (entries.isNullOrEmpty()) {
                emptyView.visibility = View.VISIBLE
                diaryRecyclerView.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                diaryRecyclerView.visibility = View.VISIBLE
                diaryAdapter.submitList(entries)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        observeDiaryEntries()
    }
}
