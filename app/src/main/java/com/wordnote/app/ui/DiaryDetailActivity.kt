package com.wordnote.app.ui

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wordnote.app.R
import com.wordnote.app.data.DiaryEntry
import com.wordnote.app.data.DiaryTodo
import com.wordnote.app.data.Word
import com.wordnote.app.ui.adapter.DiaryTodoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DiaryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATE = "extra_date"
    }

    private lateinit var viewModel: DiaryViewModel
    private lateinit var dateTextView: TextView
    private lateinit var moodContainer: LinearLayout
    private lateinit var contentEditText: EditText
    private lateinit var todoRecyclerView: RecyclerView
    private lateinit var todoInputEditText: EditText
    private lateinit var todoCountTextView: TextView
    private lateinit var linkedWordsContainer: FlexboxLayout
    private lateinit var noWordsTextView: TextView
    private lateinit var wordsAddedTextView: TextView
    private lateinit var wordsReviewedTextView: TextView

    private var entryId: Long = -1
    private var entryDate: Long = 0
    private var currentMood: Int = 0
    private var currentEntry: DiaryEntry? = null

    private lateinit var todoAdapter: DiaryTodoAdapter

    private val moodOptions = listOf(
        0 to "无",
        1 to "开心",
        2 to "平静",
        3 to "难过",
        4 to "疲惫",
        5 to "兴奋"
    )
    private val moodEmojis = mapOf(
        0 to "",
        1 to "😊",
        2 to "😐",
        3 to "😔",
        4 to "😴",
        5 to "🎉"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_detail)

        viewModel = ViewModelProvider(this)[DiaryViewModel::class.java]

        entryDate = intent.getLongExtra(EXTRA_DATE, System.currentTimeMillis())
        val startOfDay = viewModel.getStartOfDay(entryDate)

        initViews()
        setupMoodSelector()
        setupTodoList()
        setupListeners()
        loadEntry(startOfDay)
        loadStatistics()
    }

    private fun initViews() {
        dateTextView = findViewById(R.id.dateTextView)
        moodContainer = findViewById(R.id.moodContainer)
        contentEditText = findViewById(R.id.contentEditText)
        todoRecyclerView = findViewById(R.id.todoRecyclerView)
        todoInputEditText = findViewById(R.id.todoInputEditText)
        todoCountTextView = findViewById(R.id.todoCountTextView)
        linkedWordsContainer = findViewById(R.id.linkedWordsContainer)
        noWordsTextView = findViewById(R.id.noWordsTextView)
        wordsAddedTextView = findViewById(R.id.wordsAddedTextView)
        wordsReviewedTextView = findViewById(R.id.wordsReviewedTextView)

        val dateFormat = SimpleDateFormat("yyyy年M月d日 EEEE", Locale.CHINA)
        dateTextView.text = dateFormat.format(Date(entryDate))

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setupMoodSelector() {
        moodContainer.removeAllViews()

        moodOptions.forEach { (moodValue, moodLabel) ->
            val button = TextView(this).apply {
                text = if (moodValue == 0) "—" else "${moodEmojis[moodValue]} $moodLabel"
                textSize = 13f
                setPadding(dpToPx(12), dpToPx(6), dpToPx(12), dpToPx(6))
                setOnClickListener {
                    currentMood = moodValue
                    updateMoodSelection()
                }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = dpToPx(8)
            }
            button.layoutParams = params
            moodContainer.addView(button)
        }

        updateMoodSelection()
    }

    private fun updateMoodSelection() {
        for (i in 0 until moodContainer.childCount) {
            val child = moodContainer.getChildAt(i) as TextView
            val moodValue = moodOptions[i].first
            if (moodValue == currentMood) {
                child.background = GradientDrawable().apply {
                    setColor(getColor(R.color.primary_light))
                    cornerRadius = 16f * resources.displayMetrics.density
                }
                child.setTextColor(getColor(R.color.primary))
            } else {
                child.background = GradientDrawable().apply {
                    setColor(Color.TRANSPARENT)
                    setStroke(dpToPx(1), getColor(R.color.divider))
                    cornerRadius = 16f * resources.displayMetrics.density
                }
                child.setTextColor(getColor(R.color.text_secondary))
            }
        }
    }

    private fun setupTodoList() {
        todoAdapter = DiaryTodoAdapter(
            onToggle = { todo, isChecked ->
                viewModel.updateTodo(todo.copy(isCompleted = isChecked))
            },
            onDelete = { todo ->
                viewModel.deleteTodo(todo)
            }
        )
        todoRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DiaryDetailActivity)
            adapter = todoAdapter
        }
    }

    private fun setupListeners() {
        findViewById<TextView>(R.id.saveButton).setOnClickListener {
            saveEntry()
        }

        findViewById<ImageView>(R.id.addTodoButton).setOnClickListener {
            addTodo()
        }

        todoInputEditText.setOnEditorActionListener { _, _, _ ->
            addTodo()
            true
        }

        findViewById<TextView>(R.id.addWordsButton).setOnClickListener {
            showWordPickerDialog()
        }
    }

    private fun loadEntry(startOfDay: Long) {
        viewModel.getDiaryEntryByDate(startOfDay).observe(this) { entry ->
            if (entry != null && currentEntry == null) {
                currentEntry = entry
                entryId = entry.id
                currentMood = entry.mood
                contentEditText.setText(entry.content)
                updateMoodSelection()
                observeTodos()
                observeLinkedWords()
            }
        }
    }

    private fun observeTodos() {
        if (entryId == -1L) return

        viewModel.getTodosForEntry(entryId).observe(this) { todos ->
            todoAdapter.submitList(todos)
            val completed = todos.count { it.isCompleted }
            todoCountTextView.text = "$completed/${todos.size}"
        }
    }

    private fun observeLinkedWords() {
        if (entryId == -1L) return

        viewModel.getWordsForEntry(entryId).observe(this) { words ->
            linkedWordsContainer.removeAllViews()
            if (words.isEmpty()) {
                linkedWordsContainer.visibility = View.GONE
                noWordsTextView.visibility = View.VISIBLE
            } else {
                linkedWordsContainer.visibility = View.VISIBLE
                noWordsTextView.visibility = View.GONE
                words.forEach { word -> addWordChip(word) }
            }
        }
    }

    private fun addWordChip(word: Word) {
        val chip = TextView(this).apply {
            text = word.word
            textSize = 13f
            setTextColor(getColor(R.color.primary))
            setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4))
            background = GradientDrawable().apply {
                setColor(getColor(R.color.primary_light))
                cornerRadius = 16f * resources.displayMetrics.density
            }
            setOnClickListener {
                MaterialAlertDialogBuilder(this@DiaryDetailActivity)
                    .setTitle("取消关联")
                    .setMessage("确定取消关联 \"${word.word}\" 吗？")
                    .setPositiveButton("确定") { _, _ ->
                        viewModel.unlinkWordFromDiary(entryId, word.id)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        }

        val params = FlexboxLayout.LayoutParams(
            FlexboxLayout.LayoutParams.WRAP_CONTENT,
            FlexboxLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, dpToPx(6), dpToPx(6))
        }
        chip.layoutParams = params
        linkedWordsContainer.addView(chip)
    }

    private fun addTodo() {
        val text = todoInputEditText.text.toString().trim()
        if (text.isEmpty()) return

        if (entryId == -1L) {
            saveEntry { todoId ->
                viewModel.insertTodo(DiaryTodo(diaryEntryId = todoId, text = text))
            }
        } else {
            viewModel.insertTodo(DiaryTodo(diaryEntryId = entryId, text = text))
        }

        todoInputEditText.text.clear()
    }

    private fun saveEntry(onComplete: ((Long) -> Unit)? = null) {
        val content = contentEditText.text.toString().trim()
        val now = System.currentTimeMillis()

        if (currentEntry != null) {
            val updated = currentEntry!!.copy(
                content = content,
                mood = currentMood,
                updatedAt = now
            )
            viewModel.updateDiaryEntry(updated)
            onComplete?.invoke(entryId)
            Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
        } else {
            val entry = DiaryEntry(
                entryDate = entryDate,
                content = content,
                mood = currentMood,
                createdAt = now,
                updatedAt = now
            )
            viewModel.insertDiaryEntry(entry) { newId ->
                entryId = newId
                currentEntry = entry.copy(id = newId)
                onComplete?.invoke(newId)
            }
            Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadStatistics() {
        CoroutineScope(Dispatchers.IO).launch {
            val (start, end) = getTodayRange()
            val wordsAdded = viewModel.getWordsAddedToday()
            val wordsReviewed = viewModel.getWordsReviewedToday()

            withContext(Dispatchers.Main) {
                wordsAddedTextView.text = "$wordsAdded"
                wordsReviewedTextView.text = "$wordsReviewed"
            }
        }
    }

    private fun getTodayRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis
        return Pair(startOfDay, endOfDay)
    }

    private fun showWordPickerDialog() {
        viewModel.getAllWords().observe(this) { words ->
            if (words.isNullOrEmpty()) {
                Toast.makeText(this, "暂无单词，请先添加单词", Toast.LENGTH_SHORT).show()
                return@observe
            }

            val wordArray = words.map { "${it.word} - ${it.meaning}" }.toTypedArray()
            val selectedIndices = mutableSetOf<Int>()

            // Get currently linked words
            if (entryId != -1L) {
                viewModel.getWordsForEntry(entryId).observe(this) { linkedWords ->
                    val linkedIds = linkedWords.map { it.id }.toSet()
                    words.forEachIndexed { index, word ->
                        if (linkedIds.contains(word.id)) {
                            selectedIndices.add(index)
                        }
                    }
                }
            }

            MaterialAlertDialogBuilder(this)
                .setTitle("选择关联单词")
                .setMultiChoiceItems(wordArray, BooleanArray(wordArray.size) { selectedIndices.contains(it) }) { _, which, isChecked ->
                    if (isChecked) {
                        selectedIndices.add(which)
                    } else {
                        selectedIndices.remove(which)
                    }
                }
                .setPositiveButton("确定") { _, _ ->
                    if (entryId == -1L) {
                        saveEntry {
                            linkWords(words, selectedIndices)
                        }
                    } else {
                        linkWords(words, selectedIndices)
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun linkWords(words: List<Word>, selectedIndices: Set<Int>) {
        viewModel.getWordsForEntry(entryId).observe(this) { currentLinked ->
            val currentLinkedIds = currentLinked.map { it.id }.toSet()
            val selectedIds = selectedIndices.map { words[it].id }.toSet()

            // Unlink removed words
            currentLinkedIds.forEach { wordId ->
                if (wordId !in selectedIds) {
                    viewModel.unlinkWordFromDiary(entryId, wordId)
                }
            }

            // Link new words
            selectedIds.forEach { wordId ->
                if (wordId !in currentLinkedIds) {
                    viewModel.linkWordToDiary(entryId, wordId)
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}
