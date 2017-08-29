package com.example.android.bluetoothlegatt.Activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.Dao.Doctor;
import com.example.android.bluetoothlegatt.Dao.PatientItemDao;
import com.example.android.bluetoothlegatt.Manager.HttpManager;
import com.example.android.bluetoothlegatt.R;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvFirstName, tvLastName, tvAge, tvAddress, tvTel, tvBloodType, tvUnderlyingDisease, tvDoctor;
    Button btnEditProfile, btnLogout;
    PatientItemDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = null;
        setContentView(R.layout.activity_profile);

        initInstances();
    }

    private void initInstances() {
        findId();

        Call<PatientItemDao> call = HttpManager.getInstance().getService().loadPatient();
        call.enqueue(new Callback<PatientItemDao>() {
            @Override
            public void onResponse(Call<PatientItemDao> call,
                                   Response<PatientItemDao> response) {
                if (response.isSuccessful()) {
                    dao = response.body();
                    tvFirstName.setText(dao.getPatFirstName());
                    tvLastName .setText(dao.getPatLastName());
                    Date now = new Date();
                    Number age = now.getYear() - dao.getBirthDay().getYear();
                    tvAge.setText(age.toString()+" ปี");
                    tvAddress.setText(dao.getAddress()+" "+
                            dao.getSubDistrict()+" "+
                            dao.getDistrict()+" "+
                            dao.getProvince());
                    tvTel.setText(dao.getPatTel());
                    tvBloodType.setText(dao.getBloodType());

                    List<Doctor> a = dao.getDoctor();
                    String name = a.get(0).getDocFirstName()+ " " +a.get(0).getDocLastName();
                    tvUnderlyingDisease.setText(dao.getUnderlyingDisease());
                    tvDoctor.setText(name);
                } else {
                    try {
                        Toast.makeText(ProfileActivity.this,
                                response.errorBody().string(),
                                Toast.LENGTH_SHORT)
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PatientItemDao> call,
                                  Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        t.toString(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private  void findId() {
        tvFirstName = (TextView) findViewById(R.id.tvFirstName);
        tvLastName = (TextView) findViewById(R.id.tvLastName);
        tvAge = (TextView) findViewById(R.id.tvAge);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvTel = (TextView) findViewById(R.id.tvTel);
        tvBloodType = (TextView) findViewById(R.id.tvBloodType);
        tvUnderlyingDisease = (TextView) findViewById(R.id.tvUnderlyingDisease);
        tvDoctor = (TextView) findViewById(R.id.tvDoctor);

        btnEditProfile = (Button) findViewById(R.id.btnEditProfile);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        btnEditProfile.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v==btnEditProfile) {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("dao", dao);
            startActivity(intent);
            finish();
        }
    }
}
