package com.wordnote.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.wordnote.app.WordNoteApplication
import com.wordnote.app.data.DiaryEntry
import kotlinx.coroutines.launch
import java.util.Calendar

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as WordNoteApplication).repository

    val allDiaryEntries: LiveData<List<DiaryEntry>> = repository.getAllDiaryEntries()

    fun getDiaryEntryByDate(date: Long): LiveData<DiaryEntry?> {
        val startOfDay = getStartOfDay(date)
        return repository.getDiaryEntryByDateLive(startOfDay)
    }

    fun getDiaryEntriesBetween(start: Long, end: Long): LiveData<List<DiaryEntry>> {
        return repository.getDiaryEntriesBetween(start, end)
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

    fun getStartOfDay(date: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getStartOfMonth(year: Int, month: Int): Long {
        return Calendar.getInstance().apply {
            set(year, month, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getEndOfMonth(year: Int, month: Int): Long {
        return Calendar.getInstance().apply {
            set(year, month, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MONTH, 1)
        }.timeInMillis
    }
}
