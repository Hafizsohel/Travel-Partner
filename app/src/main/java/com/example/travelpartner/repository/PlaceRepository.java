package com.example.travelpartner.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.travelpartner.model.PlaceModel;
import com.example.travelpartner.viewModel.PlaceViewModel;

import java.util.List;

public class PlaceRepository {
    private PlaceViewModel viewModel;

    public PlaceRepository(Application application) {
        viewModel = new ViewModelProvider.AndroidViewModelFactory(application).create(PlaceViewModel.class);
    }

    public LiveData<List<PlaceModel>> getPlaces() {
        return viewModel.getPlaceListLiveData();
    }
}

