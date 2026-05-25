package com.wordnote.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_history")
data class QuizHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val totalWords: Int,
    val correctCount: Int,
    val categoryIds: String,
    val forgottenWordIds: String,
    val forgottenWordTexts: String,
    val correctWordIds: String = ""
)
