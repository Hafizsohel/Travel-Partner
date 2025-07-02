package com.example.travelpartner.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelpartner.R
import com.example.travelpartner.adapter.LocationAdapter
import com.example.travelpartner.databinding.FragmentLocationBinding
import com.example.travelpartner.utils.GetLocationsHelper
import com.example.travelpartner.viewmodel.LocationViewModel

class LocationFragment : Fragment() {
    private lateinit var binding: FragmentLocationBinding
    private lateinit var viewModel: LocationViewModel
    private lateinit var adapter: LocationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationBinding.inflate(layoutInflater)

        adapter = LocationAdapter(requireContext(), mutableListOf())
        binding.placeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.placeRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        adapter.onItemClicked = { selectedLocation ->
            GetLocationsHelper.navigateToLocationDetailFragment(parentFragmentManager, selectedLocation)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            toggleProgressBar(isLoading)
        }

        //

        viewModel.filteredLocation.observe(viewLifecycleOwner) { resorts ->
            adapter.updateData(resorts)
        }

        viewModel.districts.observe(viewLifecycleOwner) { districts ->
            setupDistrictDropdown(districts)
        }
        setupSearch()
        setupToolbar()
        return binding.root
    }

    private fun setupSearch() {
        binding.searchLocations.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString().trim()
                viewModel.searchLocation(query)
                binding.autoCompleteDistrict.text.clear()
            }
            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarPlace)
        binding.toolbarPlace.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
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
                viewModel.filterResortsByDistrict(it)
                binding.searchLocations.text?.clear()
            }
        }
    }

    private fun toggleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.locationProgressBar.visibility = View.VISIBLE
            binding.placeRecyclerView.visibility = View.GONE
        } else {
            binding.locationProgressBar.visibility = View.GONE
            binding.placeRecyclerView.visibility = View.VISIBLE
        }
    }

}

