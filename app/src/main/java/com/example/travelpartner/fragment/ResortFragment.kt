/*
package com.example.travelpartner.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelpartner.adapter.LocationAdapter
import com.example.travelpartner.adapter.ResortAdapter
import com.example.travelpartner.databinding.FragmentResortsBinding
import com.example.travelpartner.model.ResortModel
import com.example.travelpartner.utils.GetLocationsHelper
import com.example.travelpartner.utils.GetResortsHelper
import com.example.travelpartner.viewmodel.LocationViewModel
import com.example.travelpartner.viewmodel.ResortViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


private const val TAG = "ResortsFragment"
class ResortFragment : Fragment() {
    private lateinit var binding: FragmentResortsBinding
    private lateinit var viewModel: ResortViewModel
    private lateinit var resortAdapter: ResortAdapter
    private val resortList = mutableListOf<ResortModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResortsBinding.inflate(layoutInflater)

        resortAdapter = ResortAdapter(requireContext(), resortList)
        binding.resortRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.resortRecyclerView.adapter = resortAdapter

        viewModel = ViewModelProvider(this)[ResortViewModel::class.java]

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            toggleProgressBar(isLoading)
        }
        viewModel.resort.observe(viewLifecycleOwner) { resort ->
            resortAdapter.updateData(resort)
        }

        resortAdapter.onItemClicked = { selectedLocation ->
            GetResortsHelper.navigateToResortDetailFragment(parentFragmentManager, selectedLocation)
            Log.d(TAG, "onCreateView: $resortList")
        }

        setupToolbar()


        setupSearch()
        return binding.root
    }

    private fun setupDistrictDropdown(districts: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            districts
        )
        binding.autoCompleteDistrict.setAdapter(adapter)
    }


    private fun setupSearch() {
        binding.searchResorts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString().trim()
                viewModel.searchResort(query)
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarResort)
        binding.toolbarResort.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun toggleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.resortProgressBar.visibility = View.VISIBLE
            binding.resortRecyclerView.visibility = View.GONE
        } else {
            binding.resortProgressBar.visibility = View.GONE
            binding.resortRecyclerView.visibility = View.VISIBLE
        }
    }
}*/
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
import com.example.travelpartner.adapter.ResortAdapter
import com.example.travelpartner.databinding.FragmentResortsBinding
import com.example.travelpartner.utils.GetResortsHelper
import com.example.travelpartner.viewmodel.ResortViewModel

private const val TAG = "ResortsFragment"

class ResortFragment : Fragment() {
    private lateinit var binding: FragmentResortsBinding
    private lateinit var viewModel: ResortViewModel
    private lateinit var resortAdapter: ResortAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResortsBinding.inflate(layoutInflater)

        resortAdapter = ResortAdapter(requireContext(), mutableListOf())
        binding.resortRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.resortRecyclerView.adapter = resortAdapter

        viewModel = ViewModelProvider(this)[ResortViewModel::class.java]

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            toggleProgressBar(isLoading)
        }
        viewModel.filteredResorts.observe(viewLifecycleOwner) { resorts ->
            resortAdapter.updateData(resorts)
        }
        viewModel.districts.observe(viewLifecycleOwner) { districts ->
            setupDistrictDropdown(districts)
        }
        resortAdapter.onItemClicked = { selectedLocation ->
            GetResortsHelper.navigateToResortDetailFragment(parentFragmentManager, selectedLocation)
        }
        setupSearch()
        setupToolbar()
        return binding.root
    }
    private fun setupSearch() {
        binding.searchResorts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString().trim()
                viewModel.searchResort(query)
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
                Log.d(TAG, "Selected District: $it")
                viewModel.filterResortsByDistrict(it)
                binding.searchResorts.text?.clear()
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarResort)
        binding.toolbarResort.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun toggleProgressBar(isLoading: Boolean) {
        binding.resortProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.resortRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
}
