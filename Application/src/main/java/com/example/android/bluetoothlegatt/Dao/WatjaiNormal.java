package com.example.android.bluetoothlegatt.Dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by SirisakPks on 27/8/2560.
 */

public class WatjaiNormal {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("measuringData")
    @Expose
    private ArrayList<Float> measuringData = null;
    @SerializedName("patId")
    @Expose
    private String patId;
    @SerializedName("measuringTime")
    @Expose
    private String measuringTime;
    @SerializedName("heartRate")
    @Expose
    private Integer heartRate;
    @SerializedName("measuringId")
    @Expose
    private String measuringId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Float> getMeasuringData() {
        return measuringData;
    }

    public void setMeasuringData(ArrayList<Float> measuringData) {
        this.measuringData = measuringData;
    }

    public String getPatId() {
        return patId;
    }

    public void setPatId(String patId) {
        this.patId = patId;
    }

    public String getMeasuringTime() {
        return measuringTime;
    }

    public void setMeasuringTime(String measuringTime) {
        this.measuringTime = measuringTime;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public String getMeasuringId() {
        return measuringId;
    }

    public void setMeasuringId(String measuringId) {
        this.measuringId = measuringId;
    }

}
