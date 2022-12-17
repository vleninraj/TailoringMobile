package com.atlanta.tailoringmobile.Models;

public class MeasurementDTL {

    private String id ;
    private int MeasurementID;
    private String MeasurementName;
    private Double MeasurementValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMeasurementID() {
        return MeasurementID;
    }

    public void setMeasurementID(int measurementID) {
        MeasurementID = measurementID;
    }

    public String getMeasurementName() {
        return MeasurementName;
    }

    public void setMeasurementName(String measurementName) {
        MeasurementName = measurementName;
    }


    public Double getMeasurementValue() {
        return MeasurementValue;
    }

    public void setMeasurementValue(Double measurementValue) {
        MeasurementValue = measurementValue;
    }
}
