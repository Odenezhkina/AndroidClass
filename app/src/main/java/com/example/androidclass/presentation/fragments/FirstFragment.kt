package com.example.androidclass.presentation.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.Fragment
import com.example.androidclass.R
import com.example.androidclass.data.LocationTrackingService
import com.example.androidclass.databinding.FragmentFirstBinding

class FirstFragment : Fragment(R.layout.fragment_first) {
    private var _binding: FragmentFirstBinding? = null
    private val binding: FragmentFirstBinding get() = _binding!!

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsMap ->
            for (permission in permissionsMap) {
                if (permission.key == Manifest.permission.ACCESS_FINE_LOCATION && permission.value
                    || permission.key == Manifest.permission.ACCESS_COARSE_LOCATION && permission.value
                ) {
                    startLocationTrackingService()
                } else if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                    || !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    showToast(R.string.message_change_in_settings)
                } else {
                    showToast(R.string.message_request_rationale)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnStartService.setOnClickListener {
                // check permissions
                permissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            btnStopService.setOnClickListener { stopLocationTrackingService() }
        }
    }

    private fun startLocationTrackingService() {
        val intent =
            Intent(context, LocationTrackingService::class.java)
        startForegroundService(requireContext(), intent)
    }

    private fun stopLocationTrackingService() {
        context?.stopService(Intent(context, LocationTrackingService::class.java))
    }

    private fun showToast(@StringRes messageId: Int) {
        Toast.makeText(context, getString(messageId), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
