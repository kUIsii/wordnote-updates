package com.wordnote.app.data

import androidx.room.Embedded
import androidx.room.Relation

data class SentenceWithWords(
    @Embedded val sentence: Sentence,
    @Relation(
        parentColumn = "id",
        entityColumn = "sentenceId"
    )
    val words: List<SentenceWord>
)
