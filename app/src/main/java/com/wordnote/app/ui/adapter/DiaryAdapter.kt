package com.wordnote.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wordnote.app.R
import com.wordnote.app.data.DiaryEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DiaryAdapter(
    private val onClick: (DiaryEntry) -> Unit
) : ListAdapter<DiaryEntry, DiaryAdapter.DiaryViewHolder>(DiaryDiffCallback()) {

    private val dateFormat = SimpleDateFormat("yyyy年M月d日", Locale.CHINA)
    private val moodMap = mapOf(
        0 to "",
        1 to "😊",
        2 to "😐",
        3 to "😔",
        4 to "😴",
        5 to "🎉"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary_entry, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val moodTextView: TextView = itemView.findViewById(R.id.moodTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        private val wordCountTextView: TextView = itemView.findViewById(R.id.wordCountTextView)
        private val todoCountTextView: TextView = itemView.findViewById(R.id.todoCountTextView)

        fun bind(entry: DiaryEntry) {
            dateTextView.text = dateFormat.format(Date(entry.entryDate))
            moodTextView.text = moodMap[entry.mood] ?: ""

            val preview = entry.content.take(100)
            contentTextView.text = preview.ifBlank { "暂无内容" }
            contentTextView.alpha = if (preview.isBlank()) 0.5f else 1.0f

            itemView.setOnClickListener { onClick(entry) }
        }
    }

    class DiaryDiffCallback : DiffUtil.ItemCallback<DiaryEntry>() {
        override fun areItemsTheSame(oldItem: DiaryEntry, newItem: DiaryEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DiaryEntry, newItem: DiaryEntry): Boolean {
            return oldItem == newItem
        }
    }
}
