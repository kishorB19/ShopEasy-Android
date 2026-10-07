package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SessionManager session;

    // Tabs
    private TextView tabDashboard, tabProducts, tabOrders;
    private LinearLayout panelDashboard, panelProducts, panelOrders;

    // Dashboard stats
    private TextView tvStatUsers, tvStatOrders, tvStatRevenue;

    // Products panel
    private EditText etImageUrl, etName, etDesc, etPrice, etRating, etStock;
    private Spinner spinnerCategory;
    private ImageView ivPreview;
    private LinearLayout llProductsList;

    // Orders panel
    private LinearLayout llOrdersList;

    private static final String[] CATEGORIES = {
            "Electronics", "Footwear", "Clothing", "Books", "Kitchen", "Home", "Accessories"
    };
    private static final String[] ORDER_STATUSES = {
            "Confirmed", "Processing", "Shipped", "Out for Delivery", "Delivered", "Cancelled"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#16213e"));
        }

        setContentView(R.layout.activity_admin);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        if (!session.isLoggedIn() || !session.isAdmin()) {
            Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        showTab(0); // start on Dashboard
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardStats();
        loadProductList();
        loadOrdersList();
    }

    private void initViews() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // Tabs
        tabDashboard  = findViewById(R.id.tab_dashboard);
        tabProducts   = findViewById(R.id.tab_products);
        tabOrders     = findViewById(R.id.tab_orders);
        panelDashboard = findViewById(R.id.panel_dashboard);
        panelProducts  = findViewById(R.id.panel_products);
        panelOrders    = findViewById(R.id.panel_orders);

        tabDashboard.setOnClickListener(v -> showTab(0));
        tabProducts.setOnClickListener(v  -> showTab(1));
        tabOrders.setOnClickListener(v    -> showTab(2));

        // Dashboard stat TextViews
        tvStatUsers   = findViewById(R.id.tv_stat_users);
        tvStatOrders  = findViewById(R.id.tv_stat_orders);
        tvStatRevenue = findViewById(R.id.tv_stat_revenue);

        // Dashboard quick-link buttons
        findViewById(R.id.btn_go_products).setOnClickListener(v -> showTab(1));
        findViewById(R.id.btn_go_orders).setOnClickListener(v  -> showTab(2));

        // Products panel
        etImageUrl       = findViewById(R.id.et_image_url);
        etName           = findViewById(R.id.et_product_name);
        etDesc           = findViewById(R.id.et_product_desc);
        etPrice          = findViewById(R.id.et_product_price);
        etRating         = findViewById(R.id.et_product_rating);
        etStock          = findViewById(R.id.et_product_stock);
        spinnerCategory  = findViewById(R.id.spinner_category);
        ivPreview        = findViewById(R.id.iv_preview);
        llProductsList   = findViewById(R.id.ll_products_list);
        llOrdersList     = findViewById(R.id.ll_orders_list);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CATEGORIES);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        findViewById(R.id.btn_preview_image).setOnClickListener(v -> previewImage());
        findViewById(R.id.btn_add_product).setOnClickListener(v -> addProduct());
    }

    // ─── TAB SWITCHING ────────────────────────────────────────────────────────

    private void showTab(int index) {
        panelDashboard.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        panelProducts.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        panelOrders.setVisibility(index == 2 ? View.VISIBLE : View.GONE);

        int activeColor   = Color.parseColor("#1a237e");
        int inactiveColor = Color.TRANSPARENT;
        int activeText    = Color.parseColor("#FFD700");
        int inactiveText  = Color.parseColor("#AAAAAA");

        tabDashboard.setBackgroundColor(index == 0 ? activeColor : inactiveColor);
        tabProducts.setBackgroundColor(index == 1 ? activeColor : inactiveColor);
        tabOrders.setBackgroundColor(index == 2 ? activeColor : inactiveColor);

        tabDashboard.setTextColor(index == 0 ? activeText : inactiveText);
        tabProducts.setTextColor(index == 1 ? activeText : inactiveText);
        tabOrders.setTextColor(index == 2 ? activeText : inactiveText);

        tabDashboard.setTypeface(null, index == 0 ? Typeface.BOLD : Typeface.NORMAL);
        tabProducts.setTypeface(null, index == 1 ? Typeface.BOLD : Typeface.NORMAL);
        tabOrders.setTypeface(null, index == 2 ? Typeface.BOLD : Typeface.NORMAL);
    }

    // ─── DASHBOARD ────────────────────────────────────────────────────────────

    private void loadDashboardStats() {
        tvStatUsers.setText(String.valueOf(db.getTotalUsersCount()));
        tvStatOrders.setText(String.valueOf(db.getTotalOrdersCount()));
        double revenue = db.getTotalRevenue();
        if (revenue >= 100000) {
            tvStatRevenue.setText("₹" + String.format("%.1fL", revenue / 100000));
        } else {
            tvStatRevenue.setText("₹" + String.format("%,.0f", revenue));
        }
    }

    // ─── PRODUCTS ─────────────────────────────────────────────────────────────

    private void previewImage() {
        String url = etImageUrl.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(this, "Enter an image URL first", Toast.LENGTH_SHORT).show();
            return;
        }
        ivPreview.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(ivPreview);
    }

    private void addProduct() {
        String name     = etName.getText().toString().trim();
        String desc     = etDesc.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String ratingStr = etRating.getText().toString().trim();
        String stockStr  = etStock.getText().toString().trim();
        String imageUrl  = etImageUrl.getText().toString().trim();
        String category  = CATEGORIES[spinnerCategory.getSelectedItemPosition()];

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Name and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }
        double price, rating = 4.0;
        int stock = 10;
        try { price = Double.parseDouble(priceStr); }
        catch (NumberFormatException e) { Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show(); return; }
        if (!ratingStr.isEmpty()) try { rating = Double.parseDouble(ratingStr); } catch (NumberFormatException ignored) {}
        if (!stockStr.isEmpty())  try { stock  = Integer.parseInt(stockStr);    } catch (NumberFormatException ignored) {}

        long id = db.addProduct(name, desc, price, category, imageUrl, rating, stock);
        if (id > 0) {
            Toast.makeText(this, "✅ '" + name + "' added!", Toast.LENGTH_SHORT).show();
            etName.setText(""); etDesc.setText(""); etPrice.setText("");
            etRating.setText(""); etStock.setText(""); etImageUrl.setText("");
            ivPreview.setVisibility(View.GONE);
            loadProductList();
            loadDashboardStats();
        } else {
            Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProductList() {
        if (llProductsList == null) return;
        llProductsList.removeAllViews();
        for (Product p : db.getAllProducts()) {
            llProductsList.addView(buildProductRow(p));
        }
    }

    private View buildProductRow(Product p) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setMargins(0, 0, 0, 10);
        card.setLayoutParams(cp);
        card.setCardBackgroundColor(Color.parseColor("#16213e"));
        card.setRadius(12f);
        card.setCardElevation(3f);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(14, 14, 14, 14);
        row.setGravity(Gravity.CENTER_VERTICAL);

        ImageView thumb = new ImageView(this);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(70, 70);
        tp.setMarginEnd(14);
        thumb.setLayoutParams(tp);
        thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String img = p.getImageRes();
        if (img != null && (img.startsWith("http://") || img.startsWith("https://"))) {
            Glide.with(this).load(img).centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade()).into(thumb);
        } else {
            thumb.setBackgroundColor(Color.parseColor("#6C63FF"));
        }

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvN = new TextView(this);
        tvN.setText(p.getName());
        tvN.setTextColor(Color.WHITE);
        tvN.setTextSize(13f);
        tvN.setTypeface(null, Typeface.BOLD);

        TextView tvC = new TextView(this);
        tvC.setText(p.getCategory() + " · ₹" + String.format("%,.0f", p.getPrice()));
        tvC.setTextColor(Color.parseColor("#4ECDC4"));
        tvC.setTextSize(11f);

        info.addView(tvN);
        info.addView(tvC);

        Button btnDel = new Button(this);
        btnDel.setText("🗑");
        btnDel.setTextSize(18f);
        btnDel.setBackgroundColor(Color.TRANSPARENT);
        btnDel.setTextColor(Color.parseColor("#FF6B6B"));
        btnDel.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Delete '" + p.getName() + "'?")
                .setPositiveButton("Delete", (d, w) -> {
                    db.deleteProduct(p.getId());
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    loadProductList();
                    loadDashboardStats();
                })
                .setNegativeButton("Cancel", null)
                .show());

        row.addView(thumb);
        row.addView(info);
        row.addView(btnDel);
        card.addView(row);
        return card;
    }

    // ─── ORDERS ───────────────────────────────────────────────────────────────

    private void loadOrdersList() {
        if (llOrdersList == null) return;
        llOrdersList.removeAllViews();
        List<AdminOrder> orders = db.getAllOrdersWithUsers();

        if (orders.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No orders yet.");
            empty.setTextColor(Color.parseColor("#AAAAAA"));
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, 32, 0, 32);
            llOrdersList.addView(empty);
            return;
        }

        for (AdminOrder o : orders) {
            llOrdersList.addView(buildOrderCard(o));
        }
    }

    private View buildOrderCard(AdminOrder o) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cp.setMargins(0, 0, 0, 12);
        card.setLayoutParams(cp);
        card.setCardBackgroundColor(Color.parseColor("#16213e"));
        card.setRadius(14f);
        card.setCardElevation(4f);

        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setPadding(16, 16, 16, 16);

        // Header row: Order ID + Status badge
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        headerRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView tvOrderId = new TextView(this);
        tvOrderId.setText("Order #" + o.getOrderId());
        tvOrderId.setTextColor(Color.WHITE);
        tvOrderId.setTextSize(15f);
        tvOrderId.setTypeface(null, Typeface.BOLD);
        tvOrderId.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvStatus = new TextView(this);
        tvStatus.setText(o.getStatus());
        tvStatus.setTextSize(11f);
        tvStatus.setTextColor(Color.WHITE);
        tvStatus.setTypeface(null, Typeface.BOLD);
        tvStatus.setPadding(16, 6, 16, 6);
        tvStatus.setBackground(makeStatusBg(o.getStatus()));

        headerRow.addView(tvOrderId);
        headerRow.addView(tvStatus);

        // Customer info
        TextView tvUser = new TextView(this);
        tvUser.setText("👤 " + o.getUserName() + "  (" + o.getUserEmail() + ")");
        tvUser.setTextColor(Color.parseColor("#4ECDC4"));
        tvUser.setTextSize(12f);

        // Amount + Date
        TextView tvAmount = new TextView(this);
        tvAmount.setText("₹" + String.format("%,.0f", o.getTotal()) + "   •   " + o.getDate());
        tvAmount.setTextColor(Color.parseColor("#AAAAAA"));
        tvAmount.setTextSize(11f);

        // Address
        if (o.getAddress() != null && !o.getAddress().isEmpty()) {
            TextView tvAddr = new TextView(this);
            tvAddr.setText("📍 " + o.getAddress());
            tvAddr.setTextColor(Color.parseColor("#888888"));
            tvAddr.setTextSize(11f);
            col.addView(headerRow);
            col.addView(makeSpace(6));
            col.addView(tvUser);
            col.addView(makeSpace(4));
            col.addView(tvAmount);
            col.addView(makeSpace(4));
            col.addView(tvAddr);
        } else {
            col.addView(headerRow);
            col.addView(makeSpace(6));
            col.addView(tvUser);
            col.addView(makeSpace(4));
            col.addView(tvAmount);
        }

        // Update Status button
        col.addView(makeSpace(10));
        Button btnStatus = new Button(this);
        btnStatus.setText("Update Status");
        btnStatus.setTextSize(12f);
        btnStatus.setTextColor(Color.WHITE);
        btnStatus.setBackgroundColor(Color.parseColor("#6C63FF"));
        btnStatus.setOnClickListener(v -> showStatusDialog(o, tvStatus));

        col.addView(btnStatus);
        card.addView(col);
        return card;
    }

    private void showStatusDialog(AdminOrder order, TextView tvStatus) {
        new AlertDialog.Builder(this)
                .setTitle("Update Order #" + order.getOrderId() + " Status")
                .setItems(ORDER_STATUSES, (dialog, which) -> {
                    String newStatus = ORDER_STATUSES[which];
                    db.updateOrderStatus(order.getOrderId(), newStatus);
                    tvStatus.setText(newStatus);
                    tvStatus.setBackground(makeStatusBg(newStatus));
                    Toast.makeText(this, "Status updated to: " + newStatus, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private android.graphics.drawable.GradientDrawable makeStatusBg(String status) {
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setCornerRadius(30f);
        switch (status) {
            case "Delivered":   bg.setColor(Color.parseColor("#2E7D32")); break; // green
            case "Shipped":
            case "Out for Delivery": bg.setColor(Color.parseColor("#1565C0")); break; // blue
            case "Processing":  bg.setColor(Color.parseColor("#E65100")); break; // orange
            case "Cancelled":   bg.setColor(Color.parseColor("#B71C1C")); break; // red
            default:            bg.setColor(Color.parseColor("#4A148C")); break; // purple (Confirmed)
        }
        return bg;
    }

    private View makeSpace(int dp) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp));
        return v;
    }
}
