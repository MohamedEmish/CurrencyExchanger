package com.amosh.currencyexchanger.ui.currenciesSheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amosh.currencyexchanger.databinding.ItemCurrencyBinding

class CurrencyAdapter(
    private val selectionItems: List<String>,
    var listener: SelectionsFilterAdapterListener,
) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val binding = ItemCurrencyBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = selectionItems.size


    inner class ViewHolder(private val binding: ItemCurrencyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(item: String?) {
            if (item != null) {
                binding.tvCurrency.text = item
                binding.tvCurrency.setOnClickListener {
                    listener.updateSelection(item)
                }
            }
        }

    }

    interface SelectionsFilterAdapterListener {
        fun updateSelection(selection: String)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: String = selectionItems[position]
        holder.bindTo(item)
    }
}