package com.wordnote.app.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wordnote.app.R
import com.wordnote.app.data.WordGroup

class GroupManagementAdapter(
    private val groups: MutableList<WordGroup>,
    private val wordCounts: MutableMap<Long, Int>,
    private val onDelete: (WordGroup) -> Unit
) : RecyclerView.Adapter<GroupManagementAdapter.GroupViewHolder>() {

    var onDragStart: ((RecyclerView.ViewHolder) -> Unit)? = null

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorIndicator: View = itemView.findViewById(R.id.colorIndicator)
        val groupName: TextView = itemView.findViewById(R.id.groupName)
        val wordCount: TextView = itemView.findViewById(R.id.wordCount)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val dragHandle: ImageView = itemView.findViewById(R.id.dragHandle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        val context = holder.itemView.context

        holder.groupName.text = group.name
        val count = wordCounts[group.id] ?: 0
        holder.wordCount.text = "$count 个单词"

        // Set color indicator
        if (group.color != null) {
            try {
                val color = Color.parseColor(group.color)
                holder.colorIndicator.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
                holder.colorIndicator.visibility = View.VISIBLE
            } catch (e: Exception) {
                holder.colorIndicator.visibility = View.GONE
            }
        } else {
            // Use a default color based on position
            val defaultColors = listOf(
                "#6B8FD4", "#E8636A", "#5CB87A", "#F0A050", "#9B72CF", "#5B9BD5"
            )
            val color = Color.parseColor(defaultColors[position % defaultColors.size])
            holder.colorIndicator.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
            holder.colorIndicator.visibility = View.VISIBLE
        }

        holder.deleteButton.setOnClickListener {
            onDelete(group)
        }

        // Drag handle - start drag on touch
        holder.dragHandle.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                onDragStart?.invoke(holder)
            }
            false
        }
    }

    override fun getItemCount(): Int = groups.size
}
