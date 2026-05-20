package com.wordnote.app.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WordDatabase_Impl extends WordDatabase {
  private volatile WordDao _wordDao;

  private volatile CategoryDao _categoryDao;

  private volatile TagDao _tagDao;

  private volatile WordMeaningDao _wordMeaningDao;

  private volatile WordGroupDao _wordGroupDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(6) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `words` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `word` TEXT NOT NULL, `meaning` TEXT NOT NULL, `categoryId` INTEGER, `groupId` INTEGER, `note` TEXT, `createdAt` INTEGER NOT NULL, `forgetCount` INTEGER NOT NULL, `nextReviewAt` INTEGER NOT NULL, `lastReviewedAt` INTEGER NOT NULL, `batchId` INTEGER, FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_words_categoryId` ON `words` (`categoryId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `color` TEXT NOT NULL, `isDefault` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `word_tag` (`wordId` INTEGER NOT NULL, `tagId` INTEGER NOT NULL, PRIMARY KEY(`wordId`, `tagId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `word_meanings` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `wordId` INTEGER NOT NULL, `meaningText` TEXT NOT NULL, `note` TEXT, `isProblematic` INTEGER NOT NULL, `isHighlighted` INTEGER NOT NULL, FOREIGN KEY(`wordId`) REFERENCES `words`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_word_meanings_wordId` ON `word_meanings` (`wordId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `word_groups` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '68e45ccb5f9f91ab47a4a03c98054ef4')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `words`");
        db.execSQL("DROP TABLE IF EXISTS `categories`");
        db.execSQL("DROP TABLE IF EXISTS `tags`");
        db.execSQL("DROP TABLE IF EXISTS `word_tag`");
        db.execSQL("DROP TABLE IF EXISTS `word_meanings`");
        db.execSQL("DROP TABLE IF EXISTS `word_groups`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsWords = new HashMap<String, TableInfo.Column>(11);
        _columnsWords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("word", new TableInfo.Column("word", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("meaning", new TableInfo.Column("meaning", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("categoryId", new TableInfo.Column("categoryId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("groupId", new TableInfo.Column("groupId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("forgetCount", new TableInfo.Column("forgetCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("nextReviewAt", new TableInfo.Column("nextReviewAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("lastReviewedAt", new TableInfo.Column("lastReviewedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWords.put("batchId", new TableInfo.Column("batchId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWords = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysWords.add(new TableInfo.ForeignKey("categories", "SET NULL", "NO ACTION", Arrays.asList("categoryId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesWords = new HashSet<TableInfo.Index>(1);
        _indicesWords.add(new TableInfo.Index("index_words_categoryId", false, Arrays.asList("categoryId"), Arrays.asList("ASC")));
        final TableInfo _infoWords = new TableInfo("words", _columnsWords, _foreignKeysWords, _indicesWords);
        final TableInfo _existingWords = TableInfo.read(db, "words");
        if (!_infoWords.equals(_existingWords)) {
          return new RoomOpenHelper.ValidationResult(false, "words(com.wordnote.app.data.Word).\n"
                  + " Expected:\n" + _infoWords + "\n"
                  + " Found:\n" + _existingWords);
        }
        final HashMap<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(4);
        _columnsCategories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("color", new TableInfo.Column("color", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("isDefault", new TableInfo.Column("isDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(db, "categories");
        if (!_infoCategories.equals(_existingCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "categories(com.wordnote.app.data.Category).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsTags = new HashMap<String, TableInfo.Column>(2);
        _columnsTags.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTags = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTags = new TableInfo("tags", _columnsTags, _foreignKeysTags, _indicesTags);
        final TableInfo _existingTags = TableInfo.read(db, "tags");
        if (!_infoTags.equals(_existingTags)) {
          return new RoomOpenHelper.ValidationResult(false, "tags(com.wordnote.app.data.Tag).\n"
                  + " Expected:\n" + _infoTags + "\n"
                  + " Found:\n" + _existingTags);
        }
        final HashMap<String, TableInfo.Column> _columnsWordTag = new HashMap<String, TableInfo.Column>(2);
        _columnsWordTag.put("wordId", new TableInfo.Column("wordId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWordTag.put("tagId", new TableInfo.Column("tagId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWordTag = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWordTag = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWordTag = new TableInfo("word_tag", _columnsWordTag, _foreignKeysWordTag, _indicesWordTag);
        final TableInfo _existingWordTag = TableInfo.read(db, "word_tag");
        if (!_infoWordTag.equals(_existingWordTag)) {
          return new RoomOpenHelper.ValidationResult(false, "word_tag(com.wordnote.app.data.WordTag).\n"
                  + " Expected:\n" + _infoWordTag + "\n"
                  + " Found:\n" + _existingWordTag);
        }
        final HashMap<String, TableInfo.Column> _columnsWordMeanings = new HashMap<String, TableInfo.Column>(6);
        _columnsWordMeanings.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWordMeanings.put("wordId", new TableInfo.Column("wordId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWordMeanings.put("meaningText", new TableInfo.Column("meaningText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWordMeanings.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWordMeanings.put("isProblematic", new TableInfo.Column("isProblematic", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWordMeanings.put("isHighlighted", new TableInfo.Column("isHighlighted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWordMeanings = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysWordMeanings.add(new TableInfo.ForeignKey("words", "CASCADE", "NO ACTION", Arrays.asList("wordId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesWordMeanings = new HashSet<TableInfo.Index>(1);
        _indicesWordMeanings.add(new TableInfo.Index("index_word_meanings_wordId", false, Arrays.asList("wordId"), Arrays.asList("ASC")));
        final TableInfo _infoWordMeanings = new TableInfo("word_meanings", _columnsWordMeanings, _foreignKeysWordMeanings, _indicesWordMeanings);
        final TableInfo _existingWordMeanings = TableInfo.read(db, "word_meanings");
        if (!_infoWordMeanings.equals(_existingWordMeanings)) {
          return new RoomOpenHelper.ValidationResult(false, "word_meanings(com.wordnote.app.data.WordMeaning).\n"
                  + " Expected:\n" + _infoWordMeanings + "\n"
                  + " Found:\n" + _existingWordMeanings);
        }
        final HashMap<String, TableInfo.Column> _columnsWordGroups = new HashMap<String, TableInfo.Column>(3);
        _columnsWordGroups.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWordGroups.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWordGroups.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWordGroups = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWordGroups = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWordGroups = new TableInfo("word_groups", _columnsWordGroups, _foreignKeysWordGroups, _indicesWordGroups);
        final TableInfo _existingWordGroups = TableInfo.read(db, "word_groups");
        if (!_infoWordGroups.equals(_existingWordGroups)) {
          return new RoomOpenHelper.ValidationResult(false, "word_groups(com.wordnote.app.data.WordGroup).\n"
                  + " Expected:\n" + _infoWordGroups + "\n"
                  + " Found:\n" + _existingWordGroups);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "68e45ccb5f9f91ab47a4a03c98054ef4", "6574c4a4b5463c9ff363326d45f1382c");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "words","categories","tags","word_tag","word_meanings","word_groups");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `words`");
      _db.execSQL("DELETE FROM `categories`");
      _db.execSQL("DELETE FROM `tags`");
      _db.execSQL("DELETE FROM `word_tag`");
      _db.execSQL("DELETE FROM `word_meanings`");
      _db.execSQL("DELETE FROM `word_groups`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(WordDao.class, WordDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategoryDao.class, CategoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TagDao.class, TagDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WordMeaningDao.class, WordMeaningDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WordGroupDao.class, WordGroupDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public WordDao wordDao() {
    if (_wordDao != null) {
      return _wordDao;
    } else {
      synchronized(this) {
        if(_wordDao == null) {
          _wordDao = new WordDao_Impl(this);
        }
        return _wordDao;
      }
    }
  }

  @Override
  public CategoryDao categoryDao() {
    if (_categoryDao != null) {
      return _categoryDao;
    } else {
      synchronized(this) {
        if(_categoryDao == null) {
          _categoryDao = new CategoryDao_Impl(this);
        }
        return _categoryDao;
      }
    }
  }

  @Override
  public TagDao tagDao() {
    if (_tagDao != null) {
      return _tagDao;
    } else {
      synchronized(this) {
        if(_tagDao == null) {
          _tagDao = new TagDao_Impl(this);
        }
        return _tagDao;
      }
    }
  }

  @Override
  public WordMeaningDao wordMeaningDao() {
    if (_wordMeaningDao != null) {
      return _wordMeaningDao;
    } else {
      synchronized(this) {
        if(_wordMeaningDao == null) {
          _wordMeaningDao = new WordMeaningDao_Impl(this);
        }
        return _wordMeaningDao;
      }
    }
  }

  @Override
  public WordGroupDao wordGroupDao() {
    if (_wordGroupDao != null) {
      return _wordGroupDao;
    } else {
      synchronized(this) {
        if(_wordGroupDao == null) {
          _wordGroupDao = new WordGroupDao_Impl(this);
        }
        return _wordGroupDao;
      }
    }
  }
}
