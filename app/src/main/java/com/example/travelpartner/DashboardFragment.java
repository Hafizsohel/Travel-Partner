package com.example.travelpartner;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.example.travelpartner.Adapter.ImageSliderAdapter;
import com.example.travelpartner.databinding.FragmentDashboardBinding;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardFragment extends Fragment {
    FragmentDashboardBinding binding;
    private ImageSliderAdapter slideAdapter;
    private DrawerLayout drawerLayout;
    private ImageView[] indicators;
    //private ViewPager viewPager;
    private int currentPage = 0;
    private Timer timer;
    final long DELAY_MS = 1000;
    final long PERIOD_MS = 3000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(getLayoutInflater());


       // viewPager = binding.viewPager;
        int[] slideImages = {R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4, R.drawable.banner5};

        slideAdapter = new ImageSliderAdapter(requireContext(), slideImages);
        binding.viewPager.setAdapter(slideAdapter);

        setupIndicator();
        startAutoSlide();
        drawerLayout=binding.drawerLayer;

        binding.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });
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
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                    setCurrentIndicator(position);
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {}
            });
        } else {
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
    private void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
}


