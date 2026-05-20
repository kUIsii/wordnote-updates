package com.wordnote.app.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wordnote.app.R
import com.wordnote.app.data.Category

class CategoryAdapter(
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var wordCounts: Map<Long, Int> = emptyMap()

    fun setWordCounts(wordCounts: Map<Long, Int>) {
        this.wordCounts = wordCounts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
        private val wordCountTextView: TextView = itemView.findViewById(R.id.wordCountTextView)
        private val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        private val categoryIndicator: View = itemView.findViewById(R.id.categoryIndicator)

        fun bind(category: Category) {
            categoryNameTextView.text = category.name

            val count = wordCounts[category.id] ?: 0
            wordCountTextView.text = "${count}个单词"

            // Set indicator color from category.color
            val color = try {
                Color.parseColor(category.color)
            } catch (e: Exception) {
                Color.parseColor("#8E24AA")
            }
            categoryIndicator.setBackgroundColor(color)

            // Hide delete button for default categories
            if (category.isDefault) {
                deleteButton.visibility = View.GONE
            } else {
                deleteButton.visibility = View.VISIBLE
                deleteButton.setOnClickListener { onDeleteClick(category) }
            }

            itemView.setOnClickListener { onEditClick(category) }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
