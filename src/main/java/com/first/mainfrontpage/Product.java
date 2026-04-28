package com.first.mainfrontpage;

public class Product {
    private String productID;
    private String productName;
    private String type;
    private  String price;
    private String status;
    private String date;

    public Product(String productID,String productName,String type,String status,String date){
        this.productID=productID;
        this.productName=productName;
        this.type=type;
        this.status=status;
        this.date=date;
    }
}
