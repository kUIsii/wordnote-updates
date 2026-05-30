package com.wordnote.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WordGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: WordGroup): Long

    @Update
    suspend fun update(group: WordGroup)

    @Delete
    suspend fun delete(group: WordGroup)

    @Query("SELECT * FROM word_groups ORDER BY sortOrder ASC, createdAt DESC")
    fun getAllGroups(): LiveData<List<WordGroup>>

    @Query("SELECT * FROM word_groups ORDER BY sortOrder ASC, createdAt DESC")
    suspend fun getAllGroupsSync(): List<WordGroup>

    @Query("UPDATE word_groups SET sortOrder = :sortOrder WHERE id = :groupId")
    suspend fun updateSortOrder(groupId: Long, sortOrder: Int)

    @Query("SELECT * FROM word_groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: Long): WordGroup?

    @Query("SELECT COUNT(*) FROM words WHERE groupId = :groupId")
    suspend fun getWordCountForGroup(groupId: Long): Int
}
