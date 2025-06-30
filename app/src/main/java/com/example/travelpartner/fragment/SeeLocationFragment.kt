package com.example.travelpartner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.travelpartner.R
import com.example.travelpartner.adapter.DestinationAdapter
import com.example.travelpartner.utils.GetLocationsHelper
import com.example.travelpartner.application.GridSpacingItemDecoration
import com.example.travelpartner.databinding.FragmentSeeLocationBinding
import com.example.travelpartner.model.LocationModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val TAG = "SeeLocationFragment"
class SeeLocationFragment : Fragment() {
    private lateinit var binding: FragmentSeeLocationBinding
    private val destinationList = mutableListOf<LocationModel>()
    private lateinit var destinationAdapter: DestinationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSeeLocationBinding.inflate(layoutInflater)
        destinationAdapter = DestinationAdapter(requireContext(), destinationList)
        binding.locationsRecyclerView.adapter = destinationAdapter

        val padding = resources.getDimensionPixelSize(R.dimen.item_padding)
        binding.locationsRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.locationsRecyclerView.addItemDecoration(GridSpacingItemDecoration(padding))



        destinationAdapter.onItemClicked = { selectedLocation ->
            // Call the helper method to navigate
            GetLocationsHelper.navigateToLocationDetailFragment(parentFragmentManager, selectedLocation)
        }

        setupToolbar()
        // Fetch data from Firebase
        val databaseReference = FirebaseDatabase.getInstance().getReference("locations")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                destinationList.clear()
                for (dataSnapshot in snapshot.children) {
                    val place = dataSnapshot.getValue(LocationModel::class.java)
                    place?.let { destinationList.add(it) }
                }
                destinationAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
        return binding.root
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLocation)
        binding.toolbarLocation.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}