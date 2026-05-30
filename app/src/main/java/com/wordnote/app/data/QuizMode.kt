package com.wordnote.app.data

enum class QuizMode(val id: Int, val displayName: String, val description: String) {
    EN_TO_CN(0, "英译中", "看英文单词，回忆中文释义"),
    CN_TO_EN(1, "中译英", "看中文释义，回忆英文单词"),
    SPELLING(2, "拼写测试", "看释义，拼写单词"),
    MIXED(3, "混合模式", "随机切换不同模式")
}
