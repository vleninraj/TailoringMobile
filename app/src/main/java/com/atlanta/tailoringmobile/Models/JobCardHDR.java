package com.atlanta.tailoringmobile.Models;

public class JobCardHDR {
    private String VoucherNo;
    private String OrderDate;
    private String PartyName;
    private Double GrandAmount;
    private String Status;
    public String getVoucherNo() {
        return VoucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        VoucherNo = voucherNo;
    }



    public String getPartyName() {
        return PartyName;
    }

    public void setPartyName(String partyName) {
        PartyName = partyName;
    }

    public Double getGrandAmount() {
        return GrandAmount;
    }

    public void setGrandAmount(Double grandAmount) {
        GrandAmount = grandAmount;
    }

    public String isStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }
}
