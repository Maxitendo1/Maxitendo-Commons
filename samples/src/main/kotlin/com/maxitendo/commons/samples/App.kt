package com.maxitendo.commons.samples

import com.maxitendo.commons.RightApp


class App : RightApp() {
    override fun onCreate() {
        super.onCreate()
        // Biometric authentication is now handled by androidx.biometric
        // No initialization needed
    }
}
