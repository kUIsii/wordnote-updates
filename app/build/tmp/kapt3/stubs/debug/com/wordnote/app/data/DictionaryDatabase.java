package com.wordnote.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\t\u001a\u00020\nJ\u0006\u0010\u000b\u001a\u00020\bJ\u000e\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u000eJ\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/wordnote/app/data/DictionaryDatabase;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "database", "Landroid/database/sqlite/SQLiteDatabase;", "isOpen", "", "close", "", "isReady", "open", "uri", "Landroid/net/Uri;", "search", "Lcom/wordnote/app/data/DictEntry;", "word", "", "app_debug"})
public final class DictionaryDatabase {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.Nullable()
    private android.database.sqlite.SQLiteDatabase database;
    private boolean isOpen = false;
    
    public DictionaryDatabase(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final boolean open(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri) {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.wordnote.app.data.DictEntry search(@org.jetbrains.annotations.NotNull()
    java.lang.String word) {
        return null;
    }
    
    public final boolean isReady() {
        return false;
    }
    
    public final void close() {
    }
}