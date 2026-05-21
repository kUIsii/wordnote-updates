package com.wordnote.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diary_todos",
    foreignKeys = [
        ForeignKey(
            entity = DiaryEntry::class,
            parentColumns = ["id"],
            childColumns = ["diaryEntryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["diaryEntryId"])]
)
data class DiaryTodo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val diaryEntryId: Long,
    val text: String,
    val isCompleted: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
