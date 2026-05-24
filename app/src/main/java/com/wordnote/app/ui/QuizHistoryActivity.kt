package com.wordnote.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wordnote.app.R
import com.wordnote.app.data.QuizHistory
import com.wordnote.app.util.compatOverridePendingTransition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizHistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var recordCountText: TextView
    private lateinit var totalTestsText: TextView
    private lateinit var avgScoreText: TextView
    private lateinit var totalWordsText: TextView
    private val adapter = QuizHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_history)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            compatOverridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        recyclerView = findViewById(R.id.historyRecyclerView)
        emptyView = findViewById(R.id.emptyView)
        recordCountText = findViewById(R.id.recordCountText)
        totalTestsText = findViewById(R.id.totalTestsText)
        avgScoreText = findViewById(R.id.avgScoreText)
        totalWordsText = findViewById(R.id.totalWordsText)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.onItemClick = { record -> showQuizDetail(record) }
        adapter.onItemLongClick = { record ->
            MaterialAlertDialogBuilder(this, R.style.Theme_WordNoteApp_Dialog)
                .setTitle("删除记录")
                .setMessage("确定要删除这次测验记录吗？")
                .setPositiveButton("删除") { _, _ ->
                    viewModel.deleteQuizHistory(record)
                    Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消", null)
                .show()
            true
        }

        try {
            val historyLiveData = viewModel.allQuizHistory
            if (historyLiveData != null) {
                historyLiveData.observe(this) { history ->
                    try {
                        if (history.isNullOrEmpty()) {
                            recyclerView.visibility = View.GONE
                            emptyView.visibility = View.VISIBLE
                            recordCountText.text = ""
                            updateStats(emptyList())
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            emptyView.visibility = View.GONE
                            recordCountText.text = "共 ${history.size} 条"
                            adapter.submitList(history)
                            updateStats(history)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        recyclerView.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                    }
                }
            } else {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
    }

    private fun updateStats(history: List<QuizHistory>) {
        if (history.isEmpty()) {
            totalTestsText.text = "0"
            avgScoreText.text = "0%"
            totalWordsText.text = "0"
            return
        }

        totalTestsText.text = "${history.size}"

        val avgScore = if (history.isNotEmpty()) {
            history.sumOf { if (it.totalWords > 0) it.correctCount * 100 / it.totalWords else 0 } / history.size
        } else 0
        avgScoreText.text = "$avgScore%"

        val totalWords = history.sumOf { it.totalWords }
        totalWordsText.text = "$totalWords"
    }

    private fun showQuizDetail(record: QuizHistory) {
        val percentage = if (record.totalWords > 0) (record.correctCount * 100 / record.totalWords) else 0
        val forgottenCount = record.totalWords - record.correctCount
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(Date(record.createdAt))

        val detail = StringBuilder()
        detail.appendLine("时间: $dateStr")
        detail.appendLine("总计: ${record.totalWords} 个单词")
        detail.appendLine("正确: ${record.correctCount} 个 ($percentage%)")
        detail.appendLine("不熟悉: $forgottenCount 个")

        if (record.forgottenWordTexts.isNotBlank()) {
            detail.appendLine()
            detail.appendLine("不熟悉的单词:")
            record.forgottenWordTexts.split("||").forEach { item ->
                val parts = item.split("=", limit = 2)
                if (parts.size == 2) {
                    detail.appendLine("  ${parts[0]} - ${parts[1]}")
                }
            }
        }

        MaterialAlertDialogBuilder(this, R.style.Theme_WordNoteApp_Dialog)
            .setTitle("测验详情")
            .setMessage(detail.toString())
            .setPositiveButton("确定", null)
            .show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    inner class QuizHistoryAdapter : ListAdapter<QuizHistory, QuizHistoryAdapter.ViewHolder>(DiffCallback()) {

        var onItemClick: ((QuizHistory) -> Unit)? = null
        var onItemLongClick: ((QuizHistory) -> Boolean)? = null

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val dateText: TextView = itemView.findViewById(R.id.dateText)
            private val scoreText: TextView = itemView.findViewById(R.id.scoreText)
            private val detailText: TextView = itemView.findViewById(R.id.detailText)
            private val wordDetailText: TextView = itemView.findViewById(R.id.wordDetailText)

            fun bind(record: QuizHistory) {
                val percentage = if (record.totalWords > 0) (record.correctCount * 100 / record.totalWords) else 0
                val dateStr = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
                    .format(Date(record.createdAt))

                dateText.text = dateStr
                scoreText.text = "$percentage%"
                detailText.text = "${record.correctCount}/${record.totalWords} 正确"

                val scoreColor = when {
                    percentage >= 80 -> getColor(R.color.primary)
                    percentage >= 50 -> Color.parseColor("#FB8C00")
                    else -> getColor(R.color.cat_hard)
                }
                scoreText.setTextColor(scoreColor)

                if (record.forgottenWordTexts.isNotBlank()) {
                    val words = record.forgottenWordTexts.split("||").take(3).mapNotNull { item ->
                        val parts = item.split("=", limit = 2)
                        if (parts.size == 2) parts[0] else null
                    }
                    if (words.isNotEmpty()) {
                        wordDetailText.text = "不熟悉: ${words.joinToString(", ")}${if (record.forgottenWordTexts.split("||").size > 3) "..." else ""}"
                        wordDetailText.visibility = View.VISIBLE
                    } else {
                        wordDetailText.visibility = View.GONE
                    }
                } else {
                    wordDetailText.visibility = View.GONE
                }

                itemView.setOnClickListener { onItemClick?.invoke(record) }
                itemView.setOnLongClickListener { onItemLongClick?.invoke(record) == true }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quiz_history_record, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<QuizHistory>() {
        override fun areItemsTheSame(oldItem: QuizHistory, newItem: QuizHistory) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: QuizHistory, newItem: QuizHistory) = oldItem == newItem
    }
}
