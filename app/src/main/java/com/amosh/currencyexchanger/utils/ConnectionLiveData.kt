package com.amosh.currencyexchanger.utils

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData

class ConnectionLiveData(val context: Context) : LiveData<Boolean>() {

    private var connectivityManager: ConnectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback

    private val networkRequestBuilder: NetworkRequest.Builder = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)

    override fun onActive() {
        super.onActive()
        updateConnection()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> connectivityManager.registerDefaultNetworkCallback(getConnectivityMarshmallowManagerCallback())
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> marshmallowNetworkAvailableRequest()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> lollipopNetworkAvailableRequest()
            else -> {
                Log.e(this.javaClass.simpleName, "This build version is not supported")
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
        } else context.unregisterReceiver(networkReceiver)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipopNetworkAvailableRequest() =
        connectivityManager.registerNetworkCallback(networkRequestBuilder.build(), getConnectivityLollipopManagerCallback())

    @TargetApi(Build.VERSION_CODES.M)
    private fun marshmallowNetworkAvailableRequest() =
        connectivityManager.registerNetworkCallback(networkRequestBuilder.build(), getConnectivityMarshmallowManagerCallback())

    private fun getConnectivityLollipopManagerCallback(): ConnectivityManager.NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) = postValue(true)

                override fun onLost(network: Network) = postValue(false)
            }
            return connectivityManagerCallback
        } else throw IllegalAccessError("Accessing wrong API version")
    }

    private fun getConnectivityMarshmallowManagerCallback(): ConnectivityManager.NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    networkCapabilities.let { capabilities ->
                        if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
                            postValue(true)
                    }
                }

                override fun onLost(network: Network) = postValue(false)
            }
            return connectivityManagerCallback
        } else throw IllegalAccessError("Accessing wrong API version")
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = updateConnection()
    }

    private fun updateConnection() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val nw = connectivityManager.activeNetwork
                val actNw = connectivityManager.getNetworkCapabilities(nw)
                when {
                    actNw == null -> postValue(false)
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> postValue(true)
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> postValue(true)
                    //for other device how are able to connect with Ethernet
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> postValue(true)
                    //for check internet over Bluetooth
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> postValue(true)
                    else -> postValue(false)
                }
            } else {
                val nwInfo = connectivityManager.activeNetworkInfo
                if (nwInfo == null) postValue(false)
                postValue(nwInfo?.isConnectedOrConnecting == true)
            }
        } catch (e: Exception) {
            postValue(false)
        }
    }
}