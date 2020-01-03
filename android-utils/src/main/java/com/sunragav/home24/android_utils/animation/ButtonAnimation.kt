package com.sunragav.home24.android_utils.animation

import android.animation.Animator
import androidx.appcompat.widget.AppCompatButton

fun animateBtn(btn: AppCompatButton, callback: () -> Unit) {
    btn.animate().scaleX(0.7F).scaleY(0.7F).setDuration(100)
        .withEndAction {
            btn.animate().scaleX(1F).scaleY(1F)
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