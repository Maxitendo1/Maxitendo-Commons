package com.maxitendo.commons.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maxitendo.commons.R
import com.maxitendo.commons.compose.alert_dialog.rememberAlertDialogState
import com.maxitendo.commons.compose.extensions.config
import com.maxitendo.commons.compose.extensions.enableEdgeToEdgeSimple
import com.maxitendo.commons.compose.extensions.rateStarsRedirectAndThankYou
import com.maxitendo.commons.compose.screens.*
import com.maxitendo.commons.compose.theme.AppThemeSurface
import com.maxitendo.commons.dialogs.ConfirmationAdvancedAlertDialog
import com.maxitendo.commons.dialogs.RateStarsAlertDialog
import com.maxitendo.commons.extensions.*
import com.maxitendo.commons.helpers.*
import com.maxitendo.commons.models.FAQItem

class AboutActivity : BaseComposeActivity() {
    private val appName get() = intent.getStringExtra(APP_NAME) ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdgeSimple()
        setContent {
            val isTopAppBarColorIcon by config.isTopAppBarColorIcon.collectAsStateWithLifecycle(initialValue = config.topAppBarColorIcon)
            val isTopAppBarColorTitle by config.isTopAppBarColorTitle.collectAsStateWithLifecycle(initialValue = config.topAppBarColorTitle)
            AppThemeSurface {
                val rateStarsAlertDialogState = getRateStarsAlertDialogState()
                val onRateUsClickAlertDialogState = getOnRateUsClickAlertDialogState(rateStarsAlertDialogState::show)
                AboutScreen(
                    goBack = ::finish,
                    aboutSection = {
                        AboutNewSection(
                            appName = appName,
                            appVersion = intent.getStringExtra(APP_VERSION_NAME) ?: "",
                            onRateUsClick = {
                                onRateUsClick(
                                    showConfirmationAdvancedDialog = onRateUsClickAlertDialogState::show,
                                    showRateStarsDialog = rateStarsAlertDialogState::show
                                )
                            },
                            onMoreAppsClick = ::launchMoreAppsFromUsIntent,
                            onPrivacyPolicyClick = ::onPrivacyPolicyClick,
                            onFAQClick = ::launchFAQActivity,
                            onTipJarClick = ::onTipJarClick,
                            onGithubClick = ::onGithubClick,
                            showGithub = showGithub(),
                        )
                    },
                    isTopAppBarColorIcon = isTopAppBarColorIcon,
                    isTopAppBarColorTitle = isTopAppBarColorTitle,
                )
            }
        }
    }

    @Composable
    private fun getRateStarsAlertDialogState() =
        rememberAlertDialogState().apply {
            DialogMember {
                RateStarsAlertDialog(
                    alertDialogState = this,
                    onRating = ::rateStarsRedirectAndThankYou
                )
            }
        }

    @Composable
    private fun getOnRateUsClickAlertDialogState(showRateStarsDialog: () -> Unit) =
        rememberAlertDialogState().apply {
            DialogMember {
                ConfirmationAdvancedAlertDialog(
                    alertDialogState = this,
                    message = "${getString(R.string.before_asking_question_read_faq)}\n\n${getString(R.string.make_sure_latest)}",
                    messageId = null,
                    positive = R.string.read_faq,
                    negative = R.string.skip
                ) { success ->
                    if (success) {
                        launchFAQActivity()
                    } else {
                        launchRateUsPrompt(showRateStarsDialog)
                    }
                }
            }
        }

    private fun launchFAQActivity() {
        val faqItems = intent.getSerializableExtra(APP_FAQ) as ArrayList<FAQItem>
        Intent(applicationContext, FAQActivity::class.java).apply {
            putExtra(
                APP_ICON_IDS,
                intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList<String>()
            )
            putExtra(APP_LAUNCHER_NAME, intent.getStringExtra(APP_LAUNCHER_NAME) ?: "")
            putExtra(APP_FAQ, faqItems)
            startActivity(this)
        }
    }

    private fun onRateUsClick(
        showConfirmationAdvancedDialog: () -> Unit,
        showRateStarsDialog: () -> Unit,
    ) {
        if (baseConfig.wasBeforeRateShown) {
            launchRateUsPrompt(showRateStarsDialog)
        } else {
            baseConfig.wasBeforeRateShown = true
            showConfirmationAdvancedDialog()
        }
    }

    private fun launchRateUsPrompt(
        showRateStarsDialog: () -> Unit,
    ) {
        if (baseConfig.wasAppRated) {
            launchAppRatingPage()
        } else {
            showRateStarsDialog()
        }
    }

    private fun onPrivacyPolicyClick() {
        val appId = baseConfig.appId.removeSuffix(".debug")

        if (appId == "com.maxitendo.contacts") {
            try {
                val intent = Intent()
                intent.setClassName(packageName, "com.maxitendo.contacts.activities.PrivacyPolicyActivity")
                startActivity(intent)
                return
            } catch (e: Exception) {
            }
        }

        val url = when (appId) {
            "com.maxitendo.dialer" -> "https://www.github.com/Maxitendo1/about/privacy-policy-right-dialer"
            "com.maxitendo.smsmessenger" -> "https://www.github.com/Maxitendo1/about/privacy-policy-right-messages"
            "com.maxitendo.contacts" -> "https://www.github.com/Maxitendo1/about/privacy-policy-right-contacts"
            "com.maxitendo.gallery" -> "https://www.github.com/Maxitendo1/about/privacy-policy-right-gallery"
            "com.maxitendo.filemanager" -> "https://www.github.com/Maxitendo1/about/privacy-policy-right-files"
            "com.maxitendo.voicerecorder", "com.maxitendo.voicerecorderfree" -> "https://www.github.com/Maxitendo1/about/privacy-policy-right-voice-recorder"
            "com.maxitendo.calendar" -> "https://www.github.com/Maxitendo1/about/privacy-policy-right-calendar"
            else -> "https://www.github.com/Maxitendo1/about/privacy-policy"
        }
        launchViewIntent(url)
    }

    private fun onTipJarClick() {
        Intent(applicationContext, PurchaseActivity::class.java).apply {
            putExtra(APP_ICON_IDS, intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList<String>())
            putExtra(APP_LAUNCHER_NAME, intent.getStringExtra(APP_LAUNCHER_NAME) ?: "")
            putExtra(APP_NAME, intent.getStringExtra(APP_NAME) ?: "")
            putExtra(PRODUCT_ID_LIST, intent.getStringArrayListExtra(PRODUCT_ID_LIST) ?: arrayListOf("", "", ""))
            putExtra(SUBSCRIPTION_ID_LIST, intent.getStringArrayListExtra(SUBSCRIPTION_ID_LIST) ?: arrayListOf("", "", ""))
            putExtra(SUBSCRIPTION_YEAR_ID_LIST, intent.getStringArrayListExtra(SUBSCRIPTION_YEAR_ID_LIST) ?: arrayListOf("", "", ""))
            putExtra(SHOW_LIFEBUOY, resources.getBoolean(R.bool.show_lifebuoy))
            putExtra(PLAY_STORE_INSTALLED, intent.getBooleanExtra(PLAY_STORE_INSTALLED, true))
            putExtra(SHOW_COLLECTION, resources.getBoolean(R.bool.show_collection))
            startActivity(this)
        }
    }

    private fun onGithubClick() {
        val repositoryName = intent.getStringExtra(APP_REPOSITORY_NAME) ?: return
        val url = "https://github.com/Maxitendo1/$repositoryName"
        launchViewIntent(url)
    }

    @Composable
    private fun showGithub() =
        remember { !intent.getStringExtra(APP_REPOSITORY_NAME).isNullOrEmpty() }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changeAutoTheme()
    }

    private fun changeAutoTheme() {
        syncGlobalConfig {
            baseConfig.apply {
                if (isAutoTheme()) {
                    val isUsingSystemDarkTheme = isSystemInDarkMode()
                    textColor = resources.getColor(if (isUsingSystemDarkTheme) R.color.theme_dark_text_color else R.color.theme_light_text_color)
                    backgroundColor =
                        resources.getColor(if (isUsingSystemDarkTheme) R.color.theme_dark_background_color else R.color.theme_light_background_color)
                    finish()
                    startActivity(intent)
                }
            }
        }
        return
    }
}
