package com.monsalud.filedownloader.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.monsalud.filedownloader.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * ButtonState: Clicked, Loading, Completed
     */
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
    }

    /**
     * Progress bar & progress circle progress
     */
    private var barProgress = 0f
    private var circleProgress = 0f

    /**
     * Paint objects for button, text, and progress bar
     */
    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 40f
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.DKGRAY
    }
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.colorAccent)
    }

    /**
     * Create button, progress bar, and text rectangles as well as a progress circle
     * Create a ValueAnimators to animate the progress bar & progress circle
     */
    private val buttonRectangle = RectF()
    private val progressBar = RectF()
    private val circleRectangle = RectF()
    private val textBounds = Rect()

    private val progressBarAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 100f).apply {
        duration = 3000
        addUpdateListener { animator ->
            barProgress = animator.animatedValue as Float
            invalidate()
        }
    }
    private val circleAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 3000
        addUpdateListener { animator ->
            circleProgress = animator.animatedValue as Float
            invalidate()
        }
    }

    /**
     * Start the download simulation
     */
    fun startDownloadSimulation() {
        buttonState = ButtonState.Loading
        progressBarAnimator.start()
        circleAnimator.start()

        CoroutineScope(Dispatchers.IO).launch {
            invalidate()
            barProgress = 0f
            circleProgress = 0f
            while (barProgress < 100f) {
                delay(100)
                withContext(Dispatchers.Main) {
                    barProgress += 1f
                    postInvalidate()
                }
                postInvalidate()
            }
            withContext(Dispatchers.Main) {
                buttonState = ButtonState.Completed

            }
        }.start()
    }

    /**
     * Draw the button, text, and progress bar on the canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        buttonRectangle.set(0f, 0f, width.toFloat(), height.toFloat())
        buttonPaint.style = Paint.Style.FILL
        buttonPaint.color = if (buttonState == ButtonState.Loading) Color.GRAY else resources.getColor(R.color.colorPrimary)
        canvas.drawRoundRect(buttonRectangle, 16f, 16f, buttonPaint)

        if (buttonState == ButtonState.Loading) {
            // Draw progress bar
            val progressWidth = width * (barProgress / 100f)
            progressBar.set(0f, 0f, progressWidth, height.toFloat())
            canvas.drawRect(progressBar, progressPaint)

            // Draw progress circle
            val centerY = height / 2f
            val radius = min(height / 1.2f, 60f)
            val circleRight = width.toFloat() - radius - 10f
            circleRectangle.set(circleRight - 2 * radius, centerY - radius, circleRight, centerY + radius)
            canvas.drawArc(circleRectangle, 270f, circleProgress, true, circlePaint)
        }

        val text = if (buttonState == ButtonState.Loading) "We are loading!" else "Download"
        textPaint.textSize = 60f
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        val textX = width / 2f
        val textY = height / 2f + textBounds.height() / 2f
        canvas.drawText(text, textX, textY, textPaint)
    }

    /**
     * Measure the width and height of the view
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 200
        val desiredHeight = 50

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }
}