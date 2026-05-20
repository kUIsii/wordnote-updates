package com.wordnote.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import java.io.File

data class DictEntry(
    val word: String,
    val phonetic: String?,
    val translation: String?,
    val pos: String?,
    val collins: Int,
    val oxford: Int,
    val tag: String?,
    val bnc: Int,
    val frq: Int
)

class DictionaryDatabase(private val context: Context) {

    private var database: SQLiteDatabase? = null
    private var isOpen = false

    fun open(uri: Uri): Boolean {
        return try {
            close()
            // Copy from SAF URI to internal storage for reliable access
            val internalFile = File(context.filesDir, "ecdict_lite.db")
            context.contentResolver.openInputStream(uri)?.use { input ->
                internalFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return false

            database = SQLiteDatabase.openDatabase(
                internalFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY
            )

            // Verify it's a valid dict database
            database?.rawQuery("SELECT COUNT(*) FROM dict LIMIT 1", null)?.use {
                isOpen = it.moveToFirst()
            }
            if (!isOpen) {
                database?.close()
                database = null
            }
            isOpen
        } catch (e: Exception) {
            close()
            false
        }
    }

    fun search(word: String): DictEntry? {
        if (!isOpen || database == null) return null
        return try {
            database?.rawQuery(
                "SELECT word, phonetic, translation, pos, collins, oxford, tag, bnc, frq FROM dict WHERE word = ? COLLATE NOCASE",
                arrayOf(word)
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    DictEntry(
                        word = cursor.getString(0),
                        phonetic = cursor.getString(1),
                        translation = cursor.getString(2),
                        pos = cursor.getString(3),
                        collins = cursor.getInt(4),
                        oxford = cursor.getInt(5),
                        tag = cursor.getString(6),
                        bnc = cursor.getInt(7),
                        frq = cursor.getInt(8)
                    )
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun isReady(): Boolean = isOpen

    fun close() {
        try {
            database?.close()
        } catch (_: Exception) {}
        database = null
        isOpen = false
    }
}
