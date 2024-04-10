package com.example.hw4;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductlistAdapter extends RecyclerView.Adapter<ProductlistAdapter.ViewHolder> {

    private List<ProductItem> products;
    private List<WishlistItem> wishlists;
    private final wishlistAddListner wishlistAdd;
    private final wishlistRemoveListner wishlistRemove;
    private final productClickListener productClick;

    public ProductlistAdapter(List<ProductItem> products,  List<WishlistItem> wishlists,  wishlistAddListner wishlistAdd, wishlistRemoveListner wishlistRemove, productClickListener productClick) {
        this.products = products;
        this.wishlists = wishlists;
        this.wishlistAdd = wishlistAdd;
        this.wishlistRemove = wishlistRemove;
        this.productClick = productClick;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView productTextTitle;
        public final TextView productTextZip;
        public final TextView productTextShippingPrice;
        public final TextView productTextPrice;
        public final ImageView productImage;
        public final ImageView productImageCart;
        public final TextView productTextCondition;
        public final CardView productCard;

        public ViewHolder(View view) {
            super(view);
            productCard = view.findViewById(R.id.productCard);
            productTextTitle = view.findViewById(R.id.productTextTitle);
            productTextZip = view.findViewById(R.id.productTextZip);
            productTextShippingPrice = view.findViewById(R.id.productTextShippingPrice);
            productTextPrice = view.findViewById(R.id.productTextPrice);
            productImage = view.findViewById(R.id.productImage);
            productImageCart = view.findViewById(R.id.productImageCart);
            productTextCondition = view.findViewById(R.id.productTextCondition);
            productTextTitle.setMaxLines(3);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductItem item = products.get(position);
        boolean isInWishlist = isItemInWishlist(item.getItemID());

        holder.productTextTitle.setText(item.getTitle());
        holder.productTextTitle.setGravity(Gravity.CENTER);
        holder.productTextZip.setText(item.getZipCode());
        holder.productTextCondition.setText(item.getCondition());
        double shippingPrice = Double.parseDouble(item.getShippingPrice());
        if (shippingPrice == 0.0) {
            holder.productTextShippingPrice.setText("Free");
        } else {
            holder.productTextShippingPrice.setText("$" + item.getShippingPrice());
        }
        holder.productTextPrice.setText(item.getPrice());
        Picasso.get().load(item.getImageUrl()).into(holder.productImage);

        if (isInWishlist) {
            holder.productImageCart.setImageResource(R.drawable.cart_remove);
        } else {
            holder.productImageCart.setImageResource(R.drawable.cart_plus);
        }

        holder.productImageCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInWishlist) {
                    wishlistRemove.wishlistRemoveItem(item.getItemID());
                    removeItemFromWishlist(item.getItemID());
                    notifyDataSetChanged();
                } else {
                    WishlistItem newItem = new WishlistItem(item.getTitle(), item.getImageUrl(), item.getPrice(), item.getShippingPrice(), item.getItemID());
                    wishlistAdd.wishlistAddItem(item.getItemID());
                    addItemToWishlist(newItem);
                    notifyDataSetChanged();
                }
            }
        });

        holder.productCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productClick.productClick(item.getItemID());
            }
        });
    }

    private boolean isItemInWishlist(String itemId) {
        for (WishlistItem wishlistItem : wishlists) {
            if (wishlistItem.getItemID().equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    public void addItemToWishlist(WishlistItem newItem) {
        if (!isItemInWishlist(newItem.getItemID())) {
            wishlists.add(newItem);
        }
    }

    public void removeItemFromWishlist(String itemId) {
        for (WishlistItem wishlistItem : wishlists) {
            if (wishlistItem.getItemID().equals(itemId)) {
                int position = wishlists.indexOf(wishlistItem);
                wishlists.remove(wishlistItem);
                return;
            }
        }
    }


    public void setProducts(List<ProductItem> rhs) {
        this.products = rhs;
    }

    public void setWishlists(List<WishlistItem> rhs) {
        this.wishlists = rhs;
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
