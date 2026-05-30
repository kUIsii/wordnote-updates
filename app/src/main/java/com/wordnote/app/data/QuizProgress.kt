package com.wordnote.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_progress")
data class QuizProgress(
    @PrimaryKey
    val id: Long = 1,
    val wordIds: String,
    val currentIndex: Int,
    val correctCount: Int,
    val forgottenIds: String,
    val categoryIds: String,
    val wordCount: Int,
    val useForgetCount: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
