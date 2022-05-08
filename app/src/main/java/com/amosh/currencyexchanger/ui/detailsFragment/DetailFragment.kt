package com.amosh.currencyexchanger.ui.detailsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.amosh.currencyexchanger.bases.BaseFragment
import com.amosh.currencyexchanger.data.AppResult
import com.amosh.currencyexchanger.databinding.FragmentDetailsBinding
import com.amosh.currencyexchanger.ui.detailsFragment.adapters.ConversionsAdapter
import com.amosh.currencyexchanger.ui.detailsFragment.adapters.HistoryAdapter
import com.amosh.currencyexchanger.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailsBinding>() {

    override val bindLayout: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailsBinding
        get() = FragmentDetailsBinding::inflate

    private val args: DetailFragmentArgs by navArgs()
    private val detailsViewModel: DetailsViewModel by viewModels()

    private val historyAdapter by lazy {
        HistoryAdapter()
    }

    private val conversionsAdapter by lazy {
        ConversionsAdapter()
    }

    override fun prepareView(savedInstanceState: Bundle?) {
        setData()
        initObservers()
        detailsViewModel.setEvent(DetailsViewModel.DetailsEvent.GetHistory(args.fromCurrency?.symbol ?: return, args.toCurrency?.symbol ?: return))
        detailsViewModel.setEvent(DetailsViewModel.DetailsEvent.GetMultiConversions(args.fromCurrency?.symbol ?: return))
    }

    private fun initObservers() {
        with(detailsViewModel) {
            historyResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is AppResult.Error -> {
                        binding.pbHistory.visibility = View.GONE
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                    is AppResult.Loading -> {
                        binding.pbHistory.visibility = View.VISIBLE
                    }
                    is AppResult.Success -> {
                        binding.pbHistory.visibility = View.GONE
                        historyAdapter.submitList(result.data?.subList(0, 3))
                    }
                }
            }

            multiConversionResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is AppResult.Error -> {
                        binding.pbHistory.visibility = View.GONE
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                    is AppResult.Loading -> {
                        binding.pbHistory.visibility = View.VISIBLE
                    }
                    is AppResult.Success -> {
                        binding.pbHistory.visibility = View.GONE
                        conversionsAdapter.submitList(result.data)
                    }
                }
            }
        }
    }

    private fun setData() {
        with(binding) {
            tvFrom.text = args.fromCurrency?.name
            rvHistory.adapter = historyAdapter
            rvOtherConversions.adapter = conversionsAdapter
        }
    }
}