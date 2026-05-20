package com.wordnote.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0015\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000b\u001a\u00020\fH\u0002J\u0012\u0010\r\u001a\u00020\f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0014J\b\u0010\u0010\u001a\u00020\fH\u0002J\b\u0010\u0011\u001a\u00020\fH\u0002J\b\u0010\u0012\u001a\u00020\fH\u0002J\b\u0010\u0013\u001a\u00020\fH\u0002J\u001c\u0010\u0014\u001a\u00020\f2\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\f0\u0016H\u0002J\u0010\u0010\u0017\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001a\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lcom/wordnote/app/ui/CategoryActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "categoryAdapter", "Lcom/wordnote/app/ui/adapter/CategoryAdapter;", "presetColors", "", "selectedColor", "", "viewModel", "Lcom/wordnote/app/ui/WordViewModel;", "observeData", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setupAddButton", "setupRecyclerView", "setupToolbar", "showAddCategoryDialog", "showColorPickerDialog", "onColorSelected", "Lkotlin/Function1;", "showDeleteConfirmation", "category", "Lcom/wordnote/app/data/Category;", "showEditCategoryDialog", "app_debug"})
public final class CategoryActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.wordnote.app.ui.WordViewModel viewModel;
    private com.wordnote.app.ui.adapter.CategoryAdapter categoryAdapter;
    private int selectedColor;
    @org.jetbrains.annotations.NotNull()
    private final int[] presetColors = null;
    
    public CategoryActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupToolbar() {
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void setupAddButton() {
    }
    
    private final void observeData() {
    }
    
    private final void showAddCategoryDialog() {
    }
    
    private final void showColorPickerDialog(kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onColorSelected) {
    }
    
    private final void showEditCategoryDialog(com.wordnote.app.data.Category category) {
    }
    
    private final void showDeleteConfirmation(com.wordnote.app.data.Category category) {
    }
}