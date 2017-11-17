package com.example.android.bluetoothlegatt.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.Dao.Login;
import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.Manager.HttpManager;
import com.example.android.bluetoothlegatt.R;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class LoginActivity extends AppCompatActivity {
    private Login login;
    private Button btnLogin;
    private EditText edtUsername, edtPassword;
    private String username, password;
    private String checkInput;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);







        onClickButton();
    }

    private void setDataLogin() {
        if (login == null)
            login = new Login();

        /*login.setPatId(null);
        login.setStatus(null);
        login.setSuccess(null);*/
        login.setUsername(username);
        login.setPassword(password);
    }

    private void onClickButton() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = edtUsername.getText().toString();
                password = edtPassword.getText().toString();
                if  (username.length() == 0 && password.length() == 0) {
                    checkInput = "กรุณากรอกชื่อผู้ใช้งานและรหัสผ่าน";
                    Toast.makeText(LoginActivity.this, checkInput, Toast.LENGTH_LONG).show();
                } else if (username.length() == 0 || password.length() == 0) {
                    if (username.length() == 0) {
                        checkInput = "กรุณากรอกชื่อผู้ใช้งาน";
                        Toast.makeText(LoginActivity.this, checkInput, Toast.LENGTH_LONG).show();
                    } else {
                        checkInput = "กรุณากรอกรหัสผ่าน";
                        Toast.makeText(LoginActivity.this, checkInput, Toast.LENGTH_LONG).show();
                    }
                } else if (username.length() < 8 && password.length() < 9) {
                    checkInput = "กรุณากรอกชื่อผู้ใช้งานไม่น้อยกว่า 9 ตัวอักษร \nและ \nรหัสผ่านให้ครบ 10 ตัวอักษร";
                    Toast.makeText(LoginActivity.this, checkInput, Toast.LENGTH_LONG).show();
                } else if (username.length() < 8 || password.length() < 9) {
                    if (username.length() < 8) {
                        checkInput = "กรุณากรอกชื่อผู้ใช้งานไม่น้อยกว่า 9 ตัวอักษร";
                        Toast.makeText(LoginActivity.this, checkInput, Toast.LENGTH_LONG).show();
                    }
                    else {
                        checkInput = "กรุณากรอกรหัสผ่านให้ครบ 10 ตัวอักษร";
                        Toast.makeText(LoginActivity.this, checkInput, Toast.LENGTH_LONG).show();
                    }
                } else {
                    setDataLogin();
                   NetworkCall call = new NetworkCall();
                    call.execute();

                   /* Call<Login> call = HttpManager.getInstance().getService().checkLogin(login);
                    call.enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if (response.isSuccessful()) {
                                login = response.body();
                                prefs = getSharedPreferences("loginStatus", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();

                                if(login.getSuccess().equals("success")){
                                    editor.putInt("LoginStatus",0);
                                    editor.putString("PatID",login.getPatId());
                                }

                                editor.apply();

                                prefs = getSharedPreferences("loginStatus", MODE_PRIVATE);
                                int status = prefs.getInt("LoginStatus", 1);
                                if(status != 1){
                                    Toast.makeText(LoginActivity.this,"yes", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(LoginActivity.this,"no" , Toast.LENGTH_LONG).show();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<Login> call, Throwable throwable) {

                        }
                    });*/
                    //pa1709001
                    //0858388229
                                      /* if(login.getSuccess()){
                        Toast.makeText(LoginActivity.this, login.getPatId(), Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "ชื่อผู้ใช้งาน/รหัส ไม่ถูกต้อง", Toast.LENGTH_LONG).show();
                    }


                    prefs = getSharedPreferences("loginStatus", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    if(login.getSuccess().equals("success")){
                        editor.putInt("LoginStatus",0);
                        editor.putString("PatID",login.getPatId());
                    }

                    editor.apply();

                    prefs = getSharedPreferences("loginStatus", MODE_PRIVATE);
                    int status = prefs.getInt("LoginStatus", 1);
                    if(status != 1){
                        Toast.makeText(LoginActivity.this,"yes", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(LoginActivity.this,"no" , Toast.LENGTH_LONG).show();
                    }


                */}


                /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("loginStatus", true);
                startActivity(intent);
                finish();*/

            }
        });
    }

    private class NetworkCall extends AsyncTask<Call, Integer, Login> {

        @Override
        protected void onPostExecute(Login result) {
            login = result;

            prefs = getSharedPreferences("loginStatus", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            if (login != null && result != null) {
                if(result.getSuccess()){
                    System.out.println("onPost : "+result.getSuccess());
                    editor.putInt("LoginStatus",0);
                    editor.putString("PatID",login.getPatId());
                }
            }

            editor.apply();


            int status = prefs.getInt("LoginStatus", 1);
            if(status != 1){
                //Toast.makeText(LoginActivity.this,"yes", Toast.LENGTH_LONG).show();
                System.out.println(login.getPatId());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                if (login != null && result != null)
                    Toast.makeText(LoginActivity.this, login.getStatus() , Toast.LENGTH_LONG).show();

            }
        }

        @Override
        protected Login doInBackground(Call... params) {

            try {
                Call<Login> call = HttpManager.getInstance().getService().checkLogin(login);
                Response<Login> response = call.execute();
                login = response.body();
                return login;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
