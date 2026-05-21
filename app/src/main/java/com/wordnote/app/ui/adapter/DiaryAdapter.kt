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
    private val onClick: (DiaryEntry) -> Unit,
    private val onLongClick: (DiaryEntry) -> Unit
) : ListAdapter<DiaryEntry, DiaryAdapter.DiaryViewHolder>(DiaryDiffCallback()) {

    private val dayFormat = SimpleDateFormat("d", Locale.CHINA)
    private val weekdayFormat = SimpleDateFormat("EEEE", Locale.CHINA)
    private val monthYearFormat = SimpleDateFormat("yyyy年M月", Locale.CHINA)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary_entry, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        private val weekdayTextView: TextView = itemView.findViewById(R.id.weekdayTextView)
        private val monthYearTextView: TextView = itemView.findViewById(R.id.monthYearTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        private val wordCountTextView: TextView = itemView.findViewById(R.id.wordCountTextView)

        fun bind(entry: DiaryEntry) {
            val date = Date(entry.entryDate)
            dayTextView.text = dayFormat.format(date)
            weekdayTextView.text = weekdayFormat.format(date)
            monthYearTextView.text = monthYearFormat.format(date)

            val preview = entry.content.take(150)
            contentTextView.text = preview.ifBlank { "暂无内容" }
            contentTextView.alpha = if (preview.isBlank()) 0.5f else 1.0f

            val wordCount = entry.content.length
            wordCountTextView.text = "$wordCount 字"

            itemView.setOnClickListener { onClick(entry) }
            itemView.setOnLongClickListener {
                onLongClick(entry)
                true
            }
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
