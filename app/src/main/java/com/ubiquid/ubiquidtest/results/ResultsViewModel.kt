package com.ubiquid.ubiquidtest.results

import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.ubiquid.ubiquidtest.R
import com.ubiquid.ubiquidtest.ResourceProvider

class ResultsViewModel() : ViewModel() {

    private var resourceProvider : ResourceProvider? = null

    constructor(resourceProvider: ResourceProvider) : this() {
        this.resourceProvider = resourceProvider
    }

    // Visibility
    val singleResultVisibility = ObservableInt(View.GONE)
    val multipleResultsVisibility = ObservableInt(View.GONE)

    // Data
    val singleResultContent = ObservableField<String>("")
    val scanReportContent = ObservableField<String>("")
    val scanNoteContent = ObservableField<String>("")

    fun displaySingleResult(result: String) {
        multipleResultsVisibility.set(View.GONE)
        singleResultVisibility.set(View.VISIBLE)
        singleResultContent.set(result)
    }

    /**
     *  @param totalCodeScanned -   Total count of scanned codes.
     *  @param uniqueCodeScanned -  Count of unique scanned codes.
     *  @param countDownDuration -  Duration of the countdown timer, in seconds.
     *  @param note -               Note of the scanner efficiency
     *  I did not succeed at passing varargs parameters to [ResourceProvider.getString], so I made
     *  its Context public and I use it to get strings with parameters.
     *  Really awful but it should work for now.
     */
    fun displayMultipleResults(totalCodeScanned : Int, uniqueCodeScanned : Int,
                               countDownDuration : Int, note : Double) {
        singleResultVisibility.set(View.GONE)
        scanReportContent.set(
            resourceProvider?.mContext?.getString(R.string.scan_report,
                totalCodeScanned,
                uniqueCodeScanned,
                countDownDuration
            )
        )
        scanNoteContent.set(resourceProvider?.mContext?.getString(R.string.scan_note, note))
        multipleResultsVisibility.set(View.VISIBLE)
    }
}