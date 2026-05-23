package com.wordnote.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wordnote.app.R
import com.wordnote.app.data.Word
import com.wordnote.app.util.DateUtils
import com.wordnote.app.util.compatOverridePendingTransitionClose

class RecycleBinActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var adapter: RecycleBinAdapter
    private lateinit var emptyView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomBar: LinearLayout
    private lateinit var countText: TextView
    private lateinit var restoreSelectedButton: View
    private lateinit var permanentDeleteButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle_bin)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        emptyView = findViewById(R.id.emptyView)
        recyclerView = findViewById(R.id.recycleBinList)
        bottomBar = findViewById(R.id.bottomBar)
        countText = findViewById(R.id.countText)
        restoreSelectedButton = findViewById(R.id.restoreSelectedButton)
        permanentDeleteButton = findViewById(R.id.permanentDeleteButton)

        setupToolbar()
        setupList()
        setupButtons()
        observeData()
    }

    private fun setupToolbar() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupList() {
        adapter = RecycleBinAdapter { updateBottomBar() }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        restoreSelectedButton.setOnClickListener {
            val selected = adapter.getSelectedIds()
            if (selected.isEmpty()) return@setOnClickListener
            MaterialAlertDialogBuilder(this)
                .setTitle("恢复单词")
                .setMessage("确定要恢复选中的 ${selected.size} 个单词吗？")
                .setPositiveButton("恢复") { _, _ ->
                    viewModel.restoreWords(selected.toList())
                    adapter.clearSelection()
                    Toast.makeText(this, "已恢复 ${selected.size} 个单词", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消", null)
                .show()
        }

        permanentDeleteButton.setOnClickListener {
            val selected = adapter.getSelectedIds()
            if (selected.isEmpty()) return@setOnClickListener
            MaterialAlertDialogBuilder(this)
                .setTitle("彻底删除")
                .setMessage("确定要彻底删除选中的 ${selected.size} 个单词吗？此操作不可恢复。")
                .setPositiveButton("删除") { _, _ ->
                    viewModel.permanentDeleteWords(selected.toList())
                    adapter.clearSelection()
                    Toast.makeText(this, "已彻底删除 ${selected.size} 个单词", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun setupButtons() {
        // Nothing extra needed here; buttons are wired in setupList
    }

    private fun observeData() {
        viewModel.deletedWords.observe(this) { words ->
            adapter.submitList(words)
            emptyView.visibility = if (words.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (words.isEmpty()) View.GONE else View.VISIBLE
            countText.text = "${words.size} 个单词"
            if (words.isEmpty()) {
                bottomBar.visibility = View.GONE
                adapter.clearSelection()
            }
        }
    }

    private fun updateBottomBar() {
        val count = adapter.getSelectedIds().size
        bottomBar.visibility = if (count > 0) View.VISIBLE else View.GONE
    }

    // Adapter
    inner class RecycleBinAdapter(
        private val onSelectionChanged: () -> Unit
    ) : ListAdapter<Word, RecycleBinAdapter.VH>(WordDiffCallback()) {

        private val selectedIds = mutableSetOf<Long>()

        fun getSelectedIds(): Set<Long> = selectedIds.toSet()

        fun clearSelection() {
            selectedIds.clear()
            notifyDataSetChanged()
            onSelectionChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycle_bin_word, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(getItem(position))
        }

        inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
            private val wordText: TextView = itemView.findViewById(R.id.wordText)
            private val meaningText: TextView = itemView.findViewById(R.id.meaningText)
            private val deleteTimeText: TextView = itemView.findViewById(R.id.deleteTimeText)
            private val restoreButton: ImageView = itemView.findViewById(R.id.restoreButton)

            fun bind(word: Word) {
                wordText.text = word.word
                meaningText.text = word.meaning
                deleteTimeText.text = "删除于 ${DateUtils.formatDateTime(word.deletedAt)}"

                checkbox.isChecked = selectedIds.contains(word.id)
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedIds.add(word.id) else selectedIds.remove(word.id)
                    onSelectionChanged()
                }

                itemView.setOnClickListener {
                    checkbox.isChecked = !checkbox.isChecked
                }

                restoreButton.setOnClickListener {
                    MaterialAlertDialogBuilder(itemView.context)
                        .setTitle("恢复单词")
                        .setMessage("确定要恢复 \"${word.word}\" 吗？")
                        .setPositiveButton("恢复") { _, _ ->
                            viewModel.restoreWord(word.id)
                            Toast.makeText(itemView.context, "已恢复", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("取消", null)
                        .show()
                }
            }
        }
    }

    class WordDiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Word, newItem: Word) = oldItem == newItem
    }
}
