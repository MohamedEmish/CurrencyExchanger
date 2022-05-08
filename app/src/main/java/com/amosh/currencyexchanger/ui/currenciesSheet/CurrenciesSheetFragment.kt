package com.amosh.currencyexchanger.ui.currenciesSheet

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amosh.currencyexchanger.R
import com.amosh.currencyexchanger.databinding.SheetFilterListBinding
import com.amosh.currencyexchanger.utils.ScreenUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CurrenciesSheetFragment : BottomSheetDialogFragment() {

    private var _binding: SheetFilterListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CurrencyAdapter
    private lateinit var listener: OnActionsListener

    private var stringsList: List<String> = mutableListOf()
    private var preSelectedList: MutableList<String> = mutableListOf()

    override fun onStart() {
        super.onStart()
        // To show the sheet full height
        val dialog = dialog
        if (dialog != null) {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        val view = view
        view?.post {
            val parent = view.parent as View
            val params: CoordinatorLayout.LayoutParams =
                parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior
            bottomSheetBehavior.peekHeight = ScreenUtils(requireContext()).height
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SheetFilterListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setNestedScrollingEnabled(binding.rvCurrencies, true)
        setupRecycler()

    }

    private fun setupRecycler() {
        preSelectedList.distinct()
        adapter = CurrencyAdapter(
            selectionItems = stringsList,
            listener = object : CurrencyAdapter.SelectionsFilterAdapterListener {

                override fun updateSelection(selection: String) {
                    if (this@CurrenciesSheetFragment::listener.isInitialized)
                        listener.onDoneListener(selection)
                }
            })
        val layoutManager = LinearLayoutManager(context)
        binding.rvCurrencies.layoutManager = layoutManager
        binding.rvCurrencies.adapter = adapter
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDismiss()
    }

    interface OnActionsListener {
        fun onDoneListener(selection: String)
        fun onDismiss()
    }

    fun setFullStringsList(fullStringsList: List<String>): CurrenciesSheetFragment {
        this.stringsList = fullStringsList
        return this
    }

    companion object {
        const val TAG = "GenericSelectionSheet"

        @JvmStatic
        fun newInstance(
            listener: OnActionsListener,
        ): CurrenciesSheetFragment {
            val fragment = CurrenciesSheetFragment()
            fragment.listener = listener
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}