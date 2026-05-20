package com.wordnote.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "word_meanings",
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["wordId"])]
)
data class WordMeaning(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wordId: Long,
    val meaningText: String,
    val note: String? = null,
    val isProblematic: Boolean = false,
    val isHighlighted: Boolean = false
)
