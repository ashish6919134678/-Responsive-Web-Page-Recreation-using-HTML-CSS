package com.example.hw4;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private final List<WishlistItem> data;
    private wishlistRemoveListner listener;

    public WishlistAdapter(List<WishlistItem> data, wishlistRemoveListner listener) {
        this.data = data;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textWishlistTitle;
        public final TextView textZip;
        public final TextView textShippingPrice;
        public final TextView textPrice;
        public final ImageView productImage;
        public final ImageView imageCartRemove;

        public ViewHolder(View view) {
            super(view);
            textWishlistTitle = view.findViewById(R.id.textWishlistTitle);
            textZip = view.findViewById(R.id.textZip);
            textShippingPrice = view.findViewById(R.id.textShippingPrice);
            textPrice = view.findViewById(R.id.textPrice);
            productImage = view.findViewById(R.id.productImage);
            imageCartRemove = view.findViewById(R.id.imageCartRemove);
            textWishlistTitle.setMaxLines(3);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wishlist_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WishlistItem item = data.get(position);
        holder.textWishlistTitle.setText(item.getTitle());
        holder.textWishlistTitle.setGravity(Gravity.CENTER);
        holder.textZip.setText("Zip: 900**");
        double shippingPrice = Double.parseDouble(item.getShippingPrice());
        if (shippingPrice == 0.0) {
            holder.textShippingPrice.setText("Free");
        } else {
            holder.textShippingPrice.setText("$" + item.getShippingPrice());
        }
        holder.textPrice.setText(item.getPrice());
        Picasso.get().load(item.getImageUrl()).into(holder.productImage);

        holder.imageCartRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.wishlistRemoveItem(item.getItemID());
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
