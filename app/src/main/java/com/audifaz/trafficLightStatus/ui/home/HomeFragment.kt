package com.audifaz.trafficLightStatus.ui.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Settings.Global.DEVICE_NAME
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.audifaz.trafficLightStatus.R
import com.audifaz.trafficLightStatus.databinding.FragmentHomeBinding
import com.audifaz.trafficLightStatus.util.NORDIC_MANUFACTURING_ID
import com.audifaz.trafficLightStatus.util.ReaderData
import com.audifaz.trafficLightStatus.util.UC_NAME

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    lateinit var homeViewModel : HomeViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
            if(!it.equals("Waiting")){
                val mediaPlayer: MediaPlayer =
                    MediaPlayer.create(requireActivity(), R.raw.beep)
                mediaPlayer.start()
            }
        }
        binding.buttonStart.setOnClickListener {
            //Toast.makeText(requireContext(), "Listening started", Toast.LENGTH_LONG).show()
            startBleScan()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Bluetooth
    fun stopScan():Boolean{
        bleScanner.stopScan(scanCallback)
        return true
    }

    fun startBleScan() {
                Log.i("GalleryFragment", "onScanResult")
                try {
                    bleScanner.startScan(filter, scanSettings, scanCallback)
                }catch (e: java.lang.NullPointerException){
                    Log.i("PairDeviceActivity", "startBleScan: Bluetooth off")
                    val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                   val layoutInflater = LayoutInflater.from(requireContext())
                    val view = layoutInflater.inflate(R.layout.dialog_error, null)
                    builder.setView(view)
                    val dialog = builder.create()
                    val errorMessage : TextView = view.findViewById(R.id.error)
                    errorMessage.text = "Bluetooth error, please try again."

                    dialog.show()
//            dialog.setCanceledOnTouchOutside(false)
                    val buttonOk : Button = view.findViewById(R.id.okButton)

                    buttonOk.setOnClickListener {
                        dialog.dismiss()
                    }
                }
    }

    val filter = listOf(ScanFilter.Builder().setDeviceName(UC_NAME).build())

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder().setScanMode(
        ScanSettings.SCAN_MODE_LOW_LATENCY
    ).build()

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager =
            activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    var state = ""

    @OptIn(ExperimentalUnsignedTypes::class)
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result != null) {
                try {
                    val scannerBytecode =
                        result.scanRecord?.getManufacturerSpecificData(NORDIC_MANUFACTURING_ID)
                            ?.toUByteArray()!!
                    val rd = ReaderData().extractReaderData(scannerBytecode)
                    if(rd?.dev_id == 5205L){
//                        Log.i("GalleryFragment", "onScanResult: Device ${rd?.dev_id}} & ${rd.got_id} & ${rd.scanning} ")
                        if(rd.scanning && !rd.got_id && !state.equals("Red")){
                            Log.i("TrafficLight", "Red Status")
                            state="Red"
                            homeViewModel.setText("Red")
                        } else if (!rd.scanning && rd.got_id && !state.equals("Green")){
                            Log.i("TrafficLight", "Green Status")
                            state="Green"
                            homeViewModel.setText("Green")
                        }
                    }
                }catch (e: Exception){
                    Log.e("TryCatch", e.toString())
                }
            }
        }
    }

}

//                        if(!toggle){
//                            if (diffTime == -1000L) {
//                                diffTime = currentTime
//                            } else {
//                                diffTime = System.currentTimeMillis() - currentTime
//                            }
//                            currentTime = System.currentTimeMillis()
//                            counter++
//                            diffSum += diffTime
//                            if (counter == 100) {
//                                Log.i("TIME_TEST_AVG", "Average: ${diffSum / 100}")
//                                diffSum = 0L
//                                counter = 1
//                            }
//                            Log.i("TIME_TEST", "${diffTime}")
//                        }

//currentTimeUi = System.currentTimeMillis()
//diffTimeUi = currentTimeUi-currentTime
//Log.i("UI_TEST", "$diffTimeUi")
//toggle= false

//var currentTime =  0L
//var diffTime = -1000L
//var counter = 1
//var diffSum = 0L
//var toggle = false
//var currentTimeUi = 0L
//var diffTimeUi = 0L