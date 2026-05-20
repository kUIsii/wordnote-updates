package com.wordnote.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 %2\u00020\u0001:\u0001%B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0017\u001a\u00020\u0018H\u0002J\b\u0010\u0019\u001a\u00020\u0018H\u0002J\b\u0010\u001a\u001a\u00020\u0018H\u0002J\b\u0010\u001b\u001a\u00020\u0018H\u0002J\u0012\u0010\u001c\u001a\u00020\u00182\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0014J\u0010\u0010\u001f\u001a\u00020\u00182\u0006\u0010 \u001a\u00020!H\u0002J\b\u0010\"\u001a\u00020\u0018H\u0002J\b\u0010#\u001a\u00020\u0018H\u0002J\b\u0010$\u001a\u00020\u0018H\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u0016\u00a8\u0006&"}, d2 = {"Lcom/wordnote/app/ui/AddWordActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "categories", "", "Lcom/wordnote/app/data/Category;", "categorySpinner", "Landroid/widget/Spinner;", "existingMeanings", "Lcom/wordnote/app/data/WordMeaning;", "groupSpinner", "groups", "Lcom/wordnote/app/data/WordGroup;", "meaningEditText", "Landroid/widget/EditText;", "saveButton", "Lcom/google/android/material/button/MaterialButton;", "viewModel", "Lcom/wordnote/app/ui/WordViewModel;", "wordEditText", "wordId", "", "Ljava/lang/Long;", "initViews", "", "loadWordIfEditing", "observeCategories", "observeGroups", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "populateFields", "word", "Lcom/wordnote/app/data/Word;", "setupSaveButton", "setupToolbar", "showCreateGroupDialog", "Companion", "app_debug"})
public final class AddWordActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_WORD_ID = "extra_word_id";
    private com.wordnote.app.ui.WordViewModel viewModel;
    private android.widget.EditText wordEditText;
    private android.widget.EditText meaningEditText;
    private android.widget.Spinner categorySpinner;
    private android.widget.Spinner groupSpinner;
    private com.google.android.material.button.MaterialButton saveButton;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Long wordId;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.wordnote.app.data.Category> categories;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.wordnote.app.data.WordGroup> groups;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.wordnote.app.data.WordMeaning> existingMeanings;
    @org.jetbrains.annotations.NotNull()
    public static final com.wordnote.app.ui.AddWordActivity.Companion Companion = null;
    
    public AddWordActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initViews() {
    }
    
    private final void setupToolbar() {
    }
    
    private final void loadWordIfEditing() {
    }
    
    private final void populateFields(com.wordnote.app.data.Word word) {
    }
    
    private final void observeCategories() {
    }
    
    private final void observeGroups() {
    }
    
    private final void showCreateGroupDialog() {
    }
    
    private final void setupSaveButton() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/wordnote/app/ui/AddWordActivity$Companion;", "", "()V", "EXTRA_WORD_ID", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}