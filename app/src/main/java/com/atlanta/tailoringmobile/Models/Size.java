package com.atlanta.tailoringmobile.Models;

public class Size {
    private int id;
    private String Name;
    private String ShortName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String shortName) {
        ShortName = shortName;
    }
    public String toString() {
        return Name;
    }
}
