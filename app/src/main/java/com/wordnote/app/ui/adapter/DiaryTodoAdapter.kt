package com.wordnote.app.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wordnote.app.R
import com.wordnote.app.data.DiaryTodo

class DiaryTodoAdapter(
    private val onToggle: (DiaryTodo, Boolean) -> Unit,
    private val onDelete: (DiaryTodo) -> Unit
) : ListAdapter<DiaryTodo, DiaryTodoAdapter.TodoViewHolder>(TodoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.todoCheckBox)
        private val todoTextView: TextView = itemView.findViewById(R.id.todoTextView)
        private val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)

        fun bind(todo: DiaryTodo) {
            checkBox.isChecked = todo.isCompleted
            todoTextView.text = todo.text

            if (todo.isCompleted) {
                todoTextView.paintFlags = todoTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                todoTextView.alpha = 0.5f
            } else {
                todoTextView.paintFlags = todoTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                todoTextView.alpha = 1.0f
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onToggle(todo, isChecked)
            }

            deleteButton.setOnClickListener { onDelete(todo) }
        }
    }

    class TodoDiffCallback : DiffUtil.ItemCallback<DiaryTodo>() {
        override fun areItemsTheSame(oldItem: DiaryTodo, newItem: DiaryTodo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DiaryTodo, newItem: DiaryTodo): Boolean {
            return oldItem == newItem
        }
    }
}
