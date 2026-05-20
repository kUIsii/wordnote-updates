package com.wordnote.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u001a\u001a\u00020\u001bH\u0002J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001dH\u0002J\n\u0010\u001f\u001a\u0004\u0018\u00010\u0018H\u0002J\b\u0010 \u001a\u00020\u001bH\u0002J\u0012\u0010!\u001a\u00020\u001b2\b\u0010\"\u001a\u0004\u0018\u00010#H\u0014J\b\u0010$\u001a\u00020\u001bH\u0014J\u0010\u0010%\u001a\u00020\u001b2\u0006\u0010&\u001a\u00020\'H\u0002J\u0010\u0010(\u001a\u00020\u001b2\u0006\u0010&\u001a\u00020\u0018H\u0002J\b\u0010)\u001a\u00020\u001bH\u0002J\u0010\u0010*\u001a\u00020\u001b2\u0006\u0010+\u001a\u00020,H\u0002J\b\u0010-\u001a\u00020\u001bH\u0002J\u0010\u0010.\u001a\u00020\u001b2\u0006\u0010/\u001a\u000200H\u0002J\b\u00101\u001a\u00020\u001bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082.\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00180\u00170\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u00062"}, d2 = {"Lcom/wordnote/app/ui/DictionaryActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "dictDb", "Lcom/wordnote/app/data/DictionaryDatabase;", "hintView", "Landroid/widget/LinearLayout;", "loadingView", "noDatabaseView", "noResultText", "Landroid/widget/TextView;", "noResultView", "resultPhonetic", "resultScrollView", "Landroid/widget/ScrollView;", "resultTranslation", "resultWord", "searchEditText", "Landroid/widget/EditText;", "selectDbButton", "Lcom/google/android/material/button/MaterialButton;", "selectFileLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "", "tagsContainer", "doSearch", "", "dpToPx", "", "dp", "getDbUri", "initViews", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "openDatabase", "uri", "Landroid/net/Uri;", "saveDbUri", "setupSearch", "showLoading", "show", "", "showNoDatabase", "showResult", "entry", "Lcom/wordnote/app/data/DictEntry;", "tryOpenSavedDb", "app_debug"})
public final class DictionaryActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.wordnote.app.data.DictionaryDatabase dictDb;
    private android.widget.EditText searchEditText;
    private android.widget.LinearLayout loadingView;
    private android.widget.LinearLayout hintView;
    private android.widget.LinearLayout noDatabaseView;
    private android.widget.LinearLayout noResultView;
    private android.widget.TextView noResultText;
    private android.widget.ScrollView resultScrollView;
    private android.widget.TextView resultWord;
    private android.widget.TextView resultPhonetic;
    private android.widget.TextView resultTranslation;
    private android.widget.LinearLayout tagsContainer;
    private com.google.android.material.button.MaterialButton selectDbButton;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String[]> selectFileLauncher = null;
    
    public DictionaryActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initViews() {
    }
    
    private final void setupSearch() {
    }
    
    private final void doSearch() {
    }
    
    private final void showResult(com.wordnote.app.data.DictEntry entry) {
    }
    
    private final void showLoading(boolean show) {
    }
    
    private final void tryOpenSavedDb() {
    }
    
    private final void openDatabase(android.net.Uri uri) {
    }
    
    private final void showNoDatabase() {
    }
    
    private final void saveDbUri(java.lang.String uri) {
    }
    
    private final java.lang.String getDbUri() {
        return null;
    }
    
    private final int dpToPx(int dp) {
        return 0;
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
}