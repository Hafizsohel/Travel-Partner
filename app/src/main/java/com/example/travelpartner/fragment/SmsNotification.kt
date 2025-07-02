package com.example.travelpartner.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.app.NotificationChannel
import android.app.NotificationManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.travelpartner.databinding.FragmentSmsNotificationBinding

class SmsNotification : Fragment() {

    private lateinit var binding: FragmentSmsNotificationBinding

    // BroadcastReceiver that will be triggered by MyFirebaseMessagingService
    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Show the red dot when a notification is received
            binding.redDot.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSmsNotificationBinding.inflate(layoutInflater)

        createNotificationChannel()

        binding.btnNotification.setOnClickListener {
            // Hide the red dot when the bell is clicked
            binding.redDot.visibility = View.GONE
            // You can also navigate to the notification list screen here
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Register the broadcast receiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(
                notificationReceiver,
                IntentFilter("com.example.travelpartner.ACTION_NOTIFICATION_RECEIVED"),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            ContextCompat.registerReceiver(
                requireContext(),
                notificationReceiver,
                IntentFilter("com.example.travelpartner.ACTION_NOTIFICATION_RECEIVED"),
                ContextCompat.RECEIVER_EXPORTED
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister the broadcast receiver
        requireContext().unregisterReceiver(notificationReceiver)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id",
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

    }
}

