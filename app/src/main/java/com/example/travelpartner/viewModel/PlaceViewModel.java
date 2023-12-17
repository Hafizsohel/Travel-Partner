package com.example.travelpartner.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelpartner.model.PlaceModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlaceViewModel extends ViewModel {
    private static final String TAG = "PlaceViewModel";
    private final MutableLiveData<List<PlaceModel>> placeListLiveData = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<PlaceModel>> getPlaceListLiveData() {
        return placeListLiveData;
    }

    public void loadDataFromFirebase() {
        db.collection("Places")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<PlaceModel> placeModelList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Log.d(TAG, "loadPlaces: "+document);

                        String name = document.getString("name");
                        String location = document.getString("location");
                        String img_url = document.getString("img_url");

                        PlaceModel placeModel = new PlaceModel(name, location, img_url);
                        placeModelList.add(placeModel);
                    }
                    placeListLiveData.setValue(placeModelList);
                })
                .addOnFailureListener(e -> {
                });
    }
}
