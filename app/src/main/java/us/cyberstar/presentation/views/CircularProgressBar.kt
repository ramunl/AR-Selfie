package us.cyberstar.presentation.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import timber.log.Timber
import us.cyberstar.arcyber.R
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


private const val STROKE_THICKNESS_FRACTION = 0.045f
private const val TEXT_SIZE_FRACTION = 0.25f
private const val MAX_PROGRESS = 100f
private const val ANIM_DURATION_MS = 100L

class CircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){
    init {
        Timber.d("CircularProgressBar init")
       // setOnTouchListener(this)
        /*setOnLongClickListener {
            Timber.d("CircularProgressBar onLongClick")
            startCounter()
            true
        }*/
    }

    interface ProgressListener {
        fun onStart()
        fun onStop()
    }

    var progressListener: ProgressListener? = null

    var timer: Timer? = null
    fun startCounter() {
        Timber.d("startCounter")
        if (timer == null) {
            progressListener?.onStart()
            timer = Timer().apply {
                scheduleAtFixedRate(0, 500) {
                    updateProgress(progress + 1)
                }
            }
        }
    }

    fun stopCounter() {
        Timber.d("stopCounter")
        timer?.cancel()
        timer = null
        progressListener?.onStop()
    }

    //onTouch code
   /* override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startCounter()
            }
            MotionEvent.ACTION_UP -> {
                stopCounter()
            }
            MotionEvent.ACTION_MOVE -> {
            }
        }//This is where my code for movement is initialized to get original location.
        //Code for movement here. This may include using a window manager to update the view
        return false
    }*/

    private var strokeThickness: Float = 0f

    private var progressTextSize: Float = 0f

    // pre-allocate and reuse in onDraw()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val circleBounds = RectF()

    private val progressTextBounds = Rect()

    private var progress = 0f

    private val progressValueAnimator = ValueAnimator.ofFloat(0f, progress).apply {
        duration = ANIM_DURATION_MS
        interpolator = LinearInterpolator()
        addUpdateListener {
            progress = animatedValue as Float
            invalidate()
        }
    }

    private var backgroundColor: Int? = null

    private var foregroundColor: Int? = null

    private var textColor: Int? = null

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it,
                R.styleable.CircularProgressBar, 0, 0
            )

            backgroundColor = typedArray.getColor(
                R.styleable.CircularProgressBar_barBackgroundColor,
                Color.GRAY
            )

            foregroundColor = typedArray.getColor(
                R.styleable.CircularProgressBar_barForegroundColor,
                Color.BLACK
            )

            textColor = typedArray.getColor(
                R.styleable.CircularProgressBar_android_textColor,
                Color.BLACK
            )

            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawForeground(canvas)
        //drawProgressText(canvas)
    }

    /**
     * Triggered when you set width and height explicitly or when the view gets resized inside another container
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val diameter = Math.min(w, h)
        strokeThickness = diameter * STROKE_THICKNESS_FRACTION

        val centerX = w / 2
        val centerY = h / 2
        val squareSide = diameter - strokeThickness
        val halfOfStrokeWidth = squareSide / 2

        circleBounds.apply {
            left = centerX - halfOfStrokeWidth
            top = centerY - halfOfStrokeWidth
            right = centerX + halfOfStrokeWidth
            bottom = centerY + halfOfStrokeWidth
        }

        progressTextSize = diameter * TEXT_SIZE_FRACTION
    }

    private fun updateProgress(progress: Float) {
        handler.post {
            if (progressValueAnimator.isRunning) {
                progressValueAnimator.cancel()
            }
            Log.d("", "updateProgress $progress")
            progressValueAnimator.setFloatValues(this.progress, progress)
            progressValueAnimator.start()
        }
    }

    private fun drawBackground(canvas: Canvas) {
        paint.apply {
            backgroundColor?.let { color = it }
            style = Paint.Style.STROKE
            strokeWidth = strokeThickness
        }
        canvas.drawOval(circleBounds, paint)
    }

    private fun drawForeground(canvas: Canvas) {
        paint.apply {
            foregroundColor?.let { color = it }
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        val sweepAngle = progress / MAX_PROGRESS * 360
        canvas.drawArc(circleBounds, -90f, sweepAngle, false, paint)
    }

    private fun drawProgressText(canvas: Canvas) {
        val text = "${progress.toInt()}%"
        paint.apply {
            textColor?.let { color = it }
            style = Paint.Style.FILL
            textSize = progressTextSize
            textAlign = Paint.Align.CENTER
            getTextBounds(text, 0, text.length, progressTextBounds)
        }
        canvas.drawText(
            text,
            circleBounds.centerX(),
            circleBounds.centerY() + progressTextBounds.height() / 2,
            paint
        )
    }
}