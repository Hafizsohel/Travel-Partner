package com.example.travelpartner.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelpartner.R
import com.example.travelpartner.adapter.CafeAdapter
import com.example.travelpartner.databinding.FragmentCafeBinding
import com.example.travelpartner.model.CafeModel
import com.example.travelpartner.utils.GetCafesHelper
import com.example.travelpartner.viewmodel.CafeViewModel

class CafeFragment : Fragment() {
    private lateinit var binding: FragmentCafeBinding
    private lateinit var viewModel: CafeViewModel
    private lateinit var cafeAdapter: CafeAdapter
    private val cafeList = mutableListOf<CafeModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCafeBinding.inflate(layoutInflater)
        setupToolbar()

        cafeAdapter = CafeAdapter(requireContext(), cafeList)
        binding.cafeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cafeRecyclerView.adapter = cafeAdapter

        viewModel = ViewModelProvider(this)[CafeViewModel::class.java]
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            toggleProgressBar(isLoading)
        }
        viewModel.filteredCafe.observe(viewLifecycleOwner) { cafe ->
            cafeAdapter.updateData(cafe)
        }

        viewModel.districts.observe(viewLifecycleOwner) { districts ->
            setupDistrictDropdown(districts)
        }
        cafeAdapter.onItemClicked = { selectedLocation ->
            GetCafesHelper.navigateToCafeDetailFragment(
                parentFragmentManager,
                selectedLocation
            )
        }
        setupCafe()
        return binding.root
    }

    private fun setupCafe() {
        binding.searchCafe.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString().trim()
                viewModel.searchCafe(query)
                binding.autoCompleteDistrict.text.clear()
            }
            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun setupDistrictDropdown(districts: List<String>) {
        val cleanedDistricts = districts.map { it.trim() }

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown,
            R.id.dropdown_item_text,
            cleanedDistricts
        )
        binding.autoCompleteDistrict.setAdapter(adapter)

        binding.autoCompleteDistrict.setOnItemClickListener { _, _, position, _ ->
            val selectedDistrict = adapter.getItem(position)
            selectedDistrict?.let {
                viewModel.filterCafesByDistrict(it)
                binding.searchCafe.text?.clear()
            }
        }
    }
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarCafe)
        binding.toolbarCafe.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun toggleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.cafeProgressBar.visibility = View.VISIBLE
            binding.cafeRecyclerView.visibility = View.GONE
        } else {
            binding.cafeProgressBar.visibility = View.GONE
            binding.cafeRecyclerView.visibility = View.VISIBLE
        }
    }
}