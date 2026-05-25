package com.first.mainfrontpage;
import javafx.beans.property.SimpleStringProperty;

public class Salesman {
    private final SimpleStringProperty email;
    private final SimpleStringProperty mobile;
    private final SimpleStringProperty joinedOn;
    private final SimpleStringProperty status;
    private final SimpleStringProperty lastLogin;

    public Salesman(String email, String mobile, String joinedOn, String status, String lastLogin) {
        this.email = new SimpleStringProperty(email);
        this.mobile = new SimpleStringProperty(mobile);
        this.joinedOn = new SimpleStringProperty(joinedOn);
        this.status = new SimpleStringProperty(status);
        this.lastLogin = new SimpleStringProperty(lastLogin);
    }

    public String getEmail(){
        return email.get();
    }
    public String getMobile(){
        return mobile.get();
    }
    public String getJoinedOn(){
        return joinedOn.get();
    }
    public String getStatus(){
        return status.get();
    }
    public String getLastLogin(){
        return lastLogin.get();
    }

    public SimpleStringProperty emailProperty(){
        return email;
    }
    public SimpleStringProperty mobileProperty(){
        return mobile;
    }
    public SimpleStringProperty joinedOnProperty(){
        return joinedOn;
    }
    public SimpleStringProperty statusProperty(){
        return status;
    }
    public SimpleStringProperty lastLoginProperty(){
        return lastLogin;
    }
}