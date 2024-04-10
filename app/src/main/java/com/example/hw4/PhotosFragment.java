package com.example.hw4;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.hw4.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotosFragment extends Fragment implements fragmentDataLoaded {

    private ProgressBar loadingProgressBar;
    private boolean isDataLoaded = false;
    private ProductItem product;
    private LinearLayout productPhotos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photos, container, false);
        loadingProgressBar = rootView.findViewById(R.id.photosLoadingProgressBar);
        productPhotos = rootView.findViewById(R.id.productPhotos);
        if (!isDataLoaded) {
            showLoadingBar();
        } else {
            hideLoadingBar();
        }
        return rootView;
    }

    @Override
    public void dataLoaded(ProductItem product) {
        this.product = product;
        isDataLoaded = true;
        hideLoadingBar();
    }

    private void showLoadingBar() {
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            productPhotos.setVisibility(View.GONE);
        }
    }

    private void hideLoadingBar() {

        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.GONE);
            productPhotos.setVisibility(View.VISIBLE);
            renderPhotos();
        }
    }

    private void renderPhotos() {
        if (!isDataLoaded || product == null) {
            return;
        }
        productPhotos.removeAllViews();
        ArrayList<String> photosUrl = product.getPhotosUrl();
        for (String photoUrl : photosUrl) {
            ImageView imageView = new ImageView(requireContext());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            Picasso.get()
                    .load(photoUrl)
                    .into(imageView);
            productPhotos.addView(imageView);
        }
    }
}

