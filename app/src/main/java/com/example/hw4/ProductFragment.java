package com.example.hw4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.squareup.picasso.Picasso;
import java.util.Objects;

public class ProductFragment extends Fragment implements fragmentDataLoaded {
    private ProgressBar loadingProgressBar;
    private boolean isDataLoaded = false;
    private ProductItem product;
    private ImageView productImage;
    private LinearLayout productInformation;
    private TextView productTitle;
    private TextView productPriceWithShipping;
    private TextView highlightsPrice;
    private TextView highlightsBrand;
    private TextView productSpecification;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar);
        productImage = rootView.findViewById(R.id.productImage);
        productInformation = rootView.findViewById(R.id.productInformation);
        productTitle = rootView.findViewById(R.id.productTitle);
        productPriceWithShipping = rootView.findViewById(R.id.productPriceWithShipping);
        highlightsPrice = rootView.findViewById(R.id.highlightsPrice);
        highlightsBrand = rootView.findViewById(R.id.highlightsBrand);
        productSpecification = rootView.findViewById(R.id.productSpecification);
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
            productInformation.setVisibility(View.GONE);
        }
    }

    private void hideLoadingBar() {
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(View.GONE);
            productInformation.setVisibility(View.VISIBLE);
            renderDetails();
        }
    }

    private void renderDetails() {
        String image = product.getImageUrl();
        if (product.getImages().size() > 0) {
            image = product.getImages().get(0);
        }
        Picasso.get().load(image).into(productImage);
        productTitle.setText(product.getTitle());
        productTitle.setMaxLines(3);

        String price = product.getPrice();
        String deliveryPrice = product.getShippingPrice();
        if (Objects.equals(deliveryPrice, "0.0")) {
            deliveryPrice = "Free";
        }

        productPriceWithShipping.setText("$" + price + " with " + deliveryPrice + " shipping");
        highlightsPrice.setText("Price     $" + product.getPrice().toString());
        highlightsBrand.setText("Brand     " + product.getSpecifications().getOrDefault("Brand", "N/A"));

        String specifications = "";
        for (String value : product.getSpecifications().values()) {
            specifications += "• " + value + "\n";
        }
        productSpecification.setText(specifications);

        if (product.getSpecifications().isEmpty()) {
            productSpecification.setVisibility(View.GONE);
        }
        else {
            productSpecification.setVisibility(View.VISIBLE);
        }
    }
}
