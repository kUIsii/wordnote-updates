package com.wordnote.app.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0016\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b \n\u0002\u0010\u000b\n\u0002\b\u000f\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\u001e\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010!J\u0016\u0010\"\u001a\u00020\u001d2\u0006\u0010#\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010$J\u0016\u0010%\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\'J\u0016\u0010(\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u0016\u0010*\u001a\u00020\u001d2\u0006\u0010+\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010,J\u0016\u0010-\u001a\u00020\u001d2\u0006\u0010.\u001a\u00020\u001aH\u0086@\u00a2\u0006\u0002\u0010/J\u0014\u00100\u001a\b\u0012\u0004\u0012\u00020\u00140\u000fH\u0086@\u00a2\u0006\u0002\u00101J\u0018\u00102\u001a\u0004\u0018\u00010\u00102\u0006\u00103\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u0018\u00104\u001a\u0004\u0018\u00010\u00102\u0006\u00105\u001a\u000206H\u0086@\u00a2\u0006\u0002\u00107J\u0018\u00108\u001a\u0004\u0018\u00010\u00142\u0006\u00109\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u0012\u0010:\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020;0\u000f0\u000eJ\u001a\u0010<\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020=0\u000f0\u000e2\u0006\u0010\u001e\u001a\u00020\u001fJ\u001c\u0010>\u001a\b\u0012\u0004\u0012\u00020=0\u000f2\u0006\u0010\u001e\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u0012\u0010?\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001f0\u000f0\u000eJ\u0014\u0010@\u001a\b\u0012\u0004\u0012\u00020A0\u000e2\u0006\u0010B\u001a\u00020\u001fJ\u0018\u0010C\u001a\u0004\u0018\u00010\u00172\u0006\u0010 \u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u0018\u0010D\u001a\u0004\u0018\u00010\u00172\u0006\u00105\u001a\u000206H\u0086@\u00a2\u0006\u0002\u00107J\u001a\u0010E\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u000f0\u000e2\u0006\u0010\u001e\u001a\u00020\u001fJ\u001c\u0010F\u001a\b\u0012\u0004\u0012\u00020\u00170\u000f2\u0006\u0010\u001e\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u0018\u0010G\u001a\u0004\u0018\u00010\u001a2\u0006\u0010\u001e\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u0016\u0010H\u001a\u00020A2\u0006\u00103\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u0016\u0010I\u001a\u00020A2\u0006\u00109\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u001a\u0010J\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u000f0\u000e2\u0006\u00103\u001a\u00020\u001fJ\u001c\u0010K\u001a\b\u0012\u0004\u0012\u00020\u001a0\u000f2\u0006\u00109\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u001a\u0010L\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u000f0\u000e2\u0006\u0010B\u001a\u00020\u001fJ\u001c\u0010M\u001a\b\u0012\u0004\u0012\u00020\u001a0\u000f2\u0006\u0010B\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u0016\u0010N\u001a\u00020\u001f2\u0006\u0010#\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010$J\u0016\u0010O\u001a\u00020\u001f2\u0006\u0010&\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\'J\u001c\u0010P\u001a\u00020\u001d2\f\u0010Q\u001a\b\u0012\u0004\u0012\u00020=0\u000fH\u0086@\u00a2\u0006\u0002\u0010RJ\u0016\u0010S\u001a\u00020\u001f2\u0006\u0010+\u001a\u00020\u0017H\u0086@\u00a2\u0006\u0002\u0010,J\u0016\u0010T\u001a\u00020\u001f2\u0006\u0010.\u001a\u00020\u001aH\u0086@\u00a2\u0006\u0002\u0010/J\"\u0010U\u001a\b\u0012\u0004\u0012\u00020\u001f0\u000f2\f\u0010V\u001a\b\u0012\u0004\u0012\u00020\u001a0\u000fH\u0086@\u00a2\u0006\u0002\u0010RJ&\u0010W\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010X\u001a\u00020\u001f2\u0006\u0010B\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010YJ&\u0010Z\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010X\u001a\u00020\u001f2\u0006\u0010B\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010YJ\u0016\u0010[\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010)J\u001e\u0010\\\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u001fH\u0086@\u00a2\u0006\u0002\u0010!J\u001a\u0010]\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u000f0\u000e2\u0006\u0010^\u001a\u000206J\u001e\u0010_\u001a\u00020\u001d2\u0006\u0010`\u001a\u00020\u001f2\u0006\u0010a\u001a\u00020bH\u0086@\u00a2\u0006\u0002\u0010cJ\u001e\u0010d\u001a\u00020\u001d2\u0006\u0010`\u001a\u00020\u001f2\u0006\u0010e\u001a\u00020bH\u0086@\u00a2\u0006\u0002\u0010cJ \u0010f\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\b\u00109\u001a\u0004\u0018\u00010\u001fH\u0086@\u00a2\u0006\u0002\u0010gJ\u0016\u0010h\u001a\u00020\u001d2\u0006\u0010#\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010$J\u0016\u0010i\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\'J\u0016\u0010j\u001a\u00020\u001d2\u0006\u0010k\u001a\u00020=H\u0086@\u00a2\u0006\u0002\u0010lJ \u0010m\u001a\u00020\u001d2\u0006\u0010`\u001a\u00020\u001f2\b\u0010n\u001a\u0004\u0018\u000106H\u0086@\u00a2\u0006\u0002\u0010oJ\u0016\u0010p\u001a\u00020\u001d2\u0006\u0010.\u001a\u00020\u001aH\u0086@\u00a2\u0006\u0002\u0010/R\u001d\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u001d\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00140\u000f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u001d\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u000f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0012R\u001d\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u000f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0012R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006q"}, d2 = {"Lcom/wordnote/app/data/WordRepository;", "", "wordDao", "Lcom/wordnote/app/data/WordDao;", "categoryDao", "Lcom/wordnote/app/data/CategoryDao;", "tagDao", "Lcom/wordnote/app/data/TagDao;", "wordMeaningDao", "Lcom/wordnote/app/data/WordMeaningDao;", "wordGroupDao", "Lcom/wordnote/app/data/WordGroupDao;", "(Lcom/wordnote/app/data/WordDao;Lcom/wordnote/app/data/CategoryDao;Lcom/wordnote/app/data/TagDao;Lcom/wordnote/app/data/WordMeaningDao;Lcom/wordnote/app/data/WordGroupDao;)V", "allCategories", "Landroidx/lifecycle/LiveData;", "", "Lcom/wordnote/app/data/Category;", "getAllCategories", "()Landroidx/lifecycle/LiveData;", "allGroups", "Lcom/wordnote/app/data/WordGroup;", "getAllGroups", "allTags", "Lcom/wordnote/app/data/Tag;", "getAllTags", "allWords", "Lcom/wordnote/app/data/Word;", "getAllWords", "addTagToWord", "", "wordId", "", "tagId", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteCategory", "category", "(Lcom/wordnote/app/data/Category;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteGroup", "group", "(Lcom/wordnote/app/data/WordGroup;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMeaningsForWord", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteTag", "tag", "(Lcom/wordnote/app/data/Tag;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteWord", "word", "(Lcom/wordnote/app/data/Word;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllGroupsSync", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCategoryById", "categoryId", "getCategoryByName", "name", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getGroupById", "groupId", "getHighlightedMeanings", "Lcom/wordnote/app/data/HighlightedMeaning;", "getMeaningsForWord", "Lcom/wordnote/app/data/WordMeaning;", "getMeaningsForWordSync", "getProblematicWordIds", "getReviewCount", "", "currentTime", "getTagById", "getTagByName", "getTagsForWord", "getTagsForWordSync", "getWordById", "getWordCountForCategory", "getWordCountForGroup", "getWordsByCategory", "getWordsByGroup", "getWordsDueForReview", "getWordsDueForReviewSync", "insertCategory", "insertGroup", "insertMeanings", "meanings", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertTag", "insertWord", "insertWords", "words", "markAsForgotten", "nextReviewAt", "(JJJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markAsReviewed", "removeAllTagsFromWord", "removeTagFromWord", "searchWords", "query", "setMeaningHighlighted", "meaningId", "isHighlighted", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setMeaningProblematic", "isProblematic", "setWordGroup", "(JLjava/lang/Long;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateCategory", "updateGroup", "updateMeaning", "meaning", "(Lcom/wordnote/app/data/WordMeaning;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateMeaningNote", "note", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateWord", "app_debug"})
public final class WordRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.wordnote.app.data.WordDao wordDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.wordnote.app.data.CategoryDao categoryDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.wordnote.app.data.TagDao tagDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.wordnote.app.data.WordMeaningDao wordMeaningDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.wordnote.app.data.WordGroupDao wordGroupDao = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Word>> allWords = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Category>> allCategories = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Tag>> allTags = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.WordGroup>> allGroups = null;
    
    public WordRepository(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordDao wordDao, @org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.CategoryDao categoryDao, @org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.TagDao tagDao, @org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordMeaningDao wordMeaningDao, @org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordGroupDao wordGroupDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Word>> getAllWords() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertWord(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Word word, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertWords(@org.jetbrains.annotations.NotNull()
    java.util.List<com.wordnote.app.data.Word> words, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.Long>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateWord(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Word word, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteWord(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Word word, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getWordById(long wordId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.wordnote.app.data.Word> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Word>> getWordsByCategory(long categoryId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Word>> searchWords(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Word>> getWordsDueForReview(long currentTime) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getWordsDueForReviewSync(long currentTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.wordnote.app.data.Word>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object markAsForgotten(long wordId, long nextReviewAt, long currentTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object markAsReviewed(long wordId, long nextReviewAt, long currentTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Integer> getReviewCount(long currentTime) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Category>> getAllCategories() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertCategory(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Category category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateCategory(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Category category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteCategory(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Category category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCategoryById(long categoryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.wordnote.app.data.Category> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCategoryByName(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.wordnote.app.data.Category> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getWordCountForCategory(long categoryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Tag>> getAllTags() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertTag(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Tag tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteTag(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.Tag tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getTagById(long tagId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.wordnote.app.data.Tag> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getTagByName(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.wordnote.app.data.Tag> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addTagToWord(long wordId, long tagId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeTagFromWord(long wordId, long tagId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeAllTagsFromWord(long wordId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.Tag>> getTagsForWord(long wordId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getTagsForWordSync(long wordId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.wordnote.app.data.Tag>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertMeanings(@org.jetbrains.annotations.NotNull()
    java.util.List<com.wordnote.app.data.WordMeaning> meanings, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteMeaningsForWord(long wordId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.WordMeaning>> getMeaningsForWord(long wordId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getMeaningsForWordSync(long wordId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.wordnote.app.data.WordMeaning>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateMeaning(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordMeaning meaning, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setMeaningProblematic(long meaningId, boolean isProblematic, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setMeaningHighlighted(long meaningId, boolean isHighlighted, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateMeaningNote(long meaningId, @org.jetbrains.annotations.Nullable()
    java.lang.String note, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<java.lang.Long>> getProblematicWordIds() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.HighlightedMeaning>> getHighlightedMeanings() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.wordnote.app.data.WordGroup>> getAllGroups() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllGroupsSync(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.wordnote.app.data.WordGroup>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertGroup(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateGroup(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteGroup(@org.jetbrains.annotations.NotNull()
    com.wordnote.app.data.WordGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getGroupById(long groupId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.wordnote.app.data.WordGroup> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getWordCountForGroup(long groupId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getWordsByGroup(long groupId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.wordnote.app.data.Word>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setWordGroup(long wordId, @org.jetbrains.annotations.Nullable()
    java.lang.Long groupId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}