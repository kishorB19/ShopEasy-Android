package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderId.setText("Order #" + order.getOrderId());
        holder.tvOrderTotal.setText("₹" + String.format("%,.0f", order.getTotal()));
        holder.tvOrderDate.setText(order.getDate());
        holder.tvOrderAddress.setText("📍 " + order.getAddress());
        holder.tvOrderStatus.setText(order.getStatus());

        // Status color
        switch (order.getStatus()) {
            case "Confirmed":
                holder.tvOrderStatus.setTextColor(Color.parseColor("#27AE60"));
                break;
            case "Shipped":
                holder.tvOrderStatus.setTextColor(Color.parseColor("#2980B9"));
                break;
            case "Delivered":
                holder.tvOrderStatus.setTextColor(Color.parseColor("#8E44AD"));
                break;
            default:
                holder.tvOrderStatus.setTextColor(Color.parseColor("#E67E22"));
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderTotal, tvOrderDate, tvOrderAddress, tvOrderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderAddress = itemView.findViewById(R.id.tv_order_address);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
        }
    }
}
