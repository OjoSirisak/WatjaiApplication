package com.example.android.bluetoothlegatt.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.View.NotificationListItem;
import com.google.gson.Gson;

import java.util.ArrayList;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class NotificationListManager {

    private Context mContext;
    private ArrayList<WatjaiMeasure> dao;

    public NotificationListManager() {
        mContext = Contextor.getInstance().getContext();
        loadCache();
    }

    public ArrayList<WatjaiMeasure> getDao() {
        return dao;
    }

    public void setDao(ArrayList<WatjaiMeasure> dao) {
        this.dao = dao;
        saveCache();
    }

    public void insertDaoTopPosition(ArrayList<WatjaiMeasure> newDao) {
        if (dao==null)
            dao = new ArrayList<WatjaiMeasure>();
        dao.addAll(dao.size(), newDao);
        saveCache();
    }

    public String getMaximumId() {
        if (dao == null)
            return "";
        if (dao.size() == 0)
            return "";

        String maxId = dao.get(0).getMeasuringId();
        maxId.substring(0,2);
        int max = Integer.parseInt(maxId);
        int maximum = max;
        for (int i=1; i<dao.size(); i++) {
            maxId = dao.get(i).getMeasuringId();
            maxId.substring(0,2);
            max = Integer.parseInt(maxId);
            maximum = Math.max(maximum, max);
        }
        return "ME"+maximum;
    }

    public int getCount() {
        if (dao == null)
            return 0;
        return dao.size();
    }

    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("dao", dao);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle saveInstanceState) {
        dao = saveInstanceState.getParcelableArrayList("dao");
    }

    private void saveCache() {
        ArrayList<WatjaiMeasure> cacheDao = new ArrayList<WatjaiMeasure>();
        if (dao!= null)
            cacheDao.addAll(0, dao);
        String json = new Gson().toJson(cacheDao);
        SharedPreferences prefs = mContext.getSharedPreferences(
                "notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("json", json);
        editor.apply();
    }

    private void loadCache() {
        SharedPreferences prefs = mContext.getSharedPreferences(
                "notification", Context.MODE_PRIVATE);
        String json = prefs.getString("json", null);
        if (json == null)
            return;
        dao = new Gson().fromJson(json, WatjaiMeasure.class);
    }
}
