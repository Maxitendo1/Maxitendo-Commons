package com.maxitendo.indicatorfastscroll

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getColorStateListOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.maxitendo.commons.R

/**
 * Companion view for a [fast scroller][FastScrollerView] that shows its currently pressed indicator
 * in a small bubble near the user's finger.
 * This view should be vertically aligned with its fast scroller; its top and bottom should be
 * the same, though they don't necessarily need to have the same parent view. Horizontal placement
 * is independent of the fast scroller's.
 * A FastScrollerThumbView is not required for a fast scroller to work, but it provides an
 * out-of-the-box solution for visible feedback.
 *
 * @see setupWithFastScroller
 * @see FastScrollerView
 */
class FastScrollerThumbView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.indicatorFastScrollerThumbStyle
) : ConstraintLayout(
    context,
    attrs,
    defStyleAttr
), FastScrollerView.ItemIndicatorSelectedCallback {

    var thumbColor: ColorStateList by onUpdate(::applyStyle)
    var iconColor: Int by onUpdate(::applyStyle)
    var textAppearanceRes: Int by onUpdate(::applyStyle)
    var textColor: Int by onUpdate(::applyStyle)
    var fontSize: Float by onUpdate(::applyStyle)

    private val thumbView: ViewGroup
    private val textView: TextView
    private val iconView: ImageView

    private var fastScrollerView: FastScrollerView? = null

    private val thumbAnimation: SpringAnimation

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.FastScrollerThumbView,
            defStyleAttr,
            R.style.Widget_IndicatorFastScroll_FastScrollerThumb
        ).use { attrsArray ->
            throwIfMissingAttrs(styleRes = R.style.Widget_IndicatorFastScroll_FastScrollerThumb) {
                thumbColor = attrsArray.getColorStateListOrThrow(R.styleable.FastScrollerThumbView_fastScrollerThumbColor)
                iconColor = attrsArray.getColorOrThrow(R.styleable.FastScrollerThumbView_fastScrollerIconColor)
                textAppearanceRes = attrsArray.getResourceIdOrThrow(R.styleable.FastScrollerThumbView_android_textAppearance)
                textColor = attrsArray.getColorOrThrow(R.styleable.FastScrollerThumbView_android_textColor)
            }
        }
        fontSize = context.resources.getDimension(R.dimen.big_text_size)

        LayoutInflater.from(context).inflate(R.layout.fast_scroller_thumb_view, this, true)
        thumbView = findViewById(R.id.fast_scroller_thumb)
        textView = thumbView.findViewById(R.id.fast_scroller_thumb_text)
        iconView = thumbView.findViewById(R.id.fast_scroller_thumb_icon)

        applyStyle()

        thumbAnimation = SpringAnimation(thumbView, DynamicAnimation.TRANSLATION_Y).apply {
            spring = SpringForce().apply {
                dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            }
        }
    }

    /**
     * Sets up this [FastScrollerThumbView] to show the currently pressed item indicator for
     * [fastScrollerView]. It will follow the user's finger and is only visible when the fast scroller
     * is being used.
     * Only call this function once.
     *
     * @param fastScrollerView the [FastScrollerView] whose currently pressed indicator will be presented.
     */
    @SuppressLint("ClickableViewAccessibility")
    fun setupWithFastScroller(fastScrollerView: FastScrollerView) {
        this.fastScrollerView = fastScrollerView

        fastScrollerView.itemIndicatorSelectedCallbacks += this
        fastScrollerView.onItemIndicatorTouched = { isTouched ->
            isActivated = isTouched
        }
    }

    private fun applyStyle() {
        thumbView.stateListAnimator?.let { animator ->
            // Workaround for StateListAnimator not keeping its state in sync with its drawable pre-attach
            if (!thumbView.isAttachedToWindow) {
                thumbView.doOnPreDraw {
                    animator.jumpToCurrentState()
                }
            }
        }
        thumbView.backgroundTintList = thumbColor
        if (Build.VERSION.SDK_INT == 21) {
            // Workaround for 21 background tint bug
            (thumbView.background as GradientDrawable).apply {
                mutate()
                color = thumbColor
            }
        }
        TextViewCompat.setTextAppearance(textView, textAppearanceRes)
        textView.setTextColor(textColor)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
        iconView.imageTintList = ColorStateList.valueOf(iconColor)
    }

    override fun onItemIndicatorSelected(
        indicator: FastScrollItemIndicator,
        indicatorCenterY: Int,
        itemPosition: Int,
        moveInstantly: Boolean
    ) {
        val thumbTargetY = indicatorCenterY.toFloat() - (thumbView.measuredHeight / 2)

        if (moveInstantly) {
            thumbView.y = thumbTargetY
        } else {
            thumbAnimation.animateToFinalPosition(thumbTargetY)
        }

        when (indicator) {
            is FastScrollItemIndicator.Text -> {
                textView.isVisible = true
                iconView.isVisible = false

                textView.text = indicator.text
            }
            is FastScrollItemIndicator.Icon -> {
                textView.isVisible = false
                iconView.isVisible = true

                iconView.setImageResource(indicator.iconRes)
            }
        }
    }
}
