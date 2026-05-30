package com.wordnote.app.util

object SpacedRepetition {
    data class ReviewResult(
        val interval: Int,
        val easeFactor: Float,
        val repetitions: Int,
        val nextReviewAt: Long
    )

    /**
     * SM-2 algorithm implementation
     * @param quality rating 0-5
     *   0 - complete blackout
     *   1 - wrong, but recalled after seeing answer
     *   2 - wrong, but answer was familiar
     *   3 - barely remembered
     *   4 - correct, but with some hesitation
     *   5 - perfect recall
     */
    fun calculateNextReview(
        quality: Int,
        repetitions: Int,
        easeFactor: Float,
        interval: Int
    ): ReviewResult {
        val q = quality.coerceIn(0, 5)
        val newEaseFactor = (easeFactor + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)).toFloat())
            .coerceAtLeast(1.3f)

        val newInterval: Int
        val newRepetitions: Int

        if (q < 3) {
            // Forgotten, reset
            newRepetitions = 0
            newInterval = 1
        } else {
            newRepetitions = repetitions + 1
            newInterval = when (newRepetitions) {
                1 -> 1
                2 -> 6
                else -> (interval * newEaseFactor).toInt()
            }
        }

        val nextReviewAt = System.currentTimeMillis() + newInterval * 24 * 60 * 60 * 1000L

        return ReviewResult(
            interval = newInterval,
            easeFactor = newEaseFactor,
            repetitions = newRepetitions,
            nextReviewAt = nextReviewAt
        )
    }

    /**
     * Convert simple good/bad to quality rating
     */
    fun simpleQuality(isGood: Boolean, hesitationTimeMs: Long = 0): Int {
        return if (isGood) {
            when {
                hesitationTimeMs < 3000 -> 5  // quick and perfect
                hesitationTimeMs < 8000 -> 4  // some hesitation
                else -> 3                      // barely remembered
            }
        } else {
            1  // wrong but recalled after seeing answer
        }
    }
}
