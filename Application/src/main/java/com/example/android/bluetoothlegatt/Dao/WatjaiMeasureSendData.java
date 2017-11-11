package com.example.android.bluetoothlegatt.Dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by SirisakPks on 27/8/2560.
 */

public class WatjaiMeasureSendData {


    @SerializedName("measuringData")
    @Expose
    private ArrayList<Float> measuringData = null;
    @SerializedName("patId")
    @Expose
    private String patId;

    public WatjaiMeasureSendData() {

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


}
