package com.amosh.currencyexchanger.network

import java.io.Serializable

data class MultiConversionResult(
    val date: String? = null,
    val base: String? = null,
    val results: Map<String, Double>? = null,
): Serializable