package com.ubiquid.ubiquidtest

import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

/**
 *  ViewModel for the MainActivity.
 *  I could have used multiple nested LinearLayout to align unique & total scanned counts, it would
 *  avoid calling to [ResourceProvider.getString] at each call, however nested LinearLayout are also
 *  bad for performance.
 */
class MainViewModel() : ViewModel() {

    private var resourceProvider : ResourceProvider? = null

    constructor(resourceProvider: ResourceProvider) : this() {
        this.resourceProvider = resourceProvider
    }

    // Visibility
    val welcomeStringVisibility = ObservableInt(View.VISIBLE)
    val scannerViewVisibility = ObservableInt(View.GONE)
    val scanStateLayoutVisibility = ObservableInt(View.GONE)

    // Data
    val timeRemaining = ObservableField<String>("")
    val totalScannedString = ObservableField<String>("")
    val uniqueScannedString = ObservableField<String>("")

    var totalScanned = 0
    var uniqueScanned = 0

    fun setScanner() {
        welcomeStringVisibility.set(View.GONE)
        scannerViewVisibility.set(View.VISIBLE)
    }

    fun setWelcome() {
        welcomeStringVisibility.set(View.VISIBLE)
        scanStateLayoutVisibility.set(View.GONE)
        scannerViewVisibility.set(View.GONE)
    }

    /**
     *  Init scan state layout with default values.
     *  @param countDownTimerDuration - The duration of the timer (default is set to 15).
     */
    fun setCountDownScanner(countDownTimerDuration : Long) {
        setScanner()
        scanStateLayoutVisibility.set(View.VISIBLE)
        timeRemaining.set(
            resourceProvider?.getString(R.string.time_remaining) + countDownTimerDuration
        )
        totalScannedString.set(
            resourceProvider?.getString(R.string.total_scanned) + " 0"
        )
        uniqueScannedString.set(
            resourceProvider?.getString(R.string.unique_scanned) + " 0"
        )
    }

    fun updateTimeRemaining(secondsRemaining : Long) {
        timeRemaining.set(
            resourceProvider?.getString(R.string.time_remaining) + " " + secondsRemaining
        )
    }

    fun increaseTotalScanned() {
        totalScanned++
        totalScannedString.set(
            resourceProvider?.getString(R.string.total_scanned) + " " + totalScanned.toString())
    }

    fun increaseUniqueScanned() {
        uniqueScanned++
        uniqueScannedString.set(
            resourceProvider?.getString(R.string.unique_scanned) + " " + uniqueScanned.toString())
    }
}