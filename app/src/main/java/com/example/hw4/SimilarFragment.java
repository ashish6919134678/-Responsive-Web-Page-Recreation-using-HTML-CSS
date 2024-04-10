package com.example.hw4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimilarFragment extends Fragment implements fragmentDataLoaded {
    private ProgressBar loadingProgressBar;
    private boolean isDataLoaded = false;
    private ProductItem product;
    private List<SimilarProduct> filteredProducts = new ArrayList<>();
    private LinearLayout similarProductsInformation;
    private RecyclerView recyclerView;
    private SimilarProductAdapter adapter;
    private Spinner categorySpinner;
    private Spinner sortSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_similar, container, false);
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar);
        similarProductsInformation = rootView.findViewById(R.id.similarProductsInformation);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        categorySpinner = rootView.findViewById(R.id.categorySpinner);
        sortSpinner = rootView.findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        filteredProducts = new ArrayList<>();
        adapter = new SimilarProductAdapter(filteredProducts);
        recyclerView.setAdapter(adapter);
        if (!isDataLoaded) {
            showLoadingBar();
        } else {
            hideLoadingBar();
        }

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                if ("Default".equals(selectedCategory)) {
                    sortSpinner.setEnabled(false);
                } else {
                    sortSpinner.setEnabled(true);
                }
                filterAndDisplay();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSort = sortSpinner.getSelectedItem().toString();
                filterAndDisplay();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        return rootView;
    }

    @Override
    public void dataLoaded(ProductItem product) {
        this.product = product;
        isDataLoaded = true;
    }

    private void showLoadingBar() {
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            similarProductsInformation.setVisibility(View.GONE);
        }
    }

    private void hideLoadingBar() {
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.GONE);
            similarProductsInformation.setVisibility(View.VISIBLE);
            filterAndDisplay();
        }
    }

    private void filterAndDisplay() {
        ArrayList<SimilarProduct> similarProducts = product.getSimilarProducts();
        String category = categorySpinner.getSelectedItem().toString();
        String selectedSort = sortSpinner.getSelectedItem().toString();
        filteredProducts.clear();
        filteredProducts.addAll(similarProducts);

        if (category.equals("Product Name")) {
            Collections.sort(filteredProducts, new Comparator<SimilarProduct>() {
                @Override
                public int compare(SimilarProduct product1, SimilarProduct product2) {
                    return product1.getTitle().compareTo(product2.getTitle());
                }
            });
            if ("Descending".equals(selectedSort)) {
                Collections.reverse(filteredProducts);
            }
        } else if (category.equals("Days Left")) {
            Collections.sort(filteredProducts, new Comparator<SimilarProduct>() {
                @Override
                public int compare(SimilarProduct product1, SimilarProduct product2) {
                    String daysLeft1 = product1.getDaysLeft();
                    String daysLeft2 = product2.getDaysLeft();
                    int days1 = daysLeft1.equals("N/A") ? Integer.MAX_VALUE : Integer.parseInt(daysLeft1);
                    int days2 = daysLeft2.equals("N/A") ? Integer.MAX_VALUE : Integer.parseInt(daysLeft2);
                    return Integer.compare(days1, days2);
                }
            });
            if ("Descending".equals(selectedSort)) {
                Collections.reverse(filteredProducts);
            }
        } else if (category.equals("Price")) {
            Collections.sort(filteredProducts, new Comparator<SimilarProduct>() {
                @Override
                public int compare(SimilarProduct product1, SimilarProduct product2) {
                    String price1 = product1.getPrice();
                    String price2 = product2.getPrice();
                    double priceValue1 = price1.equals("N/A") ? Double.MAX_VALUE : Double.parseDouble(price1);
                    double priceValue2 = price2.equals("N/A") ? Double.MAX_VALUE : Double.parseDouble(price2);
                    return Double.compare(priceValue1, priceValue2);
                }
            });
            if ("Descending".equals(selectedSort)) {
                Collections.reverse(filteredProducts);
            }
        } else if (category.equals("Shipping Cost")) {
            Collections.sort(filteredProducts, new Comparator<SimilarProduct>() {
                @Override
                public int compare(SimilarProduct product1, SimilarProduct product2) {
                    String shippingCost1 = product1.getShippingCost();
                    String shippingCost2 = product2.getShippingCost();
                    double costValue1 = shippingCost1.equals("N/A") ? Double.MAX_VALUE : Double.parseDouble(shippingCost1);
                    double costValue2 = shippingCost2.equals("N/A") ? Double.MAX_VALUE : Double.parseDouble(shippingCost2);
                    return Double.compare(costValue1, costValue2);
                }
            });
            if ("Descending".equals(selectedSort)) {
                Collections.reverse(filteredProducts);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
