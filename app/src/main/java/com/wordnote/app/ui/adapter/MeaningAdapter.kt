package com.wordnote.app.ui.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wordnote.app.R
import com.wordnote.app.data.WordMeaning
import com.wordnote.app.databinding.ItemMeaningBinding

class MeaningAdapter(
    private val onHighlightToggle: (WordMeaning) -> Unit,
    private val onNoteClick: (WordMeaning) -> Unit,
    private val onOrderChanged: (List<WordMeaning>) -> Unit
) : RecyclerView.Adapter<MeaningAdapter.VH>() {

    private var items = mutableListOf<WordMeaning>()
    private val highlightColor = Color.parseColor("#5B9BD5")

    fun submitList(newItems: List<WordMeaning>) {
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    fun getItems(): List<WordMeaning> = items.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMeaningBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount() = items.size

    fun moveItem(from: Int, to: Int) {
        val item = items.removeAt(from)
        items.add(to, item)
        notifyItemMoved(from, to)
    }

    fun onDragFinished() {
        onOrderChanged(items.toList())
    }

    inner class VH(private val binding: ItemMeaningBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dragHandle: ImageView = binding.dragHandle
        private val seqText: TextView = binding.seqText
        private val meaningText: TextView = binding.meaningText
        private val highlightButton: TextView = binding.highlightButton
        private val noteButton: TextView = binding.noteButton

        fun bind(meaning: WordMeaning, position: Int) {
            seqText.text = "${position + 1}."
            meaningText.text = meaning.meaningText

            // Styling
            val density = itemView.resources.displayMetrics.density
            if (meaning.isProblematic) {
                meaningText.setTextColor(itemView.context.getColor(R.color.cat_hard))
                val bg = GradientDrawable().apply {
                    setColor(Color.parseColor("#1AE8636A"))
                    cornerRadius = 6f * density
                }
                meaningText.background = bg
                meaningText.setPadding(
                    (8 * density).toInt(), (2 * density).toInt(),
                    (8 * density).toInt(), (2 * density).toInt()
                )
                meaningText.paint.isFakeBoldText = true
            } else if (meaning.isHighlighted) {
                meaningText.setTextColor(highlightColor)
                val bg = GradientDrawable().apply {
                    setColor(Color.argb(35, Color.red(highlightColor), Color.green(highlightColor), Color.blue(highlightColor)))
                    cornerRadius = 6f * density
                }
                meaningText.background = bg
                meaningText.setPadding(
                    (8 * density).toInt(), (4 * density).toInt(),
                    (8 * density).toInt(), (4 * density).toInt()
                )
                meaningText.paint.isFakeBoldText = true
            } else {
                meaningText.setTextColor(itemView.context.getColor(R.color.text_primary))
                meaningText.background = null
                meaningText.setPadding(0, 0, 0, 0)
            }

            // Highlight button
            highlightButton.text = if (meaning.isHighlighted) "已标记" else "标记"
            if (meaning.isHighlighted) {
                highlightButton.setTextColor(highlightColor)
                highlightButton.background = null
            } else {
                val bg = GradientDrawable().apply {
                    setStroke((1 * density).toInt(), itemView.context.getColor(R.color.divider))
                    cornerRadius = 16f * density
                    setColor(Color.TRANSPARENT)
                }
                highlightButton.background = bg
                highlightButton.setTextColor(itemView.context.getColor(R.color.text_secondary))
            }
            highlightButton.setOnClickListener { onHighlightToggle(meaning) }

            // Note button
            noteButton.text = if (meaning.note != null) "有备注" else "备注"
            noteButton.setOnClickListener { onNoteClick(meaning) }

            // Drag handle - start drag on touch
            dragHandle.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    startDragListener?.onStartDrag(this@VH)
                }
                false
            }
        }
    }

    // Drag listener
    var startDragListener: OnStartDragListener? = null

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    // ItemTouchHelper adapter
    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val from = viewHolder.adapterPosition
            val to = target.adapterPosition
            moveItem(from, to)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // No swipe
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                onDragFinished()
            }
        }
    }
}
