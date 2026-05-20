package com.wordnote.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\t0\bH\'J\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\tH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0018\u0010\f\u001a\u0004\u0018\u00010\u00052\u0006\u0010\r\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0010\u001a\u00020\u00112\u0006\u0010\r\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0014"}, d2 = {"Lcom/wordnote/app/data/WordGroupDao;", "", "delete", "", "group", "Lcom/wordnote/app/data/WordGroup;", "(Lcom/wordnote/app/data/WordGroup;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllGroups", "Landroidx/lifecycle/LiveData;", "", "getAllGroupsSync", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getGroupById", "groupId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getWordCountForGroup", "", "insert", "update", "app_debug"})
@androidx.room.Dao()
public abstract interface WordGroupDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM word_groups ORDER BY createdAt DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.WordGroup>> getAllGroups();
    
    @androidx.room.Query(value = "SELECT * FROM word_groups ORDER BY createdAt DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllGroupsSync(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.wordnote.app.data.WordGroup>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM word_groups WHERE id = :groupId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getGroupById(long groupId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.wordnote.app.data.WordGroup> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM words WHERE groupId = :groupId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWordCountForGroup(long groupId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}