package com.example.user.androidproject;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Store {
    public String storeName;
    public String phone;
    public String category;

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

    public Store(String storeName, String phone, String category) {
        this.storeName = storeName;
        this.phone = phone;
        this.category = category;
    }

    public Store() {}
}