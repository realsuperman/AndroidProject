package com.example.user.androidproject;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class TableOrder implements Serializable {
    private static final long serialVersionUID = 1000000L;
    public String customerPhone;
    public String orderYn;
    public String seatExplain;
    public String seatName;
    public String storeIdFloor;
    public int floor;

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

    public TableOrder(String customerPhone,String orderYn,String seatExplain,String seatName,String storeIdFloor,int floor) {
        this.customerPhone = customerPhone;
        this.orderYn=orderYn;
        this.seatExplain=seatExplain;
        this.seatName=seatName;
        this.storeIdFloor=storeIdFloor;
        this.floor = floor;
    }

    public TableOrder() {}
}