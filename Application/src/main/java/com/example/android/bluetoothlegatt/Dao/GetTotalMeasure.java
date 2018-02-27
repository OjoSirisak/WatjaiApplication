package com.example.android.bluetoothlegatt.Dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by SirisakPks on 23/2/2561.
 */

public class GetTotalMeasure {
    @SerializedName("total")
    @Expose
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
