package com.wordnote.app

import android.app.Application
import com.wordnote.app.data.WordDatabase
import com.wordnote.app.data.WordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class WordNoteApplication : Application() {
    val database by lazy {
        try {
            WordDatabase.getDatabase(this)
        } catch (e: Exception) {
            android.util.Log.e("WordNoteApp", "Database construction failed, clearing instance: ${e.message}")
            WordDatabase.clearInstance()
            WordDatabase.getDatabase(this)
        }
    }
    val repository by lazy {
        try {
            WordRepository(database.wordDao(), database.categoryDao(), database.tagDao(), database.wordMeaningDao(), database.wordGroupDao(), database.quizHistoryDao())
        } catch (e: Exception) {
            android.util.Log.e("WordNoteApp", "Repository construction failed: ${e.message}")
            throw e
        }
    }

    override fun onCreate() {
        super.onCreate()
        autoRestoreIfBackupExists()
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

    private fun autoRestoreIfBackupExists() {
        try {
            val backupFile = File(filesDir, "auto_backup/word_database_pre_update")
            if (!backupFile.exists()) return

            val dbFile = getDatabasePath("word_database")
            // If database is tiny (<16KB), likely fresh install after crash - restore
            if (dbFile.exists() && dbFile.length() > 16384) return

            WordDatabase.clearInstance()
            backupFile.copyTo(dbFile, overwrite = true)
            val walFile = File(backupFile.path + "-wal")
            if (walFile.exists()) walFile.copyTo(File(dbFile.path + "-wal"), overwrite = true)
            val shmFile = File(backupFile.path + "-shm")
            if (shmFile.exists()) shmFile.copyTo(File(dbFile.path + "-shm"), overwrite = true)

            backupFile.delete()
            android.util.Log.d("WordNoteApp", "Auto-restored database from pre-update backup")
        } catch (e: Exception) {
            android.util.Log.e("WordNoteApp", "Auto-restore failed: ${e.message}")
        }
    }
}
