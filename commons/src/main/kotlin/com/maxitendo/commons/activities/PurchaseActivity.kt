package com.maxitendo.commons.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.net.toUri
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.maxitendo.commons.R
import com.maxitendo.commons.databinding.ActivityPurchaseBinding
import com.maxitendo.commons.dialogs.BottomSheetChooserDialog
import com.maxitendo.commons.dialogs.ConfirmationAdvancedDialog
import com.maxitendo.commons.dialogs.ConfirmationDialog
import com.maxitendo.commons.extensions.*
import com.maxitendo.commons.helpers.*

import com.maxitendo.commons.models.SimpleListItem
import com.maxitendo.strings.R as stringsR
import kotlinx.coroutines.*


class PurchaseActivity : BaseSimpleActivity() {

    private var appName = ""
    private var primaryColor = 0
    private var productIdList: ArrayList<String> = ArrayList()
    private var subscriptionIdList: ArrayList<String> = ArrayList()
    private var subscriptionYearIdList: ArrayList<String> = ArrayList()
    private var showLifebuoy = true
    private var playStoreInstalled = true
    private var showCollection = false

    private val purchaseHelper = PurchaseHelper(this)

    override fun getAppIconIDs() = intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList()

    override fun getAppLauncherName() = intent.getStringExtra(APP_LAUNCHER_NAME) ?: ""

    override fun getRepositoryName() = null

    private val binding by viewBinding(ActivityPurchaseBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = false
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        appName = intent.getStringExtra(APP_NAME) ?: ""
        productIdList = intent.getStringArrayListExtra(PRODUCT_ID_LIST) ?: arrayListOf("", "", "")
        subscriptionIdList = intent.getStringArrayListExtra(SUBSCRIPTION_ID_LIST) ?: arrayListOf("", "", "")
        subscriptionYearIdList = intent.getStringArrayListExtra(SUBSCRIPTION_YEAR_ID_LIST) ?: arrayListOf("", "", "")
        primaryColor = getProperPrimaryColor()
        showLifebuoy = intent.getBooleanExtra(SHOW_LIFEBUOY, true)
        playStoreInstalled = intent.getBooleanExtra(PLAY_STORE_INSTALLED, true)
        showCollection = intent.getBooleanExtra(SHOW_COLLECTION, false)

        // TODO TRANSPARENT Navigation Bar
        setWindowTransparency(true) { _, _, leftNavigationBarSize, rightNavigationBarSize ->
            binding.purchaseCoordinator.setPadding(leftNavigationBarSize, 0, rightNavigationBarSize, 0)
            updateNavigationBarColor(getProperBackgroundColor())
        }

        arrayOf(
            binding.purchaseNestedScrollview,
            binding.topDetails.root
        ).forEach {
            it.beInvisibleIf(!playStoreInstalled ||
                (resources.getBoolean(R.bool.using_no_gp) && playStoreInstalled))
        }

        arrayOf(
            binding.proHolder,
            binding.proDonateText,
            binding.proDonateButton
        ).forEach {
            it.beGoneIf(!resources.getBoolean(R.bool.using_no_gp) && playStoreInstalled)
        }

        if (playStoreInstalled) {
            //PlayStore
            purchaseHelper.initBillingClient()
            val subscriptionIdListAll: ArrayList<String> = subscriptionIdList
            subscriptionIdListAll.addAll(subscriptionYearIdList)
            purchaseHelper.retrieveDonation(productIdList, subscriptionIdListAll)

            purchaseHelper.iapSkuDetailsInitialized.observe(this) {
                if (it) setupButtonIapPurchased()
            }
            purchaseHelper.subSkuDetailsInitialized.observe(this) {
                if (it) setupButtonSupPurchased()
            }

            purchaseHelper.isIapPurchased.observe(this) {
                when (it) {
                    is Tipping.Succeeded -> {
                        baseConfig.isPro = true
                    }
                    is Tipping.NoTips -> {
                        baseConfig.isPro = false
                    }
                    is Tipping.FailedToLoad -> {
                    }
                    else -> {
                    }
                }
            }
            purchaseHelper.isSupPurchased.observe(this) {
                when (it) {
                    is Tipping.Succeeded -> {
                        baseConfig.isProSubs = true
                    }
                    is Tipping.NoTips -> {
                        baseConfig.isProSubs = false
                    }
                    is Tipping.FailedToLoad -> {
                    }
                    else -> {
                    }
                }
            }

            purchaseHelper.isIapPurchasedList.observe(this) {
                setupButtonIapChecked()
            }
            purchaseHelper.isSupPurchasedList.observe(this) {
                setupButtonSupChecked()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // No special handling needed for FOSS version
    }

    override fun onResume() {
        super.onResume()
        updateTextColors(binding.purchaseCoordinator)
        setupOptionsMenu()
        setupToolbar(binding.purchaseToolbar, NavigationIcon.Arrow)
        val backgroundColor = getProperBackgroundColor()
        binding.collapsingToolbar.setBackgroundColor(backgroundColor)
        updateTopBarColors(binding.purchaseToolbar, backgroundColor, useOverflowIcon = false)

        setupEmail()
        if (showCollection) setupCollection()
        if (!resources.getBoolean(R.bool.using_no_gp) && playStoreInstalled) {
            setupIcon()
        } else {
            setupNoPlayStoreInstalled()
        }

        val isProApp = resources.getBoolean(R.bool.is_pro_app)
        binding.themeHolder.beVisibleIf(!isProApp)
        binding.colorHolder.beVisibleIf(!isProApp)
    }

    private fun setupOptionsMenu() {
        val visible = !resources.getBoolean(R.bool.using_no_gp) && playStoreInstalled
        binding.purchaseToolbar.menu.apply {
            findItem(R.id.restorePurchases).isVisible = visible
            findItem(R.id.openSubscriptions).isVisible = visible
        }
        binding.purchaseToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.restorePurchases -> {
                    setupButtonReset()
                    val subscriptionIdListAll: ArrayList<String> = subscriptionIdList
                    subscriptionIdListAll.addAll(subscriptionYearIdList)
                    purchaseHelper.retrieveDonation(productIdList, subscriptionIdListAll)
                    true
                }
                R.id.openSubscriptions -> {
                    val url = "https://play.google.com/store/account/subscriptions"
                    launchViewIntent(url)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupEmail() {
        binding.lifebuoyHolder.beVisibleIf(showLifebuoy && playStoreInstalled)
        val lifebuoyButtonDrawable = resources.getColoredDrawableWithColor(this, R.drawable.ic_mail_vector, getProperTextColor())
        binding.lifebuoyButton.setImageDrawable(lifebuoyButtonDrawable)
        binding.lifebuoyButton.setOnClickListener {
            ConfirmationDialog(this, getString(R.string.send_email)) {
                val body = "$appName : Lifebuoy"
                val address = getString(R.string.my_email)
                val selectorIntent = Intent(ACTION_SENDTO)
                    .setData("mailto:$address".toUri())
                val emailIntent = Intent(ACTION_SEND).apply {
                    putExtra(EXTRA_EMAIL, arrayOf(address))
                    putExtra(EXTRA_SUBJECT, body)
                    selector = selectorIntent
                }

                try {
                    startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    toast(R.string.no_app_found)
                } catch (e: Exception) {
                    showErrorToast(e)
                }
            }
        }
    }

    private fun setupButtonIapPurchased() {
        binding.appOneButton.apply {
            val price = purchaseHelper.getPriceDonation(productIdList[0])
            isEnabled = price != getString(stringsR.string.no_connection)
            text = price
            setOnClickListener {
                purchaseHelper.getDonation(productIdList[0])
            }
            background.setTint(primaryColor)
        }

        binding.appTwoButton.apply {
            val price = purchaseHelper.getPriceDonation(productIdList[1])
            isEnabled = price != getString(stringsR.string.no_connection)
            text = price
            setOnClickListener {
                purchaseHelper.getDonation(productIdList[1])
            }
            background.setTint(primaryColor)
        }

        binding.appThreeButton.apply {
            val price = purchaseHelper.getPriceDonation(productIdList[2])
            isEnabled = price != getString(stringsR.string.no_connection)
            text = price
            setOnClickListener {
                purchaseHelper.getDonation(productIdList[2])
            }
            background.setTint(primaryColor)
        }
    }

    private fun setupButtonIapChecked() {
        val check = AppCompatResources.getDrawable(this@PurchaseActivity, R.drawable.ic_check_circle_mini)
        if (purchaseHelper.isIapPurchased(productIdList[0])) {
            binding.appOneButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appOneButton.isEnabled = false
        }
        if (purchaseHelper.isIapPurchased(productIdList[1])) {
            binding.appTwoButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appTwoButton.isEnabled = false
        }
        if (purchaseHelper.isIapPurchased(productIdList[2])) {
            binding.appThreeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appThreeButton.isEnabled = false
        }
    }

    private fun setupButtonSupPurchased() {
        binding.appOneSubButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionIdList[0])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_month), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionIdList[0])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }

        binding.appTwoSubButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionIdList[1])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_month), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionIdList[1])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }

        binding.appThreeSubButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionIdList[2])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_month), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionIdList[2])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }

        binding.appOneSubYearButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionYearIdList[0])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_year), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionYearIdList[0])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }

        binding.appTwoSubYearButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionYearIdList[1])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_year), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionYearIdList[1])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }

        binding.appThreeSubYearButton.apply {
            val price = purchaseHelper.getPriceSubscription(subscriptionYearIdList[2])
            if (price != getString(stringsR.string.no_connection)) {
                isEnabled = true
                val textPrice = String.format(getString(stringsR.string.per_year), price)
                text = textPrice
                setOnClickListener {
                    purchaseHelper.getSubscription(subscriptionYearIdList[2])
                }
            } else {
                text = price
            }
            background.setTint(primaryColor)
        }
    }

    private fun setupButtonSupChecked() {
        val check = AppCompatResources.getDrawable(this@PurchaseActivity, R.drawable.ic_check_circle_mini)
        if (purchaseHelper.isSubPurchased(subscriptionIdList[0])) {
            binding.appOneSubButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appOneSubButton.isEnabled = false
        }
        if (purchaseHelper.isSubPurchased(subscriptionIdList[1])) {
            binding.appTwoSubButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appTwoSubButton.isEnabled = false
        }
        if (purchaseHelper.isSubPurchased(subscriptionIdList[2])) {
            binding.appThreeSubButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appThreeSubButton.isEnabled = false
        }
        if (purchaseHelper.isSubPurchased(subscriptionYearIdList[0])) {
            binding.appOneSubYearButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appOneSubYearButton.isEnabled = false
        }
        if (purchaseHelper.isSubPurchased(subscriptionYearIdList[1])) {
            binding.appTwoSubYearButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appTwoSubYearButton.isEnabled = false
        }
        if (purchaseHelper.isSubPurchased(subscriptionYearIdList[2])) {
            binding.appThreeSubYearButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, check)
            binding.appThreeSubYearButton.isEnabled = false
        }
    }

    private fun setupButtonReset() {
        binding.appOneButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appOneSubButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appOneSubYearButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appTwoButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appTwoSubButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appTwoSubYearButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appThreeButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appThreeSubButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
        binding.appThreeSubYearButton.apply {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            text = "..."
            isEnabled = false
        }
    }

    private fun setupIcon() {
        val appDrawable = resources.getColoredDrawableWithColor(this, R.drawable.ic_plus_support, primaryColor)
        binding.topDetails.appLogo.setImageDrawable(appDrawable)
        val themeDrawable = resources.getColoredDrawableWithColor(this, R.drawable.ic_invert_colors, primaryColor)
        binding.themeLogo.setImageDrawable(themeDrawable)
        val colorDrawable = resources.getColoredDrawableWithColor(this, R.drawable.ic_palette, primaryColor)
        binding.colorLogo.setImageDrawable(colorDrawable)
        val plusDrawable = resources.getColoredDrawableWithColor(this, R.drawable.ic_plus_round, primaryColor)
        binding.plusLogo.setImageDrawable(plusDrawable)
        val lifebuoyDrawable = resources.getColoredDrawableWithColor(this, R.drawable.ic_lifebuoy, primaryColor)
        binding.lifebuoyLogo.setImageDrawable(lifebuoyDrawable)
    }

    @Suppress("DEPRECATION")
    private fun setupNoPlayStoreInstalled() {
        val isProApp = resources.getBoolean(R.bool.is_pro_app)
        binding.proDonateText.text =
            if (isProApp) Html.fromHtml(getString(stringsR.string.plus_summary))
            else if (resources.getBoolean(R.bool.using_no_gp) && playStoreInstalled) Html.fromHtml(getString(stringsR.string.donate_text_no_gp_g))
            else Html.fromHtml(getString(stringsR.string.donate_text_g))
        binding.proDonateButton.apply {
            setOnClickListener {
                launchViewIntent("https://www.github.com/Maxitendo1/Special-Contacts#-donation")
            }
            background.setTint(primaryColor)
        }
        if (isProApp) {
            binding.proUnlockText.beGone()
            binding.proSwitchHolder.beGone()
        } else {
            binding.proSwitch.isChecked = if (resources.getBoolean(R.bool.using_no_gp) && playStoreInstalled) baseConfig.isProNoGP else baseConfig.isPro
            binding.proSwitchHolder.setOnClickListener {
                binding.proSwitch.toggle()
                if (resources.getBoolean(R.bool.using_no_gp) && playStoreInstalled) baseConfig.isProNoGP = binding.proSwitch.isChecked
                else baseConfig.isPro = binding.proSwitch.isChecked
            }
        }
    }

    private fun setupCollection() {
        // Collection functionality disabled - not promoting other apps for now
        binding.collectionHolder.beGone()
    }

    private fun launchApp(packageName: String) {
        try {
            Intent(ACTION_MAIN).apply {
                addCategory(CATEGORY_LAUNCHER)
                `package` = packageName
                //component = ComponentName.unflattenFromString("$packageName/")
                addFlags(FLAG_RECEIVER_FOREGROUND)
                startActivity(this)
            }
        } catch (e: Exception) {
            try {
                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                startActivity(launchIntent)
            } catch (e: Exception) {
                showErrorToast(e)
            }
        }
    }
}
