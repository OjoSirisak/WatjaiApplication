package com.example.android.bluetoothlegatt.Dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SirisakPks on 27/8/2560.
 */

public class WatjaiNormal {

    @SerializedName("measureData")
    @Expose
    private ArrayList<Float> measureData = null;

    @SerializedName("patId")
    @Expose
    private String patId;

    @SerializedName("measureData")

    public String getPatId() {
        return patId;
    }

    public void setPatId(String patId) {
        this.patId = patId;
    }

    public ArrayList<Float> getMeasureData() {
        return measureData;
    }

    public void setMeasureData(ArrayList<Float> measureData) {
        this.measureData = measureData;
    }

}
