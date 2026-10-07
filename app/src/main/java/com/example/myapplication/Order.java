package com.example.myapplication;

public class Order {
    private int orderId;
    private double total;
    private String status;
    private String date;
    private String address;

    public Order(int orderId, double total, String status, String date, String address) {
        this.orderId = orderId;
        this.total = total;
        this.status = status;
        this.date = date;
        this.address = address;
    }

    public int getOrderId() { return orderId; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public String getAddress() { return address; }
}
