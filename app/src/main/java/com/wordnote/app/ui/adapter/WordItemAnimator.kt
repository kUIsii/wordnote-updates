package com.wordnote.app.ui.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class WordItemAnimator : DefaultItemAnimator() {

    init {
        addDuration = 300
        removeDuration = 200
        changeDuration = 200
        moveDuration = 300
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 50f
        holder.itemView.scaleX = 0.9f
        holder.itemView.scaleY = 0.9f

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(holder.itemView, "alpha", 0f, 1f),
            ObjectAnimator.ofFloat(holder.itemView, "translationY", 50f, 0f),
            ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0.9f, 1f),
            ObjectAnimator.ofFloat(holder.itemView, "scaleY", 0.9f, 1f)
        )
        animatorSet.duration = addDuration
        animatorSet.interpolator = DecelerateInterpolator()

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                dispatchAddFinished(holder)
            }
        })

        animatorSet.start()
        return true
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        holder.itemView.animate()
            .alpha(0f)
            .translationX(holder.itemView.width.toFloat())
            .setDuration(removeDuration)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                dispatchRemoveFinished(holder)
            }
            .start()
        return true
    }
}
