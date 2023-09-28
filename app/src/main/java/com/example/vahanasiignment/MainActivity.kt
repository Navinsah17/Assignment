package com.example.vahanasiignment

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.vahanasiignment.databinding.ActivityMainBinding
import com.example.vahanasiignment.repository.UniversityRepository
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: UniversityViewModel
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        onClickRequestPermission(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.title = ""
        setSupportActionBar(binding.toolbar)


//        val viewModelProviderFactory = UniversityViewModelFactoryProvider(universityRepository = UniversityRepository())
//        viewModel = ViewModelProvider(
//            this,
//            viewModelProviderFactory
//        )[UniversityViewModel::class.java]
        viewModel = UniversityViewModel.getInstance(UniversityRepository())


        val serviceIntent = Intent(this, DataRefreshService::class.java)
        startService(serviceIntent)



    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notifiy, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            /*R.id.notifaction_Off->{
                if(isMyServiceRunning()){
                    val serviceIntent = Intent(this, DataRefreshService::class.java)
                    stopService(serviceIntent)
                }
                return true
            }*/
            R.id.notifaction_On->{
                val serviceIntent = Intent(this, DataRefreshService::class.java)
                if (!isMyServiceRunning()){
                    startService(serviceIntent)

                }else{
                    stopService(serviceIntent)

                }
                return true

            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun isMyServiceRunning(): Boolean{

        val manager: ActivityManager = getSystemService(
            Context.ACTIVITY_SERVICE) as ActivityManager

        for (service : ActivityManager.RunningServiceInfo in
        manager.getRunningServices(Integer.MAX_VALUE)){

            if(DataRefreshService::class.java.name == service.service.className){
                return true
            }

        }
        return false

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("Permission: ", "Granted")
        } else {
            Log.d("Permission: ", "Denied")
        }
    }

    private fun onClickRequestPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("Permission: ", "Granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                Snackbar.make(
                    view,
                    "Allow Notification",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("OK") {
                    requestPermissionLauncher.launch(
                        android.Manifest.permission.POST_NOTIFICATIONS
                    )
                }.setActionTextColor(resources.getColor(android.R.color.holo_red_light)).show()
            }
            else -> {
                requestPermissionLauncher.launch(
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

}