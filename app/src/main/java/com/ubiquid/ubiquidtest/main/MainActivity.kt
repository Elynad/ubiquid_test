package com.ubiquid.ubiquidtest.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.zxing.Result
import com.ubiquid.ubiquidtest.R
import com.ubiquid.ubiquidtest.ResourceProvider
import com.ubiquid.ubiquidtest.databinding.ActivityMainBinding
import com.ubiquid.ubiquidtest.results.ResultsActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView

class MainActivity : AppCompatActivity() {

    // Layout variables
    private var toolbar : Toolbar? = null
    private var welcomeString : TextView? = null
    private var scannerView : ZXingScannerView? = null

    // Helper classes
    private var viewModel : MainViewModel? = null
    private var resourceProvider : ResourceProvider? = null

    private var enableCountDown : Boolean = false
    private var countDownTimer : CountDownTimer? = null
    private var results : ArrayList<String> = ArrayList()
    private var totalScanned : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup ViewModel
        if (resourceProvider == null)
            resourceProvider = ResourceProvider().resourceProvider(this)
        viewModel = MainViewModel(resourceProvider!!)

        // Inflate view
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,
                R.layout.activity_main)
        binding.viewModel = viewModel

        // Init layout variables
        toolbar = findViewById(R.id.toolbar)
        welcomeString = findViewById(R.id.welcome_string)
        scannerView = findViewById(R.id.scanner_view)

        // Init toolbar
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
            toolbar?.menu?.clear()
            toolbar?.inflateMenu(R.menu.toolbar_menu_quit)

            if (enableCountDown) {
                viewModel?.setCountDownScanner(COUNT_DOWN_MODE_DURATION)
                countDownTimer = object : CountDownTimer(COUNT_DOWN_MODE_DURATION, 1000) {
                    override fun onFinish() {
                        scannerView?.stopCamera()
                        displayResults()
                        enableCountDown = false
                    }

                    override fun onTick(millisRemaining: Long) {
                        viewModel?.updateTimeRemaining(millisRemaining / 1000)
                    }

                }
                countDownTimer?.start()
            }
            startScan()
        }
    }

    /**
     *  Starts the camera of the [ZXingScannerView] and set a [ZXingScannerView.ResultHandler] on
     *  it. When the scanner finds out a result, we check if it is not already inside [results], and
     *  add it if it does not.
     *  Then, if the CountDown mode is enable (if [enableCountDown] is true), we resume camera
     *  preview in order to scan more codes.
     *  If this mode is disabled, displays results by calling [displayResults].
     */
    private fun startScan() {
        scannerView?.startCamera()
        scannerView?.setResultHandler(object : ZXingScannerView.ResultHandler {
            override fun handleResult(rawResult: Result?) {

                viewModel?.increaseTotalScanned()
                totalScanned++

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

    /**
     *  Checks [results].
     *  If it is empty, calls [closeScanner] and displays an error [Toast] to the user.
     *  If [results] is not empty, starts [ResultsActivity] with it as parameter.
     */
    private fun displayResults() {
        if (results.isNotEmpty())
            startActivity(ResultsActivity
                .getStartIntent(this, results, COUNT_DOWN_MODE_DURATION, totalScanned))
        else {
            closeScanner()
            Toast.makeText(this, getString(R.string.nothing_found), Toast.LENGTH_LONG).show()
        }
    }

    /**
     *  Triggered when the user closes the timer. Will stop the camera, set back the 'Welcome' view,
     *  cancel the [CountDownTimer] and set back the original menu.
     */
    private fun closeScanner() {
        scannerView?.stopCamera()
        countDownTimer?.cancel()
        toolbar?.menu?.clear()
        toolbar?.inflateMenu(R.menu.toolbar_menu)
        viewModel?.setWelcome()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_scan -> {
                enableCountDown = false
                prepareScanner()
                true
            }
            R.id.action_countdown -> {
                enableCountDown = true
                prepareScanner()
                true
            }
            R.id.action_quit -> {
                closeScanner()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                prepareScanner()
            else
                Toast.makeText(this, getString(R.string.camera_permission_explanation),
                        Toast.LENGTH_LONG).show()
        }
    }

    /**
     *  When pausing the Activity, we set back welcome layout and original toolbar.
     *  We cannot do this in the onResume, as we go there after asking for permissions (it would
     *  make the user need to click twice on the button to use scanner).
     */
    override fun onPause() {
        scannerView?.stopCamera()
        countDownTimer?.cancel()
        toolbar?.menu?.clear()
        toolbar?.inflateMenu(R.menu.toolbar_menu)
        viewModel?.setWelcome()
        super.onPause()
    }

    companion object {
        @Suppress("unused") // Used for debug logs
        private const val TAG = "MainActivity"

        private const val COUNT_DOWN_MODE_DURATION = 15000L

        private const val PERMISSION_REQUEST_CODE = 5
    }
}