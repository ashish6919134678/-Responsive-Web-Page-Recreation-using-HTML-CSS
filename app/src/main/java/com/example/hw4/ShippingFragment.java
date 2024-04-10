package com.example.hw4;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.util.Objects;

public class ShippingFragment extends Fragment implements fragmentDataLoaded {
    private ProgressBar loadingProgressBar;
    private boolean isDataLoaded = false;
    private ProductItem product;
    private LinearLayout productInformation;
    private TextView storeNameLabel;
    private TextView storeNameValue;
    private TextView feedbackScoreLabel;
    private TextView feedbackScoreValue;
    private TextView popularityLabel;
    private TextView popularityValue;
    private ImageView feedbackStarsValue;
    private TextView shippingCostLabel;
    private TextView shippingCostValue;
    private TextView globalShippingLabel;
    private TextView globalShippingValue;
    private TextView handlingTimeLabel;
    private TextView handlingTimeValue;
    private TextView policyLabel;
    private TextView policyValue;
    private TextView returnsWithinLabel;
    private TextView returnsWithinValue;
    private TextView refundModeLabel;
    private TextView refundModeValue;
    private TextView shippedByLabel;
    private TextView shippedByValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shipping, container, false);
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar);
        productInformation = rootView.findViewById(R.id.productInformation);
        storeNameLabel = rootView.findViewById(R.id.storeNameLabel);
        storeNameValue = rootView.findViewById(R.id.storeNameValue);
        feedbackScoreLabel = rootView.findViewById(R.id.feedbackScoreLabel);
        feedbackScoreValue = rootView.findViewById(R.id.feedbackScoreValue);
        popularityLabel = rootView.findViewById(R.id.popularityLabel);
        popularityValue = rootView.findViewById(R.id.popularityValue);
        feedbackStarsValue = rootView.findViewById(R.id.feedbackStarsValue);
        shippingCostLabel = rootView.findViewById(R.id.shippingCostLabel);
        shippingCostValue = rootView.findViewById(R.id.shippingCostValue);
        globalShippingLabel = rootView.findViewById(R.id.globalShippingLabel);
        globalShippingValue = rootView.findViewById(R.id.globalShippingValue);
        handlingTimeLabel = rootView.findViewById(R.id.handlingTimeLabel);
        handlingTimeValue = rootView.findViewById(R.id.handlingTimeValue);
        policyLabel = rootView.findViewById(R.id.policyLabel);
        policyValue = rootView.findViewById(R.id.policyValue);
        returnsWithinLabel = rootView.findViewById(R.id.returnsWithinLabel);
        returnsWithinValue = rootView.findViewById(R.id.returnsWithinValue);
        refundModeLabel = rootView.findViewById(R.id.refundModeLabel);
        refundModeValue = rootView.findViewById(R.id.refundModeValue);
        shippedByLabel = rootView.findViewById(R.id.shippedByLabel);
        shippedByValue = rootView.findViewById(R.id.shippedByValue);
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
        if (!isDataLoaded || product == null) {
            return;
        }
        storeNameValue.setText(product.getStoreName());
        feedbackScoreValue.setText(product.getFeedbackScore());
        popularityValue.setText(product.getPopularity());
        feedbackStarsValue.setImageResource(R.drawable.star_circle);
        int colorResourceId = getColorResourceId(product.getFeedbackStar());
        feedbackStarsValue.setColorFilter(ContextCompat.getColor(requireContext(), colorResourceId), PorterDuff.Mode.SRC_IN);
        shippingCostValue.setText(Objects.equals(product.getShippingPrice(), "0.0") ?"Free": "$" + product.getShippingPrice());
        globalShippingValue.setText(Objects.equals(product.getGlobalShipping(), "false") ?"NO": "YES");
        handlingTimeValue.setText(product.getHandlingTime());
        policyValue.setText(product.getPolicy());
        returnsWithinValue.setText(product.getReturnsWithin());
        refundModeValue.setText(product.getRefundMode());
        shippedByValue.setText(product.getShippedBy());
        storeNameValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = product.getStoreUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        hideIfNA(storeNameLabel, storeNameValue);
        hideIfNA(feedbackScoreLabel, feedbackScoreValue);
        hideIfNA(popularityLabel, popularityValue);
        hideIfNA(shippingCostLabel, shippingCostValue);
        hideIfNA(globalShippingLabel, globalShippingValue);
        hideIfNA(handlingTimeLabel, handlingTimeValue);
        hideIfNA(policyLabel, policyValue);
        hideIfNA(returnsWithinLabel, returnsWithinValue);
        hideIfNA(refundModeLabel, refundModeValue);
        hideIfNA(shippedByLabel, shippedByValue);
    }

    private void hideIfNA(TextView labelView, TextView textView) {
        if ("N/A".equals(textView.getText().toString())) {
            textView.setVisibility(View.GONE);
            labelView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            labelView.setVisibility(View.VISIBLE);
        }
    }

    private int getColorResourceId(String feedbackStar) {
        switch (feedbackStar) {
            case "None":
                return R.color.white;
            case "Yellow":
                return R.color.yellow;
            case "Blue":
                return R.color.blue;
            case "Turquoise":
                return R.color.turquoise;
            case "Purple":
                return R.color.purple;
            case "Red":
                return R.color.red;
            case "Green":
                return R.color.green;
            case "YellowShooting":
                return R.color.yellowShooting;
            case "TurquoiseShooting":
                return R.color.turquoiseShooting;
            case "PurpleShooting":
                return R.color.purpleShooting;
            case "RedShooting":
                return R.color.redShooting;
            case "GreenShooting":
                return R.color.greenShooting;
            case "SilverShooting":
                return R.color.silverShooting;
            default:
                return R.color.white;
        }
    }
}
