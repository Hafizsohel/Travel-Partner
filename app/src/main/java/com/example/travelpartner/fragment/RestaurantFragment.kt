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
import com.example.travelpartner.adapter.RestaurantAdapter
import com.example.travelpartner.databinding.FragmentRestaurantBinding
import com.example.travelpartner.model.RestaurantModel
import com.example.travelpartner.utils.GetRestaurantsHelper
import com.example.travelpartner.viewmodel.RestaurantViewModel

class RestaurantFragment : Fragment() {
    private lateinit var binding: FragmentRestaurantBinding
    private lateinit var viewModel: RestaurantViewModel
    private lateinit var restaurantAdapter: RestaurantAdapter
    private val restaurantList = mutableListOf<RestaurantModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantBinding.inflate(layoutInflater)
        setupToolbar()
        viewModel = ViewModelProvider(this)[RestaurantViewModel::class.java]
        restaurantAdapter = RestaurantAdapter(requireContext(), restaurantList)
        binding.restaurantRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.restaurantRecyclerView.adapter = restaurantAdapter

        viewModel = ViewModelProvider(this)[RestaurantViewModel::class.java]

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            toggleProgressBar(isLoading)
        }
        viewModel.filteredRestaurant.observe(viewLifecycleOwner) { restaurant ->
            restaurantAdapter.updateData(restaurant)
        }

        viewModel.districts.observe(viewLifecycleOwner) { districts ->
            setupDistrictDropdown(districts)
        }

        restaurantAdapter.onItemClicked = { selectedLocation ->
            GetRestaurantsHelper.navigateToRestaurantDetailFragment(
                parentFragmentManager,
                selectedLocation
            )
        }
        setupRestaurant()
        return binding.root
    }
    private fun setupRestaurant() {
        binding.searchRestaurant.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString().trim()
                viewModel.searchRestaurant(query)
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
                viewModel.filterRestaurantsByDistrict(it)
                binding.searchRestaurant.text?.clear()
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarRestaurant)
        binding.toolbarRestaurant.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun toggleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.restaurantProgressBar.visibility = View.VISIBLE
            binding.restaurantRecyclerView.visibility = View.GONE
        } else {
            binding.restaurantProgressBar.visibility = View.GONE
            binding.restaurantRecyclerView.visibility = View.VISIBLE
        }
    }
}