package com.example.myapplication;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDatabaseHelper {

    private static FirebaseDatabaseHelper instance;
    private final FirebaseDatabase database;
    private final FirebaseAuth auth;

    private final DatabaseReference usersRef;
    private final DatabaseReference productsRef;
    private final DatabaseReference cartRef;
    private final DatabaseReference ordersRef;

    public interface DataCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public static synchronized FirebaseDatabaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseDatabaseHelper();
        }
        return instance;
    }

    private FirebaseDatabaseHelper() {
        database = FirebaseDatabase.getInstance();
        try {
            database.setPersistenceEnabled(true);
        } catch (Exception ignored) {
            // Already enabled or initialized
        }
        auth = FirebaseAuth.getInstance();

        usersRef = database.getReference("users");
        productsRef = database.getReference("products");
        cartRef = database.getReference("cart");
        ordersRef = database.getReference("orders");

        // Seed initial products if node doesn't exist
        seedInitialProductsIfEmpty();
    }

    // =================== AUTH & USER METHODS ===================

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void registerUser(String name, String email, String password, String phone, DataCallback<String> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userId", uid);
                    userMap.put("name", name);
                    userMap.put("email", email);
                    userMap.put("phone", phone);
                    userMap.put("role", email.equalsIgnoreCase("admin@shop.com") ? "admin" : "user");

                    usersRef.child(uid).setValue(userMap)
                            .addOnSuccessListener(unused -> callback.onSuccess(uid))
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void loginUser(String email, String password, DataCallback<Map<String, Object>> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Map<String, Object> userData = (Map<String, Object>) snapshot.getValue();
                                callback.onSuccess(userData);
                            } else {
                                callback.onError("User data not found in Firebase database.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            callback.onError(error.getMessage());
                        }
                    });
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // =================== PRODUCT METHODS ===================

    public void getAllProducts(DataCallback<List<Product>> callback) {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> list = new ArrayList<>();
                int idCounter = 1;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey();
                    String name = ds.child("name").getValue(String.class);
                    String desc = ds.child("description").getValue(String.class);
                    Double price = ds.child("price").getValue(Double.class);
                    String category = ds.child("category").getValue(String.class);
                    String image = ds.child("imageUrl").getValue(String.class);
                    Double rating = ds.child("rating").getValue(Double.class);
                    Integer stock = ds.child("stock").getValue(Integer.class);

                    if (name != null) {
                        Product p = new Product(
                                idCounter++,
                                name,
                                desc != null ? desc : "",
                                price != null ? price : 0.0,
                                category != null ? category : "General",
                                image != null ? image : "",
                                rating != null ? rating : 4.0,
                                stock != null ? stock : 10
                        );
                        list.add(p);
                    }
                }
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public void addProduct(String name, String description, double price,
                           String category, String imageUrl, double rating, int stock, DataCallback<Boolean> callback) {
        String productId = productsRef.push().getKey();
        if (productId == null) {
            callback.onError("Failed to generate product key");
            return;
        }

        Map<String, Object> productMap = new HashMap<>();
        productMap.put("productId", productId);
        productMap.put("name", name);
        productMap.put("description", description);
        productMap.put("price", price);
        productMap.put("category", category);
        productMap.put("imageUrl", imageUrl);
        productMap.put("rating", rating);
        productMap.put("stock", stock);

        productsRef.child(productId).setValue(productMap)
                .addOnSuccessListener(unused -> callback.onSuccess(true))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // =================== SEED PRODUCTS ===================

    private void seedInitialProductsIfEmpty() {
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    String[][] products = {
                            {"iPhone 15 Pro", "Latest Apple flagship with titanium design, A17 Pro chip, 48MP camera", "99999", "Electronics",
                                    "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=400&q=80", "4.8", "15"},
                            {"Samsung Galaxy S24", "Android powerhouse with Snapdragon 8 Gen 3, 200MP camera", "79999", "Electronics",
                                    "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400&q=80", "4.7", "20"},
                            {"Sony WH-1000XM5", "Industry-leading noise cancellation headphones with 30hr battery", "29999", "Electronics",
                                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&q=80", "4.9", "30"},
                            {"Nike Air Max 270", "Stylish sneakers with Max Air cushioning for all-day comfort", "12999", "Footwear",
                                    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&q=80", "4.5", "50"},
                            {"Levi's 511 Slim Jeans", "Classic slim fit jeans in premium stretch denim", "4999", "Clothing",
                                    "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400&q=80", "4.3", "100"},
                            {"MacBook Air M3", "Ultra-thin laptop with M3 chip, 18hr battery, fanless design", "114999", "Electronics",
                                    "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&q=80", "4.9", "10"}
                    };

                    for (String[] p : products) {
                        String key = productsRef.push().getKey();
                        if (key != null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("productId", key);
                            map.put("name", p[0]);
                            map.put("description", p[1]);
                            map.put("price", Double.parseDouble(p[2]));
                            map.put("category", p[3]);
                            map.put("imageUrl", p[4]);
                            map.put("rating", Double.parseDouble(p[5]));
                            map.put("stock", Integer.parseInt(p[6]));
                            productsRef.child(key).setValue(map);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
