package com.audifaz.trafficLightStatus

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.audifaz.trafficLightStatus.databinding.ActivityMainBinding
import com.audifaz.trafficLightStatus.util.LOCATION_PERMISSION_REQUEST_CODE

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        checkPermissions()
    }

    //Permissions
    fun checkPermissions(){
        Log.i("MainActivityPermissions", "It enters the function")
        Log.i("MainActivityPermissions", "$isLocationPermissionGranted")
        if(!isLocationPermissionGranted) {
            Log.i("MainActivityPermissions", "It does not have location permissions")
            checkForPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                "location",
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        if (!bluetoothAdapter.isEnabled) {
            Log.i("MainActivityPermissions", "It does not have bluetooth activated")
            promptEnableBluetooth()
        }
    }

    private fun checkForPermission(permission: String, name: String, requestCode: Int) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(this, "$name permission granted", Toast.LENGTH_SHORT)
                    .show()
            }
            shouldShowRequestPermissionRationale(permission) -> showDialog(
                permission,
                name,
                requestCode
            )

            else -> ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                requestCode
            )
        }
    }

    //val isLocationPermissionGranted get() = hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)

    private fun promptEnableBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        responseLauncher.launch(enableBtIntent)
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager =
            this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            Log.d("TEST", activityResult.resultCode.toString())
            if (activityResult.resultCode != AppCompatActivity.RESULT_OK) {
                Log.d("TEST", "bluetooth not enabled")
                promptEnableBluetooth()
            }
        }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage(("Permission to access your $name is required to use this app"))
            setTitle("Permission required")
            setPositiveButton("OK") { dialog, which ->
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission),
                    requestCode
                )
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "$name permission refused", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "$name permission granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> innerCheck("location")
        }
    }

    val isLocationPermissionGranted get() = hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) == PackageManager.PERMISSION_GRANTED
    }

}