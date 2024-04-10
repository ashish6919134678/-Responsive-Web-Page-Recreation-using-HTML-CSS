package com.example.hw4;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ProductDetailsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private final String TAG = "ProductDetailsActivity";
    private String itemID;
    ProductDetailsPagerAdapter pagerAdapter;
    private ProductItem product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize ViewPager and TabLayout
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Set up adapter for ViewPager
        pagerAdapter = new ProductDetailsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.information_variant);
        tabLayout.getTabAt(1).setIcon(R.drawable.truck_delivery);
        tabLayout.getTabAt(2).setIcon(R.drawable.google);
        tabLayout.getTabAt(3).setIcon(R.drawable.equal);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.product_details_action_bar);
        ImageView facebookIcon = actionBar.getCustomView().findViewById(R.id.facebookIcon);
        TextView productTabTitle = actionBar.getCustomView().findViewById(R.id.productTabTitle);

        facebookIcon.setImageResource(R.drawable.facebook);
        productTabTitle.setText(getIntent().getStringExtra("title"));
        actionBar.setDisplayHomeAsUpEnabled(true);
        productTabTitle.setMaxLines(1);


        facebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String link = product.getViewItemURLForNaturalSearch();
                String encodedLink = null;
                try {
                    encodedLink = URLEncoder.encode(link, "UTF-8");
                    String facebookShareURL = "https://www.facebook.com/sharer/sharer.php?u=" + encodedLink;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(facebookShareURL));
                    startActivity(i);
                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(ProductDetailsActivity.this, "Cannot open facebook link", Toast.LENGTH_SHORT).show();
                }
            }
        });


        itemID = getIntent().getStringExtra("itemID");
        product = (ProductItem) getIntent().getSerializableExtra("product");

        fetchProductDetails();
    }

    private void fetchProductDetails() {
        String baseUrl = Contants.BASE_URL +  "/product/details/";
        String apiUrl = baseUrl + "?itemID=" + itemID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        product.loadProductDetails(response);
                        fetchSimilarProducts();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        VolleySingleton.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    public void fetchImages() {
        String productTitle = product.getTitle();
        String engineId = "3129714a1a82943e8";
        String apiKey = "AIzaSyAd9KYYwU1AoTYiU-U5ECmybftVKV8_tQU";
        String url = "https://www.googleapis.com/customsearch/v1?q=" +
                productTitle +
                "&cx=" +
                engineId +
                "&imgSize=huge&num=8&searchType=image&key=" +
                apiKey;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        product.loadPhotosUrl(response);
                        pagerAdapter.productDetailsLoaded(product);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Photos", error.toString());
                    }
                });

        VolleySingleton.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    public void fetchSimilarProducts() {
        String apiUrl = Contants.BASE_URL +  "/product/similar?itemID=" + itemID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        product.loadSimilarProducts(response);
                        fetchImages();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        VolleySingleton.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
