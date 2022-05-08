package com.amosh.currencyexchanger.mapper

import com.amosh.currencyexchanger.data.ConversionHistory
import com.amosh.currencyexchanger.data.Currency
import com.amosh.currencyexchanger.data.Rate
import com.amosh.currencyexchanger.network.CurrencyNetworkResult
import com.amosh.currencyexchanger.network.MultiConversionResult

fun CurrencyNetworkResult.mapToList(): List<Currency> {
    val list: MutableList<Currency> = mutableListOf()
    this.currencies?.forEach { (key, value) ->
        list.add(Currency(
            symbol = key,
            name = value
        ))
    }
    return list
}

fun MultiConversionResult.mapToHistoryObject(target: String): ConversionHistory {
    return ConversionHistory(
        date = this.date,
        target = target,
        rate = this.results?.get(target)
    )
}

fun MultiConversionResult.mapToConversionRates(): List<Rate> {
    val listOfRate: MutableList<Rate> = mutableListOf()
    this.results?.forEach { (target, rate) ->
        listOfRate.add(
            Rate(
                base = this.base,
                target = target,
                rate = rate,
                date = this.date
            )
        )
    }
    return listOfRate
}