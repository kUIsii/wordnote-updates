package com.wordnote.app.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.wordnote.app.R
import com.wordnote.app.data.DictionaryDatabase
import com.wordnote.app.util.compatOverridePendingTransition

class DictionaryActivity : AppCompatActivity() {

    private lateinit var dictDb: DictionaryDatabase
    private lateinit var searchEditText: EditText
    private lateinit var loadingView: LinearLayout
    private lateinit var hintView: LinearLayout
    private lateinit var noDatabaseView: LinearLayout
    private lateinit var noResultView: LinearLayout
    private lateinit var noResultText: TextView
    private lateinit var resultScrollView: ScrollView
    private lateinit var resultWord: TextView
    private lateinit var resultPhonetic: TextView
    private lateinit var resultTranslation: TextView
    private lateinit var tagsContainer: com.google.android.flexbox.FlexboxLayout
    private lateinit var selectDbButton: com.google.android.material.button.MaterialButton
    private lateinit var searchModeButton: TextView

    private var isChineseMode = false

    private val selectFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            saveDbUri(it.toString())
            openDatabase(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        dictDb = DictionaryDatabase(this)
        initViews()
        setupSearch()
        tryOpenSavedDb()
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.dictSearchEditText)
        loadingView = findViewById(R.id.loadingView)
        hintView = findViewById(R.id.hintView)
        noDatabaseView = findViewById(R.id.noDatabaseView)
        noResultView = findViewById(R.id.noResultView)
        noResultText = findViewById(R.id.noResultText)
        resultScrollView = findViewById(R.id.resultScrollView)
        resultWord = findViewById(R.id.resultWord)
        resultPhonetic = findViewById(R.id.resultPhonetic)
        resultTranslation = findViewById(R.id.resultTranslation)
        tagsContainer = findViewById(R.id.tagsContainer)
        selectDbButton = findViewById(R.id.selectDbButton)
        searchModeButton = findViewById(R.id.searchModeButton)

        updateSearchModeUI()

        searchModeButton.setOnClickListener {
            isChineseMode = !isChineseMode
            updateSearchModeUI()
        }

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
            compatOverridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        selectDbButton.setOnClickListener {
            selectFileLauncher.launch(arrayOf("application/octet-stream", "*/*"))
        }
    }

    private fun setupSearch() {
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch()
                true
            } else false
        }
    }

    private fun updateSearchModeUI() {
        if (isChineseMode) {
            searchModeButton.text = "中→英"
            searchEditText.hint = "输入中文查询英文单词..."
        } else {
            searchModeButton.text = "英→中"
            searchEditText.hint = "输入英文单词..."
        }
    }

    private fun doSearch() {
        val query = searchEditText.text.toString().trim()
        if (query.isEmpty()) return

        if (!dictDb.isReady()) {
            noDatabaseView.visibility = android.view.View.VISIBLE
            resultScrollView.visibility = android.view.View.GONE
            noResultView.visibility = android.view.View.GONE
            return
        }

        showLoading(true)

        if (isChineseMode) {
            // Chinese to English search
            val results = dictDb.searchByChinese(query)
            showLoading(false)

            if (results.isNotEmpty()) {
                showChineseResults(query, results)
            } else {
                hintView.visibility = android.view.View.GONE
                noDatabaseView.visibility = android.view.View.GONE
                resultScrollView.visibility = android.view.View.GONE
                noResultView.visibility = android.view.View.VISIBLE
                noResultText.text = "未找到包含 \"$query\" 的单词"
            }
        } else {
            // English to Chinese search
            val result = dictDb.search(query)
            showLoading(false)

            if (result != null) {
                showResult(result)
            } else {
                hintView.visibility = android.view.View.GONE
                noDatabaseView.visibility = android.view.View.GONE
                resultScrollView.visibility = android.view.View.GONE
                noResultView.visibility = android.view.View.VISIBLE
                noResultText.text = "未找到 \"$query\""
            }
        }
    }

    private fun showChineseResults(query: String, results: List<com.wordnote.app.data.DictEntry>) {
        hintView.visibility = android.view.View.GONE
        noDatabaseView.visibility = android.view.View.GONE
        noResultView.visibility = android.view.View.GONE
        resultScrollView.visibility = android.view.View.VISIBLE

        // Group results by relevance tier
        val exactMatches = results.filter { entry ->
            val trans = entry.translation ?: ""
            trans == query || trans == "$query;" || trans == "；$query"
        }
        val startsWith = results.filter { entry ->
            val trans = entry.translation ?: ""
            (trans.startsWith(query) || trans.startsWith("$query;") || trans.startsWith("；$query")) && !exactMatches.contains(entry)
        }
        val contains = results.filter { entry -> !exactMatches.contains(entry) && !startsWith.contains(entry) }

        val sb = StringBuilder()

        if (exactMatches.isNotEmpty()) {
            sb.appendLine("直接匹配：")
            exactMatches.forEach { entry ->
                sb.appendLine("${entry.word}  ${entry.translation ?: ""}")
            }
            sb.appendLine()
        }

        if (startsWith.isNotEmpty()) {
            sb.appendLine("以此开头：")
            startsWith.forEach { entry ->
                sb.appendLine("${entry.word}  ${entry.translation ?: ""}")
            }
            sb.appendLine()
        }

        if (contains.isNotEmpty()) {
            sb.appendLine("包含 \"$query\"：")
            contains.take(15).forEach { entry ->
                sb.appendLine("${entry.word}  ${entry.translation ?: ""}")
            }
            if (contains.size > 15) {
                sb.appendLine("... 还有 ${contains.size - 15} 个")
            }
        }

        resultWord.text = "查询结果"
        resultPhonetic.text = "共找到 ${results.size} 个单词"
        resultTranslation.text = sb.toString().trimEnd()
        tagsContainer.visibility = android.view.View.GONE
    }

    private fun showResult(entry: com.wordnote.app.data.DictEntry) {
        hintView.visibility = android.view.View.GONE
        noDatabaseView.visibility = android.view.View.GONE
        noResultView.visibility = android.view.View.GONE
        resultScrollView.visibility = android.view.View.VISIBLE

        resultWord.text = entry.word
        resultPhonetic.text = entry.phonetic?.let { "/$it/" } ?: ""

        // Build translation with POS
        val translation = entry.translation ?: "无释义"
        resultTranslation.text = translation

        // Build tags
        tagsContainer.removeAllViews()
        tagsContainer.visibility = android.view.View.GONE
        val tags = mutableListOf<String>()

        if (entry.collins > 0) {
            tags.add("柯林斯 Lv.${entry.collins}")
        }
        if (entry.oxford > 0) {
            tags.add("牛津核心")
        }
        if (!entry.tag.isNullOrBlank()) {
            val examMap = mapOf(
                "cet4" to "四级", "cet6" to "六级", "zk" to "中考",
                "gk" to "高考", "ky" to "考研", "toefl" to "托福",
                "ielts" to "雅思", "gre" to "GRE"
            )
            entry.tag.split(" ").forEach { t ->
                examMap[t.lowercase()]?.let { tags.add(it) }
            }
        }
        if (entry.bnc > 0) {
            tags.add("BNC #${entry.bnc}")
        }

        if (tags.isNotEmpty()) {
            tagsContainer.visibility = android.view.View.VISIBLE
            tags.forEach { tagText ->
                val tag = TextView(this).apply {
                    text = tagText
                    textSize = 11f
                    setTextColor(resources.getColor(R.color.primary, null))
                    setPadding(dpToPx(8), dpToPx(3), dpToPx(8), dpToPx(3))
                    val bg = android.graphics.drawable.GradientDrawable().apply {
                        setColor(resources.getColor(R.color.primary_light, null))
                        cornerRadius = 12f * resources.displayMetrics.density
                    }
                    background = bg
                    val params = com.google.android.flexbox.FlexboxLayout.LayoutParams(
                        com.google.android.flexbox.FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        com.google.android.flexbox.FlexboxLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(dpToPx(6), 0, dpToPx(6), dpToPx(4))
                    }
                    layoutParams = params
                }
                tagsContainer.addView(tag)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        loadingView.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        if (show) {
            hintView.visibility = android.view.View.GONE
            noDatabaseView.visibility = android.view.View.GONE
            noResultView.visibility = android.view.View.GONE
            resultScrollView.visibility = android.view.View.GONE
        }
    }

    private fun tryOpenSavedDb() {
        val savedUri = getDbUri()
        if (savedUri != null) {
            try {
                openDatabase(android.net.Uri.parse(savedUri))
            } catch (_: Exception) {
                showNoDatabase()
            }
        } else {
            showNoDatabase()
        }
    }

    private fun openDatabase(uri: android.net.Uri) {
        if (dictDb.open(uri)) {
            noDatabaseView.visibility = android.view.View.GONE
            hintView.visibility = android.view.View.VISIBLE
        } else {
            showNoDatabase()
            Toast.makeText(this, "无法打开词典文件", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNoDatabase() {
        noDatabaseView.visibility = android.view.View.VISIBLE
        hintView.visibility = android.view.View.GONE
        resultScrollView.visibility = android.view.View.GONE
        noResultView.visibility = android.view.View.GONE
    }

    private fun saveDbUri(uri: String) {
        getSharedPreferences("dictionary", MODE_PRIVATE)
            .edit().putString("db_uri", uri).apply()
    }

    private fun getDbUri(): String? {
        return getSharedPreferences("dictionary", MODE_PRIVATE)
            .getString("db_uri", null)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        dictDb.close()
    }
}
