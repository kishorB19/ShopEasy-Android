package com.example.myapplication;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imageRes;
    private double rating;
    private int stock;

    public Product(int id, String name, String description, double price,
                   String category, String imageRes, double rating, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageRes = imageRes;
        this.rating = rating;
        this.stock = stock;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImageRes() { return imageRes; }
    public double getRating() { return rating; }
    public int getStock() { return stock; }
}
