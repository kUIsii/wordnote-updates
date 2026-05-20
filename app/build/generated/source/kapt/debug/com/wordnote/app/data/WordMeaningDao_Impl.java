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
public final class WordMeaningDao_Impl implements WordMeaningDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WordMeaning> __insertionAdapterOfWordMeaning;

  private final EntityDeletionOrUpdateAdapter<WordMeaning> __deletionAdapterOfWordMeaning;

  private final EntityDeletionOrUpdateAdapter<WordMeaning> __updateAdapterOfWordMeaning;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllForWord;

  private final SharedSQLiteStatement __preparedStmtOfSetProblematic;

  private final SharedSQLiteStatement __preparedStmtOfSetHighlighted;

  private final SharedSQLiteStatement __preparedStmtOfUpdateNote;

  public WordMeaningDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWordMeaning = new EntityInsertionAdapter<WordMeaning>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `word_meanings` (`id`,`wordId`,`meaningText`,`note`,`isProblematic`,`isHighlighted`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WordMeaning entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getWordId());
        if (entity.getMeaningText() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMeaningText());
        }
        if (entity.getNote() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNote());
        }
        final int _tmp = entity.isProblematic() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final int _tmp_1 = entity.isHighlighted() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
      }
    };
    this.__deletionAdapterOfWordMeaning = new EntityDeletionOrUpdateAdapter<WordMeaning>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `word_meanings` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WordMeaning entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfWordMeaning = new EntityDeletionOrUpdateAdapter<WordMeaning>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `word_meanings` SET `id` = ?,`wordId` = ?,`meaningText` = ?,`note` = ?,`isProblematic` = ?,`isHighlighted` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WordMeaning entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getWordId());
        if (entity.getMeaningText() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMeaningText());
        }
        if (entity.getNote() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNote());
        }
        final int _tmp = entity.isProblematic() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final int _tmp_1 = entity.isHighlighted() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllForWord = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM word_meanings WHERE wordId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetProblematic = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE word_meanings SET isProblematic = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetHighlighted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE word_meanings SET isHighlighted = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateNote = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE word_meanings SET note = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final WordMeaning meaning, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfWordMeaning.insertAndReturnId(meaning);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<WordMeaning> meanings,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWordMeaning.insert(meanings);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final WordMeaning meaning, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWordMeaning.handle(meaning);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final WordMeaning meaning, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfWordMeaning.handle(meaning);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllForWord(final long wordId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllForWord.acquire();
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
          __preparedStmtOfDeleteAllForWord.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setProblematic(final long meaningId, final boolean isProblematic,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetProblematic.acquire();
        int _argIndex = 1;
        final int _tmp = isProblematic ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, meaningId);
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
          __preparedStmtOfSetProblematic.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setHighlighted(final long meaningId, final boolean isHighlighted,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetHighlighted.acquire();
        int _argIndex = 1;
        final int _tmp = isHighlighted ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, meaningId);
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
          __preparedStmtOfSetHighlighted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateNote(final long meaningId, final String note,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateNote.acquire();
        int _argIndex = 1;
        if (note == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, note);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, meaningId);
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
          __preparedStmtOfUpdateNote.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<WordMeaning>> getMeaningsForWord(final long wordId) {
    final String _sql = "SELECT * FROM word_meanings WHERE wordId = ? ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, wordId);
    return __db.getInvalidationTracker().createLiveData(new String[] {"word_meanings"}, false, new Callable<List<WordMeaning>>() {
      @Override
      @Nullable
      public List<WordMeaning> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWordId = CursorUtil.getColumnIndexOrThrow(_cursor, "wordId");
          final int _cursorIndexOfMeaningText = CursorUtil.getColumnIndexOrThrow(_cursor, "meaningText");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsProblematic = CursorUtil.getColumnIndexOrThrow(_cursor, "isProblematic");
          final int _cursorIndexOfIsHighlighted = CursorUtil.getColumnIndexOrThrow(_cursor, "isHighlighted");
          final List<WordMeaning> _result = new ArrayList<WordMeaning>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WordMeaning _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpWordId;
            _tmpWordId = _cursor.getLong(_cursorIndexOfWordId);
            final String _tmpMeaningText;
            if (_cursor.isNull(_cursorIndexOfMeaningText)) {
              _tmpMeaningText = null;
            } else {
              _tmpMeaningText = _cursor.getString(_cursorIndexOfMeaningText);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final boolean _tmpIsProblematic;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProblematic);
            _tmpIsProblematic = _tmp != 0;
            final boolean _tmpIsHighlighted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsHighlighted);
            _tmpIsHighlighted = _tmp_1 != 0;
            _item = new WordMeaning(_tmpId,_tmpWordId,_tmpMeaningText,_tmpNote,_tmpIsProblematic,_tmpIsHighlighted);
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
  public Object getMeaningsForWordSync(final long wordId,
      final Continuation<? super List<WordMeaning>> $completion) {
    final String _sql = "SELECT * FROM word_meanings WHERE wordId = ? ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, wordId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<WordMeaning>>() {
      @Override
      @NonNull
      public List<WordMeaning> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWordId = CursorUtil.getColumnIndexOrThrow(_cursor, "wordId");
          final int _cursorIndexOfMeaningText = CursorUtil.getColumnIndexOrThrow(_cursor, "meaningText");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsProblematic = CursorUtil.getColumnIndexOrThrow(_cursor, "isProblematic");
          final int _cursorIndexOfIsHighlighted = CursorUtil.getColumnIndexOrThrow(_cursor, "isHighlighted");
          final List<WordMeaning> _result = new ArrayList<WordMeaning>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WordMeaning _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpWordId;
            _tmpWordId = _cursor.getLong(_cursorIndexOfWordId);
            final String _tmpMeaningText;
            if (_cursor.isNull(_cursorIndexOfMeaningText)) {
              _tmpMeaningText = null;
            } else {
              _tmpMeaningText = _cursor.getString(_cursorIndexOfMeaningText);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final boolean _tmpIsProblematic;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProblematic);
            _tmpIsProblematic = _tmp != 0;
            final boolean _tmpIsHighlighted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsHighlighted);
            _tmpIsHighlighted = _tmp_1 != 0;
            _item = new WordMeaning(_tmpId,_tmpWordId,_tmpMeaningText,_tmpNote,_tmpIsProblematic,_tmpIsHighlighted);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public LiveData<List<Long>> getProblematicWordIds() {
    final String _sql = "SELECT DISTINCT wordId FROM word_meanings WHERE isProblematic = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"word_meanings"}, false, new Callable<List<Long>>() {
      @Override
      @Nullable
      public List<Long> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Long> _result = new ArrayList<Long>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Long _item;
            if (_cursor.isNull(0)) {
              _item = null;
            } else {
              _item = _cursor.getLong(0);
            }
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
  public LiveData<List<HighlightedMeaning>> getHighlightedMeanings() {
    final String _sql = "SELECT wordId, meaningText FROM word_meanings WHERE isHighlighted = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"word_meanings"}, false, new Callable<List<HighlightedMeaning>>() {
      @Override
      @Nullable
      public List<HighlightedMeaning> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfWordId = 0;
          final int _cursorIndexOfMeaningText = 1;
          final List<HighlightedMeaning> _result = new ArrayList<HighlightedMeaning>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HighlightedMeaning _item;
            final long _tmpWordId;
            _tmpWordId = _cursor.getLong(_cursorIndexOfWordId);
            final String _tmpMeaningText;
            if (_cursor.isNull(_cursorIndexOfMeaningText)) {
              _tmpMeaningText = null;
            } else {
              _tmpMeaningText = _cursor.getString(_cursorIndexOfMeaningText);
            }
            _item = new HighlightedMeaning(_tmpWordId,_tmpMeaningText);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
