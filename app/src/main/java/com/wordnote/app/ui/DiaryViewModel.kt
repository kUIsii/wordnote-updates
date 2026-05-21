package com.wordnote.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.wordnote.app.WordNoteApplication
import com.wordnote.app.data.DiaryEntry
import com.wordnote.app.data.DiaryTodo
import com.wordnote.app.data.Word
import kotlinx.coroutines.launch
import java.util.Calendar

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as WordNoteApplication).repository

    val allDiaryEntries: LiveData<List<DiaryEntry>> = repository.getAllDiaryEntries()

    fun getDiaryEntryByDate(date: Long): LiveData<DiaryEntry?> {
        return repository.getDiaryEntryByDateLive(date)
    }

    fun insertDiaryEntry(entry: DiaryEntry, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertDiaryEntry(entry)
            onComplete(id)
        }
    }

    fun updateDiaryEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            repository.updateDiaryEntry(entry)
        }
    }

    fun deleteDiaryEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            repository.deleteDiaryEntry(entry)
        }
    }

    fun searchDiaryEntries(query: String): LiveData<List<DiaryEntry>> {
        return repository.searchDiaryEntries(query)
    }

    // Todo operations
    fun getTodosForEntry(entryId: Long): LiveData<List<DiaryTodo>> {
        return repository.getTodosForDiaryEntry(entryId)
    }

    fun insertTodo(todo: DiaryTodo) {
        viewModelScope.launch {
            repository.insertDiaryTodo(todo)
        }
    }

    fun updateTodo(todo: DiaryTodo) {
        viewModelScope.launch {
            repository.updateDiaryTodo(todo)
        }
    }

    fun deleteTodo(todo: DiaryTodo) {
        viewModelScope.launch {
            repository.deleteDiaryTodo(todo)
        }
    }

    // Word operations
    fun getWordsForEntry(entryId: Long): LiveData<List<Word>> {
        return repository.getWordsForDiaryEntry(entryId)
    }

    fun linkWordToDiary(entryId: Long, wordId: Long) {
        viewModelScope.launch {
            repository.linkWordToDiary(entryId, wordId)
        }
    }

    fun unlinkWordFromDiary(entryId: Long, wordId: Long) {
        viewModelScope.launch {
            repository.unlinkWordFromDiary(entryId, wordId)
        }
    }

    fun getAllWords(): LiveData<List<Word>> {
        return repository.allWords
    }

    // Statistics
    suspend fun getWordsAddedToday(): Int {
        val (start, end) = getTodayRange()
        return repository.getWordsAddedInRange(start, end)
    }

    suspend fun getWordsReviewedToday(): Int {
        val (start, end) = getTodayRange()
        return repository.getWordsReviewedInRange(start, end)
    }

    private fun getTodayRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis
        return Pair(startOfDay, endOfDay)
    }

    fun getStartOfDay(date: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
