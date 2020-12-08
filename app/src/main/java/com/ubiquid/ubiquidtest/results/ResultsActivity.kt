package com.ubiquid.ubiquidtest.results

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ubiquid.ubiquidtest.R
import com.ubiquid.ubiquidtest.ResourceProvider
import com.ubiquid.ubiquidtest.databinding.ActivityMainBinding
import com.ubiquid.ubiquidtest.databinding.ActivityResultsBinding
import com.ubiquid.ubiquidtest.main.MainViewModel

class ResultsActivity : AppCompatActivity() {

    // Layout variables
    private var toolbar : Toolbar? = null
    private var singleResult : TextView? = null
    private var multipleResultsRv : RecyclerView? = null

    // Helper classes
    private var viewModel : ResultsViewModel? = null
    private var resourceProvider : ResourceProvider? = null
    private var resultsAdapter = ResultsAdapter()

    private var results : ArrayList<String>? = null
    private var totalScanned : Int = 0
    private var countDownDuration : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup ViewModel
        if (resourceProvider == null)
            resourceProvider = ResourceProvider().resourceProvider(this)
        viewModel = ResultsViewModel(resourceProvider!!)

        // Inflate view
        val binding = DataBindingUtil.setContentView<ActivityResultsBinding>(this,
            R.layout.activity_results)
        binding.viewModel = viewModel

        // Init layout variables
        singleResult = findViewById(R.id.single_result)
        multipleResultsRv = findViewById(R.id.multiple_result_rv)

        // Retrieve results from previous Activity
        results = intent.getStringArrayListExtra(ARG_RESULTS)
        totalScanned = intent.getIntExtra(ARG_TOTAL_SCANNED, 0)
        countDownDuration = intent.getLongExtra(ARG_COUNTDOWN_DURATION, 0)

        // Init toolbar
        setupToolbar()

        // Display results
        if (results != null && results!!.size > 1) displayMultipleResults()
        else displaySingleResult()
    }

    /**
     *  Init [Toolbar] with a title, and setup its back button.
     */
    private fun setupToolbar() {
        toolbar = findViewById(R.id.results_toolbar)
        toolbar?.title = resources.getQuantityString(R.plurals.title_results, results?.size!!)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     *  Used when [results] only contains a single entry. Will display its entry on a simple
     *  [TextView].
     */
    private fun displaySingleResult() {
        viewModel?.displaySingleResult(results!![0])
    }

    /**
     *  Used when [results] contains at least 2 entries. Will display them in a [RecyclerView].
     */
    private fun displayMultipleResults() {
        multipleResultsRv?.layoutManager = LinearLayoutManager(this)
        multipleResultsRv?.adapter = resultsAdapter
        multipleResultsRv?.post { resultsAdapter.addAll(results!!) }
        viewModel?.displayMultipleResults(totalScanned, results!!.size,
            (countDownDuration / 1000).toInt(), calculateNote())
    }

    /**
     *  Used to note the efficiency of the scanner. It basically calculates the number of scanned
     *  code (not unique) by second.
     *  I choose to use total scanned code instead of unique codes, as this is not the scanner
     *  fault, but the user's one, as he just has to make the codes follow faster.
     *  @return - A note on the scanner efficiency.
     */
    private fun calculateNote() : Double {
        return (totalScanned.toDouble() / (countDownDuration / 1000).toDouble())
    }

    companion object {
        private const val TAG = "ResultsActivity"

        private const val ARG_RESULTS = "results"
        private const val ARG_TOTAL_SCANNED = "total_scanned"
        private const val ARG_COUNTDOWN_DURATION = "countdown_duration"

        fun getStartIntent(context : Context, results : ArrayList<String>, countDownDuration : Long,
                           totalScanned : Int) : Intent {
            val intent = Intent(context, ResultsActivity::class.java)
            intent.putExtra(ARG_RESULTS, results)
            intent.putExtra(ARG_TOTAL_SCANNED, totalScanned)
            intent.putExtra(ARG_COUNTDOWN_DURATION, countDownDuration)
            return intent
        }
    }
}