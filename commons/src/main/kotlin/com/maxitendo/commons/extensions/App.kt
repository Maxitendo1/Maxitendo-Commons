package com.maxitendo.commons.extensions

import android.app.Application
import android.content.Context
import com.maxitendo.commons.helpers.isNougatPlus
import java.util.Locale

fun Application.checkUseEnglish() {
    if (baseConfig.useEnglish && !isNougatPlus()) {
        val conf = resources.configuration
        conf.locale = Locale.ENGLISH
        resources.updateConfiguration(conf, resources.displayMetrics)
    }
}


