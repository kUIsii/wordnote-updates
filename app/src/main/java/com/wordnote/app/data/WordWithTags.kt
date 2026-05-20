package com.wordnote.app.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class WordWithTags(
    @Embedded
    val word: Word,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = WordTag::class,
            parentColumn = "wordId",
            entityColumn = "tagId"
        )
    )
    val tags: List<Tag>
)
