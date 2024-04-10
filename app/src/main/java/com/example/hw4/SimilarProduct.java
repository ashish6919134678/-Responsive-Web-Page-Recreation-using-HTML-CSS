package com.example.hw4;

import org.json.JSONObject;

public class SimilarProduct {
    private String title = "N/A";
    private String daysLeft = "N/A";
    private String price = "N/A";
    private String shippingCost = "N/A";
    private String imageURL = "N/A";
    private String viewItemUrl = "N/A";

    SimilarProduct(JSONObject item) {
        title = item.optString("title", "N/A");
        daysLeft = item.optString("timeLeft", "N/A");
        viewItemUrl = item.optString("viewItemURL", "#");
        imageURL = item.optString("imageURL", "#");

        JSONObject buyItNowPrice = item.optJSONObject("buyItNowPrice");
        if (buyItNowPrice != null) {
            price = buyItNowPrice.optString("__value__", "N/A");
        }

        JSONObject shippingCostJson = item.optJSONObject("shippingCost");
        if (shippingCostJson != null) {
            shippingCost = shippingCostJson.optString("__value__", "N/A");
        }

        if (!daysLeft.equals("N/A")) {
            daysLeft = extractDays(daysLeft);
        }
    }

    private static String extractDays(String durationString) {
        return durationString.substring(1, durationString.indexOf("D"));
    }

    public String getTitle() {
        return title;
    }

    public String getDaysLeft() {
        return daysLeft;
    }

    public String getPrice() {
        return price;
    }

    public String getShippingCost() {
        return shippingCost;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getViewItemUrl() {
        return viewItemUrl;
    }
}

