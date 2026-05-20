package com.wordnote.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories ORDER BY isDefault DESC, name ASC")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM categories ORDER BY isDefault DESC, name ASC")
    suspend fun getAllCategoriesSync(): List<Category>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): Category?

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): Category?

    @Query("SELECT COUNT(*) FROM words WHERE categoryId = :categoryId")
    suspend fun getWordCountForCategory(categoryId: Long): Int
}
