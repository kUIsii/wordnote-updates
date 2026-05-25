package com.wordnote.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface SentenceDao {
    @Transaction
    @Query("SELECT * FROM sentences ORDER BY createdAt DESC")
    fun getSentencesWithWords(): LiveData<List<SentenceWithWords>>

    @Transaction
    @Query("SELECT * FROM sentences WHERE id = :sentenceId")
    suspend fun getSentenceWithWords(sentenceId: Long): SentenceWithWords?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentence(sentence: Sentence): Long

    @Update
    suspend fun updateSentence(sentence: Sentence)

    @Delete
    suspend fun deleteSentence(sentence: Sentence)

    @Query("DELETE FROM sentences WHERE id = :sentenceId")
    suspend fun deleteSentenceById(sentenceId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentenceWords(words: List<SentenceWord>): List<Long>

    @Query("DELETE FROM sentence_words WHERE sentenceId = :sentenceId")
    suspend fun deleteSentenceWords(sentenceId: Long)
}
