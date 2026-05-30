package com.wordnote.app.data

import androidx.lifecycle.LiveData

class WordRepository(
    private val wordDao: WordDao,
    private val categoryDao: CategoryDao,
    private val tagDao: TagDao,
    private val wordMeaningDao: WordMeaningDao,
    private val wordGroupDao: WordGroupDao,
    private val quizHistoryDao: QuizHistoryDao,
    private val sentenceDao: SentenceDao
) {

    // Word operations
    val allWords: LiveData<List<Word>> = wordDao.getAllActiveWords()
    suspend fun getAllActiveWordsSync(): List<Word> = wordDao.getAllActiveWordsSync()

    suspend fun insertWord(word: Word): Long = wordDao.insertWord(word)
    suspend fun insertWords(words: List<Word>): List<Long> = wordDao.insertWords(words)
    suspend fun updateWord(word: Word) = wordDao.updateWord(word)
    suspend fun deleteWord(word: Word) = wordDao.deleteWord(word)
    suspend fun getWordById(wordId: Long): Word? = wordDao.getWordById(wordId)

    fun getWordsByCategory(categoryId: Long): LiveData<List<Word>> = wordDao.getActiveWordsByCategory(categoryId)
    fun searchWords(query: String): LiveData<List<Word>> = wordDao.searchWords(query)

    // Review operations
    fun getWordsDueForReview(currentTime: Long): LiveData<List<Word>> = wordDao.getWordsDueForReview(currentTime)
    suspend fun getWordsDueForReviewSync(currentTime: Long): List<Word> = wordDao.getWordsDueForReviewSync(currentTime)
    suspend fun markAsForgotten(wordId: Long, nextReviewAt: Long, currentTime: Long) = wordDao.markAsForgotten(wordId, nextReviewAt, currentTime)
    suspend fun markAsReviewed(wordId: Long, nextReviewAt: Long, currentTime: Long) = wordDao.markAsReviewed(wordId, nextReviewAt, currentTime)
    fun getReviewCount(currentTime: Long): LiveData<Int> = wordDao.getReviewCount(currentTime)

    // Category operations
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
    suspend fun getCategoryById(categoryId: Long): Category? = categoryDao.getCategoryById(categoryId)
    suspend fun getCategoryByName(name: String): Category? = categoryDao.getCategoryByName(name)
    suspend fun getWordCountForCategory(categoryId: Long): Int = categoryDao.getWordCountForCategory(categoryId)

    // Tag operations
    val allTags: LiveData<List<Tag>> = tagDao.getAllTags()

    suspend fun insertTag(tag: Tag): Long = tagDao.insertTag(tag)
    suspend fun deleteTag(tag: Tag) = tagDao.deleteTag(tag)
    suspend fun getTagById(tagId: Long): Tag? = tagDao.getTagById(tagId)
    suspend fun getTagByName(name: String): Tag? = tagDao.getTagByName(name)

    // Word-Tag operations
    suspend fun addTagToWord(wordId: Long, tagId: Long) {
        wordDao.insertWordTag(WordTag(wordId, tagId))
    }

    suspend fun removeTagFromWord(wordId: Long, tagId: Long) {
        wordDao.deleteWordTag(WordTag(wordId, tagId))
    }

    suspend fun removeAllTagsFromWord(wordId: Long) {
        wordDao.deleteAllTagsForWord(wordId)
    }

    fun getTagsForWord(wordId: Long): LiveData<List<Tag>> = wordDao.getTagsForWord(wordId)
    suspend fun getTagsForWordSync(wordId: Long): List<Tag> = wordDao.getTagsForWordSync(wordId)

    // WordMeaning operations
    suspend fun insertMeanings(meanings: List<WordMeaning>) = wordMeaningDao.insertAll(meanings)
    suspend fun deleteMeaningsForWord(wordId: Long) = wordMeaningDao.deleteAllForWord(wordId)
    fun getMeaningsForWord(wordId: Long): LiveData<List<WordMeaning>> = wordMeaningDao.getMeaningsForWordOrdered(wordId)
    suspend fun getMeaningsForWordSync(wordId: Long): List<WordMeaning> = wordMeaningDao.getMeaningsForWordOrderedSync(wordId)
    suspend fun updateMeaning(meaning: WordMeaning) = wordMeaningDao.update(meaning)
    suspend fun setMeaningProblematic(meaningId: Long, isProblematic: Boolean) = wordMeaningDao.setProblematic(meaningId, isProblematic)
    suspend fun setMeaningHighlighted(meaningId: Long, isHighlighted: Boolean) = wordMeaningDao.setHighlighted(meaningId, isHighlighted)
    suspend fun updateMeaningNote(meaningId: Long, note: String?) = wordMeaningDao.updateNote(meaningId, note)
    suspend fun updateMeaningSortOrder(meaningId: Long, sortOrder: Int) = wordMeaningDao.updateSortOrder(meaningId, sortOrder)
    suspend fun updateMeaningText(meaningId: Long, text: String) = wordMeaningDao.updateMeaningText(meaningId, text)
    fun getProblematicWordIds(): LiveData<List<Long>> = wordMeaningDao.getProblematicWordIds()
    fun getHighlightedMeanings(): LiveData<List<HighlightedMeaning>> = wordMeaningDao.getHighlightedMeanings()

    // Copy words to category
    suspend fun copyWordsToCategory(wordIds: List<Long>, targetCategoryId: Long): Int {
        val now = System.currentTimeMillis()
        var count = 0
        wordIds.forEach { wordId ->
            val word = wordDao.getWordById(wordId) ?: return@forEach
            val copy = Word(
                word = word.word,
                meaning = word.meaning,
                categoryId = targetCategoryId,
                note = word.note,
                createdAt = now
            )
            val newWordId = wordDao.insertWord(copy)
            // Copy WordMeaning records
            val meanings = wordMeaningDao.getMeaningsForWordOrderedSync(wordId)
            if (meanings.isNotEmpty()) {
                val copiedMeanings = meanings.map { m ->
                    WordMeaning(
                        wordId = newWordId,
                        meaningText = m.meaningText,
                        note = m.note,
                        isProblematic = m.isProblematic,
                        isHighlighted = m.isHighlighted,
                        sortOrder = m.sortOrder
                    )
                }
                wordMeaningDao.insertAll(copiedMeanings)
            }
            count++
        }
        return count
    }

    // WordGroup operations
    val allGroups: LiveData<List<WordGroup>> = wordGroupDao.getAllGroups()
    suspend fun getAllGroupsSync(): List<WordGroup> = wordGroupDao.getAllGroupsSync()
    suspend fun insertGroup(group: WordGroup): Long = wordGroupDao.insert(group)
    suspend fun updateGroup(group: WordGroup) = wordGroupDao.update(group)
    suspend fun deleteGroup(group: WordGroup) = wordGroupDao.delete(group)
    suspend fun getGroupById(groupId: Long): WordGroup? = wordGroupDao.getGroupById(groupId)
    suspend fun getWordCountForGroup(groupId: Long): Int = wordGroupDao.getWordCountForGroup(groupId)
    suspend fun updateGroupSortOrder(groupId: Long, sortOrder: Int) = wordGroupDao.updateSortOrder(groupId, sortOrder)

    // Word with group operations
    suspend fun getWordsByGroup(groupId: Long): List<Word> = wordDao.getWordsByGroupSync(groupId)
    suspend fun setWordGroup(wordId: Long, groupId: Long?) = wordDao.setWordGroup(wordId, groupId)
    suspend fun setWordBatchId(wordId: Long, batchId: Long?) = wordDao.setWordBatchId(wordId, batchId)

    // Similar word detection
    suspend fun findSimilarWordsExcluding(wordText: String, excludeWordId: Long): List<Word> {
        return wordDao.findSimilarWordsExcluding(wordText, excludeWordId)
    }

    suspend fun findSameCategoryWords(wordText: String, categoryId: Long): List<Word> {
        return wordDao.findSameCategoryWords(wordText, categoryId)
    }

    // Soft delete / Recycle bin operations
    suspend fun softDeleteWord(wordId: Long) {
        wordDao.softDelete(wordId, System.currentTimeMillis())
    }

    suspend fun restoreWord(wordId: Long) {
        wordDao.restore(wordId)
    }

    val deletedWords: LiveData<List<Word>> = wordDao.getDeletedWords()

    suspend fun getDeletedWordsSync(): List<Word> = wordDao.getDeletedWordsSync()

    suspend fun permanentDeleteOlderThan(cutoffTime: Long) {
        wordDao.permanentDeleteOlderThan(cutoffTime)
    }

    // Quiz History operations
    val allQuizHistory: LiveData<List<QuizHistory>> = quizHistoryDao.getAll()
    suspend fun insertQuizHistory(history: QuizHistory): Long = quizHistoryDao.insert(history)
    suspend fun deleteQuizHistory(history: QuizHistory) = quizHistoryDao.delete(history)
    suspend fun deleteAllQuizHistory() = quizHistoryDao.deleteAll()

    // Sentence operations
    val allSentencesWithWords: LiveData<List<SentenceWithWords>> = sentenceDao.getSentencesWithWords()
    suspend fun getSentenceWithWords(sentenceId: Long): SentenceWithWords? = sentenceDao.getSentenceWithWords(sentenceId)
    suspend fun insertSentence(sentence: Sentence): Long = sentenceDao.insertSentence(sentence)
    suspend fun updateSentence(sentence: Sentence) = sentenceDao.updateSentence(sentence)
    suspend fun deleteSentence(sentence: Sentence) = sentenceDao.deleteSentence(sentence)
    suspend fun deleteSentenceById(sentenceId: Long) = sentenceDao.deleteSentenceById(sentenceId)
    suspend fun insertSentenceWords(words: List<SentenceWord>): List<Long> = sentenceDao.insertSentenceWords(words)
    suspend fun deleteSentenceWords(sentenceId: Long) = sentenceDao.deleteSentenceWords(sentenceId)
}
