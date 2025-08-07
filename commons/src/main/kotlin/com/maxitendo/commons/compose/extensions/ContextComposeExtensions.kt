package com.maxitendo.commons.compose.extensions

import android.app.Activity
import android.content.Context
import com.maxitendo.commons.R
import com.maxitendo.commons.extensions.baseConfig
import com.maxitendo.commons.extensions.launchAppRatingPage
import com.maxitendo.commons.extensions.toast
import com.maxitendo.commons.helpers.BaseConfig

val Context.config: BaseConfig get() = BaseConfig.newInstance(applicationContext)

fun Activity.rateStarsRedirectAndThankYou(stars: Int) {
    if (stars == 5) {
        launchAppRatingPage()
    }
    toast(R.string.thank_you)
    baseConfig.wasAppRated = true
}
