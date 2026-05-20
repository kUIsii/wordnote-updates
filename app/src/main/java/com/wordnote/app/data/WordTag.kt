package com.wordnote.app.data

import androidx.room.Entity

@Entity(
    tableName = "word_tag",
    primaryKeys = ["wordId", "tagId"]
)
data class WordTag(
    val wordId: Long,
    val tagId: Long
)
