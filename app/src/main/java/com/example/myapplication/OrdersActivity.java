package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private DatabaseHelper db;
    private SessionManager session;
    private TextView tvNoOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        rvOrders = findViewById(R.id.rv_orders);
        tvNoOrders = findViewById(R.id.tv_no_orders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        List<Order> orders = db.getUserOrders(session.getUserId());

        if (orders.isEmpty()) {
            tvNoOrders.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            tvNoOrders.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);
            rvOrders.setAdapter(new OrderAdapter(this, orders));
        }

        findViewById(R.id.iv_back_orders).setOnClickListener(v -> finish());
    }
}
