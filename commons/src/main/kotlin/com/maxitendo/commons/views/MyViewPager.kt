package com.maxitendo.commons.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class MyViewPager : ViewPager {

    private var isPagingEnabled = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return try {
            this.isPagingEnabled && super.onInterceptTouchEvent(ev)
        } catch (ignored: Exception) {
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return try {
            this.isPagingEnabled && super.onTouchEvent(ev)
        } catch (ignored: Exception) {
            false
        }
    }

    fun setPagingEnabled(enable: Boolean) {
        isPagingEnabled = enable
    }
}
