package com.example.myapplication.models;

public class OrdersModel
{
    private String orderId;
    private String orderTitle;
    private String orderDescription;
    private String orderCategory;
    private String orderQuantity;
    private String orderIcon;
    private String orderIconUrl;
    private String originalPrice;
    private String timestamp;
    private String userId;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public OrdersModel() {
    }

    public String getOrderIconUrl() {
        return orderIconUrl;
    }

    public void setOrderIconUrl(String orderIconUrl) {
        this.orderIconUrl = orderIconUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(String orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getOrderIcon() {
        return orderIcon;
    }

    public void setOrderIcon(String orderIcon) {
        this.orderIcon = orderIcon;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OrdersModel( String orderTitle, String orderDescription, String orderCategory
            , String orderQuantity, String orderIcon, String originalPrice
            , String timestamp, String userId,String userName) {
        this.orderTitle = orderTitle;
        this.orderDescription = orderDescription;
        this.orderCategory = orderCategory;
        this.orderQuantity = orderQuantity;
        this.orderIcon = orderIcon;
        this.originalPrice = originalPrice;
        this.timestamp = timestamp;
        this.userId = userId;
        this.userName = userName;
    }
}
