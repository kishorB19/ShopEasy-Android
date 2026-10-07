package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ECommerceDB";
    private static final int DB_VERSION = 3;

    // Tables
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_CART = "cart";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_ORDER_ITEMS = "order_items";

    // Users columns
    public static final String COL_USER_ID = "user_id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_PHONE = "phone";

    // Products columns
    public static final String COL_PRODUCT_ID = "product_id";
    public static final String COL_PRODUCT_NAME = "product_name";
    public static final String COL_PRODUCT_DESC = "description";
    public static final String COL_PRODUCT_PRICE = "price";
    public static final String COL_PRODUCT_CATEGORY = "category";
    public static final String COL_PRODUCT_IMAGE = "image_res";
    public static final String COL_PRODUCT_RATING = "rating";
    public static final String COL_PRODUCT_STOCK = "stock";

    // Cart columns
    public static final String COL_CART_ID = "cart_id";
    public static final String COL_CART_USER_ID = "user_id";
    public static final String COL_CART_PRODUCT_ID = "product_id";
    public static final String COL_CART_QUANTITY = "quantity";

    // Orders columns
    public static final String COL_ORDER_ID = "order_id";
    public static final String COL_ORDER_USER_ID = "user_id";
    public static final String COL_ORDER_TOTAL = "total_amount";
    public static final String COL_ORDER_STATUS = "status";
    public static final String COL_ORDER_DATE = "order_date";
    public static final String COL_ORDER_ADDRESS = "address";

    // Order Items columns
    public static final String COL_OI_ID = "oi_id";
    public static final String COL_OI_ORDER_ID = "order_id";
    public static final String COL_OI_PRODUCT_ID = "product_id";
    public static final String COL_OI_QUANTITY = "quantity";
    public static final String COL_OI_PRICE = "price";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_PHONE + " TEXT)");

        // Create Products table
        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_NAME + " TEXT NOT NULL, " +
                COL_PRODUCT_DESC + " TEXT, " +
                COL_PRODUCT_PRICE + " REAL NOT NULL, " +
                COL_PRODUCT_CATEGORY + " TEXT, " +
                COL_PRODUCT_IMAGE + " TEXT, " +
                COL_PRODUCT_RATING + " REAL DEFAULT 4.0, " +
                COL_PRODUCT_STOCK + " INTEGER DEFAULT 10)");

        // Create Cart table
        db.execSQL("CREATE TABLE " + TABLE_CART + " (" +
                COL_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CART_USER_ID + " INTEGER NOT NULL, " +
                COL_CART_PRODUCT_ID + " INTEGER NOT NULL, " +
                COL_CART_QUANTITY + " INTEGER DEFAULT 1, " +
                "FOREIGN KEY(" + COL_CART_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_CART_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COL_PRODUCT_ID + "))");

        // Create Orders table
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                COL_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ORDER_USER_ID + " INTEGER NOT NULL, " +
                COL_ORDER_TOTAL + " REAL NOT NULL, " +
                COL_ORDER_STATUS + " TEXT DEFAULT 'Pending', " +
                COL_ORDER_DATE + " TEXT, " +
                COL_ORDER_ADDRESS + " TEXT, " +
                "FOREIGN KEY(" + COL_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))");

        // Create Order Items table
        db.execSQL("CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                COL_OI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_OI_ORDER_ID + " INTEGER NOT NULL, " +
                COL_OI_PRODUCT_ID + " INTEGER NOT NULL, " +
                COL_OI_QUANTITY + " INTEGER NOT NULL, " +
                COL_OI_PRICE + " REAL NOT NULL, " +
                "FOREIGN KEY(" + COL_OI_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COL_ORDER_ID + "))");

        // Insert sample products
        insertSampleProducts(db);

        // Insert default admin account
        ContentValues adminCv = new ContentValues();
        adminCv.put(COL_USER_NAME, "Admin");
        adminCv.put(COL_USER_EMAIL, "admin@shop.com");
        adminCv.put(COL_USER_PASSWORD, "admin123");
        adminCv.put(COL_USER_PHONE, "0000000000");
        db.insert(TABLE_USERS, null, adminCv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void insertSampleProducts(SQLiteDatabase db) {
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
                        "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&q=80", "4.9", "10"},
                {"Adidas Ultraboost 23", "Performance running shoes with Boost midsole technology", "15999", "Footwear",
                        "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400&q=80", "4.6", "40"},
                {"The Alchemist - Book", "Paulo Coelho's masterpiece about following your dreams", "399", "Books",
                        "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&q=80", "4.8", "200"},
                {"Instant Pot Duo 7-in-1", "Versatile electric pressure cooker for quick healthy meals", "8999", "Kitchen",
                        "https://images.unsplash.com/photo-1585515320310-259814833e62?w=400&q=80", "4.7", "25"},
                {"Dyson V15 Detect", "Laser-guided cordless vacuum cleaner with powerful suction", "49999", "Home",
                        "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&q=80", "4.8", "12"},
                {"Ray-Ban Aviator", "Classic aviator sunglasses with UV400 polarized lenses", "9999", "Accessories",
                        "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=400&q=80", "4.6", "60"},
                {"Fitbit Charge 6", "Advanced fitness tracker with GPS, heart rate & sleep monitoring", "19999", "Electronics",
                        "https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?w=400&q=80", "4.4", "35"},
        };

        for (String[] p : products) {
            ContentValues cv = new ContentValues();
            cv.put(COL_PRODUCT_NAME, p[0]);
            cv.put(COL_PRODUCT_DESC, p[1]);
            cv.put(COL_PRODUCT_PRICE, Double.parseDouble(p[2]));
            cv.put(COL_PRODUCT_CATEGORY, p[3]);
            cv.put(COL_PRODUCT_IMAGE, p[4]);
            cv.put(COL_PRODUCT_RATING, Double.parseDouble(p[5]));
            cv.put(COL_PRODUCT_STOCK, Integer.parseInt(p[6]));
            db.insert(TABLE_PRODUCTS, null, cv);
        }
    }

    // =================== USER METHODS ===================
    public long registerUser(String name, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NAME, name);
        cv.put(COL_USER_EMAIL, email);
        cv.put(COL_USER_PASSWORD, password);
        cv.put(COL_USER_PHONE, phone);
        long result = db.insert(TABLE_USERS, null, cv);
        db.close();
        return result;
    }

    public Cursor loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{email, password});
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=?", new String[]{email});
        boolean exists = c.getCount() > 0;
        c.close();
        db.close();
        return exists;
    }

    // =================== PRODUCT METHODS ===================
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);
        if (c.moveToFirst()) {
            do {
                list.add(cursorToProduct(c));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_PRODUCT_CATEGORY + "=?", new String[]{category});
        if (c.moveToFirst()) {
            do {
                list.add(cursorToProduct(c));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public List<Product> searchProducts(String query) {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS +
                " WHERE " + COL_PRODUCT_NAME + " LIKE ? OR " + COL_PRODUCT_CATEGORY + " LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"});
        if (c.moveToFirst()) {
            do {
                list.add(cursorToProduct(c));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public Product getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_PRODUCT_ID + "=?",
                new String[]{String.valueOf(productId)});
        Product p = null;
        if (c.moveToFirst()) {
            p = cursorToProduct(c);
        }
        c.close();
        db.close();
        return p;
    }

    public long addProduct(String name, String description, double price,
                           String category, String imageUrl, double rating, int stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PRODUCT_NAME, name);
        cv.put(COL_PRODUCT_DESC, description);
        cv.put(COL_PRODUCT_PRICE, price);
        cv.put(COL_PRODUCT_CATEGORY, category);
        cv.put(COL_PRODUCT_IMAGE, imageUrl);
        cv.put(COL_PRODUCT_RATING, rating);
        cv.put(COL_PRODUCT_STOCK, stock);
        long id = db.insert(TABLE_PRODUCTS, null, cv);
        db.close();
        return id;
    }

    public void deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COL_PRODUCT_ID + "=?", new String[]{String.valueOf(productId)});
        db.close();
    }

    private Product cursorToProduct(Cursor c) {
        return new Product(
                c.getInt(c.getColumnIndexOrThrow(COL_PRODUCT_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_PRODUCT_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_PRODUCT_DESC)),
                c.getDouble(c.getColumnIndexOrThrow(COL_PRODUCT_PRICE)),
                c.getString(c.getColumnIndexOrThrow(COL_PRODUCT_CATEGORY)),
                c.getString(c.getColumnIndexOrThrow(COL_PRODUCT_IMAGE)),
                c.getDouble(c.getColumnIndexOrThrow(COL_PRODUCT_RATING)),
                c.getInt(c.getColumnIndexOrThrow(COL_PRODUCT_STOCK))
        );
    }

    // =================== CART METHODS ===================
    public void addToCart(int userId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CART +
                " WHERE " + COL_CART_USER_ID + "=? AND " + COL_CART_PRODUCT_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        if (c.moveToFirst()) {
            int qty = c.getInt(c.getColumnIndexOrThrow(COL_CART_QUANTITY));
            db.execSQL("UPDATE " + TABLE_CART + " SET " + COL_CART_QUANTITY + "=" + (qty + 1) +
                    " WHERE " + COL_CART_USER_ID + "=" + userId + " AND " + COL_CART_PRODUCT_ID + "=" + productId);
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COL_CART_USER_ID, userId);
            cv.put(COL_CART_PRODUCT_ID, productId);
            cv.put(COL_CART_QUANTITY, 1);
            db.insert(TABLE_CART, null, cv);
        }
        c.close();
        db.close();
    }

    public void removeFromCart(int userId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COL_CART_USER_ID + "=? AND " + COL_CART_PRODUCT_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});
        db.close();
    }

    public void updateCartQuantity(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (quantity <= 0) {
            db.delete(TABLE_CART, COL_CART_USER_ID + "=? AND " + COL_CART_PRODUCT_ID + "=?",
                    new String[]{String.valueOf(userId), String.valueOf(productId)});
        } else {
            ContentValues cv = new ContentValues();
            cv.put(COL_CART_QUANTITY, quantity);
            db.update(TABLE_CART, cv, COL_CART_USER_ID + "=? AND " + COL_CART_PRODUCT_ID + "=?",
                    new String[]{String.valueOf(userId), String.valueOf(productId)});
        }
        db.close();
    }

    public List<CartItem> getCartItems(int userId) {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT c.*, p." + COL_PRODUCT_NAME + ", p." + COL_PRODUCT_PRICE +
                ", p." + COL_PRODUCT_IMAGE + " FROM " + TABLE_CART + " c JOIN " + TABLE_PRODUCTS +
                " p ON c." + COL_CART_PRODUCT_ID + "=p." + COL_PRODUCT_ID +
                " WHERE c." + COL_CART_USER_ID + "=?", new String[]{String.valueOf(userId)});
        if (c.moveToFirst()) {
            do {
                CartItem item = new CartItem(
                        c.getInt(c.getColumnIndexOrThrow(COL_CART_ID)),
                        c.getInt(c.getColumnIndexOrThrow(COL_CART_PRODUCT_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_PRODUCT_NAME)),
                        c.getDouble(c.getColumnIndexOrThrow(COL_PRODUCT_PRICE)),
                        c.getString(c.getColumnIndexOrThrow(COL_PRODUCT_IMAGE)),
                        c.getInt(c.getColumnIndexOrThrow(COL_CART_QUANTITY))
                );
                list.add(item);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public int getCartCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + COL_CART_QUANTITY + ") FROM " + TABLE_CART +
                " WHERE " + COL_CART_USER_ID + "=?", new String[]{String.valueOf(userId)});
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        db.close();
        return count;
    }

    public void clearCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COL_CART_USER_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    // =================== ORDER METHODS ===================
    public long placeOrder(int userId, double total, String address, List<CartItem> items) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ORDER_USER_ID, userId);
        cv.put(COL_ORDER_TOTAL, total);
        cv.put(COL_ORDER_STATUS, "Confirmed");
        cv.put(COL_ORDER_DATE, java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));
        cv.put(COL_ORDER_ADDRESS, address);
        long orderId = db.insert(TABLE_ORDERS, null, cv);

        for (CartItem item : items) {
            ContentValues oiCv = new ContentValues();
            oiCv.put(COL_OI_ORDER_ID, orderId);
            oiCv.put(COL_OI_PRODUCT_ID, item.getProductId());
            oiCv.put(COL_OI_QUANTITY, item.getQuantity());
            oiCv.put(COL_OI_PRICE, item.getPrice());
            db.insert(TABLE_ORDER_ITEMS, null, oiCv);
        }
        db.close();
        return orderId;
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ORDERS +
                " WHERE " + COL_ORDER_USER_ID + "=? ORDER BY " + COL_ORDER_ID + " DESC",
                new String[]{String.valueOf(userId)});
        if (c.moveToFirst()) {
            do {
                list.add(new Order(
                        c.getInt(c.getColumnIndexOrThrow(COL_ORDER_ID)),
                        c.getDouble(c.getColumnIndexOrThrow(COL_ORDER_TOTAL)),
                        c.getString(c.getColumnIndexOrThrow(COL_ORDER_STATUS)),
                        c.getString(c.getColumnIndexOrThrow(COL_ORDER_DATE)),
                        c.getString(c.getColumnIndexOrThrow(COL_ORDER_ADDRESS))
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    // =================== ADMIN METHODS ===================

    /** Returns all orders from all users, joined with user name & email */
    public List<AdminOrder> getAllOrdersWithUsers() {
        List<AdminOrder> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT o.*, u." + COL_USER_NAME + " as user_name, u." + COL_USER_EMAIL +
                " as user_email FROM " + TABLE_ORDERS + " o JOIN " + TABLE_USERS +
                " u ON o." + COL_ORDER_USER_ID + " = u." + COL_USER_ID +
                " ORDER BY o." + COL_ORDER_ID + " DESC";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                list.add(new AdminOrder(
                        c.getInt(c.getColumnIndexOrThrow(COL_ORDER_ID)),
                        c.getString(c.getColumnIndexOrThrow("user_name")),
                        c.getString(c.getColumnIndexOrThrow("user_email")),
                        c.getDouble(c.getColumnIndexOrThrow(COL_ORDER_TOTAL)),
                        c.getString(c.getColumnIndexOrThrow(COL_ORDER_STATUS)),
                        c.getString(c.getColumnIndexOrThrow(COL_ORDER_DATE)),
                        c.getString(c.getColumnIndexOrThrow(COL_ORDER_ADDRESS))
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ORDER_STATUS, newStatus);
        db.update(TABLE_ORDERS, cv, COL_ORDER_ID + "=?", new String[]{String.valueOf(orderId)});
        db.close();
    }

    public int getTotalUsersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        db.close();
        return count;
    }

    public int getTotalOrdersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ORDERS, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        db.close();
        return count;
    }

    public double getTotalRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(" + COL_ORDER_TOTAL + ") FROM " + TABLE_ORDERS, null);
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close();
        db.close();
        return total;
    }
}
