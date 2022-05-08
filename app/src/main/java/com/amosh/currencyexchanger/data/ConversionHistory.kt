package com.amosh.currencyexchanger.data

import java.io.Serializable

data class ConversionHistory(
    val date: String? = null,
    val target: String? = null,
    val rate: Double? = null,
): Serializable
