package com.example.user.androidproject;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Store {
    public String storeName;
    public String phone;
    public String category;
    public String storeNameCategory;
    public int imageView;

    public String getStoreNameCategory() {
        return storeNameCategory;
    }

    public void setStoreNameCategory(String storeNameCategory) {
        this.storeNameCategory = storeNameCategory;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Store(String storeName, String phone, String category,int imageView,String storeNameCategory) {
        this.storeName = storeName;
        this.phone = phone;
        this.category = category;
        this.imageView = imageView;
        this.storeNameCategory = storeNameCategory;
    }

    public int getImageView() {
        return imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public Store() {}
}