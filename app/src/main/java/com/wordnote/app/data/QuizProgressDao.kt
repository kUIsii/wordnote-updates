package com.wordnote.app.data

import androidx.room.*

@Dao
interface QuizProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: QuizProgress)

    @Query("SELECT * FROM quiz_progress WHERE id = 1")
    suspend fun getProgress(): QuizProgress?

    @Query("DELETE FROM quiz_progress WHERE id = 1")
    suspend fun clearProgress()
}
