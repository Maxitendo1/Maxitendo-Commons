package com.maxitendo.commons.samples

import com.github.ajalt.reprint.core.Reprint
import com.maxitendo.commons.RightApp
import com.maxitendo.commons.helpers.rustore.RuStoreModule

class App : RightApp() {
    override fun onCreate() {
        super.onCreate()
        Reprint.initialize(this)
        RuStoreModule.install(this, "309929407")
    }
}
