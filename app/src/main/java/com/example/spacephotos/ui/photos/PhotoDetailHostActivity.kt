package com.example.spacephotos.ui.photos

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.spacephotos.R
import com.example.spacephotos.databinding.*
import com.example.spacephotos.utils.ApiKeys

class PhotoDetailHostActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    val TAG: String = PhotoDetailHostActivity::class.java.name
    val url = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=111&api_key=${ApiKeys.nasa_api_key}"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "on create:")
        super.onCreate(savedInstanceState)

        val binding = ActivityPhotosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_photo_detail) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_photo_detail)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


}