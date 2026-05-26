package com.wordnote.app.ui.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import com.wordnote.app.databinding.ItemCategoryBinding

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
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private val categoryNameTextView: TextView = binding.categoryNameTextView
        private val wordCountTextView: TextView = binding.wordCountTextView
        private val deleteButton: ImageView = binding.deleteButton
        private val categoryIndicator: View = binding.categoryIndicator

        fun bind(category: Category) {
            categoryNameTextView.text = category.name

            val count = wordCounts[category.id] ?: 0
            wordCountTextView.text = "${count}个单词"

            // Set indicator color as rounded rectangle
            val color = try {
                Color.parseColor(category.color)
            } catch (e: Exception) {
                Color.parseColor("#8E24AA")
            }
            val density = itemView.resources.displayMetrics.density
            categoryIndicator.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f * density
                setColor(color)
            }

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
