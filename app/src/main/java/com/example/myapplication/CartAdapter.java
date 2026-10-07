package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onQuantityChanged(CartItem item, int newQty);
        void onRemoveItem(CartItem item);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("₹" + String.format("%,.0f", item.getPrice()));
        holder.tvTotal.setText("Total: ₹" + String.format("%,.0f", item.getTotalPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Gradient for image
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        int[] colors = {Color.parseColor("#6C63FF"), Color.parseColor("#4ECDC4"),
                Color.parseColor("#FF6B6B"), Color.parseColor("#45B7D1"),
                Color.parseColor("#96CEB4"), Color.parseColor("#DDA0DD")};
        int colorIndex = Math.abs(item.getName().hashCode()) % colors.length;
        gd.setColors(new int[]{colors[colorIndex], Color.parseColor("#1a1a2e")});
        gd.setOrientation(android.graphics.drawable.GradientDrawable.Orientation.TL_BR);
        gd.setCornerRadius(12f);
        holder.ivProduct.setBackground(gd);

        holder.btnMinus.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;
            listener.onQuantityChanged(item, newQty);
        });

        holder.btnPlus.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            listener.onQuantityChanged(item, newQty);
        });

        holder.btnRemove.setOnClickListener(v -> listener.onRemoveItem(item));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvTotal, tvQuantity;
        Button btnMinus, btnPlus, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_cart_product);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_price);
            tvTotal = itemView.findViewById(R.id.tv_cart_total);
            tvQuantity = itemView.findViewById(R.id.tv_cart_quantity);
            btnMinus = itemView.findViewById(R.id.btn_cart_minus);
            btnPlus = itemView.findViewById(R.id.btn_cart_plus);
            btnRemove = itemView.findViewById(R.id.btn_cart_remove);
        }
    }
}
