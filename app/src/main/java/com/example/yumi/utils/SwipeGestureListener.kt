package com.example.yumi.utils

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class SwipeGestureListener(
    context: Context, // Added context to initialize gestureDetector immediately
    private val onSwipeRight: (() -> Unit)? = null,
    private val onSwipeLeft: (() -> Unit)? = null,
    private val onSwipeUp: (() -> Unit)? = null,
    private val onSwipeDown: (() -> Unit)? = null
) : View.OnTouchListener {

    // GestureDetector now initialized in the constructor
    private val gestureDetector: GestureDetector = GestureDetector(context, GestureListener())

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        // gestureDetector is already initialized
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true // Important to return true here to consume the event.
        }

        override fun onFling(
            e1: MotionEvent?, // Made e1 nullable to match superclass
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null) return false // Guard against null e1

            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            return when {
                abs(diffX) > abs(diffY) -> {
                    // Horizontal swipe
                    when {
                        abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD -> {
                            if (diffX > 0) {
                                onSwipeRight?.invoke()
                            } else {
                                onSwipeLeft?.invoke()
                            }
                            true
                        }
                        else -> false
                    }
                }
                else -> {
                    // Vertical swipe
                    when {
                        abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD -> {
                            if (diffY > 0) {
                                onSwipeDown?.invoke()
                            } else {
                                onSwipeUp?.invoke()
                            }
                            true
                        }
                        else -> false
                    }
                }
            }
        }
    }
}

// Extension function for easy use
fun View.setSwipeListener(
    onSwipeRight: (() -> Unit)? = null,
    onSwipeLeft: (() -> Unit)? = null,
    onSwipeUp: (() -> Unit)? = null,
    onSwipeDown: (() -> Unit)? = null
) {
    setOnTouchListener(SwipeGestureListener(this.context, onSwipeRight, onSwipeLeft, onSwipeUp, onSwipeDown))
}
