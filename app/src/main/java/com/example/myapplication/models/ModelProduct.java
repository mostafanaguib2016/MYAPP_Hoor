package com.example.myapplication.models;


public class ModelProduct {
    private String productId;
    private String productTitle;
    private String productDescription;
    private String productCategory;
    private String productQuantity;
    private String productIcon;
    private String originalPrice;
    private String timestamp;
    private String userId;
    private String userImage;

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    public String getProductIconUrl() {
        return productIconUrl;
    }

    public void setProductIconUrl(String productIconUrl) {
        this.productIconUrl = productIconUrl;
    }

    private String productIconUrl;

    public ModelProduct(){

    }

    public ModelProduct (String productTitle,String productDescription
            ,String productCategory,String productQuantity,String productIcon,
                         String originalPrice,String timestamp,String userId,String userName,String userImage){

        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productQuantity = productQuantity;
        this.productIcon = productIcon;
        this.originalPrice = originalPrice;
        this.timestamp = timestamp;
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
    }

    public String getProductId(){
        return productId;
    }

    public void setProductId(String productId){
        this.productId = productId;
    }

    public String getProductTitle(){
        return productTitle;
    }

    public void setProductTitle(String productTitle){
        this.productTitle = productTitle;
    }

    public String getProductDescription(){
        return productDescription;
    }

    public void setProductDescription(String productDescription){
        this.productDescription = productDescription;
    }

    public String getProductCategory(){
        return productCategory;
    }

    public void setProductCategory(String productCategory){
        this.productCategory = productCategory;
    }

    public String getProductQuantity(){
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity){
        this.productQuantity = productQuantity;
    }

    public String getProductIcon(){
        return productIcon;
    }

    public void setProductIcon(String productIcon){
        this.productIcon = productIcon;
    }

    public String getOriginalPrice(){
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice){
        this.originalPrice = originalPrice;
    }


    public String getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String uid){
        this.userId = uid;
    }

}
