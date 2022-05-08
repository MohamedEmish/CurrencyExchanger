package com.amosh.currencyexchanger.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amosh.currencyexchanger.bases.BaseViewModel
import com.amosh.currencyexchanger.data.AppResult
import com.amosh.currencyexchanger.data.ConversionHistory
import com.amosh.currencyexchanger.data.Rate
import com.amosh.currencyexchanger.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel
@Inject
constructor(
    private val repository: MainRepository,
) : BaseViewModel() {

    companion object {
        val targets: MutableList<String> = arrayListOf(
            "EGP", "USD", "GBP", "EUR", "CHF", "JPY", "CAD", "AUD", "CNY", "KWD", "SAR", "AED", "QAR"
        )
    }

    fun setEvent(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.GetHistory -> handleGetHistory(event.from, event.target)
            is DetailsEvent.GetMultiConversions -> handleGetMultiConversions(event.base)
        }
    }

    private val _historyResult: MutableLiveData<AppResult<List<ConversionHistory>>> =
        MutableLiveData()
    val historyResult: LiveData<AppResult<List<ConversionHistory>>>
        get() = _historyResult

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleGetHistory(base: String, target: String) = viewModelScope.launch {
        _historyResult.value = AppResult.Loading()
        val yesterday = async { repository.getHistoryOf(base, getDateOf(-1), target) }
        val twoDaysAgo = async { repository.getHistoryOf(base, getDateOf(-2), target) }
        val threeDaysAgo = async { repository.getHistoryOf(base, getDateOf(-3), target) }

        val task = listOf(
            yesterday,
            twoDaysAgo,
            threeDaysAgo
        )

        task.awaitAll()

        _historyResult.value = AppResult.Success(listOf(
            yesterday.getCompleted().data ?: ConversionHistory(),
            twoDaysAgo.getCompleted().data ?: ConversionHistory(),
            threeDaysAgo.getCompleted().data ?: ConversionHistory()
        ))
    }

    private val _multiConversionResult: MutableLiveData<AppResult<List<Rate>>> =
        MutableLiveData()
    val multiConversionResult: LiveData<AppResult<List<Rate>>>
        get() = _multiConversionResult

    private fun handleGetMultiConversions(base: String) = viewModelScope.launch {
        _multiConversionResult.value = AppResult.Loading()
        if (targets.contains(base)) {
            targets.remove(base)
        }
        _multiConversionResult.value = repository.getMultiConversion(base, targets)
    }


    private fun getDateOf(previousDaysCount: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, previousDaysCount)
        return dateFormat.format(cal.time)
    }

    sealed class DetailsEvent {
        data class GetHistory(val from: String, val target: String) : DetailsEvent()
        data class GetMultiConversions(val base: String) : DetailsEvent()
    }
}
