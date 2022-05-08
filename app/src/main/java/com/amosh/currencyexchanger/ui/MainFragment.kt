package com.amosh.currencyexchanger.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amosh.currencyexchanger.bases.BaseFragment
import com.amosh.currencyexchanger.data.AppResult
import com.amosh.currencyexchanger.data.Currency
import com.amosh.currencyexchanger.databinding.FragmentMainBinding
import com.amosh.currencyexchanger.ui.currenciesSheet.CurrenciesSheetFragment
import com.amosh.currencyexchanger.utils.clickableOnly
import com.amosh.currencyexchanger.utils.shorten
import com.amosh.currencyexchanger.utils.textChanges
import com.amosh.currencyexchanger.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@SuppressLint("NotifyDataSetChanged")
@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>() {

    companion object {
        const val TYPE_FROM = "FROM"
        const val TYPE_TO = "TO"
        const val DEBOUNCE_DURATION = 500L
    }

    private val viewModel: MainViewModel by activityViewModels()
    private var fromCurrency: Currency? = null
    private var toCurrency: Currency? = null
    private var mLastClickTime: Long = 0

    private var sheet: CurrenciesSheetFragment? = null


    override val bindLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMainBinding
        get() = FragmentMainBinding::inflate

    override fun prepareView(savedInstanceState: Bundle?) {
        initObservers()
        with(binding) {
            etTo.apply {
                clickableOnly()
                setOnClickListener { showSheet(TYPE_TO) }
            }
            etFrom.apply {
                clickableOnly()
                setOnClickListener { showSheet(TYPE_FROM) }
            }
            btnDetails.setOnClickListener {
                if (fromCurrency == null || toCurrency == null) {
                    Toast.makeText(requireContext(), "Please select currencies", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val action = MainFragmentDirections.actionMainFragmentToDetailFragment().apply {
                    fromCurrency = this@MainFragment.fromCurrency
                    toCurrency = this@MainFragment.toCurrency
                }
                findNavController().navigate(action)
            }

            etFromAmount.textChanges()
                .debounce(DEBOUNCE_DURATION)
                .onEach {
                    it?.let { query ->
                        convertEvent(query)
                    }
                }
                .launchIn(lifecycleScope)

            btnSwap.setOnClickListener {
                swapCurrencies()
            }
        }
    }

    private fun swapCurrencies() {
        val tempFrom = toCurrency
        val tempTo = fromCurrency
        fromCurrency = tempFrom
        toCurrency = tempTo
        binding.etFrom.setText(fromCurrency?.symbol ?: "")
        binding.etTo.setText(toCurrency?.symbol ?: "")
        convertEvent(binding.etFromAmount.text ?: "")
    }

    private fun convertEvent(query: CharSequence) {
        if (fromCurrency != null && toCurrency != null) {
            viewModel.setEvent(
                MainViewModel.MainEvent.Convert(
                    from = fromCurrency?.symbol ?: return,
                    to = toCurrency?.symbol ?: return,
                    amount = query.toString().toDoubleOrNull() ?: 1.0
                )
            )
        }
    }

    /**
     * Initialize Observers
     */
    private fun initObservers() {
        with(viewModel) {
            conversionResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is AppResult.Error -> {
                        binding.pbProgress.visibility = View.GONE
                        Toast.makeText(requireContext(), result.message ?: "", Toast.LENGTH_SHORT).show()
                    }
                    is AppResult.Loading -> {
                        binding.pbProgress.visibility = View.VISIBLE
                    }
                    is AppResult.Success -> {
                        binding.pbProgress.visibility = View.GONE
                        binding.tvToAmount.text = result.data?.shorten(4)?.toString()
                    }
                }

            }
        }
    }


    private fun showSheet(type: String) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) return
        mLastClickTime = SystemClock.elapsedRealtime()

        viewModel.setEvent(MainViewModel.MainEvent.GetCurrenciesList)

        sheet =
            CurrenciesSheetFragment.newInstance(object : CurrenciesSheetFragment.OnActionsListener {
                override fun onDoneListener(selection: String) {
                    val currency = viewModel.currenciesList.value?.data?.find { "${it.symbol} ${it.name}" == selection }
                    when (type) {
                        TYPE_FROM -> {
                            binding.etFrom.setText(
                                currency?.symbol
                            )
                            fromCurrency = currency
                        }
                        TYPE_TO -> {
                            binding.etTo.setText(
                                currency?.symbol
                            )
                            toCurrency = currency
                        }
                    }
                    convertEvent(binding.etFromAmount.text ?: "")
                    sheet?.dismiss()
                }

                override fun onDismiss() = Unit
            })
                .setFullStringsList(viewModel.currenciesList.value?.data?.map { "${it.symbol} ${it.name}" } ?: listOf())
        sheet?.show(parentFragmentManager, "CurrenciesSheetFragment")
    }


}