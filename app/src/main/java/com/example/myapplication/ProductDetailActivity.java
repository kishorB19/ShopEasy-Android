package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SessionManager session;
    private Product product;
    private int quantity = 1;
    private TextView tvQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        int productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) { finish(); return; }

        product = db.getProductById(productId);
        if (product == null) { finish(); return; }

        bindData();
    }

    private void bindData() {
        ImageView ivProduct = findViewById(R.id.iv_product_detail);
        TextView tvName = findViewById(R.id.tv_detail_name);
        TextView tvPrice = findViewById(R.id.tv_detail_price);
        TextView tvCategory = findViewById(R.id.tv_detail_category);
        TextView tvRating = findViewById(R.id.tv_detail_rating);
        TextView tvDescription = findViewById(R.id.tv_detail_description);
        TextView tvStock = findViewById(R.id.tv_detail_stock);
        tvQuantity = findViewById(R.id.tv_quantity);
        Button btnMinus = findViewById(R.id.btn_minus);
        Button btnPlus = findViewById(R.id.btn_plus);
        Button btnAddToCart = findViewById(R.id.btn_detail_add_cart);
        Button btnBuyNow = findViewById(R.id.btn_buy_now);
        ImageView ivBack = findViewById(R.id.iv_back);

        tvName.setText(product.getName());
        tvPrice.setText("₹" + String.format("%,.0f", product.getPrice()));
        tvCategory.setText(product.getCategory());
        tvRating.setText("⭐ " + product.getRating() + " Rating");
        tvDescription.setText(product.getDescription());
        tvStock.setText("In Stock: " + product.getStock() + " units");
        tvQuantity.setText(String.valueOf(quantity));

        // Gradient background for product image area
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        gd.setColors(new int[]{Color.parseColor("#667eea"), Color.parseColor("#764ba2")});
        gd.setOrientation(android.graphics.drawable.GradientDrawable.Orientation.TL_BR);
        ivProduct.setBackground(gd);

        ivBack.setOnClickListener(v -> finish());

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (quantity < product.getStock()) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            for (int i = 0; i < quantity; i++) {
                db.addToCart(session.getUserId(), product.getId());
            }
            Toast.makeText(this, "Added " + quantity + " item(s) to cart!", Toast.LENGTH_SHORT).show();
        });

        btnBuyNow.setOnClickListener(v -> {
            for (int i = 0; i < quantity; i++) {
                db.addToCart(session.getUserId(), product.getId());
            }
            startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
        });
    }
}
