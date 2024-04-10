package com.example.hw4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.search_wishlist_tablayout);
        viewPager = findViewById(R.id.search_wishlist_viewpager);

        tabLayout.setupWithViewPager(viewPager);
        MainAdaptor mainAdaptor = new MainAdaptor(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mainAdaptor.addFragment(new SearchFragment(), "SEARCH");
        mainAdaptor.addFragment(new WishlistFragment(), "WISHLIST");
        viewPager.setAdapter(mainAdaptor);
    }
}