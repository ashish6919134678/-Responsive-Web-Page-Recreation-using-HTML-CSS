package com.example.hw4;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductItem implements Serializable {
    private String title;
    private String imageUrl;
    private String price;
    private String shippingPrice;
    private String itemID;
    private String zipCode;
    private String condition;
    private Map<String, String> specifications = new HashMap<>();
    private ArrayList<String> images = new ArrayList<>();
    // Shipping information
    private String storeName = "N/A";
    private String storeUrl = "#";
    private String feedbackScore= "N/A";
    private String popularity= "N/A";
    private String feedbackStar = "N/A";
    private String globalShipping = "N/A";
    private String handlingTime = "N/A";
    private String policy = "N/A";
    private String returnsWithin = "N/A";
    private String refundMode = "N/A";
    private String shippedBy = "N/A";
    private String ViewItemURLForNaturalSearch = "#";
    private ArrayList<SimilarProduct> similarProducts = new ArrayList<>();
    private ArrayList<String> photosUrl = new ArrayList<>();

    public ProductItem(JSONObject productInfo) {
        try {
            if (productInfo.has("title")) {
                JSONArray titleArray = productInfo.getJSONArray("title");
                title = titleArray.length() > 0 ? titleArray.getString(0) : "N/A";
            } else {
                title = "N/A";
            }

            if (productInfo.has("galleryURL")) {
                JSONArray imageUrlArray = productInfo.getJSONArray("galleryURL");
                imageUrl = imageUrlArray.length() > 0 ? imageUrlArray.optString(0, "N/A") : "N/A";
            } else {
                imageUrl = "N/A";
            }

            if (productInfo.has("condition")) {
                JSONArray conditionArray = productInfo.getJSONArray("condition");
                if (conditionArray.length() > 0) {
                    JSONObject conditionObject = conditionArray.getJSONObject(0);
                    if (conditionObject.has("conditionDisplayName")) {
                        condition = conditionObject.getJSONArray("conditionDisplayName").optString(0, "N/A");
                    } else {
                        condition = "N/A";
                    }
                } else {
                    condition = "N/A";
                }
            } else {
                condition = "N/A";
            }

            if (productInfo.has("postalCode")) {
                JSONArray postalCodeArray = productInfo.getJSONArray("postalCode");
                zipCode = postalCodeArray.length() > 0 ? postalCodeArray.getString(0): "N/A";
            } else {
                zipCode = "N/A";
            }

            if (productInfo.has("itemId")) {
                JSONArray itemIdArray = productInfo.getJSONArray("itemId");
                itemID = itemIdArray.length() > 0 ? itemIdArray.getString(0) : "N/A";
            } else {
                itemID = "N/A";
            }

            shippingPrice = productInfo.optJSONArray("shippingInfo")
                    .optJSONObject(0)
                    .optJSONArray("shippingServiceCost")
                    .optJSONObject(0)
                    .optString("__value__", "N/A");

            price = productInfo.optJSONArray("sellingStatus")
                    .optJSONObject(0)
                    .optJSONArray("currentPrice")
                    .optJSONObject(0)
                    .optString("__value__", "N/A");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadProductDetails(JSONObject productDetails) {
        Log.d("ProductItem", productDetails.toString());

        specifications.clear();
        try {
            JSONObject itemSpecifics = productDetails.getJSONObject("Item")
                    .getJSONObject("ItemSpecifics");
            JSONArray nameValueList = itemSpecifics.getJSONArray("NameValueList");
            for (int i = 0; i < nameValueList.length(); i++) {
                JSONObject entry = nameValueList.getJSONObject(i);
                String name = entry.getString("Name");
                JSONArray valueArray = entry.getJSONArray("Value");
                if (valueArray.length() > 0) {
                    String value = valueArray.getString(0);
                    specifications.put(name, value);
                }
            }

            JSONObject item = productDetails.getJSONObject("Item");
            ViewItemURLForNaturalSearch = item.optString("ViewItemURLForNaturalSearch", "#");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        images.clear();
        try {
            JSONObject item = productDetails.getJSONObject("Item");
            JSONArray pictureUrls = item.getJSONArray("PictureURL");
            for (int i = 0; i < pictureUrls.length(); i++) {
                String entry = pictureUrls.getString(i);
                images.add(entry);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Extract Shipping Information
        JSONObject itemObject = productDetails.optJSONObject("Item");
        if (itemObject != null) {
            JSONObject itemStoreFrontObject = itemObject.optJSONObject("Storefront");
            if (itemStoreFrontObject != null) {
                storeName = itemStoreFrontObject.optString("StoreName", "N/A");
                storeUrl = itemStoreFrontObject.optString("StoreURL", "#");
            }
            JSONObject itemSellerObject  = itemObject.optJSONObject("Seller");
            if (itemSellerObject != null) {
                feedbackStar = itemSellerObject.optString("FeedbackRatingStar", "N/A");
                feedbackScore = itemSellerObject.optString("FeedbackScore", "N/A");
                popularity = itemSellerObject.optString("PositiveFeedbackPercent", "N/A");
            }
            globalShipping = itemObject.optString("GlobalShipping", "N/A");
            handlingTime = itemObject.optString("HandlingTime", "N/A");
            JSONObject returnPolicyJson = itemObject.optJSONObject("ReturnPolicy");
            if (returnPolicyJson != null) {
                policy = returnPolicyJson.optString("ReturnsAccepted", "N/A");
                returnsWithin = returnPolicyJson.optString("ReturnsWithin", "N/A");
                refundMode = returnPolicyJson.optString("Refund", "N/A");
                shippedBy = returnPolicyJson.optString("ShippingCostPaidBy", "N/A");
            }
        }
    }

    public void loadSimilarProducts(JSONObject jsonObject) {
        similarProducts.clear();
        try {
        JSONObject itemRecommendations = jsonObject.getJSONObject("getSimilarItemsResponse")
                .getJSONObject("itemRecommendations");
        JSONArray itemArray = itemRecommendations.optJSONArray("item");

        if (itemArray != null) {
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject item = itemArray.optJSONObject(i);
                if (item != null) {
                    SimilarProduct similar = new SimilarProduct(item);
                    similarProducts.add(similar);
                }
            }
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }
    }

    public void loadPhotosUrl(JSONObject response) {
        photosUrl.clear();
        JSONArray itemsArray = response.optJSONArray("items");
        if (itemsArray != null) {
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.optJSONObject(i);
                String link = item.optString("link", "");
                if (!link.equals("")) {
                    photosUrl.add(link);
                }
            }
        }
    }

    public Map<String, String> getSpecifications() {
        return specifications;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void printProductDetails() {
        Log.d(getClass().getSimpleName(), "Title: " + title);
        Log.d(getClass().getSimpleName(), "Image URL: " + imageUrl);
        Log.d(getClass().getSimpleName(), "Price: " + price);
        Log.d(getClass().getSimpleName(), "Shipping Price: " + shippingPrice);
        Log.d(getClass().getSimpleName(), "Item ID: " + itemID);
        Log.d(getClass().getSimpleName(), "Zip Code: " + zipCode);
        Log.d(getClass().getSimpleName(), "Condition: " + condition);
        Log.d(getClass().getSimpleName(), "Specifications:");
        for (Map.Entry<String, String> entry : specifications.entrySet()) {
            Log.d(getClass().getSimpleName(), entry.getKey() + ": " + entry.getValue());
        }
        Log.d(getClass().getSimpleName(), "Store Name: " + storeName);
        Log.d(getClass().getSimpleName(), "Feedback Score: " + feedbackScore);
        Log.d(getClass().getSimpleName(), "Popularity: " + popularity);
        Log.d(getClass().getSimpleName(), "Feedback Star: " + feedbackStar);
        Log.d(getClass().getSimpleName(), "Global Shipping: " + globalShipping);
        Log.d(getClass().getSimpleName(), "Handling Time: " + handlingTime);
        Log.d(getClass().getSimpleName(), "Policy: " + policy);
        Log.d(getClass().getSimpleName(), "Returns Within: " + returnsWithin);
        Log.d(getClass().getSimpleName(), "Refund Mode: " + refundMode);
        Log.d(getClass().getSimpleName(), "Shipped By: " + shippedBy);
    }

    // Getter methods for each property
    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public String getStoreUrl() {
        return storeUrl;
    }

    public String getShippingPrice() {
        return shippingPrice;
    }

    public String getItemID() {
        return itemID;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCondition() {
        return condition;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getFeedbackScore() {
        return feedbackScore;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getFeedbackStar() {
        return feedbackStar;
    }

    public String getGlobalShipping() {
        return globalShipping;
    }

    public String getHandlingTime() {
        return handlingTime;
    }

    public String getPolicy() {
        return policy;
    }

    public String getReturnsWithin() {
        return returnsWithin;
    }

    public String getRefundMode() {
        return refundMode;
    }

    public String getShippedBy() {
        return shippedBy;
    }

    public ArrayList<SimilarProduct> getSimilarProducts() {
        return similarProducts;
    }

    public ArrayList<String> getPhotosUrl() {
        return photosUrl;
    }

    public String getViewItemURLForNaturalSearch() {
        return ViewItemURLForNaturalSearch;
    }
}
