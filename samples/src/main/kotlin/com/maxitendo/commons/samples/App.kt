package com.maxitendo.commons.samples

import com.github.ajalt.reprint.core.Reprint
import com.maxitendo.commons.RightApp


class App : RightApp() {
    override fun onCreate() {
        super.onCreate()
        Reprint.initialize(this)
    }
}
