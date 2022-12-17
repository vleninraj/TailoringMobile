package com.atlanta.tailoringmobile.Models;

public class Depo {

    private  int id;
    private String Code;
    private String Name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    @Override
    public String toString() {
        return Name;
    }
}
