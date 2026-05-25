package com.wordnote.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sentence_words",
    foreignKeys = [
        ForeignKey(
            entity = Sentence::class,
            parentColumns = ["id"],
            childColumns = ["sentenceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sentenceId"])]
)
data class SentenceWord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sentenceId: Long = 0,
    val wordText: String,
    val meaning: String,
    val sortOrder: Int = 0
)
