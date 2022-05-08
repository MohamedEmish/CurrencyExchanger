package com.amosh.currencyexchanger.data

import java.io.Serializable

data class Currency(
    val symbol: String? = null,
    val name: String? = null,
) : Serializable