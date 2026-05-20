package com.wordnote.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\t0\bH\'J\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\tH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0018\u0010\f\u001a\u0004\u0018\u00010\u00052\u0006\u0010\r\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0018\u0010\u0010\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0011\u001a\u00020\u0012H\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0014\u001a\u00020\u00152\u0006\u0010\r\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0016\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0018"}, d2 = {"Lcom/wordnote/app/data/CategoryDao;", "", "deleteCategory", "", "category", "Lcom/wordnote/app/data/Category;", "(Lcom/wordnote/app/data/Category;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllCategories", "Landroidx/lifecycle/LiveData;", "", "getAllCategoriesSync", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCategoryById", "categoryId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCategoryByName", "name", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getWordCountForCategory", "", "insertCategory", "updateCategory", "app_debug"})
@androidx.room.Dao()
public abstract interface CategoryDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertCategory(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Category category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateCategory(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Category category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteCategory(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Category category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM categories ORDER BY isDefault DESC, name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Category>> getAllCategories();
    
    @androidx.room.Query(value = "SELECT * FROM categories ORDER BY isDefault DESC, name ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllCategoriesSync(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.wordnote.app.data.Category>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM categories WHERE id = :categoryId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCategoryById(long categoryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.wordnote.app.data.Category> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM categories WHERE name = :name LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCategoryByName(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.wordnote.app.data.Category> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM words WHERE categoryId = :categoryId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWordCountForCategory(long categoryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}