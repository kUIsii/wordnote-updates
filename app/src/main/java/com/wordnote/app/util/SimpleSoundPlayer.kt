package com.wordnote.app.util

import android.media.AudioManager
import android.media.ToneGenerator

class SimpleSoundPlayer {

    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)

    fun playCorrect() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 150)
    }

    fun playWrong() {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK, 200)
    }

    fun release() {
        toneGenerator.release()
    }
}
