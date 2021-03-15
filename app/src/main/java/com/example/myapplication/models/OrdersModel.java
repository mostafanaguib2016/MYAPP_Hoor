package com.example.myapplication.models;

public class OrdersModel
{
    private String id;
    private String deliveryFee;
    private String ownerId;
    private String buyerId;
    private String latitude;
    private String longitude;
    private String buyerName;
    private String buyerPhone;
    private String shopName;
    private String timeStamp;
    private String productId;
    private String originalPrice;
    private String productTitle;

    public OrdersModel() {
    }

    public OrdersModel(String deliveryFee, String ownerId, String buyerId, String latitude
            , String longitude, String buyerName, String buyerPhone, String shopName,
                       String timeStamp, String originalPrice, String productId, String productTitle) {
        this.deliveryFee = deliveryFee;
        this.ownerId = ownerId;
        this.buyerId = buyerId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.buyerName = buyerName;
        this.buyerPhone = buyerPhone;
        this.shopName = shopName;
        this.timeStamp = timeStamp;
        this.productId = productId;
        this.originalPrice = originalPrice;
        this.productTitle = productTitle;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
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

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
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

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
