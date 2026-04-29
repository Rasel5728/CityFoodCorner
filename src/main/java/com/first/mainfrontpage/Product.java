package com.first.mainfrontpage;

public class Product {
    private String productID;
    private String productName;
    private String type;
    private int stock;
    private Double price;
    private String status;
    private String date;

    //CONSTRUCTOR
    public Product(String productID,String productName,String type,int stock,Double price,String status,String date){
        this.productID=productID;
        this.productName=productName;
        this.type=type;
        this.stock=stock;
        this.price=price;
        this.status=status;
        this.date=date;
    }

    //GETTER METHOD
    public String getProductID(){
        return productID;
    }
    public String getProductName(){
        return productName;
    }
    public String getType(){
        return type;
    }
    public int getStock(){
        return stock;
    }
    public  Double getPrice(){
        return price;
    }
    public String getStatus(){
        return status;
    }
    public String getDate(){
        return date;
    }

    //SETTER METHOD
    public void setProductID(String productID){
        this.productID=productID;
    }
    public void setProductName(String productName){
        this.productName=productName;
    }
    public void setType(String type){
        this.type=type;
    }
    public void setStock(int stock){
        this.stock=stock;
    }
    public void setPrice(Double price){
        this.price=price;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public void setDate(String date){
        this.date=date;
    }

}