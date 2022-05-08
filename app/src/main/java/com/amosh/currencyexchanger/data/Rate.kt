package com.amosh.currencyexchanger.data

import java.io.Serializable

data class Rate(
    val base: String? = null,
    val target: String? = null,
    val rate: Double? = null,
    val date: String? = null,
) : Serializable
