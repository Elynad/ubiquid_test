package com.ubiquid.ubiquidtest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class MainActivity : AppCompatActivity() {

    // Layout variables
    private var toolbar : Toolbar? = null
    private var welcomeString : TextView? = null
    private var scannerView : ZXingScannerView? = null

    private var enableCountDown : Boolean = false
    private var results : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        welcomeString = findViewById(R.id.welcome_string)
        scannerView = findViewById(R.id.scanner_view)

        setSupportActionBar(toolbar)
    }

    /**
     *  Check if the app is allowed to use user's camera, and ask for permission if it does not.
     *  If we have permission, hides the welcome [TextView], shows the [ZXingScannerView] and starts
     *  the scanner.
     */
    private fun prepareScanner() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        } else {
            // TODO : Use a viewModel
            welcomeString?.visibility = View.GONE
            scannerView?.visibility = View.VISIBLE
            startScan()
        }
    }

    /**
     *  @param enableCountDown - Determine if we are in countDown mode or not.
     *
     *  Starts the camera of the [ZXingScannerView] and set a [ZXingScannerView.ResultHandler] on
     *  it. When the scanner finds out a result, we stop the camera (to avoid memory overflows) and
     *  call [startScan] again.
     */
    private fun startScan() {
        scannerView?.startCamera()
        scannerView?.setResultHandler(object : ZXingScannerView.ResultHandler {
            override fun handleResult(rawResult: Result?) {
                Log.d(TAG, "Should handle result !")
                Log.e(TAG, "RawResult = $rawResult")
                results.add(rawResult?.text!!)
                scannerView?.stopCamera()
                if (enableCountDown)
                    startScan()
                else
                    displayResults()
            }

        })
    }

    private fun displayResults() {
        startActivity(ResultsActivity.getStartIntent(this, results))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_scan -> {
                prepareScanner()
                true
            }
            R.id.action_countdown -> {
                enableCountDown = true
                prepareScanner()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                prepareScanner()
            else
                Toast.makeText(this, getString(R.string.camera_permission_explanation),
                        Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "MainActivity"

        private const val PERMISSION_REQUEST_CODE = 5
    }
}