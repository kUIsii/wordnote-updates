package com.wordnote.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Word::class, Category::class, Tag::class, WordTag::class, WordMeaning::class, WordGroup::class, DiaryEntry::class, DiaryTodo::class, DiaryWordRef::class],
    version = 7,
    exportSchema = false
)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    abstract fun wordMeaningDao(): WordMeaningDao
    abstract fun wordGroupDao(): WordGroupDao
    abstract fun diaryDao(): DiaryDao

    companion object {
        @Volatile
        private var INSTANCE: WordDatabase? = null

        fun clearInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE words ADD COLUMN forgetCount INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE words ADD COLUMN nextReviewAt INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE words ADD COLUMN lastReviewedAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE categories ADD COLUMN color TEXT NOT NULL DEFAULT '#8E24AA'")
                database.execSQL("UPDATE categories SET color = '#E53935' WHERE name = '记不住的单词'")
                database.execSQL("UPDATE categories SET color = '#FB8C00' WHERE name = '释义有出入的单词'")
                database.execSQL("UPDATE categories SET color = '#43A047' WHERE name = '意思相近的单词'")
                database.execSQL("UPDATE categories SET color = '#1E88E5' WHERE name = '部分意思记不住'")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS word_meanings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        wordId INTEGER NOT NULL,
                        meaningText TEXT NOT NULL,
                        note TEXT,
                        isProblematic INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (wordId) REFERENCES words(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_word_meanings_wordId ON word_meanings(wordId)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE words ADD COLUMN groupId INTEGER DEFAULT NULL")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS word_groups (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        createdAt INTEGER NOT NULL DEFAULT 0
                    )
                """)
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE words ADD COLUMN batchId INTEGER DEFAULT NULL")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE word_meanings ADD COLUMN isHighlighted INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS diary_entries (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        entryDate INTEGER NOT NULL,
                        content TEXT NOT NULL DEFAULT '',
                        mood INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        updatedAt INTEGER NOT NULL DEFAULT 0
                    )
                """)
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_diary_entries_entryDate ON diary_entries(entryDate)")

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS diary_todos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        diaryEntryId INTEGER NOT NULL,
                        text TEXT NOT NULL,
                        isCompleted INTEGER NOT NULL DEFAULT 0,
                        sortOrder INTEGER NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (diaryEntryId) REFERENCES diary_entries(id) ON DELETE CASCADE
                    )
                """)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_diary_todos_diaryEntryId ON diary_todos(diaryEntryId)")

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS diary_word_refs (
                        diaryEntryId INTEGER NOT NULL,
                        wordId INTEGER NOT NULL,
                        addedAt INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(diaryEntryId, wordId),
                        FOREIGN KEY(diaryEntryId) REFERENCES diary_entries(id) ON DELETE CASCADE,
                        FOREIGN KEY(wordId) REFERENCES words(id) ON DELETE CASCADE
                    )
                """)
            }
        }

        fun getDatabase(context: Context): WordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "word_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDefaultCategories(database.categoryDao())
                        populateSampleWords(database.wordDao(), database.wordMeaningDao())
                    }
                }
            }

            suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
                val defaultCategories = listOf(
                    Category(name = "记不住的单词", color = "#E8636A", isDefault = true),
                    Category(name = "释义有出入的单词", color = "#F0A050", isDefault = true),
                    Category(name = "意思相近的单词", color = "#5CB87A", isDefault = true),
                    Category(name = "部分意思记不住", color = "#5B9BD5", isDefault = true)
                )
                defaultCategories.forEach { categoryDao.insertCategory(it) }
            }

            suspend fun populateSampleWords(wordDao: WordDao, wordMeaningDao: WordMeaningDao) {
                val batchId1 = 1000001L
                val batchId2 = 1000002L
                val batchId3 = 1000003L
                val now = System.currentTimeMillis()

                val sampleWords = listOf(
                    // 意思相近的单词 - batch 1
                    Word(word = "barrier", meaning = "障碍,屏障", categoryId = 3, batchId = batchId1, createdAt = now - 3600000),
                    Word(word = "obstacle", meaning = "障碍,阻碍", categoryId = 3, batchId = batchId1, createdAt = now - 3600000),
                    Word(word = "hindrance", meaning = "障碍,妨碍", categoryId = 3, batchId = batchId1, createdAt = now - 3600000),

                    // 意思相近的单词 - batch 2
                    Word(word = "happy", meaning = "开心,快乐", categoryId = 3, batchId = batchId2, createdAt = now - 7200000),
                    Word(word = "glad", meaning = "高兴,乐意", categoryId = 3, batchId = batchId2, createdAt = now - 7200000),
                    Word(word = "pleased", meaning = "满意的,高兴的", categoryId = 3, batchId = batchId2, createdAt = now - 7200000),

                    // 意思相近的单词 - batch 3
                    Word(word = "big", meaning = "大的", categoryId = 3, batchId = batchId3, createdAt = now - 10800000),
                    Word(word = "large", meaning = "大的,大量的", categoryId = 3, batchId = batchId3, createdAt = now - 10800000),
                    Word(word = "huge", meaning = "巨大的", categoryId = 3, batchId = batchId3, createdAt = now - 10800000),
                    Word(word = "enormous", meaning = "庞大的", categoryId = 3, batchId = batchId3, createdAt = now - 10800000),

                    // 记不住的单词
                    Word(word = "ephemeral", meaning = "短暂的,转瞬即逝的", categoryId = 1, createdAt = now - 86400000),
                    Word(word = "ubiquitous", meaning = "无处不在的", categoryId = 1, createdAt = now - 86400000),
                    Word(word = "eloquent", meaning = "雄辩的,有口才的", categoryId = 1, createdAt = now - 90000000),
                    Word(word = "meticulous", meaning = "一丝不苟的", categoryId = 1, createdAt = now - 93600000),
                    Word(word = "pragmatic", meaning = "务实的,实用主义的", categoryId = 1, createdAt = now - 97200000),

                    // 释义有出入的单词
                    Word(word = "distinct", meaning = "明显的,独特的,不同的", categoryId = 2, createdAt = now - 172800000),
                    Word(word = "considerable", meaning = "相当大的,值得考虑的", categoryId = 2, createdAt = now - 176400000),
                    Word(word = "issue", meaning = "问题,发行,期刊", categoryId = 2, createdAt = now - 180000000),

                    // 部分意思记不住
                    Word(word = "run", meaning = "跑,经营,运行,管理", categoryId = 4, createdAt = now - 259200000),
                    Word(word = "get", meaning = "得到,变得,理解,到达", categoryId = 4, createdAt = now - 262800000),
                    Word(word = "set", meaning = "设置,一套,凝固,日落", categoryId = 4, createdAt = now - 266400000)
                )

                sampleWords.forEach { wordDao.insertWord(it) }

                // Add sample meanings with some marked as problematic for testing
                val distinctWord = wordDao.getWordByWord("distinct")
                if (distinctWord != null) {
                    wordMeaningDao.insertAll(listOf(
                        WordMeaning(wordId = distinctWord.id, meaningText = "明显的", isProblematic = true),
                        WordMeaning(wordId = distinctWord.id, meaningText = "独特的"),
                        WordMeaning(wordId = distinctWord.id, meaningText = "不同的")
                    ))
                }

                val runWord = wordDao.getWordByWord("run")
                if (runWord != null) {
                    wordMeaningDao.insertAll(listOf(
                        WordMeaning(wordId = runWord.id, meaningText = "跑"),
                        WordMeaning(wordId = runWord.id, meaningText = "经营", isProblematic = true),
                        WordMeaning(wordId = runWord.id, meaningText = "运行"),
                        WordMeaning(wordId = runWord.id, meaningText = "管理", isProblematic = true)
                    ))
                }

                val getWord = wordDao.getWordByWord("get")
                if (getWord != null) {
                    wordMeaningDao.insertAll(listOf(
                        WordMeaning(wordId = getWord.id, meaningText = "得到"),
                        WordMeaning(wordId = getWord.id, meaningText = "变得"),
                        WordMeaning(wordId = getWord.id, meaningText = "理解", isProblematic = true),
                        WordMeaning(wordId = getWord.id, meaningText = "到达")
                    ))
                }

                val setWord = wordDao.getWordByWord("set")
                if (setWord != null) {
                    wordMeaningDao.insertAll(listOf(
                        WordMeaning(wordId = setWord.id, meaningText = "设置"),
                        WordMeaning(wordId = setWord.id, meaningText = "一套"),
                        WordMeaning(wordId = setWord.id, meaningText = "凝固", isProblematic = true),
                        WordMeaning(wordId = setWord.id, meaningText = "日落", isProblematic = true)
                    ))
                }

                val issueWord = wordDao.getWordByWord("issue")
                if (issueWord != null) {
                    wordMeaningDao.insertAll(listOf(
                        WordMeaning(wordId = issueWord.id, meaningText = "问题"),
                        WordMeaning(wordId = issueWord.id, meaningText = "发行", isProblematic = true),
                        WordMeaning(wordId = issueWord.id, meaningText = "期刊")
                    ))
                }
            }
        }
    }
}
