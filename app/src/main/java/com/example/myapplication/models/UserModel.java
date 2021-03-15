package com.example.myapplication.models;

import android.app.Activity;

import java.util.Calendar;

public class UserModel
{

    private String id;
    private String deliveryFee;
    private String email;
    private String latitude;
    private String longitude;
    private String name;
    private String phone;
    private String profileImage;
    private String shopName;
    private String timeStamp;
    private String profileImageUrl;



    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getFireBaseToken() {
        return fireBaseToken;
    }

    public void setFireBaseToken(String fireBaseToken) {
        this.fireBaseToken = fireBaseToken;
    }

    private String fireBaseToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp() {
        this.timeStamp = Calendar.getInstance().getTime().toString();
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isShopOpen() {
        return shopOpen;
    }

    public void setShopOpen(boolean shopOpen) {
        this.shopOpen = shopOpen;
    }

    boolean online , shopOpen;

    public UserModel(String id, String deliveryFee, String email, String latitude, String longitude, String name
            , String phone, String profileImage, String shopName, String timeStamp,
                     boolean online, boolean shopOpen,String fireBaseToken) {
        this.id = id;
        this.deliveryFee = deliveryFee;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.phone = phone;
        this.profileImage = profileImage;
        this.shopName = shopName;
        this.timeStamp = timeStamp;
        this.online = online;
        this.shopOpen = shopOpen;
        this.fireBaseToken = fireBaseToken;
    }

    public UserModel(){}

}
