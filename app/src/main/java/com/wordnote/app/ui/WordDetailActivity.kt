package com.wordnote.app.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.wordnote.app.R
import com.wordnote.app.data.Category
import com.wordnote.app.databinding.ActivityWordDetailBinding
import com.wordnote.app.databinding.ItemSimilarWordBinding
import com.wordnote.app.data.Word
import com.wordnote.app.data.WordMeaning
import com.wordnote.app.ui.adapter.MeaningAdapter
import com.wordnote.app.util.compatOverridePendingTransition
import com.wordnote.app.util.compatOverridePendingTransitionClose
import kotlinx.coroutines.launch

class WordDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_WORD_ID = "extra_word_id"
    }

    private lateinit var binding: ActivityWordDetailBinding
    private lateinit var viewModel: WordViewModel
    private lateinit var backButton: ImageView
    private lateinit var wordTextView: TextView
    private lateinit var meaningTextView: TextView
    private lateinit var forgetCountTextView: TextView
    private lateinit var nextReviewTextView: TextView
    private lateinit var forgottenButton: MaterialButton
    private lateinit var rememberedButton: MaterialButton
    private lateinit var editButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var meaningsContainer: LinearLayout
    private lateinit var meaningsCard: MaterialCardView
    private lateinit var meaningsRecyclerView: RecyclerView
    private lateinit var meaningsLabel: TextView
    private lateinit var meaningsHint: TextView
    private lateinit var meaningAdapter: MeaningAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var similarWordsCard: MaterialCardView
    private lateinit var similarWordsContainer: LinearLayout

    private var wordId: Long = -1
    private var currentWord: Word? = null
    private var categoriesMap: Map<Long, Category> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        wordId = intent.getLongExtra(EXTRA_WORD_ID, -1)
        if (wordId == -1L) {
            finish()
            return
        }

        initViews()
        setupListeners()

        viewModel.allCategories.observe(this) { categories ->
            categoriesMap = categories.associateBy { it.id }
        }

        loadWord()
        loadMeanings()
    }

    private fun initViews() {
        backButton = binding.backButton
        wordTextView = binding.wordTextView
        meaningTextView = binding.meaningTextView
        forgetCountTextView = binding.forgetCountTextView
        nextReviewTextView = binding.nextReviewTextView
        forgottenButton = binding.forgottenButton
        rememberedButton = binding.rememberedButton
        editButton = binding.editButton
        deleteButton = binding.deleteButton
        meaningsContainer = binding.meaningsContainer
        meaningsCard = binding.meaningsCard
        meaningsRecyclerView = binding.meaningsRecyclerView
        meaningsLabel = binding.meaningsLabel
        meaningsHint = binding.meaningsHint
        similarWordsCard = binding.similarWordsCard
        similarWordsContainer = binding.similarWordsContainer

        // Setup adapter
        meaningAdapter = MeaningAdapter(
            onHighlightToggle = { meaning ->
                viewModel.toggleMeaningHighlighted(meaning)
            },
            onNoteClick = { meaning ->
                showNoteDialog(meaning)
            },
            onEditClick = { meaning ->
                showEditMeaningDialog(meaning)
            },
            onOrderChanged = { newOrder ->
                viewModel.reorderMeanings(newOrder)
            }
        )
        meaningsRecyclerView.layoutManager = LinearLayoutManager(this)
        meaningsRecyclerView.adapter = meaningAdapter

        // Setup ItemTouchHelper for drag
        itemTouchHelper = ItemTouchHelper(meaningAdapter.itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(meaningsRecyclerView)

        meaningAdapter.startDragListener = object : MeaningAdapter.OnStartDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                itemTouchHelper.startDrag(viewHolder)
            }
        }
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
            compatOverridePendingTransitionClose(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        forgottenButton.setOnClickListener {
            currentWord?.let { word ->
                viewModel.markAsForgotten(word)
                Toast.makeText(this, "已标记，稍后复习", Toast.LENGTH_SHORT).show()
                loadWord()
            }
        }

        rememberedButton.setOnClickListener {
            currentWord?.let { word ->
                viewModel.markAsReviewed(word)
                Toast.makeText(this, "太棒了！", Toast.LENGTH_SHORT).show()
                loadWord()
            }
        }

        editButton.setOnClickListener {
            val intent = Intent(this, AddWordActivity::class.java).apply {
                putExtra(AddWordActivity.EXTRA_WORD_ID, wordId)
            }
            startActivity(intent)
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        deleteButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("删除单词")
                .setMessage("确定要删除 \"${currentWord?.word}\" 吗？\n可从回收站恢复")
                .setPositiveButton("删除") { _, _ ->
                    currentWord?.let { word ->
                        viewModel.deleteWord(word)
                        Toast.makeText(this, "已移入回收站", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun loadWord() {
        viewModel.getWordById(wordId).observe(this) { word ->
            word?.let {
                displayWord(it)
                loadSimilarWords(it)
            }
        }
    }

    private fun loadSimilarWords(word: Word) {
        lifecycleScope.launch {
            try {
                val similarWords = viewModel.findSimilarWordsExcluding(word.word, word.id)
                displaySimilarWords(similarWords)
            } catch (e: Exception) {
                similarWordsCard.visibility = View.GONE
            }
        }
    }

    private fun displaySimilarWords(similarWords: List<Word>) {
        if (similarWords.isEmpty()) {
            similarWordsCard.visibility = View.GONE
            return
        }

        similarWordsCard.visibility = View.VISIBLE
        similarWordsContainer.removeAllViews()

        similarWords.forEachIndexed { index, similarWord ->
            val category = categoriesMap[similarWord.categoryId]
            val categoryName = category?.name ?: "未分类"
            val categoryColor = try {
                Color.parseColor(category?.color ?: "#757575")
            } catch (e: Exception) {
                Color.parseColor("#757575")
            }

            val itemBinding = ItemSimilarWordBinding.inflate(layoutInflater, similarWordsContainer, false)

            itemBinding.colorDot.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(categoryColor)
            }
            itemBinding.categoryNameText.text = categoryName
            itemBinding.meaningPreviewText.text = similarWord.meaning

            // Hide divider after last item
            if (index >= similarWords.size - 1) {
                itemBinding.divider.visibility = View.GONE
            }

            // Click to navigate to MainActivity with this category
            itemBinding.rowContainer.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("navigate_to_category", similarWord.categoryId)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                finish()
            }

            similarWordsContainer.addView(itemBinding.root)
        }
    }

    private fun loadMeanings() {
        viewModel.getMeaningsForWord(wordId).observe(this) { meanings ->
            displayMeanings(meanings)
        }
    }

    private fun displayWord(word: Word) {
        currentWord = word
        wordTextView.text = word.word
        meaningTextView.text = word.meaning
        forgetCountTextView.text = "${word.forgetCount}"

        if (word.nextReviewAt > 0) {
            val now = System.currentTimeMillis()
            if (word.nextReviewAt <= now) {
                nextReviewTextView.text = "该复习了"
                nextReviewTextView.setTextColor(getColor(R.color.cat_hard))
            } else {
                val diff = word.nextReviewAt - now
                val minutes = diff / (1000 * 60)
                val hours = minutes / 60
                val days = hours / 24

                nextReviewTextView.text = when {
                    days > 0 -> "${days}天后"
                    hours > 0 -> "${hours}小时后"
                    minutes > 0 -> "${minutes}分钟后"
                    else -> "马上"
                }
                nextReviewTextView.setTextColor(getColor(R.color.primary))
            }
        } else {
            nextReviewTextView.text = "未设置"
            nextReviewTextView.setTextColor(getColor(R.color.text_hint))
        }
    }

    private fun displayMeanings(meanings: List<WordMeaning>) {
        if (meanings.isEmpty()) {
            val word = currentWord
            if (word != null) {
                val parts = word.meaning.split("，", ",").map { it.trim() }.filter { it.isNotBlank() }
                if (parts.size > 1) {
                    // Multiple meanings - show split button
                    meaningsCard.visibility = View.VISIBLE
                    meaningTextView.visibility = View.GONE
                    meaningsLabel.visibility = View.VISIBLE
                    meaningsHint.visibility = View.GONE
                    meaningsRecyclerView.visibility = View.GONE

                    // Clear any existing dynamic views
                    while (meaningsContainer.childCount > 3) {
                        meaningsContainer.removeViewAt(meaningsContainer.childCount - 1)
                    }

                    // Add split button
                    val splitButton = TextView(this).apply {
                        text = "拆分释义"
                        textSize = 14f
                        setPadding(dpToPx(16), dpToPx(10), dpToPx(16), dpToPx(10))
                        val bg = GradientDrawable().apply {
                            setColor(getColor(R.color.primary))
                            cornerRadius = 20f * resources.displayMetrics.density
                        }
                        background = bg
                        setTextColor(getColor(R.color.text_on_primary))
                        setOnClickListener {
                            viewModel.saveMeanings(wordId, parts)
                            Toast.makeText(this@WordDetailActivity, "已拆分 ${parts.size} 个释义", Toast.LENGTH_SHORT).show()
                        }
                    }
                    (meaningsContainer as LinearLayout).addView(splitButton)
                    return
                } else if (parts.size == 1) {
                    // Single meaning - auto-create and show marking UI
                    viewModel.createMeaningsIfEmpty(wordId, parts)
                    return  // Will re-trigger via LiveData observer
                }
            }
            meaningsCard.visibility = View.GONE
            meaningTextView.visibility = View.VISIBLE
            return
        }
        meaningsCard.visibility = View.VISIBLE
        meaningTextView.visibility = View.GONE
        meaningsLabel.visibility = View.VISIBLE
        meaningsHint.visibility = View.VISIBLE
        meaningsRecyclerView.visibility = View.VISIBLE

        // Clear any existing dynamic views (like split button)
        while (meaningsContainer.childCount > 3) {
            meaningsContainer.removeViewAt(meaningsContainer.childCount - 1)
        }

        meaningAdapter.submitList(meanings)
    }

    private fun showNoteDialog(meaning: WordMeaning) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_note_input, null)
        val input = dialogView.findViewById<EditText>(R.id.noteEditText)
        input.setText(meaning.note ?: "")

        MaterialAlertDialogBuilder(this)
            .setTitle("添加备注")
            .setView(dialogView)
            .setPositiveButton("保存") { _, _ ->
                val note = input.text.toString().trim().ifBlank { null }
                viewModel.updateMeaningNote(meaning, note)
                Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showEditMeaningDialog(meaning: WordMeaning) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_note_input, null)
        val input = dialogView.findViewById<EditText>(R.id.noteEditText)
        input.setText(meaning.meaningText)

        MaterialAlertDialogBuilder(this)
            .setTitle("编辑释义")
            .setView(dialogView)
            .setPositiveButton("保存") { _, _ ->
                val newText = input.text.toString().trim()
                if (newText.isNotBlank()) {
                    viewModel.updateMeaningText(meaning, newText)
                    Toast.makeText(this, "已更新", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onResume() {
        super.onResume()
    }
}
