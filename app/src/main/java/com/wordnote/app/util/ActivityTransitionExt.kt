package com.wordnote.app.util

import android.app.Activity
import android.os.Build
import androidx.annotation.AnimRes

private const val OVERRIDE_TRANSITION_OPEN = 0
private const val OVERRIDE_TRANSITION_CLOSE = 1

fun Activity.compatOverridePendingTransition(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, enterAnim, exitAnim)
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(enterAnim, exitAnim)
    }
}

fun Activity.compatOverridePendingTransitionClose(@AnimRes enterAnim: Int, @AnimRes exitAnim: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, enterAnim, exitAnim)
    } else {
        @Suppress("DEPRECATION")
        overridePendingTransition(enterAnim, exitAnim)
    }
}
