package com.ubiquid.ubiquidtest

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ResultsActivity : AppCompatActivity() {

    // Layout variables
    private var toolbar : Toolbar? = null
    private var singleResult : TextView? = null
    private var multipleResultsRv : RecyclerView? = null

    private var resultsAdapter = ResultsAdapter()

    private var results : ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        results = intent.getStringArrayListExtra(ARG_RESULTS)

        setupToolbar()

        singleResult = findViewById(R.id.single_result)
        multipleResultsRv = findViewById(R.id.multiple_result_rv)

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
        // TODO : Use a ViewModel
        multipleResultsRv?.visibility = View.GONE

        singleResult?.text = results!![0]
        singleResult?.visibility = View.VISIBLE
    }

    /**
     *  Used when [results] contains at least 2 entries. Will display them in a [RecyclerView].
     */
    private fun displayMultipleResults() {
        // TODO : Use a ViewModel
        singleResult?.visibility = View.GONE

        multipleResultsRv?.layoutManager = LinearLayoutManager(this)
        multipleResultsRv?.adapter = resultsAdapter
        multipleResultsRv?.post { resultsAdapter.addAll(results!!) }
        multipleResultsRv?.visibility = View.VISIBLE
    }

    companion object {
        private const val TAG = "ResultsActivity"

        private const val ARG_RESULTS = "results"

        fun getStartIntent(context : Context, results : ArrayList<String>) : Intent {
            val intent = Intent(context, ResultsActivity::class.java)
            intent.putExtra(ARG_RESULTS, results)
            return intent
        }
    }
}