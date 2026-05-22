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
    val allTags: LiveData<List<Tag>> = repository.allTags
    val highlightedMeanings: LiveData<List<HighlightedMeaning>> = repository.getHighlightedMeanings()

    private val _selectedCategoryId = MutableLiveData<Long?>(null)
    val selectedCategoryId: LiveData<Long?> = _selectedCategoryId

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    val wordsByCategory: LiveData<List<Word>> = _selectedCategoryId.switchMap { categoryId ->
        if (categoryId == null) {
            repository.allWords
        } else {
            repository.getWordsByCategory(categoryId)
        }
    }

    val filteredWords: LiveData<List<Word>> = MediatorLiveData<List<Word>>().apply {
        var currentWords: List<Word> = emptyList()
        var currentQuery: String = ""

        fun update() {
            value = if (currentQuery.isEmpty()) {
                currentWords
            } else {
                currentWords.filter { word ->
                    word.word.contains(currentQuery, ignoreCase = true) ||
                            word.meaning.contains(currentQuery, ignoreCase = true) ||
                            (word.note?.contains(currentQuery, ignoreCase = true) == true)
                }
            }
        }

        addSource(wordsByCategory) { words ->
            currentWords = words
            update()
        }

        addSource(_searchQuery) { query ->
            currentQuery = query
            update()
        }
    }

    // Review
    val reviewCount: LiveData<Int> = repository.getReviewCount(System.currentTimeMillis())

    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
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
        repository.deleteWord(word)
    }

    fun deleteWordsByIds(wordIds: List<Long>) = viewModelScope.launch {
        wordIds.forEach { id ->
            val word = repository.getWordById(id)
            word?.let { repository.deleteWord(it) }
        }
    }

    fun getWordById(wordId: Long): LiveData<Word?> = liveData {
        emit(repository.getWordById(wordId))
    }

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
    }

    // WordGroup operations
    val allGroups: LiveData<List<WordGroup>> = repository.allGroups

    fun createGroup(name: String) = viewModelScope.launch {
        repository.insertGroup(WordGroup(name = name))
    }

    fun deleteGroup(group: WordGroup) = viewModelScope.launch {
        // Unassign words from group before deleting
        val words = repository.getWordsByGroup(group.id)
        words.forEach { word ->
            repository.setWordGroup(word.id, null)
        }
        repository.deleteGroup(group)
    }

    fun assignWordToGroup(wordId: Long, groupId: Long?) = viewModelScope.launch {
        repository.setWordGroup(wordId, groupId)
    }

    fun getWordCountForGroup(groupId: Long): LiveData<Int> = liveData {
        emit(repository.getWordCountForGroup(groupId))
    }
}
