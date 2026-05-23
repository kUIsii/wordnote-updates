package com.wordnote.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val meaning: String,
    val categoryId: Long? = null,
    val groupId: Long? = null,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val forgetCount: Int = 0,
    val nextReviewAt: Long = 0L,
    val lastReviewedAt: Long = 0L,
    val batchId: Long? = null,
    val isDeleted: Boolean = false,
    val deletedAt: Long = 0L
)
