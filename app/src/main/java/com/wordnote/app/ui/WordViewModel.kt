package com.wordnote.app.ui

import android.app.Application
import androidx.lifecycle.*
import com.wordnote.app.WordNoteApplication
import com.wordnote.app.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class WordViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WordRepository = (application as WordNoteApplication).repository

    val allWords: LiveData<List<Word>> = repository.allWords
    val allCategories: LiveData<List<Category>> = repository.allCategories

    suspend fun getAllActiveWordsSync(): List<Word> = repository.getAllActiveWordsSync()
    val allTags: LiveData<List<Tag>> = repository.allTags
    val highlightedMeanings: LiveData<List<HighlightedMeaning>> = repository.getHighlightedMeanings()

    private val _selectedCategoryId = MutableLiveData<Long?>(null)
    val selectedCategoryId: LiveData<Long?> = _selectedCategoryId

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _isGlobalSearch = MutableLiveData(false)
    val isGlobalSearch: LiveData<Boolean> = _isGlobalSearch

    private val _isTodayOnly = MutableLiveData(false)
    val isTodayOnly: LiveData<Boolean> = _isTodayOnly

    fun toggleGlobalSearch() {
        _isGlobalSearch.value = !(_isGlobalSearch.value ?: false)
    }

    fun setTodayOnly(enabled: Boolean) {
        _isTodayOnly.value = enabled
    }

    // Cache: all words grouped by categoryId, built from allWords
    private var categoryWordsCache: Map<Long?, List<Word>> = emptyMap()

    // Words for the currently selected category (from cache)
    private val _categoryWords = MutableLiveData<List<Word>>(emptyList())

    val filteredWords: MediatorLiveData<List<Word>> = MediatorLiveData<List<Word>>().apply {
        var currentCategoryWords: List<Word> = emptyList()
        var currentAllWords: List<Word> = emptyList()
        var currentQuery: String = ""
        var isGlobal: Boolean = false
        var isTodayOnly: Boolean = false

        fun update() {
            // Empty query: always show current category (global toggle has no effect without search)
            // Non-empty query: use allWords when global, category words when not
            val sourceWords = if (currentQuery.isNotEmpty() && isGlobal) currentAllWords else currentCategoryWords
            val todayStart = com.wordnote.app.util.DateUtils.getStartOfDay(System.currentTimeMillis())
            val todayEnd = todayStart + 24 * 60 * 60 * 1000

            value = if (currentQuery.isEmpty()) {
                // Apply today-only filter when not searching
                if (isTodayOnly) {
                    sourceWords.filter { it.createdAt >= todayStart && it.createdAt < todayEnd }
                } else {
                    sourceWords
                }
            } else {
                // Search: no date filter, search across all
                val matchedBatchIds = sourceWords.filter { word ->
                    word.word.contains(currentQuery, ignoreCase = true) ||
                            word.meaning.contains(currentQuery, ignoreCase = true) ||
                            (word.note?.contains(currentQuery, ignoreCase = true) == true)
                }.mapNotNull { it.batchId }.toSet()

                sourceWords.filter { word ->
                    word.batchId in matchedBatchIds ||
                    word.word.contains(currentQuery, ignoreCase = true) ||
                    word.meaning.contains(currentQuery, ignoreCase = true) ||
                    (word.note?.contains(currentQuery, ignoreCase = true) == true)
                }
            }
        }

        addSource(_categoryWords) { words ->
            currentCategoryWords = words
            update()
        }

        addSource(allWords) { words ->
            currentAllWords = words
            // Rebuild cache when allWords changes
            categoryWordsCache = words.groupBy { it.categoryId }
            // Update current category view
            val catId = _selectedCategoryId.value
            _categoryWords.value = if (catId != null) categoryWordsCache[catId].orEmpty() else words
        }

        addSource(_searchQuery) { query ->
            currentQuery = query
            update()
        }

        addSource(_isGlobalSearch) { global ->
            isGlobal = global
            if (currentQuery.isNotEmpty()) update()
        }

        addSource(_isTodayOnly) { todayOnly ->
            isTodayOnly = todayOnly
            update()
        }
    }

    // Review
    val reviewCount: LiveData<Int> = repository.getReviewCount(System.currentTimeMillis())

    // Flag: observer should skip DiffUtil on next update (category switch)
    var pendingCategorySwitch = false
        private set

    fun clearPendingCategorySwitch() {
        pendingCategorySwitch = false
    }

    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
        pendingCategorySwitch = true
        _categoryWords.value = if (categoryId != null) {
            categoryWordsCache[categoryId].orEmpty()
        } else {
            allWords.value.orEmpty()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insertWord(word: Word, tags: List<String> = emptyList(), meaningTexts: List<String> = emptyList()) = viewModelScope.launch {
        val wordId = repository.insertWord(word)
        tags.forEach { tagName ->
            if (tagName.isNotBlank()) {
                val tag = repository.getTagByName(tagName.trim()) ?: Tag(name = tagName.trim())
                val tagId = if (tag.id == 0L) repository.insertTag(tag) else tag.id
                repository.addTagToWord(wordId, tagId)
            }
        }
        if (meaningTexts.isNotEmpty()) {
            val meanings = meaningTexts.filter { it.isNotBlank() }.map { text ->
                WordMeaning(wordId = wordId, meaningText = text.trim())
            }
            repository.insertMeanings(meanings)
        }
    }

    fun insertBatchWords(words: List<Word>) = viewModelScope.launch {
        repository.insertWords(words)
    }

    fun updateWord(word: Word, tags: List<String> = emptyList()) = viewModelScope.launch {
        repository.updateWord(word)
        repository.removeAllTagsFromWord(word.id)
        tags.forEach { tagName ->
            if (tagName.isNotBlank()) {
                val tag = repository.getTagByName(tagName.trim()) ?: Tag(name = tagName.trim())
                val tagId = if (tag.id == 0L) repository.insertTag(tag) else tag.id
                repository.addTagToWord(word.id, tagId)
            }
        }
    }

    fun deleteWord(word: Word) = viewModelScope.launch {
        repository.softDeleteWord(word.id)
    }

    fun deleteWordsByIds(wordIds: List<Long>) = viewModelScope.launch {
        wordIds.forEach { id ->
            repository.softDeleteWord(id)
        }
    }

    // Recycle bin operations
    val deletedWords: LiveData<List<Word>> = repository.deletedWords

    fun restoreWord(wordId: Long) = viewModelScope.launch {
        repository.restoreWord(wordId)
    }

    fun restoreWords(wordIds: List<Long>) = viewModelScope.launch {
        wordIds.forEach { id -> repository.restoreWord(id) }
    }

    fun permanentDelete(word: Word) = viewModelScope.launch {
        repository.deleteWord(word)
    }

    fun permanentDeleteWords(wordIds: List<Long>) = viewModelScope.launch {
        wordIds.forEach { id ->
            val word = repository.getWordById(id)
            word?.let { repository.deleteWord(it) }
        }
    }

    fun clearOldDeletedWords(daysToKeep: Int = 30) = viewModelScope.launch {
        val cutoff = System.currentTimeMillis() - daysToKeep * 24L * 60 * 60 * 1000
        repository.permanentDeleteOlderThan(cutoff)
    }

    fun getWordById(wordId: Long): LiveData<Word?> = liveData {
        emit(repository.getWordById(wordId))
    }

    suspend fun getWordByIdSync(wordId: Long): Word? = repository.getWordById(wordId)

    fun getTagsForWord(wordId: Long): LiveData<List<Tag>> = repository.getTagsForWord(wordId)

    // Review operations
    fun markAsForgotten(word: Word) = viewModelScope.launch {
        val currentTime = System.currentTimeMillis()
        // Review intervals: 1min, 5min, 30min, 2h, 12h, 1d, 3d, 7d, 15d, 30d
        val intervals = longArrayOf(
            1, 5, 30, 120, 720,
            1440, 4320, 10080, 21600, 43200
        )
        val intervalIndex = minOf(word.forgetCount, intervals.size - 1)
        val nextReviewAt = currentTime + TimeUnit.MINUTES.toMillis(intervals[intervalIndex])
        repository.markAsForgotten(word.id, nextReviewAt, currentTime)
    }

    fun markAsReviewed(word: Word) = viewModelScope.launch {
        val currentTime = System.currentTimeMillis()
        // If reviewed successfully, review in 1 day
        val nextReviewAt = currentTime + TimeUnit.DAYS.toMillis(1)
        repository.markAsReviewed(word.id, nextReviewAt, currentTime)
    }

    fun insertCategory(category: Category) = viewModelScope.launch {
        repository.insertCategory(category)
    }

    fun updateCategory(category: Category) = viewModelScope.launch {
        repository.updateCategory(category)
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        repository.deleteCategory(category)
    }

    fun getCategoryById(categoryId: Long): Category? {
        return allCategories.value?.find { it.id == categoryId }
    }

    fun getWordCountForCategory(categoryId: Long): LiveData<Int> = liveData {
        emit(repository.getWordCountForCategory(categoryId))
    }

    // WordMeaning operations
    fun getMeaningsForWord(wordId: Long): LiveData<List<WordMeaning>> = repository.getMeaningsForWord(wordId)

    suspend fun getMeaningsForWordSync(wordId: Long): List<WordMeaning> = repository.getMeaningsForWordSync(wordId)

    fun saveMeanings(wordId: Long, meaningTexts: List<String>) = viewModelScope.launch {
        repository.deleteMeaningsForWord(wordId)
        val meanings = meaningTexts.filter { it.isNotBlank() }.map { text ->
            WordMeaning(wordId = wordId, meaningText = text.trim())
        }
        repository.insertMeanings(meanings)
    }

    fun createMeaningsIfEmpty(wordId: Long, meaningTexts: List<String>) = viewModelScope.launch {
        val existing = repository.getMeaningsForWordSync(wordId)
        if (existing.isEmpty()) {
            val meanings = meaningTexts.filter { it.isNotBlank() }.map { text ->
                WordMeaning(wordId = wordId, meaningText = text.trim())
            }
            repository.insertMeanings(meanings)
        }
    }

    fun toggleMeaningProblematic(meaning: WordMeaning) = viewModelScope.launch {
        repository.setMeaningProblematic(meaning.id, !meaning.isProblematic)
    }

    fun toggleMeaningHighlighted(meaning: WordMeaning) = viewModelScope.launch {
        repository.setMeaningHighlighted(meaning.id, !meaning.isHighlighted)
    }

    fun updateMeaningNote(meaning: WordMeaning, note: String?) = viewModelScope.launch {
        repository.updateMeaningNote(meaning.id, note)
    }

    fun updateMeaningText(meaning: WordMeaning, text: String) = viewModelScope.launch {
        repository.updateMeaningText(meaning.id, text)
        // Sync word.meaning with all meanings
        val allMeanings = repository.getMeaningsForWordSync(meaning.wordId)
        val word = repository.getWordById(meaning.wordId)
        if (word != null && allMeanings.isNotEmpty()) {
            val newMeaning = allMeanings.joinToString("，") { it.meaningText }
            repository.updateWord(word.copy(meaning = newMeaning))
        }
    }

    val problematicWordIds: LiveData<List<Long>> = repository.getProblematicWordIds()

    // Copy words to category
    fun copyWordsToCategory(wordIds: List<Long>, targetCategoryId: Long, onResult: (Int) -> Unit = {}) = viewModelScope.launch {
        val count = repository.copyWordsToCategory(wordIds, targetCategoryId)
        withContext(Dispatchers.Main) {
            onResult(count)
        }
    }

    // Meaning reorder
    fun reorderMeanings(meanings: List<WordMeaning>) = viewModelScope.launch {
        meanings.forEachIndexed { index, meaning ->
            repository.updateMeaningSortOrder(meaning.id, index)
        }
        // Also update word.meaning to sync with main page display
        if (meanings.isNotEmpty()) {
            val wordId = meanings[0].wordId
            val word = repository.getWordById(wordId)
            if (word != null) {
                val newMeaning = meanings.joinToString("，") { it.meaningText }
                repository.updateWord(word.copy(meaning = newMeaning))
            }
        }
    }

    // WordGroup operations
    val allGroups: LiveData<List<WordGroup>> = repository.allGroups

    fun createGroup(name: String, color: String? = null) = viewModelScope.launch {
        val maxOrder = repository.getAllGroupsSync().maxOfOrNull { it.sortOrder } ?: 0
        repository.insertGroup(WordGroup(name = name, color = color, sortOrder = maxOrder + 1))
    }

    fun deleteGroup(group: WordGroup) = viewModelScope.launch {
        // Unassign words from group before deleting
        val words = repository.getWordsByGroup(group.id)
        words.forEach { word ->
            repository.setWordGroup(word.id, null)
        }
        repository.deleteGroup(group)
    }

    fun updateGroup(group: WordGroup) = viewModelScope.launch {
        repository.updateGroup(group)
    }

    fun assignWordToGroup(wordId: Long, groupId: Long?) = viewModelScope.launch {
        repository.setWordGroup(wordId, groupId)
    }

    fun assignWordsToGroup(wordIds: List<Long>, groupId: Long?) = viewModelScope.launch {
        wordIds.forEach { wordId ->
            repository.setWordGroup(wordId, groupId)
        }
    }

    fun getWordCountForGroup(groupId: Long): LiveData<Int> = liveData {
        emit(repository.getWordCountForGroup(groupId))
    }

    suspend fun getWordsByGroupSync(groupId: Long): List<Word> = repository.getWordsByGroup(groupId)

    suspend fun getWordCountForGroupSync(groupId: Long): Int = repository.getWordCountForGroup(groupId)

    fun updateGroupSortOrder(groups: List<WordGroup>) = viewModelScope.launch {
        groups.forEachIndexed { index, group ->
            repository.updateGroupSortOrder(group.id, index)
        }
    }

    // Quiz History operations
    val allQuizHistory: LiveData<List<QuizHistory>> = repository.allQuizHistory

    // Similar word detection
    suspend fun findSimilarWordsExcluding(wordText: String, excludeWordId: Long): List<Word> {
        return repository.findSimilarWordsExcluding(wordText, excludeWordId)
    }

    suspend fun findSameCategoryWords(wordText: String, categoryId: Long): List<Word> {
        return repository.findSameCategoryWords(wordText, categoryId)
    }

    fun setWordBatchId(wordId: Long, batchId: Long?) = viewModelScope.launch {
        repository.setWordBatchId(wordId, batchId)
    }

    fun insertQuizHistory(history: QuizHistory) = viewModelScope.launch {
        repository.insertQuizHistory(history)
    }

    suspend fun insertQuizHistorySync(history: QuizHistory) {
        repository.insertQuizHistory(history)
    }

    fun deleteQuizHistory(history: QuizHistory) = viewModelScope.launch {
        repository.deleteQuizHistory(history)
    }
}
