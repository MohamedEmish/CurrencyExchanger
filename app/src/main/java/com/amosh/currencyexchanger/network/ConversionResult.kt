package com.amosh.currencyexchanger.network

import java.io.Serializable

data class ConversionResult(
    val rate: Double? = null,
    val map: Map<String, Double>? = null
): Serializable
