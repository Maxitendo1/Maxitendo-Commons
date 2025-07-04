/*
 * Copyright 2018 Quiph Media Pvt Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.maxitendo.recyclerfastscroller

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewPropertyAnimator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.Keep
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import com.maxitendo.commons.R

/**
 * Sets a custom scroller for [RecyclerView].
 *
 * For best results use the FastScroller without a scrollbar set in the [RecyclerView]
 *
 * To set programmatically call the [attachFastScrollerToRecyclerView] once the [RecyclerView.LayoutManager] and [RecyclerView.Adapter] are initialized, else if a child of this view is a [RecyclerView] then the method is called directly,
 * without the programmer explicitly needing to call it.
 *
 * **Direction:** The direction of the [RecyclerViewFastScroller] can be set using the [R.styleable.RecyclerViewFastScroller_fastScrollDirection] view attrib.
 *
 * **Scroll layout drawing:** The drawing of the scrollbar layout can be changed during layout creation using the [R.styleable.RecyclerViewFastScroller_popupPosition] param which takes
 * `PopupPosition.BEFORE_TRACK` and `PopupPosition.AFTER_TRACK` attribs
 *
 * **Layout customizations:**
 *  1. Popup Layout: The popup layout background can be changed both programmatically with [RecyclerViewFastScroller.popupDrawable] and using the [R.styleable.RecyclerViewFastScroller_popupDrawable] view attrib.
 *  2. Handle Layout: The handle layout background can be changed both programmatically with [RecyclerViewFastScroller.handleDrawable] and using the [R.styleable.RecyclerViewFastScroller_handleDrawable] view attrib.
 *  3. Track Layout: The track layout background can be changed both programmatically with [RecyclerViewFastScroller.trackDrawable] and using the [R.styleable.RecyclerViewFastScroller_trackDrawable] view attrib.
 *  4. Popup [TextView] appearance can be changed both programmatically with [RecyclerViewFastScroller.textStyle] and using the [R.styleable.RecyclerViewFastScroller_popupTextStyle] view attrib.
 *
 *  By default the last item of the [RecyclerView] associated with the fastScroller has an extra padding of the height of the first visible item found, to disable this behaviour set the [R.styleable.RecyclerViewFastScroller_addLastItemPadding] as `false`
 *
 *  To disable fastScroll, set the [RecyclerViewFastScroller.isFastScrollEnabled] as `false`, default set is true
 *
 *  **Handle Size:** The fastScroller automatically adjusts the size of the handle, but if [RecyclerViewFastScroller.isFixedSizeHandle] is set as true handle [R.styleable.RecyclerViewFastScroller_handleHeight]
 *  and [R.styleable.RecyclerViewFastScroller_handleWidth] need to be provided else default value of 18dp will be taken for both.
 *
 * @version 1.0
 * */

class RecyclerViewFastScroller @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG: String = "RVFastScroller"
        private const val ERROR_MESSAGE_NO_RECYCLER_VIEW = "The RecyclerView required for initialization with FastScroller cannot be null"
        private const val DARK_GREY = 0xFF333333.toInt()
    }

    enum class FastScrollDirection(val value: Int) {
        HORIZONTAL(1), VERTICAL(0);

        companion object {
            fun getFastScrollDirectionByValue(value: Int = Defaults.fastScrollDirection.value): FastScrollDirection {
                for (fsDirection in values()) {
                    if (fsDirection.value == value) return fsDirection
                }
                return Defaults.fastScrollDirection
            }
        }
    }

    private enum class PopupPosition(val value: Int) {
        BEFORE_TRACK(0), AFTER_TRACK(1);

        companion object {
            fun getPopupPositionByValue(value: Int = Defaults.popupPosition.value): PopupPosition {
                for (popupPosition: PopupPosition in values()) {
                    if (popupPosition.value == value)
                        return popupPosition
                }
                return Defaults.popupPosition
            }
        }
    }

    // defaults to be used throughout this class. All these values can be overriden in the individual methods provided for the main class
    private object Defaults {
        val popupDrawableInt: Int = R.drawable.popup_bg_primary
        val handleDrawableInt: Int = R.drawable.custom_bg_primary
        val handleHeight: Int = R.dimen.default_handle_height
        val handleWidth: Int = R.dimen.default_handle_width
        val textStyle: Int = R.style.FastScrollerTextAppearance
        val popupPosition: PopupPosition = PopupPosition.BEFORE_TRACK
        val fastScrollDirection: FastScrollDirection = FastScrollDirection.VERTICAL
        const val isFixedSizeHandle: Boolean = false
        const val isFastScrollEnabled: Boolean = true
        const val animationDuration: Long = 100
        const val popupVisibilityDuration = 200L
        const val handleVisibilityDuration: Int = 2000
        const val trackMargin: Int = 0
    }

    /**
     * Sets a track background drawable to the track used, default is `null`
     * */
    var trackDrawable: Drawable?
        set(value) {
            trackView.background = value
        }
        get() = trackView.background

    /**
     * Sets background drawable to the [TextView] used in the popup
     * */
    var popupDrawable: Drawable?
        set(value) {
            popupTextView.background = value
        }
        get() = popupTextView.background

    /**
     * Sets a drawable to the handle used to scroll with
     **/
    var handleDrawable: Drawable?
        set(value) {
            handleImageView.setImageDrawable(requireNotNull(value) { "No drawable found for the given ID" })
        }
        get() = handleImageView.drawable

    /**
     * Sets a style to the [TextView] used in the popup displayed
     **/
    @StyleRes
    var textStyle: Int = Defaults.textStyle
        set(value) {
            TextViewCompat.setTextAppearance(popupTextView, value)
        }

    /**
     * todo@shahsurajk handleFixedSizes
     **/
    private var isFixedSizeHandle: Boolean = Defaults.isFixedSizeHandle

    /**
     * If set to `false`, the fastScroll behavior is disabled, Default is `true`
     **/
    var isFastScrollEnabled: Boolean = Defaults.isFastScrollEnabled

    /**
     * The [TextView] which is used to display the popup text.
     **/
    lateinit var popupTextView: TextView
    private var currentPopupItemPosition = -1

    var trackMarginStart: Int = 0
        set(value) {
            field = value
            setTrackMargin()
        }

    var trackMarginEnd: Int = 0
        set(value) {
            field = value
            setTrackMargin()
        }

    var fastScrollDirection: FastScrollDirection = Defaults.fastScrollDirection
        set(value) {
            field = value
            alignTrackAndHandle()
        }

    var handleWidth: Int = LayoutParams.WRAP_CONTENT
        set(value) {
            field = value
            refreshHandleImageViewSize()
        }

    var handleHeight: Int = LayoutParams.WRAP_CONTENT
        set(value) {
            field = value
            refreshHandleImageViewSize()
        }

    /**
     * The duration for which the handle should remain visible, defaults to -1 (don't hide)
     * */
    var handleVisibilityDuration: Int = -1

    // --- internal properties
    private var popupPosition: PopupPosition = Defaults.popupPosition
    private lateinit var handleImageView: AppCompatImageView
    private lateinit var trackView: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private var popupAnimationRunnable: Runnable
    private var isEngaged: Boolean = false
    private var handleStateListener: HandleStateListener? = null
    private var previousTotalVisibleItem: Int = 0
    private var hideHandleJob: Job? = null

    private val trackLength: Float
        get() = when (fastScrollDirection) {
            FastScrollDirection.VERTICAL -> trackView.height
            FastScrollDirection.HORIZONTAL -> trackView.width
        }.toFloat()

    private val handleLength: Float
        get() = when (fastScrollDirection) {
            FastScrollDirection.HORIZONTAL -> handleImageView.width
            FastScrollDirection.VERTICAL -> handleImageView.height
        }.toFloat()

    private val popupLength: Float
        get() = when (fastScrollDirection) {
            FastScrollDirection.HORIZONTAL -> popupTextView.width
            FastScrollDirection.VERTICAL -> popupTextView.height
        }.toFloat()

    // computeHorizontalScrollRange and computeHorizontalScrollOffset are unreliable with GridLayoutManager and spanCount > 1, so calculate height and offset
    // manually if required. Can be used only in cases when every item has the same height
    var calculateScrollPositionManually = false
    var fullContentHeight = 0
    private var scrollOffset = 0

    // property check
    /**
     * Checks if the [FastScrollDirection] is [FastScrollDirection.VERTICAL] or not
     *
     * @return `true` if yes else `false`
     * */
    // val isVertical : Boolean = fastScrollDirection == FastScrollDirection.VERTICAL

    // load attributes to set view based values
    private val attribs: TypedArray? = if (attrs != null) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.RecyclerViewFastScroller, 0, 0)
    } else {
        null
    }

    init {
        // add popup layout and thumb layout.
        addPopupLayout()
        addThumbAndTrack()

        attribs?.let {
            if (attribs.hasValue(R.styleable.RecyclerViewFastScroller_popupPosition)) {
                popupPosition = PopupPosition.getPopupPositionByValue(
                    attribs.getInt(
                        R.styleable.RecyclerViewFastScroller_popupPosition,
                        Defaults.popupPosition.value
                    )
                )
            }
            if (attribs.hasValue(R.styleable.RecyclerViewFastScroller_fastScrollDirection)) {
                fastScrollDirection = FastScrollDirection.getFastScrollDirectionByValue(
                    attribs.getInt(
                        R.styleable.RecyclerViewFastScroller_fastScrollDirection,
                        Defaults.fastScrollDirection.value
                    )
                )
            }

            isFixedSizeHandle = attribs.getBoolean(
                R.styleable.RecyclerViewFastScroller_handleHasFixedSize,
                Defaults.isFixedSizeHandle
            )

            isFastScrollEnabled = attribs.getBoolean(
                R.styleable.RecyclerViewFastScroller_fastScrollEnabled,
                Defaults.isFastScrollEnabled
            )

            trackView.background = attribs.getDrawable(R.styleable.RecyclerViewFastScroller_trackDrawable)

            if (it.getBoolean(R.styleable.RecyclerViewFastScroller_supportSwipeToRefresh, false)) {
                enableNestedScrolling()
            }

            // align added layouts based on configurations in use.
            alignTrackAndHandle()
            alignPopupLayout()

            // if not defined, set default popupTextView background
            popupTextView.background = if (attribs.hasValue(R.styleable.RecyclerViewFastScroller_popupDrawable)) {
                loadDrawableFromAttributes(R.styleable.RecyclerViewFastScroller_popupDrawable)
            } else {
                ContextCompat.getDrawable(context, Defaults.popupDrawableInt)
            }

            if (!attribs.hasValue(R.styleable.RecyclerViewFastScroller_popupDrawable)) {
                popupTextView.minimumWidth = resources.getDimensionPixelSize(R.dimen.fs_popup_min_width)
                popupTextView.minimumHeight = resources.getDimensionPixelSize(R.dimen.fs_popup_min_height)
            }
            //popupTextView.alpha = 0.6f

            // set default handleImageView drawable if not defined
            handleDrawable = (loadDrawableFromAttributes(R.styleable.RecyclerViewFastScroller_handleDrawable)
                ?: ContextCompat.getDrawable(context, Defaults.handleDrawableInt))
            handleDrawable!!.alpha = 153

            handleVisibilityDuration = attribs.getInt(
                R.styleable.RecyclerViewFastScroller_handleVisibilityDuration,
                Defaults.handleVisibilityDuration
            )

            handleHeight = attribs.getDimensionPixelSize(
                R.styleable.RecyclerViewFastScroller_handleHeight,
                loadDimenFromResource(Defaults.handleHeight)
            )

            handleWidth = attribs.getDimensionPixelSize(
                R.styleable.RecyclerViewFastScroller_handleWidth,
                loadDimenFromResource(Defaults.handleWidth)
            )

            trackMarginStart = attribs.getDimensionPixelSize(
                R.styleable.RecyclerViewFastScroller_trackMarginStart,
                Defaults.trackMargin
            )

            trackMarginEnd = attribs.getDimensionPixelSize(
                R.styleable.RecyclerViewFastScroller_trackMarginEnd,
                Defaults.trackMargin
            )

            TextViewCompat.setTextAppearance(
                popupTextView,
                attribs.getResourceId(
                    R.styleable.RecyclerViewFastScroller_popupTextStyle,
                    Defaults.textStyle
                )
            )

            attribs.recycle()
        }
        popupAnimationRunnable = Runnable { popupTextView.animateVisibility(false) }
    }

    override fun onDetachedFromWindow() {
        detachFastScrollerFromRecyclerView()
        super.onDetachedFromWindow()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onFinishInflate() {
        super.onFinishInflate()
        // skip the two children add, aka popup and track and check if the RecyclerView is added via XML or not, if added call the attach directly.
        if (childCount > 2) {
            for (childAt: Int in 2 until childCount) {
                val currentView = getChildAt(childAt)
                if (currentView is RecyclerView) {
                    removeView(currentView)
                    addView(currentView, 0)
                    attachFastScrollerToRecyclerView(currentView)
                }
            }
        }
        post {
            val touchListener = OnTouchListener { _, motionEvent ->
                val locationArray = IntArray(2)

                // getting the position of this view on the screen, getting absolute X and Y coordinates
                trackView.getLocationInWindow(locationArray)
                val (xAbsPosition, yAbsPosition) = Pair(locationArray[0], locationArray[1])

                val touchAction = motionEvent.action.and(motionEvent.actionMasked)
                log("Touch Action: $touchAction")

                when (touchAction) {
                    MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {

                        // disallow parent to spy on touch events
                        requestDisallowInterceptTouchEvent(true)

                        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                            if (!adapterDataObserver.isInitialized()) {
                                registerDataObserver()
                            }

                            // set the engaged flag to prevent the handle from scrolling again as the OnScrolled event in the ScrollListener is called even for programmatic scrolls
                            isEngaged = true

                            if (isFastScrollEnabled) {
                                handleStateListener?.onEngaged()
                                // make the popup visible only if fastScroll is enabled
                                popupTextView.animateVisibility()
                            }
                        }

                        //
                        // ------------- Common methods to Move and Down events -------------------
                        // calculate relative Y position internal to the view, from motion absolute px touch value and absolute start point of the view
                        //

                        // subtract the handle height offset
                        val handleOffset = handleLength / 2

                        val currentRelativePos = when (fastScrollDirection) {
                            FastScrollDirection.HORIZONTAL -> motionEvent.rawX - xAbsPosition - handleOffset
                            FastScrollDirection.VERTICAL -> motionEvent.rawY - yAbsPosition - handleOffset
                        }

                        // move the handle only if fastScrolled, else leave the translation of the handle to the onScrolled method on the listener

                        if (isFastScrollEnabled) {
                            moveHandle(currentRelativePos)

                            val position = recyclerView.computePositionForOffsetAndScroll(currentRelativePos)
                            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                                handleStateListener?.onDragged(
                                    when (fastScrollDirection) {
                                        FastScrollDirection.HORIZONTAL -> handleImageView.x
                                        FastScrollDirection.VERTICAL -> handleImageView.y
                                    }, position
                                )
                            }
                            updateTextInPopup(
                                min((recyclerView.adapter?.itemCount ?: 0) - 1, position)
                            )
                        } else {
                            when ((recyclerView.layoutManager as LinearLayoutManager).orientation) {
                                RecyclerView.HORIZONTAL -> recyclerView.scrollBy(currentRelativePos.toInt(), 0)
                                RecyclerView.VERTICAL -> recyclerView.scrollBy(0, currentRelativePos.toInt())
                            }
                        }

                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isEngaged = false
                        if (isFastScrollEnabled) {
                            handleStateListener?.onReleased()
                            // hide the popup with a default anim delay
                            handler.postDelayed(
                                popupAnimationRunnable,
                                Defaults.popupVisibilityDuration
                            )
                        }
                        super.onTouchEvent(motionEvent)
                    }
                    else -> false
                }
            }

            // set the same touch listeners to both handle and track as they have the same functionality
            if (isFastScrollEnabled) {
                handleImageView.setOnTouchListener(touchListener)
            }
        }
    }

    private fun alignPopupLayout() {
        val lpPopupLayout = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).also {
            when (popupPosition) {
                PopupPosition.BEFORE_TRACK -> {
                    when (fastScrollDirection) {
                        FastScrollDirection.HORIZONTAL -> it.addRule(ABOVE, trackView.id)
                        FastScrollDirection.VERTICAL -> it.addRule(START_OF, trackView.id)
                    }
                }
                PopupPosition.AFTER_TRACK -> {
                    when (fastScrollDirection) {
                        FastScrollDirection.HORIZONTAL -> it.addRule(BELOW, trackView.id)
                        FastScrollDirection.VERTICAL -> it.addRule(END_OF, trackView.id)
                    }
                }
            }
        }
        popupTextView.layoutParams = lpPopupLayout
    }

    private fun alignTrackAndHandle() {
        val startPaddingId = if (isRTLLayout()) {
            R.dimen.default_handle_right_padding
        } else {
            R.dimen.default_handle_left_padding
        }

        val endPaddingId = if (isRTLLayout()) {
            R.dimen.default_handle_left_padding
        } else {
            R.dimen.default_handle_right_padding
        }

        val leftPadding = resources.getDimensionPixelOffset(startPaddingId)
        val rightPadding = resources.getDimensionPixelOffset(endPaddingId)
        when (fastScrollDirection) {
            FastScrollDirection.HORIZONTAL -> {
                handleImageView.setPadding(0, leftPadding, 0, rightPadding)
                popupTextView.layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).also { it.addRule(ALIGN_BOTTOM, R.id.trackView) }
                trackView.layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                ).also { it.addRule(ALIGN_PARENT_BOTTOM) }
            }
            FastScrollDirection.VERTICAL -> {
                handleImageView.setPadding(leftPadding, 0, rightPadding, 0)
                popupTextView.layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).also {
                    it.addRule(ALIGN_END, R.id.trackView)
                }
                trackView.layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT
                ).also {
                    it.addRule(ALIGN_PARENT_END)
                }
            }
        }
        post {
            when (fastScrollDirection) {
                FastScrollDirection.HORIZONTAL -> {
                    handleImageView.y = 0F
                    popupTextView.y = trackView.y - popupTextView.height
                }
                FastScrollDirection.VERTICAL -> {
                    handleImageView.x = 0F
                    popupTextView.x = if (isRTLLayout()) {
                        trackView.x + popupTextView.width
                    } else {
                        trackView.x - popupTextView.width
                    }
                }
            }

            onScrollListener.onScrolled(recyclerView, 0, 0)
        }
    }

    private fun setTrackMargin() {
        with(trackView.layoutParams as MarginLayoutParams) {
            when (fastScrollDirection) {
                FastScrollDirection.HORIZONTAL -> {
                    marginStart = trackMarginStart
                    marginEnd = trackMarginEnd
                }
                FastScrollDirection.VERTICAL -> setMargins(0, trackMarginStart, 0, trackMarginEnd)
            }
        }
    }

    private fun refreshHandleImageViewSize(newComputedSize: Int = -1) {
        // todo@shahsurajk add fork for horizontal layout
        if (newComputedSize == -1) {
            handleImageView.layoutParams = LinearLayout.LayoutParams(handleWidth, handleHeight)
        } else {
            TODO("@shahsurajk dynamic sizing of handle")
        }
    }

    private fun addThumbAndTrack() {
        View.inflate(context, R.layout.fastscroller_track_thumb, this)
        handleImageView = findViewById(R.id.thumbIV)
        trackView = findViewById(R.id.trackView)
    }

    private fun addPopupLayout() {
        View.inflate(context, R.layout.fastscroller_popup, this)
        popupTextView = findViewById(R.id.fastscrollPopupTV)
    }

    private fun isRTLLayout() = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

    /**
     * Sets [isNestedScrollingEnabled] to true to enable support for fast scrolling when swipe
     * refresh layout is added as parent layout.
     */
    private fun enableNestedScrolling() {
        isNestedScrollingEnabled = true
    }

    private fun moveHandle(offset: Float) {
        post {
            // animateVisibility() without animation
            handleImageView.scaleX = 1F
            handleImageView.scaleY = 1F
        }

        if (handleVisibilityDuration > 0) {
            hideHandleJob?.cancel()

            hideHandleJob = CoroutineScope(Dispatchers.Main).launch {
                delay(handleVisibilityDuration.toLong())
                handleImageView.animateVisibility(false)
            }
        }

        moveViewToRelativePositionWithBounds(handleImageView, offset)
        moveViewToRelativePositionWithBounds(popupTextView, offset - popupLength + trackMarginStart)
    }

    /**
     * Checks the bounds of the view before moving
     *
     * @param view the view to move and
     * @param finalOffset the offset to move to
     * */
    private fun moveViewToRelativePositionWithBounds(view: View, finalOffset: Float) {
        when (fastScrollDirection) {
            FastScrollDirection.HORIZONTAL -> view.x = min(max(finalOffset, 0F), (trackLength - view.width.toFloat()))
            FastScrollDirection.VERTICAL -> view.y = min(max(finalOffset, 0F), (trackLength - view.height.toFloat()))
        }
    }

    /**
     * Custom animator extension, as [ViewPropertyAnimator] doesn't have individual listeners, de-cluttering, also present in android KTX
     * */
    private inline fun ViewPropertyAnimator.onAnimationCancelled(crossinline body: () -> Unit) {
        this.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator) {}

            override fun onAnimationEnd(p0: Animator) {}

            override fun onAnimationStart(p0: Animator) {}

            override fun onAnimationCancel(p0: Animator) {
                body()
            }
        })
    }

    /**
     * Animates the view visibility based on the [makeVisible] param
     *
     * @param makeVisible
     * */
    private fun View.animateVisibility(makeVisible: Boolean = true) {
        if (makeVisible) {
            scaleX = 1f
            scaleY = 1f
        } else {
            animate().scaleX(0f).setDuration(Defaults.animationDuration)
                .onAnimationCancelled {
                    animate().scaleX(0f).duration = Defaults.animationDuration
                }
            animate().scaleY(0f).setDuration(Defaults.animationDuration)
                .onAnimationCancelled {
                    animate().scaleY(0f).duration = Defaults.animationDuration
                }
        }
    }

    // set of load methods for handy loading from attribs
    private fun loadDimenFromResource(@DimenRes dimenSize: Int): Int = context.resources.getDimensionPixelSize(dimenSize)

    private fun loadDrawableFromAttributes(@StyleableRes styleId: Int) = attribs?.getDrawable(styleId)

    // extension functions to get the total visible count of items.
    private fun LinearLayoutManager.getTotalCompletelyVisibleItemCount(): Int {
        // a visible element was not found
        // it means that the item views are bigger,
        // thus let's compute with the visible elements' position
        // instead of completely visible positions

        val firstVisibleItemPosition = this.findFirstCompletelyVisibleItemPosition().takeIf { it != RecyclerView.NO_POSITION }
            ?: this.findFirstVisibleItemPosition()

        val lastVisibleItemPosition = this.findLastCompletelyVisibleItemPosition().takeIf { it != RecyclerView.NO_POSITION }
            ?: this.findLastVisibleItemPosition()

        return if (firstVisibleItemPosition == RecyclerView.NO_POSITION || lastVisibleItemPosition == RecyclerView.NO_POSITION) {
            RecyclerView.NO_POSITION
        } else {
            lastVisibleItemPosition - firstVisibleItemPosition
        }
    }

    /**
     * [RecyclerView.LayoutManager] has different types of scroll mechanisms, this extension function uses the [LinearLayoutManager.scrollToPositionWithOffset] if its an instance of [LinearLayoutManager]
     * else uses the standard [RecyclerView.LayoutManager.scrollToPosition] method. The offset in [LinearLayoutManager] is the position where the view should be after scrolling relative to the [RecyclerView]
     * */
    private fun RecyclerView.safeScrollToPosition(position: Int) {
        with(this.layoutManager) {
            when (this) {
                is LinearLayoutManager -> scrollToPositionWithOffset(position, 0)
                is RecyclerView.LayoutManager -> scrollToPosition(position)
            }
        }
    }

    /**
     * Computes the position to make the [RecyclerView] to scroll to.
     *
     * **Calculation:** [relativeRawPos] is divided with difference of the scroll extent using [RecyclerView].compute<X>ScrollExtent, where X is be the direction
     * and the height of the handle and then this value is multiplied by the total item count, this give us the General position. But for [LinearLayoutManager]
     * the current visible items are also taken into consideration
     *
     *
     *@param relativeRawPos the relative raw position, calculated during [MotionEvent.ACTION_MOVE] or [MotionEvent.ACTION_DOWN]
     * */
    private fun RecyclerView.computePositionForOffsetAndScroll(relativeRawPos: Float): Int {
        val layoutManager: RecyclerView.LayoutManager? = this.layoutManager
        val recyclerViewItemCount = this.adapter?.itemCount ?: 0

        val newOffset = relativeRawPos / (trackLength - handleLength)
        return when (layoutManager) {
            is LinearLayoutManager -> {
                val totalVisibleItems = layoutManager.getTotalCompletelyVisibleItemCount()

                if (totalVisibleItems == RecyclerView.NO_POSITION) {
                    return RecyclerView.NO_POSITION
                }

                // the last item would have one less visible item, this is to offset it.
                previousTotalVisibleItem = max(previousTotalVisibleItem, totalVisibleItems)
                // check bounds and then set position
                val position =
                    if (layoutManager.reverseLayout) {
                        min(
                            recyclerViewItemCount,
                            max(0, recyclerViewItemCount - (newOffset * (recyclerViewItemCount - totalVisibleItems)).roundToInt())
                        )
                    } else {
                        min(
                            recyclerViewItemCount,
                            max(0, (newOffset * (recyclerViewItemCount - totalVisibleItems)).roundToInt())
                        )
                    }

                val toScrollPosition = min((this.adapter?.itemCount ?: 0) - (previousTotalVisibleItem + 1), position)
                safeScrollToPosition(toScrollPosition)
                position
            }
            else -> {
                val position = (newOffset * recyclerViewItemCount).roundToInt()
                safeScrollToPosition(position)
                position
            }
        }
    }

    /**
     * Update popup text or hide popup when the interface is not implemented
     * */
    private fun updateTextInPopup(position: Int) {
        if (position == currentPopupItemPosition || position !in 0 until (recyclerView.adapter?.itemCount ?: 1)) {
            return
        }

        currentPopupItemPosition = position
        when (val adapter = recyclerView.adapter) {
            null -> throw IllegalAccessException("No adapter found, if you have an adapter then try placing if before calling the attachFastScrollerToRecyclerView() method")
            is OnPopupTextUpdate -> popupTextView.text = adapter.onChange(position).toString()
            is OnPopupViewUpdate -> adapter.onUpdate(position, popupTextView)
            else -> popupTextView.visibility = View.GONE
        }
    }

    private val adapterDataObserver = lazy {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                previousTotalVisibleItem = 0
                currentPopupItemPosition = -1
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                previousTotalVisibleItem = 0
                currentPopupItemPosition = -1
            }
        }
    }

    private fun registerDataObserver() {
        recyclerView.adapter?.registerAdapterDataObserver(adapterDataObserver.value)
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (isEngaged && isFastScrollEnabled) {
                return
            }

            scrollOffset += dy
            val orientation = (recyclerView.layoutManager as LinearLayoutManager).orientation
            val (range, extent, offset) = when (orientation) {
                RecyclerView.HORIZONTAL -> Triple(
                    recyclerView.computeHorizontalScrollRange(),
                    recyclerView.computeHorizontalScrollExtent(),
                    recyclerView.computeHorizontalScrollOffset()
                )
                RecyclerView.VERTICAL -> {
                    if (calculateScrollPositionManually) {
                        Triple(
                            fullContentHeight,
                            recyclerView.computeVerticalScrollExtent(),
                            scrollOffset
                        )
                    } else {
                        Triple(
                            recyclerView.computeVerticalScrollRange(),
                            recyclerView.computeVerticalScrollExtent(),
                            recyclerView.computeVerticalScrollOffset()
                        )
                    }
                }
                else -> error("The orientation of the LinearLayoutManager should be horizontal or vertical")
            }

            // check if the layout is scrollable. i.e. range is large than extent, else disable fast scrolling and track touches.
            if (extent < range) {
                if (dy != 0 && orientation == RecyclerView.VERTICAL) {
                    handleImageView.animateVisibility()
                    handleImageView.isEnabled = true
                    trackView.isEnabled = true
                }
            } else {
                if (dx != 0 && orientation == RecyclerView.HORIZONTAL) {
                    handleImageView.animateVisibility(false)
                    trackView.isEnabled = false
                    handleImageView.isEnabled = false
                }
                return
            }

            val availableTrackLength = extent.toFloat() - handleLength
            val scrollPositionPercent = (offset.toFloat() / (range - extent))
            val finalOffset = availableTrackLength * scrollPositionPercent

            if ((dy != 0 && orientation == RecyclerView.VERTICAL) || (dx != 0 && orientation == RecyclerView.HORIZONTAL)) {
                moveHandle(finalOffset)
            }
        }
    }

    private fun initImpl() {
        registerDataObserver()
        recyclerView.addOnScrollListener(onScrollListener)
    }

    /**
     * Sets a [HandleStateListener] to this fast scroll
     *
     * @since 1.0
     * @see HandleStateListener
     **/
    fun setHandleStateListener(handleStateListener: HandleStateListener) {
        this.handleStateListener = handleStateListener
    }

    fun updateColors(primaryColor: Int) {
        handleImageView.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN)
        popupTextView.setTextColor(getContrastColor(primaryColor))
        popupTextView.background.mutate().setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN)
    }

    fun setScrollVertically(scrollVertically: Boolean) {
        if (scrollVertically && fastScrollDirection == FastScrollDirection.HORIZONTAL) {
            fastScrollDirection = FastScrollDirection.VERTICAL
            val tmp = handleWidth
            handleWidth = handleHeight
            handleHeight = tmp
        } else if (!scrollVertically && fastScrollDirection == FastScrollDirection.VERTICAL) {
            fastScrollDirection = FastScrollDirection.HORIZONTAL
            val tmp = handleWidth
            handleWidth = handleHeight
            handleHeight = tmp
        }
    }

    fun resetManualScrolling() {
        calculateScrollPositionManually = false
        fullContentHeight = 0
        scrollOffset = 0
    }

    /**
     * ### Call this method only if [RecyclerView] is not a child to this view, else can cause memory leaks and undesired behavior
     *
     * To re-init the [RecyclerViewFastScroller] call the [detachFastScrollerFromRecyclerView] and then re-call this method.
     *
     * This method adds a [RecyclerView.OnScrollListener] to the [RecyclerView] and also attaches an itemDecorator which adds margin to the last item of the [RecyclerView.Adapter]
     * this bottom empty margin can be skipped if the [R.styleable.RecyclerViewFastScroller_addLastItemPadding] is set to false during layout creation, this will not add the [RecyclerView.ItemDecoration] as well.
     *
     * The [RecyclerView.OnScrollListener] is used to compute the position of the Handle during runtime,thus calling [RecyclerView.clearOnScrollListeners] might cause this view to not function properly.
     *
     * A [RecyclerView.AdapterDataObserver] is also added to listen to data changes.
     *
     * @see detachFastScrollerFromRecyclerView
     * @since 1.0
     * */
    @Keep
    fun attachFastScrollerToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        initImpl()
    }

    /**
     * This method should only be explicitly called if there's a need to reset the [RecyclerViewFastScroller] else if will be called by this view in the [onDetachedFromWindow] method.
     *
     * This method removes the [RecyclerView.OnScrollListener], the [RecyclerView.ItemDecoration] if set and the [RecyclerView.AdapterDataObserver] associated with it.
     *
     * @see attachFastScrollerToRecyclerView
     * @since 1.0
     **/
    @SuppressLint("ClickableViewAccessibility")
    fun detachFastScrollerFromRecyclerView() {
        // unregister the observer to prevent memory leaks only if initialized
        if (adapterDataObserver.isInitialized()) {
            try {
                recyclerView.adapter?.unregisterAdapterDataObserver(adapterDataObserver.value)
            } catch (ignored: Exception) {
            }
        }

        handleImageView.setOnTouchListener(null)
        popupTextView.setOnTouchListener(null)
        recyclerView.removeOnScrollListener(onScrollListener)
    }

    @Keep
    /**
     * Provides the [TextView] along with the current position of the [RecyclerViewFastScroller]
     *
     * All the visual, position-based changes should be done using this interface
     *
     * For updating only the text check [OnPopupTextUpdate] interface
     *
     * Both these interfaces cannot be set and [OnPopupTextUpdate] has priority over [OnPopupViewUpdate]
     *
     * @see OnPopupTextUpdate
     * @since 1.0
     **/
    interface OnPopupViewUpdate {
        fun onUpdate(position: Int, popupTextView: TextView)
    }

    @Keep
    /**
     * A simpler callback to just provide the [CharSequence] to be set to the [popupTextView] based on the position.
     *
     * To perform visual changes on the [TextView] check the [OnPopupViewUpdate] interface
     *
     * Both these interfaces cannot be set and [OnPopupTextUpdate] has priority over [OnPopupViewUpdate]

     * @see OnPopupViewUpdate
     * @since 1.0
     **/
    interface OnPopupTextUpdate {
        fun onChange(position: Int): CharSequence
    }

    @Keep
    /**
     * An interface to listen to different states of the handle in the fastscroller, all the methods in this are only called if [isFastScrollEnabled] is true
     *
     * @since 1.0
     * @see isFastScrollEnabled
     **/
    interface HandleStateListener {
        /**
         * Called when the handle is pressed and engaged for fastscroll behavior
         **/
        fun onEngaged() {}

        /**
         * Called when the handle is dragged to perform the fast scroll operation.
         *
         * @param offset The offset to which the handle is currently scroller to
         * @param position The computed position which is sent to the [OnPopupViewUpdate] or the [OnPopupTextUpdate] callbacks
         *
         * @see OnPopupViewUpdate
         * @see OnPopupTextUpdate
         * */
        fun onDragged(offset: Float, position: Int) {}

        /**
         * Called when the handled is released, this marks the end of the fast scroll operation.
         * */
        fun onReleased() {}
    }

    private fun log(message: String = "") {
        // Debug logging removed for integrated library
    }

    fun getContrastColor(color: Int): Int {
        val y = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000
        return if (y >= 149 && color != Color.BLACK) DARK_GREY else Color.WHITE
    }
}
