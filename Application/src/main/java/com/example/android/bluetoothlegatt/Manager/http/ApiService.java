package com.example.android.bluetoothlegatt.Manager.http;

import com.example.android.bluetoothlegatt.Dao.PatientItemDao;
import com.example.android.bluetoothlegatt.Dao.WatjaiNormal;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

/**
 * Created by SirisakPks on 24/8/2560.
 */

public interface ApiService {

    @GET("patients/PA1708001")
    Call<PatientItemDao> loadPatient();

    @PATCH("patients/PA1708001" )
    @Headers({"Content-Type: application/json"})
    Call<PatientItemDao> updatePatient(@Body PatientItemDao dao);

    @POST("watjainormal" )
    @Headers({"Content-Type: application/json"})
    Call<WatjaiNormal> insertECG(@Body WatjaiNormal ecg);
}
