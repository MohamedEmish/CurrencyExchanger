package com.amosh.currencyexchanger.ui.detailsFragment.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amosh.currencyexchanger.data.ConversionHistory
import com.amosh.currencyexchanger.databinding.ItemHistoryBinding
import com.amosh.currencyexchanger.utils.shorten

class HistoryAdapter : ListAdapter<ConversionHistory, HistoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bindTo(item)
    }

    inner class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindTo(item: ConversionHistory) {
            with(binding) {
                tvDate.text = item.date
                tvTargetCurrency.text = item.target
                tvValue.text = item.rate?.shorten(4)?.toString()
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ConversionHistory> = object : DiffUtil.ItemCallback<ConversionHistory>() {
            override fun areItemsTheSame(oldItem: ConversionHistory, newItem: ConversionHistory): Boolean =
                oldItem.target == newItem.target &&
                    oldItem.date == newItem.date

            override fun areContentsTheSame(oldItem: ConversionHistory, newItem: ConversionHistory): Boolean =
                oldItem.target == newItem.target &&
                    oldItem.date == newItem.date
        }
    }
}