package com.sunragav.home24.android_utils.animation

import android.animation.Animator
import androidx.appcompat.widget.AppCompatButton

fun AppCompatButton.animate(callback: () -> Unit) {
    animate().scaleX(0.7F).scaleY(0.7F).setDuration(100)
        .withEndAction {
            animate().scaleX(1F).scaleY(1F).setListener(null)
        }.setListener(object :
            Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                callback.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
}