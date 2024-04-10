package com.example.hw4;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SimilarProductAdapter extends RecyclerView.Adapter<SimilarProductAdapter.ViewHolder> {

    private final List<SimilarProduct> products;

    public SimilarProductAdapter(List<SimilarProduct> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_similar_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimilarProduct product = products.get(position);
        holder.productNameTextView.setText(product.getTitle());

        Picasso.get()
                .load(product.getImageURL())
                .into(holder.productImageView);

        holder.shippingPrice.setText(product.getShippingCost()=="0.0"?"Free": product.getShippingCost());
        holder.daysLeft.setText(product.getDaysLeft() + " Days Left");
        holder.price.setText(product.getPrice());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        ImageView productImageView;
        TextView shippingPrice;
        TextView daysLeft;
        TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            shippingPrice = itemView.findViewById(R.id.shippingPrice);
            daysLeft = itemView.findViewById(R.id.daysLeft);
            price = itemView.findViewById(R.id.price);
        }
    }
}
