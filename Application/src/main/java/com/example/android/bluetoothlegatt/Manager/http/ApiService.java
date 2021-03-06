package com.example.android.bluetoothlegatt.Manager.http;

import com.example.android.bluetoothlegatt.Dao.GetTotalMeasure;
import com.example.android.bluetoothlegatt.Dao.Login;
import com.example.android.bluetoothlegatt.Dao.PatientItemDao;
import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.Dao.WatjaiMeasureSendData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by SirisakPks on 24/8/2560.
 */

public interface ApiService {

    @GET("patients/{patId}")
    Call<PatientItemDao> loadPatient(@Path("patId") String id);

    @PATCH("patients/{patId}" )
    @Headers({"Content-Type: application/json"})
    Call<PatientItemDao> updatePatient(@Body PatientItemDao dao, @Path("patId") String id);

    @POST("watjaimeasure" )
    @Headers({"Content-Type: application/json"})
    Call<WatjaiMeasureSendData> insertECGtoDetecting(@Body WatjaiMeasureSendData ecg);

    @GET("patients/{patId}/history")
    Call<ArrayList<WatjaiMeasure>> loadHistory(@Path("patId") String id);

    @GET("watjaimeasure/showabnormal/{patId}/after/{measuringId}")
    Call<ArrayList<WatjaiMeasure>> loadWatjaiMeasureAlert(@Path("patId") String patId, @Path("measuringId") String measuringId);

    @GET("watjaimeasure/getTotalAbnormal/{patId}")
    Call<GetTotalMeasure> totalMeasureAlert(@Path("patId") String patId);

    @GET("watjaimeasure/changereadstatus/{measuringId}")
    @Headers({"Content-Type: application/json"})
    Call<Object> changeReadStatus(@Path("measuringId") String measuringId);

    @GET("watjaimeasure/{measuringId}")
    Call<ArrayList<WatjaiMeasure>> findMeasuring(@Path("measuringId") String id);

    @POST("checkuser/patient")
    @Headers({"Content-Type: application/json"})
    Call<Login> checkLogin(@Body Login dao);
}
