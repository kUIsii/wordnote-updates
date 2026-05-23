package com.wordnote.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.GestureDetectorCompat
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wordnote.app.R
import com.wordnote.app.data.Category
import com.wordnote.app.data.Word
import com.wordnote.app.data.WordGroup
import com.wordnote.app.ui.adapter.WordAdapter
import com.wordnote.app.util.UpdateChecker
import com.wordnote.app.util.compatOverridePendingTransition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WordViewModel
    private lateinit var wordAdapter: WordAdapter
    private lateinit var wordRecyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var inputEditText: TextInputEditText
    private lateinit var addButton: FloatingActionButton
    private lateinit var statsTextView: TextView
    private lateinit var searchEditText: EditText
    private lateinit var manageCategoriesButton: ImageView
    private lateinit var settingsButton: ImageView

    private lateinit var tabContainer: LinearLayout

    private var selectedCategoryId: Long? = null
    private var selectedCategoryName: String? = null
    private var selectedTab: TextView? = null
    private var categoriesList: List<Category> = emptyList()
    private var groupsList: List<WordGroup> = emptyList()
    private var updateDialogShown = false
    private val scrollPositions = mutableMapOf<Long, Int>()
    private var pendingScrollPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        initViews()
        setupRecyclerView()
        setupSwipeGesture()
        setupInput()
        setupSearch()
        observeData()
        checkForUpdateOnStartup()
    }

    private fun checkForUpdate() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                val currentVersionName = packageInfo.versionName ?: "1.0"

                Toast.makeText(this@MainActivity, "正在检查更新...", Toast.LENGTH_SHORT).show()

                val updateInfo = withContext(Dispatchers.IO) {
                    UpdateChecker.checkForUpdate(currentVersionName)
                }

                if (updateInfo != null) {
                    showUpdateDialog(updateInfo)
                } else {
                    Toast.makeText(this@MainActivity, "当前已是最新版本", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "检查更新失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkForUpdateOnStartup() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                val currentVersionName = packageInfo.versionName ?: "1.0"

                val updateInfo = withContext(Dispatchers.IO) {
                    UpdateChecker.checkForUpdate(currentVersionName)
                }

                if (updateInfo != null && !updateDialogShown) {
                    showUpdateDialog(updateInfo)
                }
            } catch (_: Exception) {
                // Silent fail on startup check
            }
        }
    }

    private fun showUpdateDialog(updateInfo: UpdateChecker.UpdateInfo) {
        if (updateDialogShown) return
        updateDialogShown = true

        MaterialAlertDialogBuilder(this, R.style.Theme_WordNoteApp_Dialog)
            .setTitle("发现新版本 ${updateInfo.versionName}")
            .setMessage(updateInfo.body.ifBlank { "有新版本可用，是否更新？" })
            .setPositiveButton("更新") { _, _ ->
                startUpdate(updateInfo)
            }
            .setNegativeButton("稍后") { _, _ ->
                updateDialogShown = false
            }
            .setOnCancelListener {
                updateDialogShown = false
            }
            .show()
    }

    private fun startUpdate(updateInfo: UpdateChecker.UpdateInfo) {
        val progressDialog = android.app.ProgressDialog(this).apply {
            setTitle("下载中")
            setMessage("正在下载新版本...")
            setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL)
            isIndeterminate = false
            max = 100
            setCancelable(false)
            show()
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                UpdateChecker.downloadAndInstall(this@MainActivity, updateInfo) { progress ->
                    progressDialog.progress = progress
                }
                progressDialog.dismiss()
            } catch (e: Exception) {
                progressDialog.dismiss()
                MaterialAlertDialogBuilder(this@MainActivity, R.style.Theme_WordNoteApp_Dialog)
                    .setTitle("下载失败")
                    .setMessage("错误信息：\n${e.message}\n\n请检查网络连接后重试")
                    .setPositiveButton("确定", null)
                    .show()
            }
        }
    }

    private fun initViews() {
        wordRecyclerView = findViewById(R.id.wordRecyclerView)
        emptyView = findViewById(R.id.emptyView)
        inputEditText = findViewById(R.id.inputEditText)
        addButton = findViewById(R.id.addButton)
        statsTextView = findViewById(R.id.statsTextView)
        searchEditText = findViewById(R.id.searchEditText)
        tabContainer = findViewById(R.id.tabContainer)
        manageCategoriesButton = findViewById(R.id.manageCategoriesButton)
        settingsButton = findViewById(R.id.settingsButton)

        findViewById<ImageView>(R.id.dictionaryButton).setOnClickListener {
            startActivity(Intent(this, DictionaryActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        manageCategoriesButton.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun setupRecyclerView() {
        wordAdapter = WordAdapter(
            onWordClick = { word ->
                val intent = Intent(this, WordDetailActivity::class.java).apply {
                    putExtra(WordDetailActivity.EXTRA_WORD_ID, word.id)
                }
                startActivity(intent)
                compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            },
            onEditClick = { word ->
                showEditWordSheet(word)
            },
            onDeleteClick = { word ->
                showDeleteWordDialog(word)
            },
            onSelectionChanged = { selectedIds ->
                updateSelectionUI(selectedIds)
            }
        )

        wordRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = wordAdapter
        }
    }

    private fun setupSwipeGesture() {
        val gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX < 0) {
                        // Swipe left → next category
                        swipeToCategory(1)
                    } else {
                        // Swipe right → previous category
                        swipeToCategory(-1)
                    }
                    return true
                }
                return false
            }

            override fun onDown(e: MotionEvent): Boolean = false
        })

        wordRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(e)
            }
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    private fun swipeToCategory(direction: Int) {
        if (categoriesList.isEmpty()) return
        val currentIdx = categoriesList.indexOfFirst { it.id == selectedCategoryId }
        val newIdx = currentIdx + direction
        if (newIdx < 0 || newIdx >= categoriesList.size) return
        val tab = tabContainer.getChildAt(newIdx) as? TextView ?: return
        selectTab(tab, categoriesList[newIdx].id)
    }

    private fun updateSelectionUI(selectedIds: Set<Long>) {
        val selectionBar = findViewById<LinearLayout>(R.id.selectionBar)
        val selectionCountText = findViewById<TextView>(R.id.selectionCountText)
        val deleteSelectedButton = findViewById<MaterialButton>(R.id.deleteSelectedButton)
        val copySelectedButton = findViewById<MaterialButton>(R.id.copySelectedButton)
        val cancelButton = findViewById<MaterialButton>(R.id.cancelSelectionButton)

        if (selectedIds.isEmpty()) {
            selectionBar.visibility = View.GONE
        } else {
            selectionBar.visibility = View.VISIBLE
            selectionCountText.text = "已选择 ${selectedIds.size} 个"
        }

        deleteSelectedButton.setOnClickListener {
            if (selectedIds.isNotEmpty()) {
                showBatchDeleteDialog(selectedIds)
            }
        }

        copySelectedButton.setOnClickListener {
            if (selectedIds.isNotEmpty()) {
                showCopyCategoryDialog(selectedIds)
            }
        }

        cancelButton.setOnClickListener {
            wordAdapter.exitSelectionMode()
        }
    }

    private fun showBatchDeleteDialog(wordIds: Set<Long>) {
        MaterialAlertDialogBuilder(this)
            .setTitle("批量删除")
            .setMessage("确定要删除选中的 ${wordIds.size} 个单词吗？\n可从回收站恢复")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteWordsByIds(wordIds.toList())
                wordAdapter.deleteSelectedWords()
                Toast.makeText(this, "已移入回收站 ${wordIds.size} 个单词", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showCopyCategoryDialog(wordIds: Set<Long>) {
        if (categoriesList.isEmpty()) {
            Toast.makeText(this, "没有可用的分类", Toast.LENGTH_SHORT).show()
            return
        }
        val categoryNames = categoriesList.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("复制到分类")
            .setItems(categoryNames) { _, which ->
                val targetCategory = categoriesList[which]
                viewModel.copyWordsToCategory(wordIds.toList(), targetCategory.id) { count ->
                    wordAdapter.exitSelectionMode()
                    Toast.makeText(this, "已复制 $count 个单词到「${targetCategory.name}」", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun setupTabs(categories: List<Category> = emptyList()) {
        tabContainer.removeAllViews()
        categoriesList = categories

        if (categories.isEmpty()) {
            selectedCategoryId = null
            viewModel.selectCategory(null)
            return
        }

        // Create category tabs only (no "全部")
        categories.forEach { category ->
            val color = try { android.graphics.Color.parseColor(category.color) } catch (e: Exception) { 0 }
            val tab = createTabView(category.name, category.id, color)
            tabContainer.addView(tab)
        }

        // Select first category by default
        val firstTab = tabContainer.getChildAt(0) as? TextView
        if (firstTab != null && selectedCategoryId == null) {
            selectTab(firstTab, categories[0].id)
        } else {
            // Restore previous selection
            selectedCategoryId?.let { catId ->
                val idx = categories.indexOfFirst { it.id == catId }
                if (idx >= 0) {
                    val tab = tabContainer.getChildAt(idx) as? TextView
                    if (tab != null) selectTab(tab, catId)
                } else {
                    // Category was deleted, select first
                    val tab = tabContainer.getChildAt(0) as? TextView
                    if (tab != null) selectTab(tab, categories[0].id)
                }
            }
        }
    }

    private fun createTabView(text: String, categoryId: Long?, color: Int): TextView {
        val tab = TextView(this).apply {
            this.text = text
            textSize = 13f
            setTextColor(resources.getColor(R.color.text_secondary, null))
            gravity = android.view.Gravity.CENTER
            setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8))
            setBackgroundResource(R.drawable.tab_bg_inactive)
            tag = color
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dpToPx(32)
            ).apply {
                marginEnd = dpToPx(8)
            }
            layoutParams = params
        }
        tab.setOnClickListener { selectTab(tab, categoryId) }
        return tab
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun selectTab(tab: TextView, categoryId: Long?) {
        // Save current scroll position before switching
        selectedCategoryId?.let { currentId ->
            val pos = (wordRecyclerView.layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() ?: 0
            scrollPositions[currentId] = pos
        }

        selectedTab?.let { prevTab ->
            prevTab.setBackgroundResource(R.drawable.tab_bg_inactive)
            prevTab.setTextColor(resources.getColor(R.color.text_secondary, null))
        }

        val color = tab.tag as? Int ?: 0
        val bg = android.graphics.drawable.GradientDrawable().apply {
            setColor(color)
            cornerRadius = 16f * resources.displayMetrics.density
        }
        tab.background = bg
        tab.setTextColor(android.graphics.Color.WHITE)

        selectedTab = tab
        selectedCategoryId = categoryId
        selectedCategoryName = categoriesList.find { it.id == categoryId }?.name
        viewModel.selectCategory(categoryId)

        // Update input mode based on category
        updateInputMode()

        // Set pending scroll position to be restored after data loads
        pendingScrollPosition = scrollPositions[categoryId]
    }

    private fun updateInputMode() {
        if (selectedCategoryName == "意思相近的单词") {
            inputEditText.maxLines = 5
            inputEditText.minLines = 1
            inputEditText.isSingleLine = false
            inputEditText.hint = "单词 释义（空格分隔）\n多行输入可分组"
        } else {
            inputEditText.maxLines = 1
            inputEditText.minLines = 1
            inputEditText.isSingleLine = true
            inputEditText.hint = "单词 释义（空格分隔）"
        }
    }

    private fun setupInput() {
        addButton.setOnClickListener {
            addWordFromInput()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                addWordFromInput()
                true
            } else false
        }
    }

    private fun addWordFromInput() {
        val input = inputEditText.text.toString().trim()
        if (input.isEmpty()) {
            Toast.makeText(this, "请输入单词和释义", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCategoryId == null) {
            Toast.makeText(this, "请先选择一个分类", Toast.LENGTH_SHORT).show()
            return
        }

        val lines = input.split("\n").filter { it.isNotBlank() }
        if (lines.size > 1) {
            // Multi-line: batch mode with shared batchId
            addBatchWordsFromInput(input)
        } else {
            // Single line: single word mode
            addSingleWordFromInput(input)
        }
    }

    private fun addSingleWordFromInput(input: String) {
        val parts = input.split("\\s+".toRegex(), limit = 2)
        if (parts.size < 2) {
            Toast.makeText(this, "格式：单词 中文释义", Toast.LENGTH_SHORT).show()
            return
        }

        val word: String
        val meaning: String

        val firstHasChinese = parts[0].any { it in '一'..'鿿' }
        val secondHasChinese = parts[1].any { it in '一'..'鿿' }

        if (secondHasChinese && !firstHasChinese) {
            word = parts[0].trim()
            meaning = parts[1].trim()
        } else if (firstHasChinese && !secondHasChinese) {
            meaning = parts[0].trim()
            word = parts[1].trim()
        } else {
            word = parts[0].trim()
            meaning = parts[1].trim()
        }

        val meaningTexts = meaning.split("，", ",").map { it.trim() }.filter { it.isNotBlank() }

        val wordEntity = Word(
            word = word,
            meaning = meaning,
            categoryId = selectedCategoryId
        )

        viewModel.insertWord(wordEntity, meaningTexts = meaningTexts)
        inputEditText.text?.clear()
        Toast.makeText(this, "已添加", Toast.LENGTH_SHORT).show()
    }

    private fun addBatchWordsFromInput(input: String) {
        val lines = input.split("\n").filter { it.isNotBlank() }
        if (lines.isEmpty()) {
            Toast.makeText(this, "请输入单词", Toast.LENGTH_SHORT).show()
            return
        }

        val batchId = System.currentTimeMillis()
        val wordsToAdd = mutableListOf<Word>()
        var skippedCount = 0

        lines.forEach { line ->
            val trimmedLine = line.trim()
            val parts = trimmedLine.split("\\s+".toRegex(), limit = 2)

            if (parts.size < 2) {
                // No meaning provided, skip this line
                skippedCount++
                return@forEach
            }

            val word: String
            val meaning: String

            val firstHasChinese = parts[0].any { it in '一'..'鿿' }
            val secondHasChinese = parts[1].any { it in '一'..'鿿' }

            if (secondHasChinese && !firstHasChinese) {
                word = parts[0].trim()
                meaning = parts[1].trim()
            } else if (firstHasChinese && !secondHasChinese) {
                meaning = parts[0].trim()
                word = parts[1].trim()
            } else {
                word = parts[0].trim()
                meaning = parts[1].trim()
            }

            if (word.isNotBlank() && meaning.isNotBlank()) {
                wordsToAdd.add(Word(
                    word = word,
                    meaning = meaning,
                    categoryId = selectedCategoryId,
                    batchId = batchId
                ))
            } else {
                skippedCount++
            }
        }

        if (wordsToAdd.isEmpty()) {
            Toast.makeText(this, "没有有效的单词可添加", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.insertBatchWords(wordsToAdd)
        inputEditText.text?.clear()

        val message = if (skippedCount > 0) {
            "已添加 ${wordsToAdd.size} 个单词（跳过 $skippedCount 个无效行）"
        } else {
            "已添加 ${wordsToAdd.size} 个单词"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showEditWordSheet(word: Word) {
        val dialog = BottomSheetDialog(this, R.style.Theme_WordNoteApp_BottomSheet)
        val sheetView = layoutInflater.inflate(R.layout.sheet_edit_word, null)

        val wordInput = sheetView.findViewById<TextInputEditText>(R.id.wordInput)
        val meaningInput = sheetView.findViewById<TextInputEditText>(R.id.meaningInput)
        val categorySpinner = sheetView.findViewById<Spinner>(R.id.categorySpinner)
        val groupSpinner = sheetView.findViewById<Spinner>(R.id.groupSpinner)
        val saveButton = sheetView.findViewById<MaterialButton>(R.id.saveButton)

        // Fill current values
        wordInput.setText(word.word)
        meaningInput.setText(word.meaning)

        // Category spinner
        val categoryNames = categoriesList.map { it.name }
        val catAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = catAdapter
        word.categoryId?.let { catId ->
            val idx = categoriesList.indexOfFirst { it.id == catId }
            if (idx >= 0) categorySpinner.setSelection(idx)
        }

        // Group spinner
        val groupNames = mutableListOf("无分组")
        groupsList.forEach { groupNames.add(it.name) }
        groupNames.add("+ 新建分组")
        val groupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupNames)
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupSpinner.adapter = groupAdapter
        word.groupId?.let { gId ->
            val idx = groupsList.indexOfFirst { it.id == gId } + 1
            if (idx > 0 && idx < groupNames.size - 1) groupSpinner.setSelection(idx)
        }

        groupSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == groupNames.size - 1 && groupNames[position] == "+ 新建分组") {
                    showCreateGroupDialogInSheet(dialog, groupSpinner, groupNames)
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        saveButton.setOnClickListener {
            val newWord = wordInput.text.toString().trim()
            val newMeaning = meaningInput.text.toString().trim()

            if (newWord.isEmpty()) {
                wordInput.error = "不能为空"
                return@setOnClickListener
            }
            if (newMeaning.isEmpty()) {
                meaningInput.error = "不能为空"
                return@setOnClickListener
            }

            val selectedCat = if (categorySpinner.selectedItemPosition >= 0) {
                categoriesList[categorySpinner.selectedItemPosition]
            } else null

            val selectedGroup = if (groupSpinner.selectedItemPosition > 0 && groupSpinner.selectedItemPosition <= groupsList.size) {
                groupsList[groupSpinner.selectedItemPosition - 1]
            } else null

            val updatedWord = word.copy(
                word = newWord,
                meaning = newMeaning,
                categoryId = selectedCat?.id,
                groupId = selectedGroup?.id
            )

            viewModel.updateWord(updatedWord)

            // Update meanings
            val meaningTexts = newMeaning.split("，", ",").map { it.trim() }.filter { it.isNotBlank() }
            viewModel.saveMeanings(word.id, meaningTexts)

            Toast.makeText(this, "已更新", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.setContentView(sheetView)
        dialog.show()
    }

    private fun showCreateGroupDialogInSheet(parentDialog: BottomSheetDialog, spinner: Spinner, groupNames: MutableList<String>) {
        val input = EditText(this).apply {
            hint = "输入分组名称"
            setPadding(48, 32, 48, 32)
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("新建词语分组")
            .setView(input)
            .setPositiveButton("创建") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotBlank()) {
                    viewModel.createGroup(name)
                    Toast.makeText(this, "分组已创建", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消") { _, _ ->
                spinner.setSelection(0)
            }
            .setOnCancelListener {
                spinner.setSelection(0)
            }
            .show()
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
        })
    }

    private fun observeData() {
        viewModel.allCategories.observe(this) { categories ->
            wordAdapter.setCategories(categories)
            if (tabContainer.childCount != categories.size) {
                setupTabs(categories)
            }
        }

        viewModel.highlightedMeanings.observe(this) { meanings ->
            wordAdapter.setHighlightedMeanings(meanings)
        }

        viewModel.allGroups.observe(this) { groups ->
            groupsList = groups
            wordAdapter.setGroups(groups)
        }

        viewModel.filteredWords.observe(this) { words ->
            wordAdapter.submitWordList(words)
            emptyView.visibility = if (words.isEmpty()) View.VISIBLE else View.GONE
            wordRecyclerView.visibility = if (words.isEmpty()) View.GONE else View.VISIBLE

            // Restore scroll position after data loads
            pendingScrollPosition?.let { pos ->
                if (pos > 0) {
                    wordRecyclerView.post {
                        (wordRecyclerView.layoutManager as? LinearLayoutManager)?.scrollToPosition(pos)
                    }
                }
                pendingScrollPosition = null
            }
        }

        viewModel.allWords.observe(this) { words ->
            val reviewCount = viewModel.reviewCount.value ?: 0
            statsTextView.text = "共 ${words.size} 个 · 待复习 $reviewCount 个"
        }

        viewModel.reviewCount.observe(this) { count ->
            val total = viewModel.allWords.value?.size ?: 0
            statsTextView.text = "共 $total 个 · 待复习 $count 个"
        }
    }

    private fun showDeleteWordDialog(word: Word) {
        MaterialAlertDialogBuilder(this)
            .setTitle("删除单词")
            .setMessage("确定要删除 \"${word.word}\" 吗？\n可从回收站恢复")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteWord(word)
                Toast.makeText(this, "已移入回收站", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.selectCategory(selectedCategoryId)
    }
}
