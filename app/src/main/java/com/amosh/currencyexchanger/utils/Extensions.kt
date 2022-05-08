package com.amosh.currencyexchanger.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

fun EditText.clickableOnly() {
    this.isFocusable = false
    this.isClickable = true
}

@ExperimentalCoroutinesApi
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(s)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

fun Double.shorten(decimals: Int): Double{ return String.format("%.$decimals" + "f", this).replace(',', '.').toDouble() }

fun <T> List<T>.toArrayList(): ArrayList<T>{
    return ArrayList(this)
}

@Suppress("DEPRECATION") // Fixed for higher versions
fun Context?.isOffline(): Boolean {
    val connectivityManager =
        this?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            ?: return true
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return true
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return true
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> false
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> false
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> false
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> false
            else -> true
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return true
        return !nwInfo.isConnected
    }
}