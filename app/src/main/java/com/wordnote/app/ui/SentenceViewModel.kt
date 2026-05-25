package com.wordnote.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.wordnote.app.WordNoteApplication
import com.wordnote.app.data.Sentence
import com.wordnote.app.data.SentenceWithWords
import com.wordnote.app.data.SentenceWord
import kotlinx.coroutines.launch

class SentenceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as WordNoteApplication).repository

    val allSentencesWithWords: LiveData<List<SentenceWithWords>> = repository.allSentencesWithWords

    suspend fun getSentenceWithWords(sentenceId: Long): SentenceWithWords? {
        return repository.getSentenceWithWords(sentenceId)
    }

    fun insertSentence(sentence: Sentence, words: List<SentenceWord>, onResult: (Long) -> Unit = {}) = viewModelScope.launch {
        val sentenceId = repository.insertSentence(sentence)
        if (words.isNotEmpty()) {
            val wordsWithId = words.map { it.copy(sentenceId = sentenceId) }
            repository.insertSentenceWords(wordsWithId)
        }
        onResult(sentenceId)
    }

    fun updateSentence(sentence: Sentence, words: List<SentenceWord>) = viewModelScope.launch {
        repository.updateSentence(sentence)
        repository.deleteSentenceWords(sentence.id)
        if (words.isNotEmpty()) {
            val wordsWithId = words.map { it.copy(sentenceId = sentence.id) }
            repository.insertSentenceWords(wordsWithId)
        }
    }

    fun deleteSentence(sentence: Sentence) = viewModelScope.launch {
        repository.deleteSentence(sentence)
    }
}
