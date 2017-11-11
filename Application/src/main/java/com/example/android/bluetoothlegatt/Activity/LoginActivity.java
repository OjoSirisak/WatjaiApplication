package com.example.android.bluetoothlegatt.Activity;

import android.content.Intent;
import android.os.AsyncTask;
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
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private Login login;
    private Button btnLogin;
    private EditText edtUsername, edtPassword;
    private String username, password;
    private Thread t;
    private String checkInput;

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
                } else if (username.length() == 0 || password.length() == 0) {
                    if (username.length() == 0)
                        checkInput = "กรุณากรอกชื่อผู้ใช้งาน";
                    else
                        checkInput = "กรุณากรอกรหัสผ่าน";
                } else if (username.length() < 8 && password.length() < 9) {
                    checkInput = "กรุณากรอกชื่อผู้ใช้งานไม่น้อยกว่า 9 ตัวอักษร \nและ \nรหัสผ่านให้ครบ 10 ตัวอักษร";
                } else if (username.length() < 8 || password.length() < 9) {
                    if (username.length() < 8)
                        checkInput = "กรุณากรอกชื่อผู้ใช้งานไม่น้อยกว่า 9 ตัวอักษร";
                    else
                        checkInput = "กรุณากรอกรหัสผ่านให้ครบ 10 ตัวอักษร";
                } else {
                    setDataLogin();
                    NetworkCall call = new NetworkCall();
                    call.execute();
                    //pa1709001
                    //0858388229
                    System.out.println(username + password);
                    System.out.println(login.getPatId());
                }


                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("loginStatus", true);
                startActivity(intent);
                finish();

            }
        });
    }

    private class NetworkCall extends AsyncTask<Call, Integer, Login> {

        @Override
        protected void onPostExecute(Login result) {
            login = result;
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
