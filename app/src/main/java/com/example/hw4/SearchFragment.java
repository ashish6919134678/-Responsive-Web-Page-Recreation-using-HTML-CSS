package com.example.hw4;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.textfield.TextInputLayout;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SearchFragment extends Fragment {

    private EditText keywordInput;
    private Spinner categoryDropdown;
    private CheckBox nearbySearchCheckbox;
    private LinearLayout nearbySearchLayout;
    private AutoCompleteTextView zipcodeInput;
    private static final String TAG = "Search Fragment";
    private RequestQueue requestQueue;
    ArrayAdapter<String> adapterx;
    private String currentLocation = null;
    private Button searchButton;
    private Button clearButton;
    private CheckBox conditionNewCheckbox;
    private CheckBox conditionUsedCheckbox;
    private CheckBox conditionUnspecifiedCheckbox;
    private CheckBox shippingLocalCheckbox;
    private CheckBox shippingFreeCheckbox;
    private CheckBox nearbySearch;
    private EditText distanceInput;
    private RadioButton currentLocationRadioButton;
    private RadioButton zipcodeRadioButton;



    TextInputLayout keywordWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        requestQueue = VolleySingleton.getInstance(requireContext()).getRequestQueue();

        fetchCurrentLocation();

        keywordInput = view.findViewById(R.id.keywordInput);
        categoryDropdown = view.findViewById(R.id.categoryDropdown);
        conditionNewCheckbox = view.findViewById(R.id.checkboxNew);
        conditionUsedCheckbox = view.findViewById(R.id.checkboxUsed);
        conditionUnspecifiedCheckbox = view.findViewById(R.id.checkboxUnspecified);
        shippingLocalCheckbox = view.findViewById(R.id.shippingLocal);
        shippingFreeCheckbox = view.findViewById(R.id.shippingFree);
        nearbySearchCheckbox = view.findViewById(R.id.nearbySearchCheckbox);
        distanceInput = view.findViewById(R.id.distanceInput);
        currentLocationRadioButton = view.findViewById(R.id.currentLocationRadioButton);
        zipcodeRadioButton = view.findViewById(R.id.zipcodeRadioButton);
        zipcodeInput = view.findViewById(R.id.zipcodeInput);
        searchButton = view.findViewById(R.id.search_button);
        clearButton = view.findViewById(R.id.clear_button);

        keywordWrapper = view.findViewById(R.id.text_input_layout);
        nearbySearchLayout = view.findViewById(R.id.distanceLayout);
        distanceInput.setText("10");

//        keywordInput.setText("iphone");
//        currentLocation = "90002";

        currentLocationRadioButton.setChecked(true);

        String[] categories = {"All", "Art", "Baby", "Books", "Clothing, Shoes & Accessories", "Computers/Tablets & Networking", "Health & Beauty", "Music", "Video Games & Consoles"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        categoryDropdown.setAdapter(adapter);

        adapterx = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line);
        zipcodeInput.setAdapter(adapterx);
        zipcodeInput.setThreshold(1);
        zipcodeInput.requestFocus();

        categoryDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        nearbySearchCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                nearbySearchLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        currentLocationRadioButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentLocationRadioButton.setChecked(true);
                zipcodeRadioButton.setChecked(false);
            }
        });

        zipcodeRadioButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "zipcode");
                zipcodeRadioButton.setChecked(true);
                currentLocationRadioButton.setChecked(false);
            }
        });

        zipcodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = charSequence.toString().trim();
                zipcodeRadioButton.setChecked(true);
                currentLocationRadioButton.setChecked(false);
                if (!input.isEmpty()) {
                    fetchPostalCodes(input);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        keywordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                keywordWrapper.setError("");
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButtonClicked();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearButtonClicked();
            }
        });

//        searchButtonClicked();
        return view;
    }

    private void searchButtonClicked() {
        String keyword = String.valueOf(keywordInput.getText());
        if (keyword.isEmpty()) {
            keywordWrapper.setError("Please enter mandatory field");
        }
        else if (zipcodeRadioButton.isChecked() && zipcodeInput.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Enter the zipcode", Toast.LENGTH_SHORT).show();
        }
        else if (currentLocationRadioButton.isChecked() && currentLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Current location Error: " + currentLocation, Toast.LENGTH_SHORT).show();
        }
        else {
            JSONObject json = new JSONObject();
            try {
                // keyword
                json.put("keyword", keywordInput.getText().toString());
                // category
                json.put("category", categoryDropdown.getSelectedItem().toString());
                // condition
                JSONObject condition = new JSONObject();
                condition.put("unspecified", conditionUnspecifiedCheckbox.isChecked());
                condition.put("used", conditionUsedCheckbox.isChecked());
                condition.put("new", conditionNewCheckbox.isChecked());
                json.put("condition", condition);
                // shipping
                JSONObject shipping = new JSONObject();
                shipping.put("free", shippingFreeCheckbox.isChecked());
                shipping.put("local", shippingLocalCheckbox.isChecked());
                json.put("shipping", shipping);
                // distance
                json.put("distance", distanceInput.getText().toString());
                // zipcode
                String zipcode = currentLocationRadioButton.isChecked() ? currentLocation : zipcodeInput.getText().toString();
                json.put("zipcode", zipcode);
                startSearchResultsActivity(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void startSearchResultsActivity(JSONObject jsonData) {
        Intent intent = new Intent(requireContext(), SearchResultsActivity.class);
        String jsonString = jsonData.toString();
        intent.putExtra("jsonData", jsonString);
        startActivity(intent);
    }

    private void clearButtonClicked() {
        keywordInput.setText("");
        categoryDropdown.setSelection(0);
        conditionNewCheckbox.setChecked(false);
        conditionUsedCheckbox.setChecked(false);
        conditionUnspecifiedCheckbox.setChecked(false);
        shippingLocalCheckbox.setChecked(false);
        shippingFreeCheckbox.setChecked(false);
        nearbySearchCheckbox.setChecked(false);
        distanceInput.setText("");
        zipcodeInput.setText("");
        zipcodeRadioButton.setChecked(false);
        currentLocationRadioButton.setChecked(true);
    }

    private void fetchPostalCodes(String postalCode) {
        String apiUrl = Contants.BASE_URL + "/geonames?postalcode=" + postalCode;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray postalCodes) {
                        try {
                            adapterx.clear();
                            for (int i = 0; i < postalCodes.length(); i++) {
                                String postalCodeValue = postalCodes.getString(i);
                                adapterx.add(postalCodeValue);
                                Log.d(TAG, postalCodeValue);
                            }
                            adapterx.notifyDataSetChanged();
                            zipcodeInput.showDropDown();
                        } catch (JSONException e) {
                            Log.d(TAG, e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchCurrentLocation() {
        String token = "048edd66ba88f1";
        String apiUrl = "https://ipinfo.io/json?token=" + token;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            currentLocation = response.getString("postal");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}