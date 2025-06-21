package com.maxitendo.commons.helpers

import android.app.Activity
import androidx.lifecycle.MutableLiveData

/**
 * FOSS-compliant PurchaseHelper stub implementation
 * This version removes all Google Play Billing dependencies for FOSS builds
 */
class PurchaseHelper (
    val activity: Activity,
    ) {
    // FOSS stub - no billing functionality
    val iapSkuDetailsInitialized = MutableLiveData(false)
    val subSkuDetailsInitialized = MutableLiveData(false)
    
    private var iapList = ArrayList<String>()
    private var subList = ArrayList<String>()
    private var iapPurchased: ArrayList<String> = arrayListOf()
    private var subPurchased: ArrayList<String> = arrayListOf()
    
    val isIapPurchasedList = MutableLiveData<ArrayList<String>>()
    val isSupPurchasedList = MutableLiveData<ArrayList<String>>()
    val isIapPurchased = MutableLiveData<Tipping>()
    val isSupPurchased = MutableLiveData<Tipping>()
    
    // FOSS stub methods - no actual billing functionality
    fun initBillingClient() {
        // No-op for FOSS builds
    }
    
    fun retrieveDonation(iaps: ArrayList<String>, subs: ArrayList<String>) {
        // No-op for FOSS builds
        this.iapList = iaps
        this.subList = subs
    }
    
    fun queryIapPurchases() {
        // No-op for FOSS builds
    }
    
    fun querySubPurchases() {
        // No-op for FOSS builds
    }
    
    fun queryIapPurchasesHistory() {
        // No-op for FOSS builds
    }
    
    fun querySubPurchasesHistory() {
        // No-op for FOSS builds
    }
    
    fun initIapPurchases(iapList: ArrayList<String>) {
        this.iapList = iapList
    }
    
    fun initSubPurchases(subList: ArrayList<String>) {
        this.subList = subList
    }
    
    fun purchaseIap(product: String) {
        // No-op for FOSS builds
    }
    
    fun purchaseSub(product: String, planId: String? = null) {
        // No-op for FOSS builds
    }
    
    fun getPriceDonation(product: String): String {
        return "Free" // Always free for FOSS builds
    }
    
    fun getDonation(product: String) {
        // No-op for FOSS builds
    }
    
    fun isIapPurchased(product: String): Boolean {
        return false // Always false for FOSS builds
    }
    
    fun isSubPurchased(product: String): Boolean {
        return false // Always false for FOSS builds
    }
    
    fun getPriceSubscription(product: String): String {
        return "Free" // Always free for FOSS builds
    }
    
    fun getSubscription(product: String) {
        // No-op for FOSS builds
    }
}

sealed class Tipping {
    data object FailedToLoad : Tipping()
    data object Succeeded : Tipping()
    data object NoTips : Tipping()
}
