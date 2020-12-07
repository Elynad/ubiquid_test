package com.ubiquid.ubiquidtest

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class ResultsActivity : AppCompatActivity() {

    private var results : ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        results = intent.getStringArrayListExtra(ARG_RESULTS)
        results?.forEach {
            Log.d(TAG, "Item $it")
        }
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