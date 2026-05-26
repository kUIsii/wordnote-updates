package com.wordnote.app.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wordnote.app.R
import com.wordnote.app.data.QuizHistory
import com.wordnote.app.databinding.ActivityQuizHistoryBinding
import com.wordnote.app.util.compatOverridePendingTransition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizHistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var binding: ActivityQuizHistoryBinding
    private val adapter = QuizHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        binding.backButton.setOnClickListener {
            finish()
            compatOverridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = adapter

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

        viewModel.allQuizHistory.observe(this) { history ->
            if (history.isNullOrEmpty()) {
                binding.historyRecyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
                binding.recordCountText.text = ""
                updateStats(emptyList())
            } else {
                binding.historyRecyclerView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
                binding.recordCountText.text = "共 ${history.size} 条"
                adapter.submitList(history)
                updateStats(history)
            }
        }
    }

    private fun updateStats(history: List<QuizHistory>) {
        if (history.isEmpty()) {
            binding.totalTestsText.text = "0"
            binding.avgScoreText.text = "0%"
            binding.totalWordsText.text = "0"
            return
        }

        binding.totalTestsText.text = "${history.size}"

        val avgScore = if (history.isNotEmpty()) {
            history.sumOf { if (it.totalWords > 0) it.correctCount * 100 / it.totalWords else 0 } / history.size
        } else 0
        binding.avgScoreText.text = "$avgScore%"

        val totalWords = history.sumOf { it.totalWords }
        binding.totalWordsText.text = "$totalWords"
    }

    private fun showQuizDetail(record: QuizHistory) {
        val forgottenIds = record.forgottenWordIds
            .split(",")
            .filter { it.isNotBlank() }
            .map { it.trim().toLong() }
            .toLongArray()

        val correctIds = record.correctWordIds
            .split(",")
            .filter { it.isNotBlank() }
            .map { it.trim().toLong() }
            .toLongArray()

        val intent = Intent(this, QuizResultActivity::class.java).apply {
            putExtra(QuizResultActivity.EXTRA_TOTAL, record.totalWords)
            putExtra(QuizResultActivity.EXTRA_CORRECT, record.correctCount)
            putExtra(QuizResultActivity.EXTRA_FORGOTTEN_IDS, forgottenIds)
            putExtra(QuizResultActivity.EXTRA_CORRECT_IDS, correctIds)
        }
        startActivity(intent)
        compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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
