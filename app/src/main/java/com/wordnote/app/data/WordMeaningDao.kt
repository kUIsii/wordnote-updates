package com.wordnote.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

data class HighlightedMeaning(val wordId: Long, val meaningText: String)

@Dao
interface WordMeaningDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meaning: WordMeaning): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(meanings: List<WordMeaning>)

    @Update
    suspend fun update(meaning: WordMeaning)

    @Delete
    suspend fun delete(meaning: WordMeaning)

    @Query("DELETE FROM word_meanings WHERE wordId = :wordId")
    suspend fun deleteAllForWord(wordId: Long)

    @Query("SELECT * FROM word_meanings WHERE wordId = :wordId ORDER BY id ASC")
    fun getMeaningsForWord(wordId: Long): LiveData<List<WordMeaning>>

    @Query("SELECT * FROM word_meanings WHERE wordId = :wordId ORDER BY id ASC")
    suspend fun getMeaningsForWordSync(wordId: Long): List<WordMeaning>

    @Query("UPDATE word_meanings SET isProblematic = :isProblematic WHERE id = :meaningId")
    suspend fun setProblematic(meaningId: Long, isProblematic: Boolean)

    @Query("UPDATE word_meanings SET isHighlighted = :isHighlighted WHERE id = :meaningId")
    suspend fun setHighlighted(meaningId: Long, isHighlighted: Boolean)

    @Query("UPDATE word_meanings SET note = :note WHERE id = :meaningId")
    suspend fun updateNote(meaningId: Long, note: String?)

    @Query("SELECT DISTINCT wordId FROM word_meanings WHERE isProblematic = 1")
    fun getProblematicWordIds(): LiveData<List<Long>>

    @Query("SELECT wordId, meaningText FROM word_meanings WHERE isHighlighted = 1")
    fun getHighlightedMeanings(): LiveData<List<HighlightedMeaning>>

    @Query("UPDATE word_meanings SET sortOrder = :sortOrder WHERE id = :meaningId")
    suspend fun updateSortOrder(meaningId: Long, sortOrder: Int)

    @Query("UPDATE word_meanings SET sortOrder = :sortOrder WHERE id IN (:meaningIds)")
    suspend fun updateSortOrders(meaningIds: List<Long>, sortOrder: Int)

    @Query("SELECT * FROM word_meanings WHERE wordId = :wordId ORDER BY sortOrder ASC, id ASC")
    fun getMeaningsForWordOrdered(wordId: Long): LiveData<List<WordMeaning>>

    @Query("SELECT * FROM word_meanings WHERE wordId = :wordId ORDER BY sortOrder ASC, id ASC")
    suspend fun getMeaningsForWordOrderedSync(wordId: Long): List<WordMeaning>
}
