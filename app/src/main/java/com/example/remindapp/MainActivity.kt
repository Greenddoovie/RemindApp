package com.example.remindapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.remindapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permissionCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it == false) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Overlay Permission Is Needed", Toast.LENGTH_LONG).show()
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }
    }

    private fun checkPermission() {
        permissionCallback.launch(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()
        val idx = intent?.extras?.get("remindIdx")

        if (idx != null) {
            val id = idx as Int
            val bundle = Bundle()
            bundle.putInt("remindIdx", id)
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.action_navigation_home_to_navigation_notifications, bundle)
        }
    }
}