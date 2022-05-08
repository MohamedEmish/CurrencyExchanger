package com.amosh.currencyexchanger.di

import android.content.Context
import com.amosh.currencyexchanger.network.ApiService
import com.amosh.currencyexchanger.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideInsuranceRepository(
        @ApplicationContext context: Context,
        apiService: ApiService
    ): MainRepository {
        return MainRepository(
            context,
            apiService
        )
    }
}