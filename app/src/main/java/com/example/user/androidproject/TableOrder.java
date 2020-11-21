package com.example.user.androidproject;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class TableOrder implements Serializable {
    private static final long serialVersionUID = 1000000L;
    private String customerPhone;
    private String orderYn;
    private String seatExplain;
    private String seatName;
    private String storeIdFloor;
    private String orderCode;
    private int floor;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getOrderYn() {
        return orderYn;
    }

    public void setOrderYn(String orderYn) {
        this.orderYn = orderYn;
    }

    public String getSeatExplain() {
        return seatExplain;
    }

    public void setSeatExplain(String seatExplain) {
        this.seatExplain = seatExplain;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String getStoreIdFloor() {
        return storeIdFloor;
    }

    public void setStoreIdFloor(String storeIdFloor) {
        this.storeIdFloor = storeIdFloor;
    }

    public TableOrder(String customerPhone,String orderYn,String seatExplain,String seatName,String storeIdFloor,int floor,String orderCode) {
        this.customerPhone = customerPhone;
        this.orderYn=orderYn;
        this.seatExplain=seatExplain;
        this.seatName=seatName;
        this.storeIdFloor=storeIdFloor;
        this.floor = floor;
        this.orderCode = orderCode;
    }

    public TableOrder() {}
}