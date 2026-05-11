package com.first.mainfrontpage;

public class Customer {
    private String customerId;
    private double total;
    private String date;
    private String cashier;

    public Customer(String customerId,double total,String date,String cashier){
        this.customerId=customerId;
        this.total=total;
        this.date=date;
        this.cashier=cashier;
    }

    public String getCustomerId(){return customerId;}
    public double getTotal(){return total;}
    public String getDate(){return date;}
    public String getCashier(){return cashier;}

}
