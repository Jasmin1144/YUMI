package com.example.yumi.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

/**
 * Placeholder for SparkleView.
 * Actual sparkle particle animation is needed.
 */
class SparkleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val particles = mutableListOf<SparkleParticle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null

    // TODO: Implement realistic sparkle effect
    // This could involve:
    // - Particle system logic (birth, death, movement, fading)
    // - Different sparkle shapes/colors
    // - Custom attributes for density, speed, colors

    init {
        paint.color = Color.YELLOW // Default sparkle color
    }

    fun startSparkle() {
        if (animator?.isRunning == true) {
            return // Already running
        }
        if (width == 0 || height == 0) {
            // Wait for layout if view not measured yet
            post { if (isAttachedToWindow) actualStartSparkle() }
            return
        }
        actualStartSparkle()
    }

    private fun actualStartSparkle() {
        particles.clear()
        repeat(50) { // Number of particles
            particles.add(SparkleParticle(
                Random.nextFloat() * width,
                Random.nextFloat() * height,
                Random.nextFloat() * 2f + 1f, // size
                (Random.nextFloat() - 0.5f) * 2f, // xSpeed
                (Random.nextFloat() - 0.5f) * 2f  // ySpeed
            ))
        }

        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000 // Animation duration
            repeatCount = ValueAnimator.INFINITE // Loop indefinitely or for a set time
            addUpdateListener {
                particles.forEach { particle ->
                    particle.update()
                    // Reset particle if it goes off-screen or fades out
                    if (particle.alpha <= 0 || particle.x < 0 || particle.x > width || particle.y < 0 || particle.y > height) {
                        particle.reset(width.toFloat(), height.toFloat())
                    }
                }
                invalidate()
            }
            start()
        }
    }


    fun stopSparkle() {
        animator?.cancel()
        particles.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        particles.forEach { particle ->
            paint.alpha = particle.alpha
            canvas.drawCircle(particle.x, particle.y, particle.size, paint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopSparkle() // Clean up animator when view is detached
    }

    private data class SparkleParticle(
        var x: Float,
        var y: Float,
        var size: Float,
        var xSpeed: Float,
        var ySpeed: Float,
        var alpha: Int = 255
    ) {
        fun update() {
            x += xSpeed
            y += ySpeed
            alpha = (alpha - 2).coerceAtLeast(0) // Fade out
        }

        fun reset(maxWidth: Float, maxHeight: Float) {
            x = Random.nextFloat() * maxWidth
            y = Random.nextFloat() * maxHeight
            alpha = 255
            xSpeed = (Random.nextFloat() - 0.5f) * 2f
            ySpeed = (Random.nextFloat() - 0.5f) * 2f
        }
    }
}
