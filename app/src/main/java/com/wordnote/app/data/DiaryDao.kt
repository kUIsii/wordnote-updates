package com.wordnote.app.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntry): Long

    @Update
    suspend fun updateEntry(entry: DiaryEntry)

    @Delete
    suspend fun deleteEntry(entry: DiaryEntry)

    @Query("SELECT * FROM diary_entries WHERE entryDate = :startOfDay LIMIT 1")
    suspend fun getEntryByDate(startOfDay: Long): DiaryEntry?

    @Query("SELECT * FROM diary_entries WHERE entryDate = :startOfDay LIMIT 1")
    fun getEntryByDateLive(startOfDay: Long): LiveData<DiaryEntry?>

    @Query("SELECT * FROM diary_entries ORDER BY entryDate DESC")
    fun getAllEntries(): LiveData<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries ORDER BY entryDate DESC")
    suspend fun getAllEntriesSync(): List<DiaryEntry>

    @Query("SELECT * FROM diary_entries WHERE content LIKE '%' || :query || '%' ORDER BY entryDate DESC")
    fun searchEntries(query: String): LiveData<List<DiaryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: DiaryTodo): Long

    @Update
    suspend fun updateTodo(todo: DiaryTodo)

    @Delete
    suspend fun deleteTodo(todo: DiaryTodo)

    @Query("SELECT * FROM diary_todos WHERE diaryEntryId = :entryId ORDER BY sortOrder ASC")
    fun getTodosForEntry(entryId: Long): LiveData<List<DiaryTodo>>

    @Query("SELECT * FROM diary_todos WHERE diaryEntryId = :entryId ORDER BY sortOrder ASC")
    suspend fun getTodosForEntrySync(entryId: Long): List<DiaryTodo>

    @Query("DELETE FROM diary_todos WHERE diaryEntryId = :entryId")
    suspend fun deleteAllTodosForEntry(entryId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordRef(ref: DiaryWordRef)

    @Delete
    suspend fun deleteWordRef(ref: DiaryWordRef)

    @Query("DELETE FROM diary_word_refs WHERE diaryEntryId = :entryId")
    suspend fun deleteAllWordRefsForEntry(entryId: Long)

    @Query("SELECT w.* FROM words w INNER JOIN diary_word_refs dwr ON w.id = dwr.wordId WHERE dwr.diaryEntryId = :entryId ORDER BY dwr.addedAt ASC")
    fun getWordsForEntry(entryId: Long): LiveData<List<Word>>

    @Query("SELECT w.* FROM words w INNER JOIN diary_word_refs dwr ON w.id = dwr.wordId WHERE dwr.diaryEntryId = :entryId ORDER BY dwr.addedAt ASC")
    suspend fun getWordsForEntrySync(entryId: Long): List<Word>

    @Query("SELECT COUNT(*) FROM words WHERE createdAt BETWEEN :startTime AND :endTime")
    suspend fun getWordsAddedInRange(startTime: Long, endTime: Long): Int

    @Query("SELECT COUNT(*) FROM words WHERE lastReviewedAt BETWEEN :startTime AND :endTime")
    suspend fun getWordsReviewedInRange(startTime: Long, endTime: Long): Int

    @Query("SELECT COUNT(*) FROM diary_entries")
    fun getTotalDiaryCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM diary_todos WHERE diaryEntryId = :entryId AND isCompleted = 1")
    suspend fun getCompletedTodoCount(entryId: Long): Int

    @Query("SELECT COUNT(*) FROM diary_todos WHERE diaryEntryId = :entryId")
    suspend fun getTotalTodoCount(entryId: Long): Int

    @Query("SELECT * FROM diary_entries WHERE entryDate BETWEEN :startTime AND :endTime ORDER BY entryDate DESC")
    fun getEntriesBetween(startTime: Long, endTime: Long): LiveData<List<DiaryEntry>>
}
