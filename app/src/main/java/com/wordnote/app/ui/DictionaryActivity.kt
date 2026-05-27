package com.wordnote.app.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexboxLayout
import com.wordnote.app.R
import com.wordnote.app.data.DictionaryDatabase
import com.wordnote.app.databinding.ActivityDictionaryBinding
import com.wordnote.app.util.compatOverridePendingTransition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class DictionaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDictionaryBinding
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
    private lateinit var resultPos: TextView
    private lateinit var examplesContainer: LinearLayout
    private lateinit var examplesList: LinearLayout
    private lateinit var selectDbButton: com.google.android.material.button.MaterialButton
    private lateinit var searchModeButton: TextView
    private lateinit var searchHistoryContainer: LinearLayout
    private lateinit var historyChipContainer: FlexboxLayout
    private lateinit var clearHistoryButton: TextView

    private var isChineseMode = false
    private val MAX_HISTORY = 10

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
        binding = ActivityDictionaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dictDb = DictionaryDatabase(this)
        initViews()
        setupSearch()
        tryOpenSavedDb()
    }

    private fun initViews() {
        searchEditText = binding.dictSearchEditText
        loadingView = binding.loadingView
        hintView = binding.hintView
        noDatabaseView = binding.noDatabaseView
        noResultView = binding.noResultView
        noResultText = binding.noResultText
        resultScrollView = binding.resultScrollView
        resultWord = binding.resultWord
        resultPhonetic = binding.resultPhonetic
        resultTranslation = binding.resultTranslation
        tagsContainer = binding.tagsContainer
        resultPos = binding.resultPos
        examplesContainer = binding.examplesContainer
        examplesList = binding.examplesList
        selectDbButton = binding.selectDbButton
        searchModeButton = binding.searchModeButton
        searchHistoryContainer = binding.searchHistoryContainer
        historyChipContainer = binding.historyChipContainer
        clearHistoryButton = binding.clearHistoryButton

        updateSearchModeUI()

        searchModeButton.setOnClickListener {
            isChineseMode = !isChineseMode
            updateSearchModeUI()
            showHistory()
        }

        clearHistoryButton.setOnClickListener {
            clearHistory()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                showHistory()
            }
        }

        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (s.isNullOrEmpty()) {
                    showHistory()
                    // Reset to hint state
                    hintView.visibility = View.VISIBLE
                    noDatabaseView.visibility = View.GONE
                    resultScrollView.visibility = View.GONE
                    noResultView.visibility = View.GONE
                    examplesContainer?.visibility = View.GONE
                }
            }
        })

        binding.backButton.setOnClickListener {
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

        saveSearchHistory(query)
        searchHistoryContainer.visibility = View.GONE

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

        // Hide single-word result area
        resultWord.visibility = android.view.View.GONE
        resultPhonetic.visibility = android.view.View.GONE
        resultTranslation.visibility = android.view.View.GONE
        resultPos.visibility = android.view.View.GONE
        examplesContainer.visibility = android.view.View.GONE
        tagsContainer.visibility = android.view.View.GONE

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

        val container = binding.chineseResultContainer
        container.removeAllViews()
        container.visibility = android.view.View.VISIBLE

        // Header
        addSectionHeader(container, "共找到 ${results.size} 个结果")

        // Exact matches
        if (exactMatches.isNotEmpty()) {
            addSectionLabel(container, "直接匹配")
            exactMatches.forEach { entry ->
                addWordCard(container, entry, query)
            }
        }

        // Starts with
        if (startsWith.isNotEmpty()) {
            addSectionLabel(container, "以此开头")
            startsWith.forEach { entry ->
                addWordCard(container, entry, query)
            }
        }

        // Contains
        if (contains.isNotEmpty()) {
            addSectionLabel(container, "包含 \"$query\"")
            contains.take(15).forEach { entry ->
                addWordCard(container, entry, query)
            }
            if (contains.size > 15) {
                addSectionFooter(container, "... 还有 ${contains.size - 15} 个结果")
            }
        }
    }

    private fun addSectionHeader(container: LinearLayout, text: String) {
        val tv = TextView(this).apply {
            this.text = text
            setTextColor(resources.getColor(R.color.text_secondary, null))
            textSize = 13f
            setPadding(dpToPx(4), dpToPx(12), dpToPx(4), dpToPx(8))
        }
        container.addView(tv)
    }

    private fun addSectionLabel(container: LinearLayout, text: String) {
        val tv = TextView(this).apply {
            this.text = text
            setTextColor(resources.getColor(R.color.primary, null))
            textSize = 12f
            setPadding(dpToPx(4), dpToPx(10), dpToPx(4), dpToPx(4))
        }
        container.addView(tv)
    }

    private fun addSectionFooter(container: LinearLayout, text: String) {
        val tv = TextView(this).apply {
            this.text = text
            setTextColor(resources.getColor(R.color.text_hint, null))
            textSize = 12f
            setPadding(dpToPx(4), dpToPx(6), dpToPx(4), dpToPx(4))
        }
        container.addView(tv)
    }

    private fun addWordCard(container: LinearLayout, entry: com.wordnote.app.data.DictEntry, query: String) {
        val card = com.google.android.material.card.MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dpToPx(3), 0, dpToPx(3))
            }
            setCardBackgroundColor(resources.getColor(R.color.card_background, null))
            cardElevation = 0f
            strokeWidth = 0
            radius = 12f * resources.displayMetrics.density
            useCompatPadding = false
        }

        val inner = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(14), dpToPx(10), dpToPx(14), dpToPx(10))
        }

        // Word
        val wordTv = TextView(this).apply {
            this.text = entry.word
            setTextColor(resources.getColor(R.color.text_primary, null))
            textSize = 16f
            paint.isFakeBoldText = true
        }
        inner.addView(wordTv)

        // Translation - highlight the query
        val trans = entry.translation ?: ""
        val transTv = TextView(this).apply {
            this.text = trans
            setTextColor(resources.getColor(R.color.text_secondary, null))
            textSize = 13f
            setPadding(0, dpToPx(2), 0, 0)
            maxLines = 2
        }
        inner.addView(transTv)

        card.addView(inner)
        container.addView(card)
    }

    private fun showResult(entry: com.wordnote.app.data.DictEntry) {
        hintView.visibility = android.view.View.GONE
        noDatabaseView.visibility = android.view.View.GONE
        noResultView.visibility = android.view.View.GONE
        resultScrollView.visibility = android.view.View.VISIBLE

        // Hide Chinese results container
        val chineseContainer = binding.chineseResultContainer
        chineseContainer.visibility = android.view.View.GONE
        chineseContainer.removeAllViews()

        // Show single-word result area
        resultWord.visibility = android.view.View.VISIBLE
        resultPhonetic.visibility = android.view.View.VISIBLE
        resultTranslation.visibility = android.view.View.VISIBLE

        resultWord.text = entry.word
        resultPhonetic.text = entry.phonetic?.let { "/$it/" } ?: ""

        // Show POS
        val posMap = mapOf(
            "n" to "noun", "v" to "verb", "adj" to "adjective",
            "adv" to "adverb", "prep" to "preposition", "conj" to "conjunction",
            "pron" to "pronoun", "int" to "interjection", "abbr" to "abbreviation"
        )
        if (!entry.pos.isNullOrBlank()) {
            val posDisplay = entry.pos.split("/").mapNotNull { p ->
                posMap[p.trim().lowercase()]?.let { cn ->
                    when (cn) {
                        "noun" -> "名词"
                        "verb" -> "动词"
                        "adjective" -> "形容词"
                        "adverb" -> "副词"
                        "preposition" -> "介词"
                        "conjunction" -> "连词"
                        "pronoun" -> "代词"
                        "interjection" -> "感叹词"
                        "abbreviation" -> "缩写"
                        else -> p
                    }
                } ?: p
            }.joinToString(" / ")
            resultPos.text = posDisplay
            resultPos.visibility = android.view.View.VISIBLE
        } else {
            resultPos.visibility = android.view.View.GONE
        }

        // Build translation with POS
        val translation = entry.translation ?: "无释义"
        resultTranslation.text = translation

        // Show loading state for examples
        examplesContainer.visibility = android.view.View.VISIBLE
        examplesList.removeAllViews()
        val loadingTv = TextView(this).apply {
            text = "正在加载例句..."
            setTextColor(resources.getColor(R.color.text_hint, null))
            textSize = 12f
            tag = "examples_loading"
        }
        examplesList.addView(loadingTv)

        // Build tags
        buildTags(entry)

        // Fetch examples from Free Dictionary API
        fetchExamples(entry.word)
    }

    private fun buildTags(entry: com.wordnote.app.data.DictEntry) {
        tagsContainer.removeAllViews()
        tagsContainer.visibility = android.view.View.GONE
        val tags = mutableListOf<Pair<String, Int>>() // text to color type

        if (entry.collins > 0) {
            tags.add("柯林斯 Lv.${entry.collins}" to 0)
        }
        if (entry.oxford > 0) {
            tags.add("牛津核心" to 0)
        }
        if (!entry.tag.isNullOrBlank()) {
            val examMap = mapOf(
                "cet4" to "四级", "cet6" to "六级", "zk" to "中考",
                "gk" to "高考", "ky" to "考研", "toefl" to "托福",
                "ielts" to "雅思", "gre" to "GRE"
            )
            entry.tag.split(" ").forEach { t ->
                examMap[t.lowercase()]?.let { tags.add(it to 1) }
            }
        }
        if (entry.bnc > 0) {
            tags.add("BNC #${entry.bnc}" to 2)
        }

        if (tags.isNotEmpty()) {
            tagsContainer.visibility = android.view.View.VISIBLE
            tags.forEach { (tagText, colorType) ->
                val (textColor, bgColor) = when (colorType) {
                    1 -> Pair(
                        resources.getColor(R.color.cat_hard, null),
                        Color.argb(20, 232, 99, 106)
                    )
                    2 -> Pair(
                        resources.getColor(R.color.cat_similar, null),
                        Color.argb(20, 92, 184, 122)
                    )
                    else -> Pair(
                        resources.getColor(R.color.primary, null),
                        resources.getColor(R.color.primary_light, null)
                    )
                }
                val tag = TextView(this).apply {
                    text = tagText
                    textSize = 11f
                    setTextColor(textColor)
                    setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4))
                    background = GradientDrawable().apply {
                        setColor(bgColor)
                        cornerRadius = 12f * resources.displayMetrics.density
                    }
                    val params = FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, dpToPx(6), dpToPx(4))
                    }
                    layoutParams = params
                }
                tagsContainer.addView(tag)
            }
        }
    }

    private fun fetchExamples(word: String) {
        lifecycleScope.launch {
            try {
                val examples = withContext(Dispatchers.IO) {
                    val url = URL("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.apply {
                        connectTimeout = 5000
                        readTimeout = 5000
                        setRequestProperty("Accept", "application/json")
                    }

                    if (conn.responseCode != 200) return@withContext emptyList()

                    val text = conn.inputStream.bufferedReader().readText()
                    parseExamples(text)
                }

                if (examples.isNotEmpty()) {
                    showExamples(examples)
                    // Fetch translations for each example
                    examples.forEachIndexed { index, (example, _) ->
                        fetchTranslation(index, example)
                    }
                } else {
                    showNoExamplesHint()
                }
            } catch (_: Exception) {
                showNoExamplesHint()
            }
        }
    }

    private fun showNoExamplesHint() {
        examplesList.removeAllViews()
        val hint = TextView(this).apply {
            text = "暂无例句"
            setTextColor(resources.getColor(R.color.text_hint, null))
            textSize = 12f
        }
        examplesList.addView(hint)
        examplesContainer.visibility = android.view.View.VISIBLE
        // Auto-hide after 2 seconds
        examplesList.postDelayed({
            examplesContainer.visibility = android.view.View.GONE
        }, 2000)
    }

    private fun parseExamples(json: String): List<Pair<String, String>> {
        val results = mutableListOf<Pair<String, String>>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val entry = arr.getJSONObject(i)
                val meanings = entry.getJSONArray("meanings")
                for (j in 0 until meanings.length()) {
                    val meaning = meanings.getJSONObject(j)
                    val definitions = meaning.getJSONArray("definitions")
                    for (k in 0 until definitions.length()) {
                        val def = definitions.optJSONObject(k) ?: continue
                        val example = def.optString("example", "")
                        if (example.isNotBlank()) {
                            val definition = def.optString("definition", "")
                            results.add(example to definition)
                            if (results.size >= 3) return results
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return results
    }

    private fun fetchTranslation(index: Int, englishText: String) {
        lifecycleScope.launch {
            try {
                val translated = withContext(Dispatchers.IO) {
                    val encoded = java.net.URLEncoder.encode(englishText, "UTF-8")
                    val url = URL("https://api.mymemory.translated.net/get?q=$encoded&langpair=en|zh-CN")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.apply {
                        connectTimeout = 5000
                        readTimeout = 5000
                        setRequestProperty("Accept", "application/json")
                    }
                    if (conn.responseCode != 200) return@withContext null
                    val text = conn.inputStream.bufferedReader().readText()
                    val json = org.json.JSONObject(text)
                    val responseData = json.optJSONObject("responseData")
                    responseData?.optString("translatedText")
                }

                if (!translated.isNullOrBlank() && translated != englishText) {
                    updateExampleTranslation(index, translated)
                }
            } catch (_: Exception) {}
        }
    }

    private fun updateExampleTranslation(index: Int, translation: String) {
        if (index >= examplesList.childCount) return
        val itemView = examplesList.getChildAt(index) as? LinearLayout ?: return

        // Check if translation view already exists
        if (itemView.childCount >= 3) return

        val transTv = TextView(this).apply {
            text = translation
            setTextColor(resources.getColor(R.color.text_secondary, null))
            textSize = 12f
            setPadding(0, dpToPx(3), 0, 0)
        }
        // Insert after the English example (before definition if exists)
        itemView.addView(transTv, if (itemView.childCount > 1) 1 else itemView.childCount)
    }

    private fun showExamples(examples: List<Pair<String, String>>) {
        examplesContainer.visibility = android.view.View.VISIBLE
        examplesList.removeAllViews()

        examples.forEachIndexed { index, (example, definition) ->
            val itemView = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dpToPx(14), dpToPx(10), dpToPx(14), dpToPx(10))
                background = GradientDrawable().apply {
                    setColor(resources.getColor(R.color.surface_input, null))
                    cornerRadius = 10f * resources.displayMetrics.density
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dpToPx(6)
                }
            }

            // Top row: example text + save button
            val topRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val exampleTv = TextView(this).apply {
                text = "\"$example\""
                setTextColor(resources.getColor(R.color.text_primary, null))
                textSize = 13f
                typeface = Typeface.DEFAULT
                setLineSpacing(dpToPx(2).toFloat(), 1f)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            topRow.addView(exampleTv)

            // Save button
            val saveBtn = TextView(this).apply {
                text = "记录"
                setTextColor(resources.getColor(R.color.primary, null))
                textSize = 12f
                setPadding(dpToPx(8), dpToPx(4), dpToPx(4), dpToPx(4))
                setOnClickListener {
                    val intent = android.content.Intent(this@DictionaryActivity, SentenceEditActivity::class.java)
                    intent.putExtra(SentenceEditActivity.EXTRA_SENTENCE_TEXT, example)
                    intent.putExtra(SentenceEditActivity.EXTRA_SENTENCE_TRANSLATION, definition)
                    startActivity(intent)
                }
            }
            topRow.addView(saveBtn)

            itemView.addView(topRow)

            // Definition hint (English)
            if (definition.isNotBlank()) {
                val defTv = TextView(this).apply {
                    text = definition
                    setTextColor(resources.getColor(R.color.text_hint, null))
                    textSize = 11f
                    setPadding(0, dpToPx(4), 0, 0)
                    maxLines = 2
                }
                itemView.addView(defTv)
            }

            examplesList.addView(itemView)
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
        // Show history if available
        showHistory()
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

    // Search history methods
    private fun getHistoryKey(): String = if (isChineseMode) "history_cn" else "history_en"

    private fun getSearchHistory(): MutableList<String> {
        val prefs = getSharedPreferences("dictionary_history", MODE_PRIVATE)
        val json = prefs.getString(getHistoryKey(), null) ?: return mutableListOf()
        return try {
            org.json.JSONArray(json).let { arr ->
                (0 until arr.length()).map { arr.getString(it) }.toMutableList()
            }
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    private fun saveSearchHistory(query: String) {
        val history = getSearchHistory()
        history.remove(query)
        history.add(0, query)
        if (history.size > MAX_HISTORY) {
            history.subList(MAX_HISTORY, history.size).clear()
        }
        val prefs = getSharedPreferences("dictionary_history", MODE_PRIVATE)
        prefs.edit().putString(getHistoryKey(), org.json.JSONArray(history).toString()).apply()
    }

    private fun clearHistory() {
        val prefs = getSharedPreferences("dictionary_history", MODE_PRIVATE)
        prefs.edit().remove(getHistoryKey()).apply()
        searchHistoryContainer.visibility = View.GONE
    }

    private fun showHistory() {
        val history = getSearchHistory()
        if (history.isEmpty()) {
            searchHistoryContainer.visibility = View.GONE
            return
        }
        searchHistoryContainer.visibility = View.VISIBLE
        historyChipContainer.removeAllViews()

        history.forEach { item ->
            val chip = TextView(this).apply {
                text = item
                textSize = 13f
                setTextColor(resources.getColor(R.color.text_secondary, null))
                setPadding(dpToPx(12), dpToPx(6), dpToPx(12), dpToPx(6))
                val bg = GradientDrawable().apply {
                    setColor(resources.getColor(R.color.background, null))
                    cornerRadius = 16f * resources.displayMetrics.density
                    setStroke(dpToPx(1), resources.getColor(R.color.divider, null))
                }
                background = bg
                setOnClickListener {
                    searchEditText.setText(item)
                    searchEditText.setSelection(item.length)
                    doSearch()
                }
            }
            val params = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, dpToPx(6), dpToPx(6))
            }
            chip.layoutParams = params
            historyChipContainer.addView(chip)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        dictDb.close()
    }
}
