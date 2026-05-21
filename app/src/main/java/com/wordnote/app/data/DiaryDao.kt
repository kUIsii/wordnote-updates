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

    @Query("SELECT * FROM diary_entries WHERE entryDate BETWEEN :startTime AND :endTime ORDER BY entryDate DESC")
    fun getEntriesBetween(startTime: Long, endTime: Long): LiveData<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries WHERE entryDate BETWEEN :startTime AND :endTime ORDER BY entryDate DESC")
    suspend fun getEntriesBetweenSync(startTime: Long, endTime: Long): List<DiaryEntry>
}
