package com.example.myapplication;

public class CartItem {
    private int cartId;
    private int productId;
    private String name;
    private double price;
    private String imageRes;
    private int quantity;

    public CartItem(int cartId, int productId, String name, double price, String imageRes, int quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
        this.quantity = quantity;
    }

    public int getCartId() { return cartId; }
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImageRes() { return imageRes; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotalPrice() { return price * quantity; }
}
