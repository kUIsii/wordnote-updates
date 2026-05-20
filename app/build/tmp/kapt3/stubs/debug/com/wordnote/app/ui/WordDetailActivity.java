package com.wordnote.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 -2\u00020\u0001:\u0001-B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0019\u001a\u00020\u001a2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001cH\u0002J\u0010\u0010\u001e\u001a\u00020\u001a2\u0006\u0010\u001f\u001a\u00020\u0006H\u0002J\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!H\u0002J\b\u0010#\u001a\u00020\u001aH\u0002J\b\u0010$\u001a\u00020\u001aH\u0002J\b\u0010%\u001a\u00020\u001aH\u0002J\u0012\u0010&\u001a\u00020\u001a2\b\u0010\'\u001a\u0004\u0018\u00010(H\u0014J\b\u0010)\u001a\u00020\u001aH\u0014J\b\u0010*\u001a\u00020\u001aH\u0002J\u0010\u0010+\u001a\u00020\u001a2\u0006\u0010,\u001a\u00020\u001dH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"Lcom/wordnote/app/ui/WordDetailActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "backButton", "Landroid/widget/ImageView;", "currentWord", "Lcom/wordnote/app/data/Word;", "deleteButton", "Lcom/google/android/material/button/MaterialButton;", "editButton", "forgetCountTextView", "Landroid/widget/TextView;", "forgottenButton", "meaningTextView", "meaningsCard", "Lcom/google/android/material/card/MaterialCardView;", "meaningsContainer", "Landroid/widget/LinearLayout;", "nextReviewTextView", "rememberedButton", "viewModel", "Lcom/wordnote/app/ui/WordViewModel;", "wordId", "", "wordTextView", "displayMeanings", "", "meanings", "", "Lcom/wordnote/app/data/WordMeaning;", "displayWord", "word", "dpToPx", "", "dp", "initViews", "loadMeanings", "loadWord", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "setupListeners", "showNoteDialog", "meaning", "Companion", "app_debug"})
public final class WordDetailActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXTRA_WORD_ID = "extra_word_id";
    private com.wordnote.app.ui.WordViewModel viewModel;
    private android.widget.ImageView backButton;
    private android.widget.TextView wordTextView;
    private android.widget.TextView meaningTextView;
    private android.widget.TextView forgetCountTextView;
    private android.widget.TextView nextReviewTextView;
    private com.google.android.material.button.MaterialButton forgottenButton;
    private com.google.android.material.button.MaterialButton rememberedButton;
    private com.google.android.material.button.MaterialButton editButton;
    private com.google.android.material.button.MaterialButton deleteButton;
    private android.widget.LinearLayout meaningsContainer;
    private com.google.android.material.card.MaterialCardView meaningsCard;
    private long wordId = -1L;
    @org.jetbrains.annotations.Nullable()
    private com.wordnote.app.data.Word currentWord;
    @org.jetbrains.annotations.NotNull()
    public static final com.wordnote.app.ui.WordDetailActivity.Companion Companion = null;
    
    public WordDetailActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initViews() {
    }
    
    private final void setupListeners() {
    }
    
    private final void loadWord() {
    }
    
    private final void loadMeanings() {
    }
    
    private final void displayWord(com.wordnote.app.data.Word word) {
    }
    
    private final void displayMeanings(java.util.List<com.wordnote.app.data.WordMeaning> meanings) {
    }
    
    private final void showNoteDialog(com.wordnote.app.data.WordMeaning meaning) {
    }
    
    private final int dpToPx(int dp) {
        return 0;
    }
    
    @java.lang.Override()
    protected void onResume() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/wordnote/app/ui/WordDetailActivity$Companion;", "", "()V", "EXTRA_WORD_ID", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}