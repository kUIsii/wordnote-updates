package com.wordnote.app.ui

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wordnote.app.R
import com.wordnote.app.data.Category
import com.wordnote.app.databinding.ActivityMainBinding
import com.wordnote.app.data.Word
import com.wordnote.app.data.WordGroup
import com.wordnote.app.ui.adapter.WordAdapter
import com.wordnote.app.ui.adapter.GroupManagementAdapter
import com.wordnote.app.util.UpdateChecker
import com.wordnote.app.util.compatOverridePendingTransition
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WordViewModel
    private lateinit var wordAdapter: WordAdapter
    private lateinit var wordRecyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var inputEditText: TextInputEditText
    private lateinit var addButton: FloatingActionButton
    private lateinit var statsTextView: TextView
    private lateinit var searchEditText: EditText
    private lateinit var globalSearchToggle: ImageView
    private lateinit var moreButton: ImageView
    private lateinit var settingsButton: ImageView
    private lateinit var dateGroupingButton: ImageView

    private lateinit var tabContainer: LinearLayout

    companion object {
        private const val VIEW_MODE_FLAT = 0
        private const val VIEW_MODE_TODAY = 1
        private const val VIEW_MODE_GROUPED = 2
    }

    private var selectedCategoryId: Long? = null
    private var selectedCategoryName: String? = null
    private var selectedTab: TextView? = null
    private var categoriesList: List<Category> = emptyList()
    private var groupsList: List<WordGroup> = emptyList()
    private var updateDialogShown = false
    private data class ScrollState(val position: Int, val offset: Int)
    private val scrollPositions = mutableMapOf<Long, ScrollState>()
    private var pendingScrollRestore: ScrollState? = null
    private val scrollHandler = Handler(Looper.getMainLooper())

    // Save scroll state for dark mode switch
    private var savedScrollPosition = 0
    private var savedScrollOffset = 0
    private var isRestoringScrollState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WordViewModel::class.java]

        initViews()
        setupRecyclerView()
        setupSwipeGesture()
        setupInput()
        setupSearch()
        setupSearchAnimation()
        setupSearchSuggestions()
        observeData()
        checkForUpdateOnStartup()

        // Handle navigation to specific category
        handleNavigationIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNavigationIntent(intent)
    }

    private fun handleNavigationIntent(intent: Intent) {
        val categoryId = intent.getLongExtra("navigate_to_category", -1L)
        if (categoryId != -1L) {
            // Wait for categories to load, then select the target
            viewModel.allCategories.observe(this) { categories ->
                val targetCategory = categories.find { it.id == categoryId }
                if (targetCategory != null) {
                    val idx = categories.indexOf(targetCategory)
                    if (idx >= 0) {
                        val tab = tabContainer.getChildAt(idx) as? TextView
                        if (tab != null) {
                            selectTab(tab, targetCategory.id)
                        }
                    }
                }
                // Remove the observer after handling
                viewModel.allCategories.removeObservers(this)
            }
        }
    }

    private fun checkForUpdate() {
        lifecycleScope.launch {
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
        lifecycleScope.launch {
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

        lifecycleScope.launch {
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
        wordRecyclerView = binding.wordRecyclerView
        emptyView = binding.emptyView
        inputEditText = binding.inputEditText
        addButton = binding.addButton
        statsTextView = binding.statsTextView
        searchEditText = binding.searchEditText
        globalSearchToggle = binding.globalSearchToggle
        tabContainer = binding.tabContainer
        moreButton = binding.moreButton
        settingsButton = binding.settingsButton
        dateGroupingButton = binding.dateGroupingButton

        binding.quickQuizCard.setOnClickListener {
            startActivity(Intent(this, QuizSetupActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.dictionaryButton.setOnClickListener {
            startActivity(Intent(this, DictionaryActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        moreButton.setOnClickListener {
            startActivity(Intent(this, MoreActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            compatOverridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        dateGroupingButton.setOnClickListener {
            val prefs = getSharedPreferences("settings", MODE_PRIVATE)
            val currentMode = prefs.getInt("view_mode", VIEW_MODE_FLAT)
            val nextMode = when (currentMode) {
                VIEW_MODE_FLAT -> VIEW_MODE_TODAY
                VIEW_MODE_TODAY -> VIEW_MODE_GROUPED
                VIEW_MODE_GROUPED -> VIEW_MODE_FLAT
                else -> VIEW_MODE_FLAT
            }
            prefs.edit().putInt("view_mode", nextMode).apply()
            applyViewMode(nextMode)
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
            },
            onBatchAppend = { word ->
                showBatchAppendSheet(word)
            }
        )

        wordRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = wordAdapter
        }

        setupSwipeToDelete()

        // Restore view mode from SharedPreferences
        val savedViewMode = getSharedPreferences("settings", MODE_PRIVATE)
            .getInt("view_mode", VIEW_MODE_FLAT)
        applyViewMode(savedViewMode)

        // Custom scrollbar indicator
        val scrollbarThumb = View(this).apply {
            layoutParams = FrameLayout.LayoutParams(dpToPx(4), 0).apply {
                gravity = android.view.Gravity.END or android.view.Gravity.TOP
                marginEnd = dpToPx(2)
            }
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#60000000"))
                cornerRadius = 2f * resources.displayMetrics.density
            }
            alpha = 0f
        }
        (wordRecyclerView.parent as FrameLayout).addView(scrollbarThumb)

        var scrollBarHideRunnable: Runnable? = null
        val hideScrollbar = Runnable {
            scrollbarThumb.animate().alpha(0f).setDuration(300).start()
        }

        wordRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val lm = rv.layoutManager as? LinearLayoutManager ?: return
                val totalItemCount = lm.itemCount
                if (totalItemCount <= 0) {
                    scrollbarThumb.alpha = 0f
                    return
                }

                val firstVisible = lm.findFirstVisibleItemPosition()
                val lastVisible = lm.findLastVisibleItemPosition()
                val visibleCount = (lastVisible - firstVisible + 1).coerceAtLeast(1)

                if (visibleCount >= totalItemCount) {
                    scrollbarThumb.alpha = 0f
                    return
                }

                val rvHeight = rv.height
                val thumbHeight = (visibleCount.toFloat() / totalItemCount * rvHeight).toInt()
                    .coerceAtLeast(dpToPx(32))
                    .coerceAtMost(rvHeight)

                val maxFirstVisible = totalItemCount - visibleCount
                val scrollRatio = if (maxFirstVisible > 0) firstVisible.toFloat() / maxFirstVisible else 0f
                val thumbTop = (scrollRatio * (rvHeight - thumbHeight)).toInt()

                val lp = scrollbarThumb.layoutParams as FrameLayout.LayoutParams
                lp.height = thumbHeight
                lp.topMargin = thumbTop
                scrollbarThumb.layoutParams = lp

                scrollbarThumb.animate().alpha(1f).setDuration(150).start()

                scrollBarHideRunnable?.let { scrollHandler.removeCallbacks(it) }
                scrollBarHideRunnable = hideScrollbar
                scrollHandler.postDelayed(hideScrollbar, 1500)
            }
        })
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
        val selectionBar = binding.selectionBar
        val selectionCountText = binding.selectionCountText
        val deleteSelectedButton = binding.deleteSelectedButton
        val copySelectedButton = binding.copySelectedButton
        val groupSelectedButton = binding.groupSelectedButton
        val cancelButton = binding.cancelSelectionButton

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

        groupSelectedButton.setOnClickListener {
            if (selectedIds.isNotEmpty()) {
                showBatchGroupDialog(selectedIds)
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

    private fun showBatchGroupDialog(selectedWordIds: Set<Long>) {
        val groups = groupsList
        val groupNames = groups.map { it.name }.toTypedArray()

        if (groups.isEmpty()) {
            // No groups exist, prompt to create one
            showCreateGroupForBatchDialog(selectedWordIds)
            return
        }

        val items = arrayOf(*groupNames, "新建分组", "移除分组")
        MaterialAlertDialogBuilder(this)
            .setTitle("选择分组（${selectedWordIds.size} 个单词）")
            .setItems(items) { _, which ->
                if (which < groups.size) {
                    // Assign to existing group
                    val group = groups[which]
                    viewModel.assignWordsToGroup(selectedWordIds.toList(), group.id)
                    wordAdapter.exitSelectionMode()
                    Toast.makeText(this, "已添加到「${group.name}」", Toast.LENGTH_SHORT).show()
                } else if (which == groups.size) {
                    // Create new group
                    showCreateGroupForBatchDialog(selectedWordIds)
                } else {
                    // Remove from group
                    viewModel.assignWordsToGroup(selectedWordIds.toList(), null)
                    wordAdapter.exitSelectionMode()
                    Toast.makeText(this, "已移除分组", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showCreateGroupForBatchDialog(wordIds: Set<Long>) {
        val input = EditText(this).apply {
            hint = "分组名称"
            setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("新建分组")
            .setView(input)
            .setPositiveButton("创建并添加") { _, _ ->
                val groupName = input.text.toString().trim()
                if (groupName.isNotBlank()) {
                    viewModel.createGroup(groupName)
                    // Observe groups to get the new group ID, then assign
                    val observer = object : androidx.lifecycle.Observer<List<WordGroup>> {
                        override fun onChanged(groups: List<WordGroup>) {
                            val newGroup = groups.find { it.name == groupName }
                            if (newGroup != null) {
                                viewModel.assignWordsToGroup(wordIds.toList(), newGroup.id)
                                wordAdapter.exitSelectionMode()
                                Toast.makeText(this@MainActivity, "已添加到「$groupName」", Toast.LENGTH_SHORT).show()
                                viewModel.allGroups.removeObserver(this)
                            }
                        }
                    }
                    viewModel.allGroups.observe(this, observer)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showGroupManagementDialog() {
        val dialog = BottomSheetDialog(this, R.style.Theme_WordNoteApp_BottomSheet)

        // Create content programmatically
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }

        // Title
        val title = TextView(this).apply {
            text = "词语分组管理"
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(getColor(R.color.text_primary))
            setPadding(0, 0, 0, dpToPx(16))
        }
        container.addView(title)

        // Group list
        val groupRecyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1f
            )
        }
        container.addView(groupRecyclerView)

        // Add group button
        val addBtn = MaterialButton(this).apply {
            text = "新建分组"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(12)
            }
        }
        container.addView(addBtn)

        // Setup adapter with drag-and-drop
        val groupItems = groupsList.toMutableList()
        var groupWordCounts = mutableMapOf<Long, Int>()

        // Load word counts
        lifecycleScope.launch {
            groupsList.forEach { group ->
                val count = viewModel.getWordCountForGroupSync(group.id)
                groupWordCounts[group.id] = count
            }
            groupRecyclerView.adapter?.notifyDataSetChanged()
        }

        val groupAdapter = GroupManagementAdapter(
            groups = groupItems,
            wordCounts = groupWordCounts,
            onDelete = { group ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("删除分组")
                    .setMessage("确定要删除「${group.name}」吗？\n分组中的单词不会被删除。")
                    .setPositiveButton("删除") { _, _ ->
                        viewModel.deleteGroup(group)
                        groupItems.remove(group)
                        groupRecyclerView.adapter?.notifyDataSetChanged()
                        Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        )
        groupRecyclerView.adapter = groupAdapter

        // Drag and drop
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                java.util.Collections.swap(groupItems, fromPos, toPos)
                groupAdapter.notifyItemMoved(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // Save new order when drag ends
                viewModel.updateGroupSortOrder(groupItems)
            }

            override fun isLongPressDragEnabled() = true
        })
        itemTouchHelper.attachToRecyclerView(groupRecyclerView)
        groupAdapter.onDragStart = { viewHolder: RecyclerView.ViewHolder ->
            itemTouchHelper.startDrag(viewHolder)
        }

        addBtn.setOnClickListener {
            val input = EditText(this).apply {
                hint = "分组名称"
                setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12))
            }
            MaterialAlertDialogBuilder(this)
                .setTitle("新建分组")
                .setView(input)
                .setPositiveButton("创建") { _, _ ->
                    val name = input.text.toString().trim()
                    if (name.isNotBlank()) {
                        viewModel.createGroup(name)
                        // Add to local list and refresh
                        groupItems.add(WordGroup(name = name, sortOrder = groupItems.size))
                        groupRecyclerView.adapter?.notifyItemInserted(groupItems.size - 1)
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        }

        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = (resources.displayMetrics.heightPixels * 0.7).toInt()

        dialog.setContentView(container)
        dialog.show()
    }

    private fun setupTabs(categories: List<Category>) {
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

    private fun applyViewMode(mode: Int) {
        val isGrouped = mode == VIEW_MODE_GROUPED
        val isTodayOnly = mode == VIEW_MODE_TODAY
        wordAdapter.setDateGroupingMode(isGrouped)
        wordAdapter.setTodayOnlyMode(isTodayOnly)
        viewModel.setTodayOnly(isTodayOnly)
        updateDateGroupingButtonIcon(mode)

        val modeName = when (mode) {
            VIEW_MODE_FLAT -> "正常顺序"
            VIEW_MODE_TODAY -> "仅当日"
            VIEW_MODE_GROUPED -> "折叠"
            else -> ""
        }
        Toast.makeText(this, "显示模式：$modeName", Toast.LENGTH_SHORT).show()
    }

    private fun updateDateGroupingButtonIcon(mode: Int) {
        when (mode) {
            VIEW_MODE_FLAT -> {
                dateGroupingButton.alpha = 0.6f
                dateGroupingButton.contentDescription = "正常顺序"
            }
            VIEW_MODE_TODAY -> {
                dateGroupingButton.alpha = 0.8f
                dateGroupingButton.contentDescription = "仅当日"
            }
            VIEW_MODE_GROUPED -> {
                dateGroupingButton.alpha = 1.0f
                dateGroupingButton.contentDescription = "折叠"
            }
        }
    }

    private fun selectTab(tab: TextView, categoryId: Long?) {
        // Save current scroll position + offset before switching
        selectedCategoryId?.let { currentId ->
            val lm = wordRecyclerView.layoutManager as? LinearLayoutManager
            if (lm != null) {
                val pos = lm.findFirstVisibleItemPosition()
                if (pos != RecyclerView.NO_POSITION) {
                    val view = lm.findViewByPosition(pos)
                    val offset = view?.top ?: 0
                    scrollPositions[currentId] = ScrollState(pos, offset)
                }
            }
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
        wordAdapter.setCurrentCategoryName(selectedCategoryName)

        updateInputMode()

        pendingScrollRestore = scrollPositions[categoryId]
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

        // Check for same-category duplicates
        lifecycleScope.launch {
            try {
                val catId = selectedCategoryId ?: return@launch
                val sameCatWords = viewModel.findSameCategoryWords(word, catId)
                if (sameCatWords.isNotEmpty()) {
                    val currentCatName = viewModel.getCategoryById(catId)?.name
                    if (currentCatName == "意思相近的单词") {
                        Toast.makeText(this@MainActivity, "该单词已存在于当前分类，仍可添加", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "该单词已存在于当前分类，无法重复添加", Toast.LENGTH_LONG).show()
                        return@launch
                    }
                }

                val wordEntity = Word(
                    word = word,
                    meaning = meaning,
                    categoryId = selectedCategoryId
                )
                viewModel.insertWord(wordEntity, meaningTexts = meaningTexts)
                inputEditText.text?.clear()
                Toast.makeText(this@MainActivity, "已添加", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "添加失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
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
        var duplicateCount = 0
        val catId = selectedCategoryId ?: return
        val isSimilarCategory = viewModel.getCategoryById(catId)?.name == "意思相近的单词"

        lifecycleScope.launch {
            lines.forEach { line ->
                val trimmedLine = line.trim()
                val parts = trimmedLine.split("\\s+".toRegex(), limit = 2)

                if (parts.size < 2) {
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
                    // Check same-category duplicate
                    val sameCatWords = viewModel.findSameCategoryWords(word, catId)
                    if (sameCatWords.isNotEmpty() && !isSimilarCategory) {
                        duplicateCount++
                        return@forEach
                    }
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
                Toast.makeText(this@MainActivity, "没有有效的单词可添加", Toast.LENGTH_SHORT).show()
                return@launch
            }

            viewModel.insertBatchWords(wordsToAdd)
            inputEditText.text?.clear()

            val message = buildString {
                append("已添加 ${wordsToAdd.size} 个单词")
                val parts = mutableListOf<String>()
                if (skippedCount > 0) parts.add("跳过 $skippedCount 个无效行")
                if (duplicateCount > 0) parts.add("$duplicateCount 个重复单词")
                if (parts.isNotEmpty()) append("（${parts.joinToString("，")}）")
            }
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
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
        groupNames.add("管理分组")
        val groupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupNames)
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupSpinner.adapter = groupAdapter
        word.groupId?.let { gId ->
            val idx = groupsList.indexOfFirst { it.id == gId } + 1
            if (idx > 0 && idx < groupNames.size - 2) groupSpinner.setSelection(idx)
        }

        groupSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == groupNames.size - 2 && groupNames[position] == "+ 新建分组") {
                    showCreateGroupDialogInSheet(dialog, groupSpinner, groupNames)
                } else if (position == groupNames.size - 1 && groupNames[position] == "管理分组") {
                    dialog.dismiss()
                    showGroupManagementDialog()
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

    private fun showBatchAppendSheet(lastWord: Word) {
        val dialog = BottomSheetDialog(this, R.style.Theme_WordNoteApp_BottomSheet)
        val sheetView = layoutInflater.inflate(R.layout.sheet_edit_word, null)

        val sheetTitle = sheetView.findViewById<TextView>(R.id.sheetTitle)
        sheetTitle.text = "追加到分组"

        val wordInput = sheetView.findViewById<TextInputEditText>(R.id.wordInput)
        val meaningInput = sheetView.findViewById<TextInputEditText>(R.id.meaningInput)
        val categorySpinner = sheetView.findViewById<Spinner>(R.id.categorySpinner)
        val groupSpinner = sheetView.findViewById<Spinner>(R.id.groupSpinner)
        val saveButton = sheetView.findViewById<MaterialButton>(R.id.saveButton)

        // Hide category and group spinners for batch append (keep same category/group)
        categorySpinner.visibility = View.GONE
        sheetView.findViewById<TextView>(R.id.sheetTitle).parent?.let { parent ->
            // Find the "分类" label and hide it too
        }
        // Hide the "分类" label
        val categoryLabel = sheetView.findViewById<TextView>(R.id.sheetTitle)
        // Actually, let's just hide the spinners and their labels
        categorySpinner.visibility = View.GONE
        groupSpinner.visibility = View.GONE

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

            val effectiveBatchId = lastWord.batchId ?: System.currentTimeMillis()
            if (lastWord.batchId == null) {
                viewModel.setWordBatchId(lastWord.id, effectiveBatchId)
            }
            val wordEntity = Word(
                word = newWord,
                meaning = newMeaning,
                categoryId = lastWord.categoryId,
                batchId = effectiveBatchId,
                createdAt = lastWord.createdAt
            )

            viewModel.insertWord(wordEntity)
            Toast.makeText(this, "已追加到分组", Toast.LENGTH_SHORT).show()
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

        globalSearchToggle.setOnClickListener {
            viewModel.toggleGlobalSearch()
        }
    }

    private fun setupSearchAnimation() {
        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.animate()
                    .scaleX(1.02f)
                    .scaleY(1.02f)
                    .setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            } else {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        }
    }

    private fun highlightSearchResult(text: String, query: String): SpannableString {
        val spannable = SpannableString(text)
        if (query.isEmpty()) return spannable

        val lowerText = text.lowercase()
        val lowerQuery = query.lowercase()
        var startIndex = lowerText.indexOf(lowerQuery)

        while (startIndex >= 0) {
            val endIndex = startIndex + query.length
            spannable.setSpan(
                BackgroundColorSpan(Color.parseColor("#FFFF00")),
                startIndex, endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            startIndex = lowerText.indexOf(lowerQuery, endIndex)
        }

        return spannable
    }

    private fun setupSearchSuggestions() {
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s?.toString() ?: ""
                if (query.length >= 2) {
                    showSearchSuggestions(query)
                } else {
                    hideSearchSuggestions()
                }
            }
        })
    }

    private fun showSearchSuggestions(query: String) {
        // TODO: Show search suggestions dropdown
    }

    private fun hideSearchSuggestions() {
        // TODO: Hide search suggestions dropdown
    }

    private fun animateSearchResults() {
        val controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
        wordRecyclerView.layoutAnimation = controller
        wordRecyclerView.scheduleLayoutAnimation()
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

        viewModel.isGlobalSearch.observe(this) { isGlobal ->
            wordAdapter.setGlobalSearchMode(isGlobal)
            if (isGlobal) {
                globalSearchToggle.alpha = 1.0f
                globalSearchToggle.setColorFilter(Color.WHITE)
                globalSearchToggle.background = android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.OVAL
                    setColor(getColor(R.color.primary))
                }
                globalSearchToggle.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
            } else {
                globalSearchToggle.alpha = 0.4f
                globalSearchToggle.clearColorFilter()
                // Restore ripple background via TypedValue
                val typedValue = android.util.TypedValue()
                theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true)
                globalSearchToggle.setBackgroundResource(typedValue.resourceId)
                globalSearchToggle.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6))
            }
        }

        viewModel.allGroups.observe(this) { groups ->
            groupsList = groups
            wordAdapter.setGroups(groups)
        }

        viewModel.filteredWords.observe(this) { words ->
            val forceRefresh = viewModel.pendingCategorySwitch
            if (forceRefresh) viewModel.clearPendingCategorySwitch()
            wordAdapter.submitWordList(words, forceRefresh)
            val currentQuery = viewModel.searchQuery.value ?: ""
            if (currentQuery.isNotBlank() && words.isNotEmpty()) {
                animateSearchResults()
            }
            emptyView.visibility = if (words.isEmpty()) View.VISIBLE else View.GONE
            wordRecyclerView.visibility = if (words.isEmpty()) View.GONE else View.VISIBLE

            // Restore scroll state after dark mode switch
            if (isRestoringScrollState) {
                isRestoringScrollState = false
                wordRecyclerView.post {
                    val lm = wordRecyclerView.layoutManager as? LinearLayoutManager ?: return@post
                    lm.scrollToPositionWithOffset(savedScrollPosition, savedScrollOffset)
                }
            } else {
                pendingScrollRestore?.let { state ->
                    pendingScrollRestore = null
                    wordRecyclerView.post {
                        val lm = wordRecyclerView.layoutManager as? LinearLayoutManager ?: return@post
                        if (state.position > 0) {
                            lm.scrollToPositionWithOffset(state.position, state.offset)
                        } else {
                            lm.scrollToPositionWithOffset(0, 0)
                        }
                    }
                }
            }
        }

        viewModel.allWords.observe(this) { words ->
            val reviewCount = viewModel.reviewCount.value ?: 0
            statsTextView.text = "共 ${words.size} 个 · 待复习 $reviewCount 个"
        }

        viewModel.reviewCount.observe(this) { count ->
            val total = viewModel.allWords.value?.size ?: 0
            statsTextView.text = "共 $total 个 · 待复习 $count 个"
            binding.reviewCountText.text = if (count > 0) {
                "有 $count 个单词待复习"
            } else {
                "暂无待复习单词"
            }
        }
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                if (viewHolder !is WordAdapter.WordViewHolder) return 0
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val listItem = wordAdapter.currentList[position]
                if (listItem is com.wordnote.app.ui.adapter.ListItem.WordItem) {
                    showDeleteConfirmation(listItem.word) {
                        wordAdapter.notifyItemChanged(position)
                    }
                } else {
                    wordAdapter.notifyItemChanged(position)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (dX < 0) {
                    val itemView = viewHolder.itemView
                    val density = resources.displayMetrics.density
                    val cornerRadius = 12f * density

                    c.save()
                    c.clipRect(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )

                    // Draw red background
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.parseColor("#FF5252")
                    }
                    val background = RectF(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )
                    c.drawRoundRect(background, cornerRadius, cornerRadius, paint)

                    // Draw delete icon
                    val icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete)
                    icon?.setTint(Color.WHITE)
                    icon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + iconMargin
                        val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
                        it.setBounds(iconLeft, iconTop, iconLeft + it.intrinsicWidth, iconTop + it.intrinsicHeight)
                        it.draw(c)
                    }

                    c.restore()
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(wordRecyclerView)
    }

    private fun showDeleteConfirmation(word: Word, onCancel: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setTitle("删除单词")
            .setMessage("确定要删除 \"${word.word}\" 吗？")
            .setPositiveButton("删除") { _, _ ->
                animateDeleteItem(word)
            }
            .setNegativeButton("取消") { _, _ ->
                onCancel()
            }
            .setOnCancelListener {
                onCancel()
            }
            .show()
    }

    private fun animateDeleteItem(word: Word) {
        viewModel.deleteWord(word)
        Toast.makeText(this, "已移入回收站", Toast.LENGTH_SHORT).show()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save scroll position
        val lm = wordRecyclerView.layoutManager as? LinearLayoutManager
        if (lm != null) {
            val pos = lm.findFirstVisibleItemPosition()
            if (pos != RecyclerView.NO_POSITION) {
                val view = lm.findViewByPosition(pos)
                val offset = view?.top ?: 0
                outState.putInt("scroll_position", pos)
                outState.putInt("scroll_offset", offset)
            }
        }
        // Save selected category
        outState.putLong("selected_category_id", selectedCategoryId ?: -1L)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore scroll position
        savedScrollPosition = savedInstanceState.getInt("scroll_position", 0)
        savedScrollOffset = savedInstanceState.getInt("scroll_offset", 0)
        isRestoringScrollState = true

        // Restore selected category
        val catId = savedInstanceState.getLong("selected_category_id", -1L)
        if (catId != -1L) {
            selectedCategoryId = catId
            // Update ViewModel with restored category
            viewModel.selectCategory(catId)
        }
    }
}
