package com.example.yumi.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.yumi.R // Assuming R class is accessible for custom attributes

/**
 * Placeholder for CircularProgressView.
 * Actual circular progress drawing and animation is needed.
 */
class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()
    private var progress: Int = 0
    private var progressColor: Int = Color.GREEN
    private var progressBackgroundColor: Int = Color.LTGRAY // Renamed to avoid conflict
    private var strokeWidthValue: Float = 8f // Default stroke width

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressView)
        try {
            progressColor = typedArray.getColor(R.styleable.CircularProgressView_progressColor, Color.GREEN)
            progressBackgroundColor = typedArray.getColor(R.styleable.CircularProgressView_progressBackgroundColor, Color.LTGRAY)
            strokeWidthValue = typedArray.getDimension(R.styleable.CircularProgressView_strokeWidth, 8f)
        } finally {
            typedArray.recycle()
        }

        backgroundPaint.color = progressBackgroundColor
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = strokeWidthValue

        progressPaint.color = progressColor
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = strokeWidthValue
        progressPaint.strokeCap = Paint.Cap.ROUND
    }

    fun setProgress(value: Int) {
        this.progress = value.coerceIn(0, 100)
        invalidate() // Redraw the view
    }

    fun setProgressColor(color: Int) {
        this.progressColor = color
        progressPaint.color = this.progressColor
        invalidate()
    }

    fun setProgressBackgroundColor(color: Int) {
        this.progressBackgroundColor = color
        backgroundPaint.color = this.progressBackgroundColor
        invalidate()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = strokeWidthValue / 2
        rectF.set(padding, padding, w - padding, h - padding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw background circle
        canvas.drawOval(rectF, backgroundPaint)
        // Draw progress arc
        val angle = 360 * (progress / 100f)
        canvas.drawArc(rectF, -90f, angle, false, progressPaint)
    }
}

// It's good practice to define custom attributes in res/values/attrs.xml
// Example for attrs.xml:
/*
<resources>
    <declare-styleable name="CircularProgressView">
        <attr name="progressColor" format="color" />
        <attr name="progressBackgroundColor" format="color" />
        <attr name="strokeWidth" format="dimension" />
    </declare-styleable>
</resources>
*/
