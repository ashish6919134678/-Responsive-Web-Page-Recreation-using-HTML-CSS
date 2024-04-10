package com.example.hw4;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public ProductDetailsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new ProductFragment());
        fragments.add(new ShippingFragment());
        fragments.add(new PhotosFragment());
        fragments.add(new SimilarFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Set tab titles
        switch (position) {
            case 0:
                return "PRODUCT";
            case 1:
                return "SHIPPING";
            case 2:
                return "PHOTOS";
            case 3:
                return "SIMILAR";
            default:
                return "";
        }
    }

    public void productDetailsLoaded(ProductItem product) {
        for (Fragment fragment : fragments) {
            if (fragment instanceof fragmentDataLoaded) {
                ((fragmentDataLoaded) fragment).dataLoaded(product);
            }
        }
    }
}
