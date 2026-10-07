package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private DatabaseHelper db;
    private SessionManager session;
    private TextView tvTotal, tvEmpty;
    private Button btnCheckout;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        rvCart = findViewById(R.id.rv_cart);
        tvTotal = findViewById(R.id.tv_cart_grand_total);
        tvEmpty = findViewById(R.id.tv_cart_empty);
        btnCheckout = findViewById(R.id.btn_checkout);

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        loadCart();

        findViewById(R.id.iv_back_cart).setOnClickListener(v -> finish());

        btnCheckout.setOnClickListener(v -> showCheckoutDialog());
    }

    private void loadCart() {
        cartItems = db.getCartItems(session.getUserId());
        if (cartItems.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
            tvTotal.setText("Total: ₹0");
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvCart.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(true);
            adapter = new CartAdapter(this, cartItems, new CartAdapter.OnCartChangeListener() {
                @Override
                public void onQuantityChanged(CartItem item, int newQty) {
                    db.updateCartQuantity(session.getUserId(), item.getProductId(), newQty);
                    loadCart();
                }

                @Override
                public void onRemoveItem(CartItem item) {
                    db.removeFromCart(session.getUserId(), item.getProductId());
                    loadCart();
                    Toast.makeText(CartActivity.this, "Item removed", Toast.LENGTH_SHORT).show();
                }
            });
            rvCart.setAdapter(adapter);
            updateTotal();
        }
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        tvTotal.setText("Grand Total: ₹" + String.format("%,.0f", total));
    }

    private void showCheckoutDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_checkout, null);
        EditText etAddress = dialogView.findViewById(R.id.et_address);

        new AlertDialog.Builder(this)
                .setTitle("Confirm Order")
                .setView(dialogView)
                .setPositiveButton("Place Order", (dialog, which) -> {
                    String address = etAddress.getText().toString().trim();
                    if (address.isEmpty()) {
                        Toast.makeText(this, "Please enter delivery address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double total = 0;
                    for (CartItem item : cartItems) total += item.getTotalPrice();
                    long orderId = db.placeOrder(session.getUserId(), total, address, cartItems);
                    db.clearCart(session.getUserId());
                    Toast.makeText(this, "Order #" + orderId + " placed successfully! 🎉", Toast.LENGTH_LONG).show();
                    loadCart();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
