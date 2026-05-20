package com.wordnote.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_groups")
data class WordGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
