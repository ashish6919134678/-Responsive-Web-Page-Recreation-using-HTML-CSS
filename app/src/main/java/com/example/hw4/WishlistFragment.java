package com.example.hw4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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
import java.util.List;


public class WishlistFragment extends Fragment implements wishlistRemoveListner {

    private RecyclerView recyclerViewWishlist;
    private WishlistAdapter adapter;
    private List<WishlistItem> wishlistItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        recyclerViewWishlist = view.findViewById(R.id.recyclerViewWishlist);
        setupRecyclerView();
        fetchDataAndUpdateUI();
        return view;
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewWishlist.setLayoutManager(layoutManager);
        wishlistItems = new ArrayList<>();
        adapter = new WishlistAdapter(wishlistItems, this);
        recyclerViewWishlist.setAdapter(adapter);
    }

    public void onResume() {
        super.onResume();
        fetchDataAndUpdateUI();
    }

    private void fetchDataAndUpdateUI() {
        // Replace with your actual API URL
        String apiUrl = Contants.BASE_URL +  "/wishlist";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray wishlistArray = response.getJSONArray("wishlist");
                            wishlistItems.clear();
                            for (int i = 0; i < wishlistArray.length(); i++) {
                                JSONObject itemObject = wishlistArray.getJSONObject(i);
                                String title = itemObject.optString("title", "");
                                String imageUrl = itemObject.optString("imageUrl", "");
                                String price = itemObject.optString("price", "");
                                String shippingPrice = itemObject.optString("shippingPrice", "");
                                String itemID = itemObject.optString("itemID", "");
                                title += "\n\n\n";
                                WishlistItem wishlistItem = new WishlistItem(title, imageUrl, price, shippingPrice, itemID);
                                wishlistItems.add(wishlistItem);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the Volley request queue
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void wishlistRemoveItem(String itemId) {
        String apiUrl = Contants.BASE_URL +  "/wishlist/" + itemId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                apiUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "Item removed from wishlist", Toast.LENGTH_SHORT).show();
                        fetchDataAndUpdateUI();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Error removing item from wishlist", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        VolleySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

}
