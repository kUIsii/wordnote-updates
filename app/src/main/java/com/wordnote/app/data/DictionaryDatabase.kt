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

    fun searchByChinese(chinese: String): List<DictEntry> {
        if (!isOpen || database == null) return emptyList()
        return try {
            val results = mutableListOf<DictEntry>()
            // Search for words containing the Chinese term
            database?.rawQuery(
                "SELECT word, phonetic, translation, pos, collins, oxford, tag, bnc, frq FROM dict WHERE translation LIKE ? COLLATE NOCASE",
                arrayOf("%$chinese%")
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    results.add(DictEntry(
                        word = cursor.getString(0),
                        phonetic = cursor.getString(1),
                        translation = cursor.getString(2),
                        pos = cursor.getString(3),
                        collins = cursor.getInt(4),
                        oxford = cursor.getInt(5),
                        tag = cursor.getString(6),
                        bnc = cursor.getInt(7),
                        frq = cursor.getInt(8)
                    ))
                }
            }

            // Rank by relevance: exact match > starts with > contains
            // Then sort by frequency (lower frq = more common)
            results.sortedWith(compareBy<DictEntry> { entry ->
                val trans = entry.translation ?: ""
                when {
                    // Exact match: translation is exactly the Chinese word (or with minor punctuation)
                    trans == chinese || trans == "$chinese;" || trans == "；$chinese" -> 0
                    // Starts with the Chinese word
                    trans.startsWith(chinese) || trans.startsWith("$chinese;") || trans.startsWith("；$chinese") -> 1
                    // Contains as a standalone term (preceded by ; or space)
                    trans.contains("；$chinese") || trans.contains("; $chinese") || trans.contains(" $chinese") -> 2
                    // Default: contains anywhere
                    else -> 3
                }
            }.thenBy { entry ->
                // Sort by frequency within each group (lower = more common)
                val frq = entry.frq
                if (frq == 0) 99999 else frq
            }).take(30)
        } catch (e: Exception) {
            emptyList()
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
