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
import com.example.travelpartner.adapter.RiverAdapter
import com.example.travelpartner.databinding.FragmentRiverBinding
import com.example.travelpartner.model.RiverModel
import com.example.travelpartner.utils.GetRiversHelper
import com.example.travelpartner.viewmodel.RiverViewModel


class RiverFragment : Fragment() {
    private lateinit var binding: FragmentRiverBinding
    private lateinit var viewModel: RiverViewModel
    private lateinit var riverAdapter: RiverAdapter
    private val riverList = mutableListOf<RiverModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRiverBinding.inflate(layoutInflater)
        setupToolbar()

        riverAdapter = RiverAdapter(requireContext(), riverList)
        binding.riverRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.riverRecyclerView.adapter = riverAdapter

        viewModel = ViewModelProvider(this)[RiverViewModel::class.java]
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            toggleProgressBar(isLoading)
        }
        viewModel.filteredRiver.observe(viewLifecycleOwner) { river ->
            riverAdapter.updateData(river)
        }

        viewModel.districts.observe(viewLifecycleOwner) { districts ->
            setupDistrictDropdown(districts)
        }
        riverAdapter.onItemClicked = { selectedLocation ->
            GetRiversHelper.navigateToRiverDetailFragment(
                parentFragmentManager,
                selectedLocation
            )
        }
        setupRiver()
        return binding.root
    }

    private fun setupRiver() {
        binding.searchRiver.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString().trim()
                viewModel.searchRiver(query)
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
                viewModel.filterRiversByDistrict(it)
                binding.searchRiver.text?.clear()
            }
        }
    }
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarRiver)
        binding.toolbarRiver.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun toggleProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.riverProgressBar.visibility = View.VISIBLE
            binding.riverRecyclerView.visibility = View.GONE
        } else {
            binding.riverProgressBar.visibility = View.GONE
            binding.riverRecyclerView.visibility = View.VISIBLE
        }
    }
}