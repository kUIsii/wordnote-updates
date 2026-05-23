package com.wordnote.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface QuizHistoryDao {
    @Insert
    suspend fun insert(history: QuizHistory): Long

    @Query("SELECT * FROM quiz_history ORDER BY createdAt DESC")
    fun getAll(): LiveData<List<QuizHistory>>

    @Query("SELECT * FROM quiz_history ORDER BY createdAt DESC")
    suspend fun getAllSync(): List<QuizHistory>

    @Delete
    suspend fun delete(history: QuizHistory)

    @Query("DELETE FROM quiz_history")
    suspend fun deleteAll()
}
