package com.example.user.androidproject;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private static final long serialVersionUID = 1000000L;

    public String storeId;
    public String storePw;
    public String idPw;
    public String email;
    public String idTaxNoEmail;
    public String taxNoEmail;
    public String phone;
    public String storeName;
    public String category;
    public String logo;
    public String taxNo;
    public String storeNameCategory;
    public int floor;

    public String getStoreNameCategory() {
        return storeNameCategory;
    }

    public void setStoreNameCategory(String storeNameCategory) {
        this.storeNameCategory = storeNameCategory;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public String getIdTaxNoEmail() {
        return idTaxNoEmail;
    }

    public void setIdTaxNoEmail(String idTaxNoEmail) {
        this.idTaxNoEmail = idTaxNoEmail;
    }

    public String getTaxNoEmail() {
        return taxNoEmail;
    }

    public void setTaxNoEmail(String taxNoEmail) {
        this.taxNoEmail = taxNoEmail;
    }

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

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public User(String storeId, String storePw, String idPw, String taxNo, String email, String taxNoEmail, String idTaxNoEmail, String phone, String category, String storeName, int floor,String logo,String storeNameCategory) {
        this.storeId = storeId;
        this.storePw = storePw;
        this.idPw = idPw;
        this.taxNo = taxNo;
        this.email = email;
        this.taxNoEmail = taxNoEmail;
        this.idTaxNoEmail = idTaxNoEmail;
        this.phone = phone;
        this.category=category;
        this.storeName = storeName;
        this.floor = floor;
        this.logo = logo;
        this.storeNameCategory = storeNameCategory;
    }

    public User() {}
}