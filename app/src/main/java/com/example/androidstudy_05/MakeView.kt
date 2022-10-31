package com.example.androidstudy_05

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import kotlin.math.roundToInt

class MakeView(context: Context) {

    private val context: Context = context

    fun makeLottieView(): LottieAnimationView {
        var lottie = LottieAnimationView(context).apply {
            setPadding(0, 0, changeDP(4), 0)
            setAnimation(R.raw.ani_sm_flot_tape)
            loop(true)
            playAnimation()
        }
        var layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        return lottie
    }

    private fun changeDP(value: Int): Int {
        var displayMetrics = context.resources.displayMetrics
        return (value * displayMetrics.density).roundToInt()
    }
}