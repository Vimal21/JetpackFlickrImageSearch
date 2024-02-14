package com.flickrs.imagesearch.ui.commons

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


 object Network {

     /**
      * Checks if the device has an active network connection.
      *
      * This function checks if the device has an active network connection by querying
      * the device's connectivity status using the provided [context].
      *
      * @param context The application context.
      * @return `true` if the device has an active network connection, `false` otherwise.
      */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}