package com.maxitendo.commons.views

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import com.google.android.material.appbar.AppBarLayout
import com.maxitendo.commons.R
import com.maxitendo.commons.activities.BaseSimpleActivity
import com.maxitendo.commons.databinding.MenuSearchTopBinding
import com.maxitendo.commons.extensions.*
import com.maxitendo.commons.helpers.LOWER_ALPHA
import com.maxitendo.commons.helpers.MEDIUM_ALPHA

class MySearchMenuTop(context: Context, attrs: AttributeSet) : AppBarLayout(context, attrs) {
    var isSearchOpen = false
    var useArrowIcon = false
    var onSearchOpenListener: (() -> Unit)? = null
    var onSearchClosedListener: (() -> Unit)? = null
    var onSearchTextChangedListener: ((text: String) -> Unit)? = null
    var onNavigateBackClickListener: (() -> Unit)? = null

    val binding = MenuSearchTopBinding.inflate(LayoutInflater.from(context), this, true)

    fun getToolbar() = binding.topToolbar

    fun setupMenu() {
        binding.topToolbarSearchIcon.setOnClickListener {
            if (isSearchOpen) {
                closeSearch()
            } else if (useArrowIcon && onNavigateBackClickListener != null) {
                onNavigateBackClickListener!!()
            } else {
                binding.topToolbarSearch.requestFocus()
                (context as? Activity)?.showKeyboard(binding.topToolbarSearch)
            }
        }

        post {
            binding.topToolbarSearch.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    openSearch()
                }
            }
        }

        binding.topToolbarSearch.onTextChangeListener { text ->
            onSearchTextChangedListener?.invoke(text)
        }
    }

    fun focusView() {
        binding.topToolbarSearch.requestFocus()
    }

    private fun openSearch() {
        isSearchOpen = true
        onSearchOpenListener?.invoke()
        binding.topToolbarSearchIcon.setImageResource(R.drawable.ic_chevron_left_vector)
        binding.topToolbarSearchIcon.contentDescription = resources.getString(R.string.back)
    }

    fun closeSearch() {
        isSearchOpen = false
        onSearchClosedListener?.invoke()
        binding.topToolbarSearch.setText("")
        if (!useArrowIcon) {
            binding.topToolbarSearchIcon.setImageResource(R.drawable.ic_search_vector)
            binding.topToolbarSearchIcon.contentDescription = resources.getString(R.string.search)
        }
        (context as? Activity)?.hideKeyboard()
    }

    fun getCurrentQuery() = binding.topToolbarSearch.text.toString()

    fun updateHintText(text: String) {
        binding.topToolbarSearch.hint = text
    }

    fun toggleHideOnScroll(hideOnScroll: Boolean) {
        val params = binding.topAppBarLayout.layoutParams as LayoutParams
        if (hideOnScroll) {
            params.scrollFlags = LayoutParams.SCROLL_FLAG_SCROLL or LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        } else {
            params.scrollFlags = params.scrollFlags.removeBit(LayoutParams.SCROLL_FLAG_SCROLL or LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
        }
    }

    fun toggleForceArrowBackIcon(useArrowBack: Boolean) {
        this.useArrowIcon = useArrowBack
        val (icon, accessibilityString) = if (useArrowBack) {
            Pair(R.drawable.ic_chevron_left_vector, R.string.back)
        } else {
            Pair(R.drawable.ic_search_vector, R.string.search)
        }

        binding.topToolbarSearchIcon.setImageResource(icon)
        binding.topToolbarSearchIcon.contentDescription = resources.getString(accessibilityString)
    }

    fun updateColors() {
        val backgroundColor = context.getProperBackgroundColor()
        val contrastColor = backgroundColor.getContrastColor()
        val primaryColor = context.getProperPrimaryColor()
        val bottomNavigationBackgroundColor = context.getBottomNavigationBackgroundColor()

        setBackgroundColor(backgroundColor)
        binding.topAppBarLayout.setBackgroundColor(backgroundColor)
        val searchIconColor = if (context.baseConfig.topAppBarColorIcon) primaryColor else contrastColor
        binding.topToolbarSearchIcon.applyColorFilter(searchIconColor)
        //binding.topToolbarHolder.background?.applyColorFilter(primaryColor.adjustAlpha(LOWER_ALPHA))
        //binding.topToolbarSearch.setTextColor(contrastColor)
        //binding.topToolbarSearch.setHintTextColor(contrastColor.adjustAlpha(MEDIUM_ALPHA))
        binding.topToolbarSearch.setColors(contrastColor, primaryColor, context.getProperTextCursorColor())
        (context as? BaseSimpleActivity)?.updateTopBarColors(binding.topToolbar, Color.TRANSPARENT, useColorForStatusBar = false)

        binding.topToolbarHolder.setBackgroundResource(R.drawable.search_bg)
        binding.topToolbarHolder.backgroundTintList = ColorStateList.valueOf(bottomNavigationBackgroundColor)
        binding.topToolbarSearchClear.applyColorFilter(contrastColor)
    }

    fun requestFocusAndShowKeyboard() {
        binding.topToolbarSearch.requestFocus()
        (context as? Activity)?.showKeyboard(binding.topToolbarSearch)
    }

    fun clearSearch() {
        binding.topToolbarSearchClear.beVisibleIf(binding.topToolbarSearch.text!!.isNotEmpty())
        binding.topToolbarSearchClear.setOnClickListener {
            binding.topToolbarSearch.setText("")
        }
    }
}
