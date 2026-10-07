package com.example.myapplication;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCart(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("₹" + String.format("%,.0f", product.getPrice()));
        holder.tvCategory.setText(product.getCategory());
        holder.tvRating.setText("⭐ " + product.getRating());

        String imageRes = product.getImageRes();
        if (imageRes != null && (imageRes.startsWith("http://") || imageRes.startsWith("https://"))) {
            // Load real image from URL using Glide
            Glide.with(context)
                    .load(imageRes)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(getProductDrawable(product.getName()))
                    .error(getProductDrawable(product.getName()))
                    .centerCrop()
                    .into(holder.ivProduct);
        } else {
            // Fallback to colored gradient placeholder
            holder.ivProduct.setImageDrawable(getProductDrawable(imageRes != null ? imageRes : product.getName()));
        }

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
        holder.btnAddToCart.setOnClickListener(v -> listener.onAddToCart(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private android.graphics.drawable.Drawable getProductDrawable(String key) {
        int[] colors = {
                Color.parseColor("#FF6B6B"), Color.parseColor("#4ECDC4"),
                Color.parseColor("#45B7D1"), Color.parseColor("#96CEB4"),
                Color.parseColor("#FFEAA7"), Color.parseColor("#DDA0DD"),
                Color.parseColor("#98D8C8"), Color.parseColor("#F7DC6F"),
                Color.parseColor("#BB8FCE"), Color.parseColor("#85C1E9"),
                Color.parseColor("#F1948A"), Color.parseColor("#82E0AA")
        };
        int colorIndex = Math.abs((key != null ? key : "").hashCode()) % colors.length;

        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        gd.setGradientType(android.graphics.drawable.GradientDrawable.LINEAR_GRADIENT);
        int baseColor = colors[colorIndex];
        int lighterColor = Color.argb(255,
                Math.min(255, Color.red(baseColor) + 40),
                Math.min(255, Color.green(baseColor) + 40),
                Math.min(255, Color.blue(baseColor) + 40));
        gd.setColors(new int[]{lighterColor, baseColor});
        gd.setOrientation(android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM);
        gd.setCornerRadius(16f);
        return gd;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvCategory, tvRating;
        Button btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvCategory = itemView.findViewById(R.id.tv_product_category);
            tvRating = itemView.findViewById(R.id.tv_product_rating);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}
