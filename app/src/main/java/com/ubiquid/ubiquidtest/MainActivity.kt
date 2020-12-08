package com.ubiquid.ubiquidtest

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.Result
import com.ubiquid.ubiquidtest.databinding.ActivityMainBinding
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.function.LongToDoubleFunction

class MainActivity : AppCompatActivity() {

    // Layout variables
    private var toolbar : Toolbar? = null
    private var welcomeString : TextView? = null
    private var scannerView : ZXingScannerView? = null

    // Helper classes
    private var viewModel : MainViewModel? = null
    private var resourceProvider : ResourceProvider? = null

    private var enableCountDown : Boolean = false
    private var results : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (resourceProvider == null)
            resourceProvider = ResourceProvider().resourceProvider(this)
        viewModel = MainViewModel(resourceProvider!!)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,
            R.layout.activity_main)
        binding.viewModel = viewModel

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
            viewModel?.setScanner()

            if (enableCountDown) {
                viewModel?.setCountDownScanner(COUNT_DOWN_MODE_DURATION)
                val timer = object : CountDownTimer(COUNT_DOWN_MODE_DURATION, 1000) {
                    override fun onFinish() {
                        Log.d(TAG, "COUNT DOWN FINISHED !")
                        scannerView?.stopCamera()
                        displayResults() // TODO : Display error toast if no results were found
                        enableCountDown = false
                    }

                    override fun onTick(millisRemaining: Long) {
                        viewModel?.updateTimeRemaining(millisRemaining / 1000)
                    }

                }
                startScan()
                timer.start()
            } else
                startScan()
        }
    }

    /**
     *  Starts the camera of the [ZXingScannerView] and set a [ZXingScannerView.ResultHandler] on
     *  it. When the scanner finds out a result, we stop the camera (to avoid memory overflows) and
     *  call [startScan] (itself) again.
     *  TODO : Update doc
     */
    private fun startScan() {
        scannerView?.startCamera()
        scannerView?.setResultHandler(object : ZXingScannerView.ResultHandler {
            override fun handleResult(rawResult: Result?) {

                viewModel?.increaseTotalScanned()

                // Check if we did not already scan this
                if (!results.contains(rawResult?.text)) {
                    results.add(rawResult?.text!!)
                    viewModel?.increaseUniqueScanned()
                }

                if (enableCountDown) scannerView?.resumeCameraPreview(this)
                else displayResults()
            }
        })
    }

    private fun displayResults() {
        if (results.isNotEmpty())
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

    override fun onPause() {
        scannerView?.stopCamera()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel?.setWelcome()
    }

    companion object {
        private const val TAG = "MainActivity"

        private const val COUNT_DOWN_MODE_DURATION = 15000L

        private const val PERMISSION_REQUEST_CODE = 5
    }
}