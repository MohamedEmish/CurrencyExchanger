package com.amosh.currencyexchanger.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amosh.currencyexchanger.bases.BaseViewModel
import com.amosh.currencyexchanger.data.AppResult
import com.amosh.currencyexchanger.data.Currency
import com.amosh.currencyexchanger.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val repository: MainRepository,
) : BaseViewModel() {

    fun setEvent(event: MainEvent) {
        when (event) {
            is MainEvent.GetCurrenciesList -> handleGetCurrenciesList()
            is MainEvent.Convert -> handleConvert(event.from, event.to, event.amount)
        }
    }

    private val _conversionResult: MutableLiveData<AppResult<Double>> =
        MutableLiveData()
    val conversionResult: LiveData<AppResult<Double>>
        get() = _conversionResult

    private fun handleConvert(from: String, to: String, amount: Double) = viewModelScope.launch {
        _conversionResult.value = AppResult.Loading()
        _conversionResult.value = repository.convert(from, to, amount)
    }

    private val _currenciesList: MutableLiveData<AppResult<List<Currency>>> =
        MutableLiveData()
    val currenciesList: LiveData<AppResult<List<Currency>>>
        get() = _currenciesList

    private fun handleGetCurrenciesList() = viewModelScope.launch {
        if (currenciesList.value?.data.isNullOrEmpty()) {
            _currenciesList.value = repository.getCurrenciesList()
        }
    }

    init {
        setEvent(MainEvent.GetCurrenciesList)
    }

    sealed class MainEvent {
        object GetCurrenciesList : MainEvent()
        data class Convert(val from: String, val to: String, val amount: Double) : MainEvent()
    }
}
