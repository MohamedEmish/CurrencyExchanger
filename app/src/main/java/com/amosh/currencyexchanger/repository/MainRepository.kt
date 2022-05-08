package com.amosh.currencyexchanger.repository

import android.content.Context
import com.amosh.currencyexchanger.bases.BaseRepository
import com.amosh.currencyexchanger.data.AppResult
import com.amosh.currencyexchanger.data.ConversionHistory
import com.amosh.currencyexchanger.data.Currency
import com.amosh.currencyexchanger.data.Rate
import com.amosh.currencyexchanger.mapper.mapToConversionRates
import com.amosh.currencyexchanger.mapper.mapToHistoryObject
import com.amosh.currencyexchanger.mapper.mapToList
import com.amosh.currencyexchanger.network.ApiService
import com.amosh.currencyexchanger.network.MultiConversionResult
import dagger.hilt.android.qualifiers.ApplicationContext

class MainRepository constructor(
    @ApplicationContext val context: Context,
    private val apiService: ApiService,
) : BaseRepository(context) {

    suspend fun getCurrenciesList(): AppResult<List<Currency>> {
        return try {
            val networkResponse = apiService.getCurrencies()
            AppResult.Success(networkResponse.mapToList())
        } catch (e: Exception) {
            AppResult.Error(e.message)
        }
    }

    suspend fun convert(
        from: String,
        to: String,
        amount: Double,
    ): AppResult<Double> {
        return try {
            val networkResponse = apiService.convert(
                fromSymbol = from,
                toSymbol = to,
                amount = amount
            )
            AppResult.Success((networkResponse.result?.rate ?: 1.0) * (networkResponse.amount ?: 1.0))
        } catch (e: Exception) {
            AppResult.Error(e.message)
        }
    }

    suspend fun getMultiConversion(
        base: String,
        targets: List<String>,
    ): AppResult<List<Rate>> {
        return try {
            val networkResponse = apiService.getMultiConversion(
                base = base,
                target = targets.joinToString(",")
            )
            AppResult.Success(networkResponse.mapToConversionRates())
        } catch (e: Exception) {
            AppResult.Error(e.message)
        }
    }

    suspend fun getHistoryOf(
        base: String,
        date: String,
        target: String,
    ): AppResult<ConversionHistory> {
        return try {
            val networkResponse = apiService.getHistoryOf(
                base = base,
                date = date
            )
            AppResult.Success(networkResponse.mapToHistoryObject(target))
        } catch (e: Exception) {
            AppResult.Error(e.message)
        }
    }
}
