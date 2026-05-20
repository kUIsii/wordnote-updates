package com.wordnote.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\fH\'J\u001c\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\r0\f2\u0006\u0010\b\u001a\u00020\tH\'J\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\r2\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\r0\fH\'J\u0016\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0013\u001a\u00020\u00032\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00050\rH\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u001e\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010\u001aJ\u001e\u0010\u001b\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u001c\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010\u001aJ\u0016\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J \u0010\u001e\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\t2\b\u0010\u001f\u001a\u0004\u0018\u00010 H\u00a7@\u00a2\u0006\u0002\u0010!\u00a8\u0006\""}, d2 = {"Lcom/wordnote/app/data/WordMeaningDao;", "", "delete", "", "meaning", "Lcom/wordnote/app/data/WordMeaning;", "(Lcom/wordnote/app/data/WordMeaning;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAllForWord", "wordId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getHighlightedMeanings", "Landroidx/lifecycle/LiveData;", "", "Lcom/wordnote/app/data/HighlightedMeaning;", "getMeaningsForWord", "getMeaningsForWordSync", "getProblematicWordIds", "insert", "insertAll", "meanings", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setHighlighted", "meaningId", "isHighlighted", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setProblematic", "isProblematic", "update", "updateNote", "note", "", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface WordMeaningDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordMeaning meaning, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertAll(@org.jetbrains.annotations.NotNull()
    java.util.List<com.wordnote.app.data.WordMeaning> meanings, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordMeaning meaning, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordMeaning meaning, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM word_meanings WHERE wordId = :wordId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllForWord(long wordId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM word_meanings WHERE wordId = :wordId ORDER BY id ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.WordMeaning>> getMeaningsForWord(long wordId);
    
    @androidx.room.Query(value = "SELECT * FROM word_meanings WHERE wordId = :wordId ORDER BY id ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMeaningsForWordSync(long wordId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.wordnote.app.data.WordMeaning>> $completion);
    
    @androidx.room.Query(value = "UPDATE word_meanings SET isProblematic = :isProblematic WHERE id = :meaningId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setProblematic(long meaningId, boolean isProblematic, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE word_meanings SET isHighlighted = :isHighlighted WHERE id = :meaningId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setHighlighted(long meaningId, boolean isHighlighted, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE word_meanings SET note = :note WHERE id = :meaningId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateNote(long meaningId, @org.jetbrains.annotations.Nullable()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT DISTINCT wordId FROM word_meanings WHERE isProblematic = 1")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<java.lang.Long>> getProblematicWordIds();
    
    @androidx.room.Query(value = "SELECT wordId, meaningText FROM word_meanings WHERE isHighlighted = 1")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.HighlightedMeaning>> getHighlightedMeanings();
}