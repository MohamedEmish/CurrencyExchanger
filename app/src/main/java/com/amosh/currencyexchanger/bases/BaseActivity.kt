package com.amosh.currencyexchanger.bases

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.amosh.currencyexchanger.R
import com.amosh.currencyexchanger.utils.ConnectionLiveData

/**
 * Base class for all [AppCompatActivity] instances
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private lateinit var connectionLiveData: LiveData<Boolean>
    private var oldValue: Boolean = true

    private var _binding: VB? = null
    abstract val bindLayout: (LayoutInflater) -> VB

    protected val binding: VB
        get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindLayout.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        prepareView(savedInstanceState)
        checkConnectivity()
    }

    abstract fun prepareView(savedInstanceState: Bundle?)

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun checkConnectivity() {
        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this, Observer { isConnected ->
            isConnected?.let {
                if (it == oldValue) return@let
                oldValue = it
                if (!it)
                    Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, "you are online", Toast.LENGTH_SHORT).show()
            }
        })
    }
}