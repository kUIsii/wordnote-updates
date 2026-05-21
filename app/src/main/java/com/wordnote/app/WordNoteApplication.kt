package com.wordnote.app

import android.app.Application
import com.wordnote.app.data.WordDatabase
import com.wordnote.app.data.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordNoteApplication : Application() {
    val database by lazy { WordDatabase.getDatabase(this) }
    val repository by lazy { WordRepository(database.wordDao(), database.categoryDao(), database.tagDao(), database.wordMeaningDao(), database.wordGroupDao(), database.diaryDao()) }

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val defaultColorMap = mapOf(
                    "记不住的单词" to "#E53935",
                    "释义有出入的单词" to "#FB8C00",
                    "意思相近的单词" to "#43A047",
                    "部分意思记不住" to "#1E88E5"
                )
                val categories = database.categoryDao().getAllCategoriesSync()
                categories.forEach { cat ->
                    val expectedColor = defaultColorMap[cat.name]
                    if (expectedColor != null && cat.color == "#8E24AA") {
                        database.categoryDao().updateCategory(cat.copy(color = expectedColor))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
