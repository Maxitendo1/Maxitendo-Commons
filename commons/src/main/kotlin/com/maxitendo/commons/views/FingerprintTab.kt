package com.maxitendo.commons.views

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.provider.Settings
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.biometric.auth.AuthPromptHost
import com.maxitendo.commons.R
import com.maxitendo.commons.databinding.TabFingerprintBinding
import com.maxitendo.commons.extensions.*
import com.maxitendo.commons.helpers.PROTECTION_FINGERPRINT
import com.maxitendo.commons.interfaces.HashListener
import com.maxitendo.commons.interfaces.SecurityTab

class FingerprintTab(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs), SecurityTab {
    private lateinit var hashListener: HashListener
    private lateinit var biometricPromptHost: AuthPromptHost
    private lateinit var binding: TabFingerprintBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = TabFingerprintBinding.bind(this)
        val textColor = context.getProperTextColor()
        context.updateTextColors(binding.fingerprintLockHolder)
        binding.fingerprintImage.applyColorFilter(textColor)

        binding.fingerprintSettings.background.applyColorFilter(context.getBottomNavigationBackgroundColor())
        binding.fingerprintSettings.setOnClickListener {
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    override fun initTab(
        requiredHash: String,
        listener: HashListener,
        scrollView: MyScrollView?,
        biometricPromptHost: AuthPromptHost,
        showBiometricAuthentication: Boolean
    ) {
        this.biometricPromptHost = biometricPromptHost
        hashListener = listener
        if (showBiometricAuthentication) {
            performBiometricAuthentication()
        }
    }

    override fun visibilityChanged(isVisible: Boolean) {
        if (isVisible) {
            checkBiometricAvailability()
        }
    }

    private fun checkBiometricAvailability() {
        val hasBiometric = context.isBiometricIdAvailable()
        binding.fingerprintSettings.beGoneIf(hasBiometric)
        binding.fingerprintLabel.text = context.getString(if (hasBiometric) R.string.place_finger else R.string.no_fingerprints_registered)

        if (hasBiometric) {
            performBiometricAuthentication()
        }
    }

    private fun performBiometricAuthentication() {
        biometricPromptHost.activity?.showBiometricPrompt(
            successCallback = { _, _ -> hashListener.receivedHash("", PROTECTION_FINGERPRINT) },
            failureCallback = { /* Handle failure if needed */ }
        )
    }
}
