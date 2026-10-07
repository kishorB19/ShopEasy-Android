package com.example.myapplication;

public class AdminOrder {
    private int orderId;
    private String userName;
    private String userEmail;
    private double total;
    private String status;
    private String date;
    private String address;

    public AdminOrder(int orderId, String userName, String userEmail,
                      double total, String status, String date, String address) {
        this.orderId = orderId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.total = total;
        this.status = status;
        this.date = date;
        this.address = address;
    }

    public int getOrderId()    { return orderId; }
    public String getUserName()  { return userName; }
    public String getUserEmail() { return userEmail; }
    public double getTotal()     { return total; }
    public String getStatus()    { return status; }
    public String getDate()      { return date; }
    public String getAddress()   { return address; }
}
