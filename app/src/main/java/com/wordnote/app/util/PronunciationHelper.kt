package com.wordnote.app.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class PronunciationHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            isReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    fun pronounce(word: String) {
        if (isReady) {
            tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "word_pronunciation")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
