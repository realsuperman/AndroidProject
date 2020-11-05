package com.example.user.androidproject;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class StoreGrade {
    public String storeId;
    public String phone;
    public String score;
    public String storeIdPhone;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getStoreIdPhone() {
        return storeIdPhone;
    }

    public void setStoreIdPhone(String storeIdPhone) {
        this.storeIdPhone = storeIdPhone;
    }

    public StoreGrade(String storeId,String phone,String score,String storeIdPhone) {
        this.storeId = storeId;
        this.phone=phone;
        this.score=score;
        this.storeIdPhone=storeIdPhone;
    }

    public StoreGrade() {}
}