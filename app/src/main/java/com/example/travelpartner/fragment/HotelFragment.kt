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
import com.example.travelpartner.adapter.HotelAdapter
import com.example.travelpartner.databinding.FragmentHotelsBinding
import com.example.travelpartner.model.HotelModel
import com.example.travelpartner.utils.GetHotelsHelper
import com.example.travelpartner.viewmodel.HotelViewModel

class HotelFragment : Fragment() {
    private lateinit var binding: FragmentHotelsBinding
    private lateinit var viewModel: HotelViewModel
    private lateinit var hotelAdapter: HotelAdapter
    private val hotelList = mutableListOf<HotelModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHotelsBinding.inflate(inflater, container, false)
        setupToolbar()
        viewModel = ViewModelProvider(this)[HotelViewModel::class.java]

        hotelAdapter = HotelAdapter(requireContext(), hotelList)
        binding.hotelRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.hotelRecyclerView.adapter = hotelAdapter


        viewModel = ViewModelProvider(this)[HotelViewModel::class.java]

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            toggleProgressBar(isLoading)
        }

        hotelAdapter.onItemClicked = { selectedLocation ->
            GetHotelsHelper.navigateToHotelDetailFragment(parentFragmentManager, selectedLocation)
        }

        viewModel.filteredHotel.observe(viewLifecycleOwner) { hotels ->
            hotelAdapter.updateData(hotels)
        }

        viewModel.districts.observe(viewLifecycleOwner) { districts ->
            setupDistrictDropdown(districts)
        }
        setupSearch()
        return binding.root
    }

    private fun setupSearch() {
        binding.searchHotels.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val query = charSequence.toString().trim()
                viewModel.searchHotel(query)
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
                viewModel.filterHotelsByDistrict(it)
                binding.searchHotels.text?.clear()
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarHotels)
        binding.toolbarHotels.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun toggleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.hotelProgressBar.visibility = View.VISIBLE
            binding.hotelRecyclerView.visibility = View.GONE
        } else {
            binding.hotelProgressBar.visibility = View.GONE
            binding.hotelRecyclerView.visibility = View.VISIBLE
        }
    }
}