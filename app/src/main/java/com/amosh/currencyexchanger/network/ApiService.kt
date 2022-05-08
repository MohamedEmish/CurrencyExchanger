package com.amosh.currencyexchanger.network

import com.amosh.currencyexchanger.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/currencies")
    suspend fun getCurrencies(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
    ): CurrencyNetworkResult

    @GET("/convert")
    suspend fun convert(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("from") fromSymbol: String,
        @Query("to") toSymbol: String,
        @Query("amount") amount: Double = 1.0,
    ): ConvertNetworkResult

    @GET("/fetch-multi")
    suspend fun getMultiConversion(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("from") base: String,
        @Query("to") target: String,
    ): MultiConversionResult

    @GET("/historical")
    suspend fun getHistoryOf(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("from") base: String,
        @Query("date") date: String,
    ): MultiConversionResult


}