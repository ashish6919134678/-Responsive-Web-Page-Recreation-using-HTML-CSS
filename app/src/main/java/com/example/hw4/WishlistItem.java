package com.example.hw4;

public class WishlistItem {
    private String title;
    private String imageUrl;
    private String price;
    private String shippingPrice;
    private String itemID;

    public WishlistItem(String title, String imageUrl, String price, String shippingPrice, String itemID) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.price = price;
        this.shippingPrice = shippingPrice;
        this.itemID = itemID;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public String getShippingPrice() {
        return shippingPrice;
    }

    public String getItemID() {
        return itemID;
    }
}
