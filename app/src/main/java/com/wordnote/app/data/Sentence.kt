package com.wordnote.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sentences")
data class Sentence(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalText: String,
    val translation: String? = null,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
