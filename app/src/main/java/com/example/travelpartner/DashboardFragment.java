package com.example.travelpartner;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.example.travelpartner.adapter.ImageSliderAdapter;
import com.example.travelpartner.adapter.PlaceAdapter;
import com.example.travelpartner.databinding.FragmentDashboardBinding;
import com.example.travelpartner.model.PlaceModel;
import com.example.travelpartner.viewModel.PlaceViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";
    FragmentDashboardBinding binding;
    private ImageSliderAdapter slideAdapter;
    private DrawerLayout drawerLayout;
    private ImageView[] indicators;
    private int currentPage = 0;
    private Timer timer;
    final long DELAY_MS = 1000;
    final long PERIOD_MS = 3000;

    List<PlaceModel> placeModelList= new ArrayList<>();;
    private PlaceAdapter placeAdapter;
    private PlaceViewModel placeViewModel;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(getLayoutInflater());


        int[] slideImages = {R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4, R.drawable.banner5};

        slideAdapter = new ImageSliderAdapter(requireContext(), slideImages);
        binding.viewPager.setAdapter(slideAdapter);

        setupIndicator();
        startAutoSlide();
        db = FirebaseFirestore.getInstance();

        binding.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(binding.drawerLayer);
            }
        });


        List<PlaceModel> placeModelList = new ArrayList<>();

        PlaceAdapter placeAdapter = new PlaceAdapter(getContext(), placeModelList);

        binding.placeRec.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.placeRec.setAdapter(placeAdapter);


        PlaceViewModel placeViewModel = new ViewModelProvider(this).get(PlaceViewModel.class);
        placeViewModel.getPlaceListLiveData().observe(getViewLifecycleOwner(), placeModels -> {
            placeModelList.clear();
            placeModelList.addAll(placeModels);
            placeAdapter.notifyDataSetChanged();
        });

        placeViewModel.loadDataFromFirebase();







        return binding.getRoot();
    }

    private void setupIndicator() {
        if (binding.viewPager != null && binding.viewPager.getAdapter() != null) {
            int slideCount = binding.viewPager.getAdapter().getCount();
            indicators = new ImageView[slideCount];

            for (int i = 0; i < slideCount; i++) {
                indicators[i] = new ImageView(requireContext());
                indicators[i].setImageResource(R.drawable.unselected_indicator);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(8, 0, 8, 0);
                binding.indicatorLayout.addView(indicators[i], params);
            }
            setCurrentIndicator(0);

            binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    setCurrentIndicator(position);
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }


    }

    private void setCurrentIndicator(int position) {
        if (indicators != null) {
            for (int i = 0; i < indicators.length; i++) {
                if (i == position) {
                    indicators[i].setImageResource(R.drawable.selected_indicator);
                } else {
                    indicators[i].setImageResource(R.drawable.unselected_indicator);
                }
            }
        }
    }

    private void startAutoSlide() {
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                if (currentPage == indicators.length) {
                    currentPage = 0;
                }
                binding.viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}


