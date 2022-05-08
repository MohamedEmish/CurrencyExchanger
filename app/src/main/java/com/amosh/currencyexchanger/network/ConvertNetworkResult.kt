package com.amosh.currencyexchanger.network

import java.io.Serializable

data class ConvertNetworkResult(
    val base: String? = null,
    val amount: Double? = null,
    val result: ConversionResult? = null,
) : Serializable