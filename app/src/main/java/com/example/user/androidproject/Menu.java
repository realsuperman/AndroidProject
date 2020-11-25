package com.example.user.androidproject;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Menu implements Serializable {
    private static final long serialVersionUID = 1000000L;
    public String storeId;
    public int price;
    public String menuName;
    public String menuCode;
    public String logo;
    public String country;
    public String storeId_menuCode;
    public String storeIdMenuName;

    public String getStoreIdMenuName() {
        return storeIdMenuName;
    }

    public void setStoreIdMenuName(String storeIdMenuName) {
        this.storeIdMenuName = storeIdMenuName;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStoreId_menuCode() {
        return storeId_menuCode;
    }

    public void setStoreId_menuCode(String storeId_menuCode) {
        this.storeId_menuCode = storeId_menuCode;
    }

    public Menu(String storeId, int price, String menuName, String menuCode, String logo, String country, String storeId_menuCode,String storeIdMenuName) {
        this.storeId = storeId;
        this.price=price;
        this.menuName=menuName;
        this.menuCode=menuCode;
        this.country=country;
        this.storeId_menuCode=storeId_menuCode;
        this.logo = logo;
        this.storeIdMenuName = storeIdMenuName;
    }

    public Menu() {}
}