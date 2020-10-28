package com.example.user.androidproject;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String storeId;
    public String storePw;
    public String idPw;
    public String email;
    public int taxNo;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStorePw() {
        return storePw;
    }

    public void setStorePw(String storePw) {
        this.storePw = storePw;
    }

    public String getIdPw() {
        return idPw;
    }

    public void setIdPw(String idPw) {
        this.idPw = idPw;
    }

    public int getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(int taxNo) {
        this.taxNo = taxNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String storeId, String storePw, String idPw, int taxNo, String email) {
        this.storeId = storeId;
        this.storePw = storePw;
        this.idPw = idPw;
        this.taxNo = taxNo;
        this.email = email;
    }

    public User() {}
}