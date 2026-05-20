package com.wordnote.app.ui.adapter;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0002\u0003\u0004B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0002\u0005\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/wordnote/app/ui/adapter/ListItem;", "", "()V", "DateHeader", "WordItem", "Lcom/wordnote/app/ui/adapter/ListItem$DateHeader;", "Lcom/wordnote/app/ui/adapter/ListItem$WordItem;", "app_debug"})
public abstract class ListItem {
    
    private ListItem() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\n\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u00d6\u0003J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001J\t\u0010\u0012\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u0013"}, d2 = {"Lcom/wordnote/app/ui/adapter/ListItem$DateHeader;", "Lcom/wordnote/app/ui/adapter/ListItem;", "date", "", "dateLabel", "(Ljava/lang/String;Ljava/lang/String;)V", "getDate", "()Ljava/lang/String;", "getDateLabel", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
    public static final class DateHeader extends com.wordnote.app.ui.adapter.ListItem {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String date = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String dateLabel = null;
        
        public DateHeader(@org.jetbrains.annotations.NotNull()
        java.lang.String date, @org.jetbrains.annotations.NotNull()
        java.lang.String dateLabel) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getDate() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getDateLabel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.wordnote.app.ui.adapter.ListItem.DateHeader copy(@org.jetbrains.annotations.NotNull()
        java.lang.String date, @org.jetbrains.annotations.NotNull()
        java.lang.String dateLabel) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0007H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00072\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\fR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u001a"}, d2 = {"Lcom/wordnote/app/ui/adapter/ListItem$WordItem;", "Lcom/wordnote/app/ui/adapter/ListItem;", "word", "Lcom/wordnote/app/data/Word;", "index", "", "isFirstInBatch", "", "isLastInBatch", "(Lcom/wordnote/app/data/Word;IZZ)V", "getIndex", "()I", "()Z", "getWord", "()Lcom/wordnote/app/data/Word;", "component1", "component2", "component3", "component4", "copy", "equals", "other", "", "hashCode", "toString", "", "app_debug"})
    public static final class WordItem extends com.wordnote.app.ui.adapter.ListItem {
        @org.jetbrains.annotations.NotNull()
        private final com.wordnote.app.data.Word word = null;
        private final int index = 0;
        private final boolean isFirstInBatch = false;
        private final boolean isLastInBatch = false;
        
        public WordItem(@org.jetbrains.annotations.NotNull()
        com.wordnote.app.data.Word word, int index, boolean isFirstInBatch, boolean isLastInBatch) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.wordnote.app.data.Word getWord() {
            return null;
        }
        
        public final int getIndex() {
            return 0;
        }
        
        public final boolean isFirstInBatch() {
            return false;
        }
        
        public final boolean isLastInBatch() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.wordnote.app.data.Word component1() {
            return null;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final boolean component3() {
            return false;
        }
        
        public final boolean component4() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.wordnote.app.ui.adapter.ListItem.WordItem copy(@org.jetbrains.annotations.NotNull()
        com.wordnote.app.data.Word word, int index, boolean isFirstInBatch, boolean isLastInBatch) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}