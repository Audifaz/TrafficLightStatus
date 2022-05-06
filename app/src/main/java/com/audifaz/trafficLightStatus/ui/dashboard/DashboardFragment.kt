package com.audifaz.trafficLightStatus.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.audifaz.trafficLightStatus.R
import com.audifaz.trafficLightStatus.databinding.FragmentDashboardBinding

const val TAG = "SettingsFragment"

//settings fragment
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.switchMute.setOnCheckedChangeListener { button, isTurnOn ->
            Log.i(TAG, "onCreateView: $isTurnOn")
        }
        
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.originalRadioButton->{
                    Log.i(TAG, "onCreateView: Original")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
                R.id.AlternativeRadioButton->{
                    Log.i(TAG, "onCreateView: Alternative")
//                    requireActivity().setTheme(R.style.Theme_TrafficLightStatusInverse)
                }
                R.id.hornRadioButton->{
                    Log.i(TAG, "onCreateView: Horn")
                }
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}