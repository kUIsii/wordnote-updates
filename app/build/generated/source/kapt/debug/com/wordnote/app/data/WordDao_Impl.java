package com.wordnote.app.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class WordDao_Impl implements WordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Word> __insertionAdapterOfWord;

  private final EntityInsertionAdapter<WordTag> __insertionAdapterOfWordTag;

  private final EntityDeletionOrUpdateAdapter<Word> __deletionAdapterOfWord;

  private final EntityDeletionOrUpdateAdapter<WordTag> __deletionAdapterOfWordTag;

  private final EntityDeletionOrUpdateAdapter<Word> __updateAdapterOfWord;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsForgotten;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsReviewed;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllTagsForWord;

  private final SharedSQLiteStatement __preparedStmtOfSetWordGroup;

  public WordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWord = new EntityInsertionAdapter<Word>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `words` (`id`,`word`,`meaning`,`categoryId`,`groupId`,`note`,`createdAt`,`forgetCount`,`nextReviewAt`,`lastReviewedAt`,`batchId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Word entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getWord() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getWord());
        }
        if (entity.getMeaning() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMeaning());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getCategoryId());
        }
        if (entity.getGroupId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getGroupId());
        }
        if (entity.getNote() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getNote());
        }
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getForgetCount());
        statement.bindLong(9, entity.getNextReviewAt());
        statement.bindLong(10, entity.getLastReviewedAt());
        if (entity.getBatchId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getBatchId());
        }
      }
    };
    this.__insertionAdapterOfWordTag = new EntityInsertionAdapter<WordTag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `word_tag` (`wordId`,`tagId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WordTag entity) {
        statement.bindLong(1, entity.getWordId());
        statement.bindLong(2, entity.getTagId());
      }
    };
    this.__deletionAdapterOfWord = new EntityDeletionOrUpdateAdapter<Word>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `words` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Word entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfWordTag = new EntityDeletionOrUpdateAdapter<WordTag>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `word_tag` WHERE `wordId` = ? AND `tagId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WordTag entity) {
        statement.bindLong(1, entity.getWordId());
        statement.bindLong(2, entity.getTagId());
      }
    };
    this.__updateAdapterOfWord = new EntityDeletionOrUpdateAdapter<Word>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `words` SET `id` = ?,`word` = ?,`meaning` = ?,`categoryId` = ?,`groupId` = ?,`note` = ?,`createdAt` = ?,`forgetCount` = ?,`nextReviewAt` = ?,`lastReviewedAt` = ?,`batchId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Word entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getWord() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getWord());
        }
        if (entity.getMeaning() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMeaning());
        }
        if (entity.getCategoryId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getCategoryId());
        }
        if (entity.getGroupId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getGroupId());
        }
        if (entity.getNote() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getNote());
        }
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getForgetCount());
        statement.bindLong(9, entity.getNextReviewAt());
        statement.bindLong(10, entity.getLastReviewedAt());
        if (entity.getBatchId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getBatchId());
        }
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfMarkAsForgotten = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE words SET forgetCount = forgetCount + 1, nextReviewAt = ?, lastReviewedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsReviewed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE words SET nextReviewAt = ?, lastReviewedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllTagsForWord = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM word_tag WHERE wordId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetWordGroup = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE words SET groupId = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertWord(final Word word, final Continuation<? super Long> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfWord.insertAndReturnId(word);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object insertWords(final List<Word> words, final Continuation<? super List<Long>> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfWord.insertAndReturnIdsList(words);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object insertWordTag(final WordTag wordTag, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWordTag.insert(wordTag);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object deleteWord(final Word word, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWord.handle(word);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object deleteWordTag(final WordTag wordTag, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWordTag.handle(wordTag);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object updateWord(final Word word, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWord.handle(word);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object markAsForgotten(final long wordId, final long nextReviewAt, final long currentTime,
      final Continuation<? super Unit> arg3) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsForgotten.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, nextReviewAt);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, currentTime);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, wordId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkAsForgotten.release(_stmt);
        }
      }
    }, arg3);
  }

  @Override
  public Object markAsReviewed(final long wordId, final long nextReviewAt, final long currentTime,
      final Continuation<? super Unit> arg3) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsReviewed.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, nextReviewAt);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, currentTime);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, wordId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkAsReviewed.release(_stmt);
        }
      }
    }, arg3);
  }

  @Override
  public Object deleteAllTagsForWord(final long wordId, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllTagsForWord.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, wordId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllTagsForWord.release(_stmt);
        }
      }
    }, arg1);
  }

  @Override
  public Object setWordGroup(final long wordId, final Long groupId,
      final Continuation<? super Unit> arg2) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetWordGroup.acquire();
        int _argIndex = 1;
        if (groupId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, groupId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, wordId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetWordGroup.release(_stmt);
        }
      }
    }, arg2);
  }

  @Override
  public LiveData<List<Word>> getAllWords() {
    final String _sql = "SELECT * FROM words ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"words"}, false, new Callable<List<Word>>() {
      @Override
      @Nullable
      public List<Word> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfForgetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "forgetCount");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final List<Word> _result = new ArrayList<Word>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Word _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            if (_cursor.isNull(_cursorIndexOfWord)) {
              _tmpWord = null;
            } else {
              _tmpWord = _cursor.getString(_cursorIndexOfWord);
            }
            final String _tmpMeaning;
            if (_cursor.isNull(_cursorIndexOfMeaning)) {
              _tmpMeaning = null;
            } else {
              _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpGroupId;
            if (_cursor.isNull(_cursorIndexOfGroupId)) {
              _tmpGroupId = null;
            } else {
              _tmpGroupId = _cursor.getLong(_cursorIndexOfGroupId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpForgetCount;
            _tmpForgetCount = _cursor.getInt(_cursorIndexOfForgetCount);
            final long _tmpNextReviewAt;
            _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            final long _tmpLastReviewedAt;
            _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            final Long _tmpBatchId;
            if (_cursor.isNull(_cursorIndexOfBatchId)) {
              _tmpBatchId = null;
            } else {
              _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            }
            _item = new Word(_tmpId,_tmpWord,_tmpMeaning,_tmpCategoryId,_tmpGroupId,_tmpNote,_tmpCreatedAt,_tmpForgetCount,_tmpNextReviewAt,_tmpLastReviewedAt,_tmpBatchId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getWordById(final long wordId, final Continuation<? super Word> arg1) {
    final String _sql = "SELECT * FROM words WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, wordId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Word>() {
      @Override
      @Nullable
      public Word call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfForgetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "forgetCount");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final Word _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            if (_cursor.isNull(_cursorIndexOfWord)) {
              _tmpWord = null;
            } else {
              _tmpWord = _cursor.getString(_cursorIndexOfWord);
            }
            final String _tmpMeaning;
            if (_cursor.isNull(_cursorIndexOfMeaning)) {
              _tmpMeaning = null;
            } else {
              _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpGroupId;
            if (_cursor.isNull(_cursorIndexOfGroupId)) {
              _tmpGroupId = null;
            } else {
              _tmpGroupId = _cursor.getLong(_cursorIndexOfGroupId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpForgetCount;
            _tmpForgetCount = _cursor.getInt(_cursorIndexOfForgetCount);
            final long _tmpNextReviewAt;
            _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            final long _tmpLastReviewedAt;
            _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            final Long _tmpBatchId;
            if (_cursor.isNull(_cursorIndexOfBatchId)) {
              _tmpBatchId = null;
            } else {
              _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            }
            _result = new Word(_tmpId,_tmpWord,_tmpMeaning,_tmpCategoryId,_tmpGroupId,_tmpNote,_tmpCreatedAt,_tmpForgetCount,_tmpNextReviewAt,_tmpLastReviewedAt,_tmpBatchId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public LiveData<List<Word>> getWordsByCategory(final long categoryId) {
    final String _sql = "SELECT * FROM words WHERE categoryId = ? ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, categoryId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"words"}, false, new Callable<List<Word>>() {
      @Override
      @Nullable
      public List<Word> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfForgetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "forgetCount");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final List<Word> _result = new ArrayList<Word>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Word _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            if (_cursor.isNull(_cursorIndexOfWord)) {
              _tmpWord = null;
            } else {
              _tmpWord = _cursor.getString(_cursorIndexOfWord);
            }
            final String _tmpMeaning;
            if (_cursor.isNull(_cursorIndexOfMeaning)) {
              _tmpMeaning = null;
            } else {
              _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpGroupId;
            if (_cursor.isNull(_cursorIndexOfGroupId)) {
              _tmpGroupId = null;
            } else {
              _tmpGroupId = _cursor.getLong(_cursorIndexOfGroupId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpForgetCount;
            _tmpForgetCount = _cursor.getInt(_cursorIndexOfForgetCount);
            final long _tmpNextReviewAt;
            _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            final long _tmpLastReviewedAt;
            _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            final Long _tmpBatchId;
            if (_cursor.isNull(_cursorIndexOfBatchId)) {
              _tmpBatchId = null;
            } else {
              _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            }
            _item = new Word(_tmpId,_tmpWord,_tmpMeaning,_tmpCategoryId,_tmpGroupId,_tmpNote,_tmpCreatedAt,_tmpForgetCount,_tmpNextReviewAt,_tmpLastReviewedAt,_tmpBatchId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Word>> searchWords(final String query) {
    final String _sql = "SELECT * FROM words WHERE word LIKE '%' || ? || '%' OR meaning LIKE '%' || ? || '%' OR note LIKE '%' || ? || '%' ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 2;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 3;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"words"}, false, new Callable<List<Word>>() {
      @Override
      @Nullable
      public List<Word> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfForgetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "forgetCount");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final List<Word> _result = new ArrayList<Word>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Word _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            if (_cursor.isNull(_cursorIndexOfWord)) {
              _tmpWord = null;
            } else {
              _tmpWord = _cursor.getString(_cursorIndexOfWord);
            }
            final String _tmpMeaning;
            if (_cursor.isNull(_cursorIndexOfMeaning)) {
              _tmpMeaning = null;
            } else {
              _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpGroupId;
            if (_cursor.isNull(_cursorIndexOfGroupId)) {
              _tmpGroupId = null;
            } else {
              _tmpGroupId = _cursor.getLong(_cursorIndexOfGroupId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpForgetCount;
            _tmpForgetCount = _cursor.getInt(_cursorIndexOfForgetCount);
            final long _tmpNextReviewAt;
            _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            final long _tmpLastReviewedAt;
            _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            final Long _tmpBatchId;
            if (_cursor.isNull(_cursorIndexOfBatchId)) {
              _tmpBatchId = null;
            } else {
              _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            }
            _item = new Word(_tmpId,_tmpWord,_tmpMeaning,_tmpCategoryId,_tmpGroupId,_tmpNote,_tmpCreatedAt,_tmpForgetCount,_tmpNextReviewAt,_tmpLastReviewedAt,_tmpBatchId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Word>> getWordsDueForReview(final long currentTime) {
    final String _sql = "SELECT * FROM words WHERE nextReviewAt <= ? AND nextReviewAt > 0 ORDER BY nextReviewAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    return __db.getInvalidationTracker().createLiveData(new String[] {"words"}, false, new Callable<List<Word>>() {
      @Override
      @Nullable
      public List<Word> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfForgetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "forgetCount");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final List<Word> _result = new ArrayList<Word>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Word _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            if (_cursor.isNull(_cursorIndexOfWord)) {
              _tmpWord = null;
            } else {
              _tmpWord = _cursor.getString(_cursorIndexOfWord);
            }
            final String _tmpMeaning;
            if (_cursor.isNull(_cursorIndexOfMeaning)) {
              _tmpMeaning = null;
            } else {
              _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpGroupId;
            if (_cursor.isNull(_cursorIndexOfGroupId)) {
              _tmpGroupId = null;
            } else {
              _tmpGroupId = _cursor.getLong(_cursorIndexOfGroupId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpForgetCount;
            _tmpForgetCount = _cursor.getInt(_cursorIndexOfForgetCount);
            final long _tmpNextReviewAt;
            _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            final long _tmpLastReviewedAt;
            _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            final Long _tmpBatchId;
            if (_cursor.isNull(_cursorIndexOfBatchId)) {
              _tmpBatchId = null;
            } else {
              _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            }
            _item = new Word(_tmpId,_tmpWord,_tmpMeaning,_tmpCategoryId,_tmpGroupId,_tmpNote,_tmpCreatedAt,_tmpForgetCount,_tmpNextReviewAt,_tmpLastReviewedAt,_tmpBatchId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getWordsDueForReviewSync(final long currentTime,
      final Continuation<? super List<Word>> arg1) {
    final String _sql = "SELECT * FROM words WHERE nextReviewAt <= ? AND nextReviewAt > 0 ORDER BY nextReviewAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Word>>() {
      @Override
      @NonNull
      public List<Word> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfForgetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "forgetCount");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final List<Word> _result = new ArrayList<Word>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Word _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            if (_cursor.isNull(_cursorIndexOfWord)) {
              _tmpWord = null;
            } else {
              _tmpWord = _cursor.getString(_cursorIndexOfWord);
            }
            final String _tmpMeaning;
            if (_cursor.isNull(_cursorIndexOfMeaning)) {
              _tmpMeaning = null;
            } else {
              _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpGroupId;
            if (_cursor.isNull(_cursorIndexOfGroupId)) {
              _tmpGroupId = null;
            } else {
              _tmpGroupId = _cursor.getLong(_cursorIndexOfGroupId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpForgetCount;
            _tmpForgetCount = _cursor.getInt(_cursorIndexOfForgetCount);
            final long _tmpNextReviewAt;
            _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            final long _tmpLastReviewedAt;
            _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            final Long _tmpBatchId;
            if (_cursor.isNull(_cursorIndexOfBatchId)) {
              _tmpBatchId = null;
            } else {
              _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            }
            _item = new Word(_tmpId,_tmpWord,_tmpMeaning,_tmpCategoryId,_tmpGroupId,_tmpNote,_tmpCreatedAt,_tmpForgetCount,_tmpNextReviewAt,_tmpLastReviewedAt,_tmpBatchId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public LiveData<Integer> getReviewCount(final long currentTime) {
    final String _sql = "SELECT COUNT(*) FROM words WHERE nextReviewAt <= ? AND nextReviewAt > 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    return __db.getInvalidationTracker().createLiveData(new String[] {"words"}, false, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<List<Tag>> getTagsForWord(final long wordId) {
    final String _sql = "SELECT t.* FROM tags t INNER JOIN word_tag wt ON t.id = wt.tagId WHERE wt.wordId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, wordId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"tags",
        "word_tag"}, false, new Callable<List<Tag>>() {
      @Override
      @Nullable
      public List<Tag> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final List<Tag> _result = new ArrayList<Tag>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Tag _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            _item = new Tag(_tmpId,_tmpName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTagsForWordSync(final long wordId, final Continuation<? super List<Tag>> arg1) {
    final String _sql = "SELECT t.* FROM tags t INNER JOIN word_tag wt ON t.id = wt.tagId WHERE wt.wordId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, wordId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Tag>>() {
      @Override
      @NonNull
      public List<Tag> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final List<Tag> _result = new ArrayList<Tag>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Tag _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            _item = new Tag(_tmpId,_tmpName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public Object getWordsByGroupSync(final long groupId,
      final Continuation<? super List<Word>> arg1) {
    final String _sql = "SELECT * FROM words WHERE groupId = ? ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, groupId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Word>>() {
      @Override
      @NonNull
      public List<Word> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfForgetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "forgetCount");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final List<Word> _result = new ArrayList<Word>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Word _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            if (_cursor.isNull(_cursorIndexOfWord)) {
              _tmpWord = null;
            } else {
              _tmpWord = _cursor.getString(_cursorIndexOfWord);
            }
            final String _tmpMeaning;
            if (_cursor.isNull(_cursorIndexOfMeaning)) {
              _tmpMeaning = null;
            } else {
              _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpGroupId;
            if (_cursor.isNull(_cursorIndexOfGroupId)) {
              _tmpGroupId = null;
            } else {
              _tmpGroupId = _cursor.getLong(_cursorIndexOfGroupId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpForgetCount;
            _tmpForgetCount = _cursor.getInt(_cursorIndexOfForgetCount);
            final long _tmpNextReviewAt;
            _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            final long _tmpLastReviewedAt;
            _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            final Long _tmpBatchId;
            if (_cursor.isNull(_cursorIndexOfBatchId)) {
              _tmpBatchId = null;
            } else {
              _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            }
            _item = new Word(_tmpId,_tmpWord,_tmpMeaning,_tmpCategoryId,_tmpGroupId,_tmpNote,_tmpCreatedAt,_tmpForgetCount,_tmpNextReviewAt,_tmpLastReviewedAt,_tmpBatchId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public LiveData<List<Word>> getGroupedWords() {
    final String _sql = "SELECT * FROM words WHERE groupId IS NOT NULL ORDER BY groupId, createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"words"}, false, new Callable<List<Word>>() {
      @Override
      @Nullable
      public List<Word> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfForgetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "forgetCount");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final List<Word> _result = new ArrayList<Word>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Word _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            if (_cursor.isNull(_cursorIndexOfWord)) {
              _tmpWord = null;
            } else {
              _tmpWord = _cursor.getString(_cursorIndexOfWord);
            }
            final String _tmpMeaning;
            if (_cursor.isNull(_cursorIndexOfMeaning)) {
              _tmpMeaning = null;
            } else {
              _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpGroupId;
            if (_cursor.isNull(_cursorIndexOfGroupId)) {
              _tmpGroupId = null;
            } else {
              _tmpGroupId = _cursor.getLong(_cursorIndexOfGroupId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpForgetCount;
            _tmpForgetCount = _cursor.getInt(_cursorIndexOfForgetCount);
            final long _tmpNextReviewAt;
            _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            final long _tmpLastReviewedAt;
            _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            final Long _tmpBatchId;
            if (_cursor.isNull(_cursorIndexOfBatchId)) {
              _tmpBatchId = null;
            } else {
              _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            }
            _item = new Word(_tmpId,_tmpWord,_tmpMeaning,_tmpCategoryId,_tmpGroupId,_tmpNote,_tmpCreatedAt,_tmpForgetCount,_tmpNextReviewAt,_tmpLastReviewedAt,_tmpBatchId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getWordByWord(final String word, final Continuation<? super Word> arg1) {
    final String _sql = "SELECT * FROM words WHERE word = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (word == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, word);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Word>() {
      @Override
      @Nullable
      public Word call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfForgetCount = CursorUtil.getColumnIndexOrThrow(_cursor, "forgetCount");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final Word _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            if (_cursor.isNull(_cursorIndexOfWord)) {
              _tmpWord = null;
            } else {
              _tmpWord = _cursor.getString(_cursorIndexOfWord);
            }
            final String _tmpMeaning;
            if (_cursor.isNull(_cursorIndexOfMeaning)) {
              _tmpMeaning = null;
            } else {
              _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            }
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpGroupId;
            if (_cursor.isNull(_cursorIndexOfGroupId)) {
              _tmpGroupId = null;
            } else {
              _tmpGroupId = _cursor.getLong(_cursorIndexOfGroupId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpForgetCount;
            _tmpForgetCount = _cursor.getInt(_cursorIndexOfForgetCount);
            final long _tmpNextReviewAt;
            _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            final long _tmpLastReviewedAt;
            _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            final Long _tmpBatchId;
            if (_cursor.isNull(_cursorIndexOfBatchId)) {
              _tmpBatchId = null;
            } else {
              _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            }
            _result = new Word(_tmpId,_tmpWord,_tmpMeaning,_tmpCategoryId,_tmpGroupId,_tmpNote,_tmpCreatedAt,_tmpForgetCount,_tmpNextReviewAt,_tmpLastReviewedAt,_tmpBatchId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
