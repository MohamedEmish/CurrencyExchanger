package com.amosh.currencyexchanger.ui

import android.os.Bundle
import android.view.LayoutInflater
import com.amosh.currencyexchanger.bases.BaseActivity
import com.amosh.currencyexchanger.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val bindLayout: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun prepareView(savedInstanceState: Bundle?) = Unit
}