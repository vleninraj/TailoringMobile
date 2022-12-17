package com.atlanta.tailoringmobile.Models;

public class JobCardDTL {

    private String id;
    private  int ProductID;
    private String ProductName;
    private Double Qty;
    private Double Rate;
    private String SubPartyCode;
    private String SubPartyName;

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }



    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public Double getQty() {
        return Qty;
    }

    public void setQty(Double qty) {
        Qty = qty;
    }

    public Double getRate() {
        return Rate;
    }

    public void setRate(Double rate) {
        Rate = rate;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubPartyName() {
        return SubPartyName;
    }

    public void setSubPartyName(String subPartyName) {
        SubPartyName = subPartyName;
    }

    public String getSubPartyCode() {
        return SubPartyCode;
    }

    public void setSubPartyCode(String subPartyCode) {
        SubPartyCode = subPartyCode;
    }
}
