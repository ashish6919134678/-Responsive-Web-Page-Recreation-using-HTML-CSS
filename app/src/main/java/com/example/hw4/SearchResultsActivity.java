// SearchResultsActivity.java
package com.example.hw4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SearchResultsActivity extends AppCompatActivity implements wishlistAddListner, wishlistRemoveListner, productClickListener {

    private ProgressBar loadingProgressBar;
    private TextView loadingText;
    private RecyclerView recyclerView;
    private ProductlistAdapter adapter;

    private final String TAG = "SEARCH RESULTSXX";
    private VolleySingleton volleySingleton;

    List<ProductItem> products;
    List<WishlistItem> wishlists;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        loadingText = findViewById(R.id.loadingText);
        recyclerView = findViewById(R.id.recyclerViewProducts);

        getSupportActionBar().setTitle("Search Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProductlistAdapter(products, wishlists, this, this, this);
        recyclerView.setAdapter(adapter);

        // Retrieve JSON data from the Intent
        String jsonString = getIntent().getStringExtra("jsonData");
        try {
            assert jsonString != null;
            JSONObject jsonData = new JSONObject(jsonString);
            performSearch(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void performSearch(JSONObject jsonData) {
        String baseUrl = Contants.BASE_URL + "/search";
        String apiUrl = buildUrl(baseUrl, jsonData);

        volleySingleton = VolleySingleton.getInstance(getApplicationContext());

        loadingProgressBar.setVisibility(android.view.View.VISIBLE);
        loadingText.setText("Searching Products...");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray itemsArray = response.getJSONArray("items");
                            JSONArray wishlistArray = response.getJSONArray("wishlist");
                            products = parseProducts(itemsArray);
                            wishlists = parseWishlist(wishlistArray);
                            adapter.setProducts(products);
                            adapter.setWishlists(wishlists);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON response", e);
                        }
                        loadingProgressBar.setVisibility(android.view.View.GONE);
                        loadingText.setVisibility(android.view.View.GONE);
                        recyclerView.setVisibility(android.view.View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingProgressBar.setVisibility(android.view.View.GONE);
                        loadingText.setText("Search failed!");
                    }
                });
        volleySingleton.addToRequestQueue(jsonObjectRequest);
    }

    private String buildQueryString(JSONObject jsonData) {
        StringBuilder queryString = new StringBuilder();

        try {
            buildQueryString(jsonData, queryString, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }

        return queryString.toString();
    }

    private void buildQueryString(JSONObject jsonData, StringBuilder queryString, String parentKey) throws JSONException {
        Iterator<String> keys = jsonData.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonData.get(key);
            String formattedKey = parentKey.isEmpty() ? key : parentKey + "[" + key + "]";
            if (value instanceof Boolean || value instanceof String) {
                queryString.append(formattedKey)
                        .append("=")
                        .append(value.toString())
                        .append("&");
            } else if (value instanceof JSONObject) {
                buildQueryString((JSONObject) value, queryString, formattedKey);
            } else {
                assert false;
            }
        }
    }

    private String buildUrl(String baseUrl, JSONObject jsonData) {
        StringBuilder urlWithParams = new StringBuilder(baseUrl);
        if (jsonData != null) {
            String queryString = buildQueryString(jsonData);
            if (!queryString.isEmpty()) {
                urlWithParams.append("?").append(queryString);
            }
        }
        return urlWithParams.toString();
    }

    private List<ProductItem> parseProducts(JSONArray itemsArray) {
        List<ProductItem> products = new ArrayList<>();
        try {
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObject = itemsArray.getJSONObject(i);
                ProductItem productItem = new ProductItem(itemObject);
                products.add(productItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return products;
    }

    private ProductItem findProduct(String itemID) {
        for (int i = 0; i < this.products.size(); i++) {
            ProductItem item = this.products.get(i);
            if (Objects.equals(item.getItemID(), itemID)) {
                return item;
            }
        }
        return null;
    }

    private List<WishlistItem> parseWishlist(JSONArray wishlistArray) {
        List<WishlistItem> wishlists = new ArrayList<>();
        try {
            for (int i = 0; i < wishlistArray.length(); i++) {
                JSONObject itemObject = wishlistArray.getJSONObject(i);
                String title = itemObject.optString("title", "");
                String imageUrl = itemObject.optString("imageUrl", "");
                String price = itemObject.optString("price", "");
                String shippingPrice = itemObject.optString("shippingPrice", "");
                String itemID = itemObject.optString("itemID", "");
                title += "\n\n\n";
                WishlistItem wishlistItem = new WishlistItem(title, imageUrl, price, shippingPrice, itemID);
                wishlists.add(wishlistItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wishlists;
    }


    @Override
    public void wishlistAddItem(String itemID) {
        ProductItem product =  findProduct(itemID);
        if (product == null) {
            Toast.makeText(this, "Error adding to wishlist: Product not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String wishlistUrl = Contants.BASE_URL +  "/wishlist";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("title", product.getTitle());
            requestBody.put("imageUrl", product.getImageUrl());
            requestBody.put("price", product.getPrice());
            requestBody.put("shippingPrice", product.getShippingPrice());
            requestBody.put("itemID", product.getItemID());
        } catch (Exception e) {
            Toast.makeText(this, "Error adding to wishlist: requestBody", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, wishlistUrl, requestBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(SearchResultsActivity.this, "Added to wishlist", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchResultsActivity.this, "Error adding to wishlist", Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void wishlistRemoveItem(String itemID) {

    }

    @Override
    public void productClick(String itemID) {
        ProductItem item = findProduct(itemID);
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("itemID", itemID);
        intent.putExtra("title", item.getTitle());
        intent.putExtra("product", item);
        startActivity(intent);
    }
}

