package com.amosh.currencyexchanger.ui.detailsFragment.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amosh.currencyexchanger.data.Rate
import com.amosh.currencyexchanger.databinding.ItemHistoryBinding
import com.amosh.currencyexchanger.utils.shorten

class ConversionsAdapter : ListAdapter<Rate, ConversionsAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bindTo(item)
    }

    inner class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindTo(item: Rate) {
            with(binding) {
                tvDate.visibility = View.GONE
                tvTargetCurrency.text = item.target
                tvValue.text = item.rate?.shorten(4)?.toString()
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Rate> = object : DiffUtil.ItemCallback<Rate>() {
            override fun areItemsTheSame(oldItem: Rate, newItem: Rate): Boolean =
                oldItem.target == newItem.target &&
                    oldItem.rate == newItem.rate

            override fun areContentsTheSame(oldItem: Rate, newItem: Rate): Boolean =
                oldItem.target == newItem.target &&
                    oldItem.rate == newItem.rate
        }
    }
}