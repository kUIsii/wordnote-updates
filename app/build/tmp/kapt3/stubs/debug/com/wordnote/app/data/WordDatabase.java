package com.wordnote.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&J\b\u0010\u000b\u001a\u00020\fH&\u00a8\u0006\u000e"}, d2 = {"Lcom/wordnote/app/data/WordDatabase;", "Landroidx/room/RoomDatabase;", "()V", "categoryDao", "Lcom/wordnote/app/data/CategoryDao;", "tagDao", "Lcom/wordnote/app/data/TagDao;", "wordDao", "Lcom/wordnote/app/data/WordDao;", "wordGroupDao", "Lcom/wordnote/app/data/WordGroupDao;", "wordMeaningDao", "Lcom/wordnote/app/data/WordMeaningDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.wordnote.app.data.Word.class, com.wordnote.app.data.Category.class, com.wordnote.app.data.Tag.class, com.wordnote.app.data.WordTag.class, com.wordnote.app.data.WordMeaning.class, com.wordnote.app.data.WordGroup.class}, version = 6, exportSchema = false)
public abstract class WordDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.wordnote.app.data.WordDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_1_2 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_2_3 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_3_4 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_4_5 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_5_6 = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.wordnote.app.data.WordDatabase.Companion Companion = null;
    
    public WordDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.wordnote.app.data.WordDao wordDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.wordnote.app.data.CategoryDao categoryDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.wordnote.app.data.TagDao tagDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.wordnote.app.data.WordMeaningDao wordMeaningDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.wordnote.app.data.WordGroupDao wordGroupDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0001\u0016B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u0015R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\bR\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\bR\u0011\u0010\r\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\bR\u0011\u0010\u000f\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\b\u00a8\u0006\u0017"}, d2 = {"Lcom/wordnote/app/data/WordDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/wordnote/app/data/WordDatabase;", "MIGRATION_1_2", "Landroidx/room/migration/Migration;", "getMIGRATION_1_2", "()Landroidx/room/migration/Migration;", "MIGRATION_2_3", "getMIGRATION_2_3", "MIGRATION_3_4", "getMIGRATION_3_4", "MIGRATION_4_5", "getMIGRATION_4_5", "MIGRATION_5_6", "getMIGRATION_5_6", "clearInstance", "", "getDatabase", "context", "Landroid/content/Context;", "DatabaseCallback", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final void clearInstance() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.room.migration.Migration getMIGRATION_1_2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.room.migration.Migration getMIGRATION_2_3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.room.migration.Migration getMIGRATION_3_4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.room.migration.Migration getMIGRATION_4_5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.room.migration.Migration getMIGRATION_5_6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.wordnote.app.data.WordDatabase getDatabase(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0016\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\nJ\u001e\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u0010\u00a8\u0006\u0011"}, d2 = {"Lcom/wordnote/app/data/WordDatabase$Companion$DatabaseCallback;", "Landroidx/room/RoomDatabase$Callback;", "()V", "onCreate", "", "db", "Landroidx/sqlite/db/SupportSQLiteDatabase;", "populateDefaultCategories", "categoryDao", "Lcom/wordnote/app/data/CategoryDao;", "(Lcom/wordnote/app/data/CategoryDao;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "populateSampleWords", "wordDao", "Lcom/wordnote/app/data/WordDao;", "wordMeaningDao", "Lcom/wordnote/app/data/WordMeaningDao;", "(Lcom/wordnote/app/data/WordDao;Lcom/wordnote/app/data/WordMeaningDao;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
        static final class DatabaseCallback extends androidx.room.RoomDatabase.Callback {
            
            public DatabaseCallback() {
                super();
            }
            
            @java.lang.Override()
            public void onCreate(@org.jetbrains.annotations.NotNull()
            androidx.sqlite.db.SupportSQLiteDatabase db) {
            }
            
            @org.jetbrains.annotations.Nullable()
            public final java.lang.Object populateDefaultCategories(@org.jetbrains.annotations.NotNull()
            com.wordnote.app.data.CategoryDao categoryDao, @org.jetbrains.annotations.NotNull()
            kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
                return null;
            }
            
            @org.jetbrains.annotations.Nullable()
            public final java.lang.Object populateSampleWords(@org.jetbrains.annotations.NotNull()
            com.wordnote.app.data.WordDao wordDao, @org.jetbrains.annotations.NotNull()
            com.wordnote.app.data.WordMeaningDao wordMeaningDao, @org.jetbrains.annotations.NotNull()
            kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
                return null;
            }
        }
    }
}