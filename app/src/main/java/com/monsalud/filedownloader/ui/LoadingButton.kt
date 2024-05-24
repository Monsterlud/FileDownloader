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
import com.monsalud.filedownloader.ui.ButtonState
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
     * Progress bar progress
     */
    var progress = 0f

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

    /**
     * Create button & progress bar rectangles as well as a rectangle for the text
     * Create a ValueAnimator to animate the progress bar
     */
    private val buttonRectangle = RectF()
    private val textBounds = Rect()

    private val progressBar = RectF()
    val valueAnimator = ValueAnimator.ofFloat(0f, 100f).apply {
        duration = 3000
        addUpdateListener { animator ->
            progress = animator.animatedValue as Float
            invalidate()
        }
    }

    /**
     * Start the download simulation
     */
    fun startDownloadSimulation() {
        buttonState = ButtonState.Loading
        valueAnimator.start()

        CoroutineScope(Dispatchers.IO).launch {
            invalidate()
            progress = 0f
            while (progress < 100f) {
                delay(100)
                withContext(Dispatchers.Main) {
                    progress += 1f
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
        buttonPaint.color = if (buttonState == ButtonState.Loading) Color.GRAY else Color.BLUE
        canvas.drawRoundRect(buttonRectangle, 16f, 16f, buttonPaint)

        if (buttonState == ButtonState.Loading) {
            val progressWidth = width * (progress / 100f)
            progressBar.set(0f, 0f, progressWidth, height.toFloat())
            canvas.drawRect(progressBar, progressPaint)
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