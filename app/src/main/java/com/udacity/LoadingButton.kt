package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var bgColor: Int = Color.BLACK
    private var textColor: Int = Color.BLACK
    private var inProgressBgColor: Int = Color.BLACK
    private var inProgressArcColor: Int = Color.BLACK

    private var buttonText: String = ""
    private val textRect = Rect()

    private var progress: Float = 0f

    private var valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when(new) {
            ButtonState.Loading -> {
                setButtonText("We are loading")
                valueAnimator= ValueAnimator.ofFloat(0f, 100f).apply {
                    addUpdateListener {
                        progress = animatedValue as Float
                        invalidate()
                    }
                    repeatMode = ValueAnimator.RESTART
                    repeatCount = ValueAnimator.INFINITE
                    duration = 6000
                    start()
                }
                disableButton()
            }

            ButtonState.Completed -> {
                setButtonText("Download")
                valueAnimator.cancel()
                progress = 0f
                enableButton()
            }

            ButtonState.Clicked -> {
            }
        }
        invalidate()
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0).apply {
            try {
                bgColor = getColor(
                    R.styleable.LoadingButton_bgColor,
                    ContextCompat.getColor(context, R.color.colorPrimary)
                )

                textColor = getColor(
                    R.styleable.LoadingButton_textColor,
                    ContextCompat.getColor(context, R.color.colorPrimary)
                )

                progress = getFloat(
                    R.styleable.LoadingButton_progress, 0f)
            } finally {
                recycle()
            }
        }

        inProgressBgColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        inProgressArcColor = ContextCompat.getColor(context, R.color.colorAccent)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.color = bgColor
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        paint.getTextBounds(buttonText, 0, buttonText.length, textRect)

        if (buttonState == ButtonState.Loading) {
            paint.color = inProgressBgColor
            canvas?.drawRect(
                0f, 0f,
                width * progress, height.toFloat(), paint
            )

            val arcDiameter = 60.0f
            val arcLeft= width.toFloat() / 2 + textRect.right / 2
            val arcTop = height.toFloat() / 2 - arcDiameter / 2
            paint.color = inProgressArcColor
            canvas?.drawArc(arcLeft,
                arcTop,
                arcLeft + arcDiameter,
                arcTop + arcDiameter,
                0f,
                360 * progress,
                true,
                paint)
        }

        paint.color = textColor
        val textCenterX = width.toFloat() / 2
        val textCenterY = height.toFloat() / 2 - textRect.centerY()
        canvas?.drawText(buttonText, textCenterX, textCenterY, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun disableButton() {
        loading_button.isEnabled = false
    }

    private fun enableButton() {
        loading_button.isEnabled = true
    }

    private fun setButtonText(text: String) {
        buttonText = text
        invalidate()
        requestLayout()
    }
}