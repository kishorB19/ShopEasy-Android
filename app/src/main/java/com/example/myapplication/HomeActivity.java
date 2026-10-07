package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter adapter;
    private DatabaseHelper db;
    private SessionManager session;
    private EditText etSearch;
    private TextView tvWelcome, tvCartBadge;
    private HorizontalScrollView hsvCategories;
    private LinearLayout llCategories;
    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fix: set status bar color in code (most reliable across all Android versions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#16213e"));
            getWindow().getDecorView().setSystemUiVisibility(0); // light icons on dark bar
        }

        setContentView(R.layout.activity_home);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupCategories();
        loadProducts("All");
        updateCartBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvCartBadge = findViewById(R.id.tv_cart_badge);
        etSearch = findViewById(R.id.et_search);
        rvProducts = findViewById(R.id.rv_products);
        llCategories = findViewById(R.id.ll_categories);

        tvWelcome.setText("Hello, " + session.getUserName() + "! 👋");

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        List<Product> products = db.getAllProducts();
        adapter = new ProductAdapter(this, products, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }

            @Override
            public void onAddToCart(Product product) {
                db.addToCart(session.getUserId(), product.getId());
                Toast.makeText(HomeActivity.this, product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
                updateCartBadge();
            }
        });
        rvProducts.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    loadProducts(selectedCategory);
                } else {
                    adapter.updateList(db.searchProducts(s.toString()));
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.iv_cart).setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, CartActivity.class)));

        findViewById(R.id.iv_orders).setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, OrdersActivity.class)));

        // Admin Panel button — only visible to admin account
        android.widget.ImageView ivAdmin = findViewById(R.id.iv_admin);
        if (session.isAdmin()) {
            ivAdmin.setVisibility(View.VISIBLE);
            ivAdmin.setOnClickListener(v ->
                    startActivity(new Intent(HomeActivity.this, AdminActivity.class)));
        } else {
            ivAdmin.setVisibility(View.GONE);
        }

        findViewById(R.id.iv_logout).setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setupCategories() {
        String[] categories = {"All", "Electronics", "Footwear", "Clothing", "Books", "Kitchen", "Home", "Accessories"};
        int[] colors = {0xFF6C63FF, 0xFF4ECDC4, 0xFFFF6B6B, 0xFFFFA07A, 0xFF98D8C8, 0xFFDDA0DD, 0xFF85C1E9, 0xFFF7DC6F};

        for (int i = 0; i < categories.length; i++) {
            final String cat = categories[i];
            TextView tv = new TextView(this);
            tv.setText(cat);
            tv.setPadding(40, 20, 40, 20);
            tv.setTextSize(14f);
            tv.setTextColor(0xFFFFFFFF);
            tv.setBackgroundColor(colors[i % colors.length]);

            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setColor(colors[i % colors.length]);
            bg.setCornerRadius(50f);
            tv.setBackground(bg);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            tv.setLayoutParams(params);

            tv.setOnClickListener(v -> {
                selectedCategory = cat;
                loadProducts(cat);
            });
            llCategories.addView(tv);
        }
    }

    private void loadProducts(String category) {
        List<Product> products;
        if (category.equals("All")) {
            products = db.getAllProducts();
        } else {
            products = db.getProductsByCategory(category);
        }
        adapter.updateList(products);
    }

    private void updateCartBadge() {
        int count = db.getCartCount(session.getUserId());
        if (tvCartBadge != null) {
            tvCartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            tvCartBadge.setText(String.valueOf(count));
        }
    }
}
