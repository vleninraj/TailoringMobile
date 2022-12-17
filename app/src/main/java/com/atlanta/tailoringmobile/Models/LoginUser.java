package com.atlanta.tailoringmobile.Models;

public class LoginUser {
    private int id;
    private String UserName;
    private String Password;
    private String PinNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPinNumber() {
        return PinNumber;
    }

    public void setPinNumber(String pinNumber) {
        PinNumber = pinNumber;
    }
    @Override
    public String toString() {
        return UserName;
    }
}
