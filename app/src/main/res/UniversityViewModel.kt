package com.example.vahanasiignment.ui

import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vahanasiignment.UniversityApplication
import com.example.vahanasiignment.models.University
import com.example.vahanasiignment.repository.UniversityRepository
import com.example.vahanasiignment.utils.Resource

import kotlinx.coroutines.launch

class UniversityViewModel(app: Application, val universityRepository: UniversityRepository):AndroidViewModel(app) {
    var universityList: MutableLiveData<Resource<List<University>>> = MutableLiveData()


    init {
        getUniversity()
    }

    companion object {
        private var instance: UniversityViewModel? = null

        fun getInstance(universityRepository: UniversityRepository): UniversityViewModel {
            if (instance == null) {
                instance = UniversityViewModel(app = Application(),universityRepository)
            }
            return instance!!
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<UniversityApplication>().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            /*val activeNetworkInfo = connectivityManager.activeNetworkInfo ?: return false
            return activeNetworkInfo.isConnected*/
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }

        }
        return false
    }



    fun getUniversity() = viewModelScope.launch {
        try {
            if (hasInternetConnection()){
                universityList.postValue(Resource.Loading())
                val response = universityRepository.getUniversity()
                if (response.isSuccessful) {
                    response.body()?.let { result ->
                        universityList.postValue(Resource.Success(result))
                        Log.d("TAG", result[0].name)
                    }
                }
                else {
                    // Handle the case where the network request was not successful
                    universityList.postValue(Resource.Error("Network request failed"))
                }
            } else {
                // Handle the case where there is no internet connection
                universityList.postValue(Resource.Error("No internet connection"))
            }


        }catch (e:Exception){
            universityList.postValue(Resource.Error("Network Failure"))

        }


    }

//    private fun showNoInternetDialog() {
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setIcon(android.R.drawable.ic_dialog_alert)
//        builder.setCancelable(false)
//        builder.setTitle("No Internet Connection")
//        builder.setMessage("Please check your internet connection")
//        builder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
//            // Close the app when the user clicks "OK"
//            ?.finish()
//        }
//        val dialog = builder.create()
//        dialog.show()
//    }


}