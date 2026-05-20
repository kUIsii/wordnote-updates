package com.wordnote.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\t0\bH\'J\u0018\u0010\n\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0018\u0010\u000e\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0013"}, d2 = {"Lcom/wordnote/app/data/TagDao;", "", "deleteTag", "", "tag", "Lcom/wordnote/app/data/Tag;", "(Lcom/wordnote/app/data/Tag;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllTags", "Landroidx/lifecycle/LiveData;", "", "getTagById", "tagId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTagByName", "name", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertTag", "app_debug"})
@androidx.room.Dao()
public abstract interface TagDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertTag(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Tag tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteTag(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Tag tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM tags ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Tag>> getAllTags();
    
    @androidx.room.Query(value = "SELECT * FROM tags WHERE id = :tagId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTagById(long tagId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.wordnote.app.data.Tag> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM tags WHERE name = :name LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTagByName(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.wordnote.app.data.Tag> $completion);
}