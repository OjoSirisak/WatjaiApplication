package com.example.android.bluetoothlegatt.Dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by SirisakPks on 27/8/2560.
 */

public class WatjaiMeasure implements Parcelable {

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
    @SerializedName("abnormalStatus")
    @Expose
    private Boolean abnormalStatus;
    @SerializedName("abnormalDetail")
    @Expose
    private String abnormalDetail;
    @SerializedName("measuringId")
    @Expose
    private String measuringId;
    @SerializedName("readStatus")
    @Expose
    private Boolean readStatus;
    @SerializedName("commentStatus")
    @Expose
    private Boolean commentStatus;
    @SerializedName("comment")
    @Expose
    private String comment;

    public WatjaiMeasure(Parcel in) {
        id = in.readString();
        patId = in.readString();
        measuringTime = in.readString();
        if (in.readByte() == 0) {
            heartRate = null;
        } else {
            heartRate = in.readInt();
        }
        byte tmpAbnormalStatus = in.readByte();
        abnormalStatus = tmpAbnormalStatus == 0 ? null : tmpAbnormalStatus == 1;
        abnormalDetail = in.readString();
        measuringId = in.readString();
        byte tmpReadStatus = in.readByte();
        readStatus = tmpReadStatus == 0 ? null : tmpReadStatus == 1;
        byte tmpCommentStatus = in.readByte();
        commentStatus = tmpCommentStatus == 0 ? null : tmpCommentStatus == 1;
        measuringData = (ArrayList<Float>) in.readSerializable();
        comment = in.readString();
    }

    public WatjaiMeasure() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(patId);
        dest.writeString(measuringTime);
        if (heartRate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(heartRate);
        }
        dest.writeByte((byte) (abnormalStatus == null ? 0 : abnormalStatus ? 1 : 2));
        dest.writeString(abnormalDetail);
        dest.writeString(measuringId);
        dest.writeByte((byte) (readStatus == null ? 0 : readStatus ? 1 : 2));
        dest.writeByte((byte) (commentStatus == null ? 0 : commentStatus ? 1 : 2));
        dest.writeSerializable(measuringData);
        dest.writeString(comment);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WatjaiMeasure> CREATOR = new Creator<WatjaiMeasure>() {
        @Override
        public WatjaiMeasure createFromParcel(Parcel in) {
            return new WatjaiMeasure(in);
        }

        @Override
        public WatjaiMeasure[] newArray(int size) {
            return new WatjaiMeasure[size];
        }
    };

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

    public Boolean getAbnormalStatus() {
        return abnormalStatus;
    }

    public void setAbnormalStatus(Boolean abnormalStatus) {
        this.abnormalStatus = abnormalStatus;
    }

    public String getAbnormalDetail() {
        return abnormalDetail;
    }

    public void setAbnormalDetail(String abnormalDetail) {
        this.abnormalDetail = abnormalDetail;
    }

    public String getMeasuringId() {
        return measuringId;
    }

    public void setMeasuringId(String measuringId) {
        this.measuringId = measuringId;
    }

    public Boolean getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Boolean readStatus) {
        this.readStatus = readStatus;
    }

    public Boolean getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(Boolean commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
