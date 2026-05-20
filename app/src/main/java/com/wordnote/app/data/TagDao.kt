package com.wordnote.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): LiveData<List<Tag>>

    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: Long): Tag?

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun getTagByName(name: String): Tag?
}
