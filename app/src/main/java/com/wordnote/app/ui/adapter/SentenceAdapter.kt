package com.wordnote.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wordnote.app.R
import com.wordnote.app.data.SentenceWithWords
import com.wordnote.app.databinding.ItemDateHeaderBinding
import com.wordnote.app.databinding.ItemSentenceBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SentenceAdapter(
    private val onItemClick: (SentenceWithWords) -> Unit,
    private val onDeleteClick: (SentenceWithWords) -> Unit
) : ListAdapter<SentenceAdapter.ListItem, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_SENTENCE = 1
    }

    sealed class ListItem {
        data class DateHeader(val dateStr: String) : ListItem()
        data class SentenceItem(val sentence: SentenceWithWords) : ListItem()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.DateHeader -> TYPE_DATE_HEADER
            is ListItem.SentenceItem -> TYPE_SENTENCE
        }
    }

    override fun submitList(list: List<ListItem>?) {
        super.submitList(list)
    }

    fun submitSentences(sentences: List<SentenceWithWords>) {
        val items = mutableListOf<ListItem>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var lastDate = ""

        sentences.forEach { sentence ->
            val date = sdf.format(Date(sentence.sentence.createdAt))
            if (date != lastDate) {
                lastDate = date
                val cal = Calendar.getInstance().apply { timeInMillis = sentence.sentence.createdAt }
                val now = Calendar.getInstance()
                val isToday = cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                val isYesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.let {
                    cal.get(Calendar.YEAR) == it.get(Calendar.YEAR) &&
                        cal.get(Calendar.DAY_OF_YEAR) == it.get(Calendar.DAY_OF_YEAR)
                }
                val displayDate = when {
                    isToday -> "今天"
                    isYesterday -> "昨天"
                    else -> SimpleDateFormat("M月d日", Locale.getDefault()).format(Date(sentence.sentence.createdAt))
                }
                items.add(ListItem.DateHeader(displayDate))
            }
            items.add(ListItem.SentenceItem(sentence))
        }
        submitList(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val binding = ItemDateHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DateHeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemSentenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SentenceViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ListItem.DateHeader -> (holder as DateHeaderViewHolder).bind(item)
            is ListItem.SentenceItem -> (holder as SentenceViewHolder).bind(item.sentence)
        }
    }

    class DateHeaderViewHolder(private val binding: ItemDateHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateText: TextView = binding.dateTextView

        fun bind(item: ListItem.DateHeader) {
            dateText.text = item.dateStr
        }
    }

    inner class SentenceViewHolder(private val binding: ItemSentenceBinding) : RecyclerView.ViewHolder(binding.root) {
        private val sentenceTextView: TextView = binding.sentenceTextView
        private val translationTextView: TextView = binding.translationTextView
        private val wordCountTextView: TextView = binding.wordCountTextView
        private val deleteButton: ImageView = binding.deleteButton

        fun bind(item: SentenceWithWords) {
            sentenceTextView.text = item.sentence.originalText

            if (!item.sentence.translation.isNullOrBlank()) {
                translationTextView.text = item.sentence.translation
                translationTextView.visibility = View.VISIBLE
            } else {
                translationTextView.visibility = View.GONE
            }

            if (item.words.isNotEmpty()) {
                wordCountTextView.text = "${item.words.size}个生词"
                wordCountTextView.visibility = View.VISIBLE
            } else {
                wordCountTextView.visibility = View.GONE
            }

            itemView.setOnClickListener { onItemClick(item) }
            deleteButton.setOnClickListener { onDeleteClick(item) }
        }
    }

    class ListItemDiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return when {
                oldItem is ListItem.DateHeader && newItem is ListItem.DateHeader -> oldItem.dateStr == newItem.dateStr
                oldItem is ListItem.SentenceItem && newItem is ListItem.SentenceItem -> oldItem.sentence.sentence.id == newItem.sentence.sentence.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }
}
