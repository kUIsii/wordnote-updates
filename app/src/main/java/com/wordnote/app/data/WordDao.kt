package com.wordnote.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<Word>): List<Long>

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("SELECT * FROM words ORDER BY createdAt ASC")
    fun getAllWords(): LiveData<List<Word>>

    @Query("SELECT * FROM words WHERE id = :wordId")
    suspend fun getWordById(wordId: Long): Word?

    @Query("SELECT * FROM words WHERE categoryId = :categoryId ORDER BY createdAt ASC")
    fun getWordsByCategory(categoryId: Long): LiveData<List<Word>>

    @Query("SELECT * FROM words WHERE word LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%' ORDER BY createdAt ASC")
    fun searchWords(query: String): LiveData<List<Word>>

    @Query("SELECT * FROM words WHERE nextReviewAt <= :currentTime AND nextReviewAt > 0 ORDER BY nextReviewAt ASC")
    fun getWordsDueForReview(currentTime: Long): LiveData<List<Word>>

    @Query("SELECT * FROM words WHERE nextReviewAt <= :currentTime AND nextReviewAt > 0 ORDER BY nextReviewAt ASC")
    suspend fun getWordsDueForReviewSync(currentTime: Long): List<Word>

    @Query("UPDATE words SET forgetCount = forgetCount + 1, nextReviewAt = :nextReviewAt, lastReviewedAt = :currentTime WHERE id = :wordId")
    suspend fun markAsForgotten(wordId: Long, nextReviewAt: Long, currentTime: Long)

    @Query("UPDATE words SET nextReviewAt = :nextReviewAt, lastReviewedAt = :currentTime WHERE id = :wordId")
    suspend fun markAsReviewed(wordId: Long, nextReviewAt: Long, currentTime: Long)

    @Query("SELECT COUNT(*) FROM words WHERE nextReviewAt <= :currentTime AND nextReviewAt > 0")
    fun getReviewCount(currentTime: Long): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordTag(wordTag: WordTag)

    @Delete
    suspend fun deleteWordTag(wordTag: WordTag)

    @Query("DELETE FROM word_tag WHERE wordId = :wordId")
    suspend fun deleteAllTagsForWord(wordId: Long)

    @Query("SELECT t.* FROM tags t INNER JOIN word_tag wt ON t.id = wt.tagId WHERE wt.wordId = :wordId")
    fun getTagsForWord(wordId: Long): LiveData<List<Tag>>

    @Query("SELECT t.* FROM tags t INNER JOIN word_tag wt ON t.id = wt.tagId WHERE wt.wordId = :wordId")
    suspend fun getTagsForWordSync(wordId: Long): List<Tag>

    @Query("SELECT * FROM words WHERE groupId = :groupId ORDER BY createdAt ASC")
    suspend fun getWordsByGroupSync(groupId: Long): List<Word>

    @Query("UPDATE words SET groupId = :groupId WHERE id = :wordId")
    suspend fun setWordGroup(wordId: Long, groupId: Long?)

    @Query("SELECT * FROM words WHERE groupId IS NOT NULL ORDER BY groupId, createdAt ASC")
    fun getGroupedWords(): LiveData<List<Word>>

    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    suspend fun getWordByWord(word: String): Word?

    // Soft delete operations
    @Query("UPDATE words SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :wordId")
    suspend fun softDelete(wordId: Long, deletedAt: Long)

    @Query("UPDATE words SET isDeleted = 0, deletedAt = 0 WHERE id = :wordId")
    suspend fun restore(wordId: Long)

    @Query("SELECT * FROM words WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun getDeletedWords(): LiveData<List<Word>>

    @Query("SELECT * FROM words WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    suspend fun getDeletedWordsSync(): List<Word>

    @Query("DELETE FROM words WHERE isDeleted = 1 AND deletedAt < :cutoffTime")
    suspend fun permanentDeleteOlderThan(cutoffTime: Long)

    // Non-deleted word queries
    @Query("SELECT * FROM words WHERE isDeleted = 0 ORDER BY createdAt ASC")
    fun getAllActiveWords(): LiveData<List<Word>>

    @Query("SELECT * FROM words WHERE isDeleted = 0 ORDER BY createdAt ASC")
    suspend fun getAllActiveWordsSync(): List<Word>

    @Query("SELECT * FROM words WHERE isDeleted = 0 AND categoryId = :categoryId ORDER BY createdAt ASC")
    fun getActiveWordsByCategory(categoryId: Long): LiveData<List<Word>>
}
