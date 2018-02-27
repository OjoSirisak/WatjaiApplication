package com.example.android.bluetoothlegatt.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.Activity.BlankNotificationActivity;
import com.example.android.bluetoothlegatt.Activity.HelpActivity;
import com.example.android.bluetoothlegatt.Activity.HistoryActivity;
import com.example.android.bluetoothlegatt.Activity.NotificationActivity;
import com.example.android.bluetoothlegatt.Activity.ProfileActivity;
import com.example.android.bluetoothlegatt.Activity.SettingActivity;
import com.example.android.bluetoothlegatt.Bluetooth.DeviceControlActivity;
import com.example.android.bluetoothlegatt.Bluetooth.DeviceScanActivity;
import com.example.android.bluetoothlegatt.Dao.GetTotalMeasure;
import com.example.android.bluetoothlegatt.Dao.WatjaiMeasure;
import com.example.android.bluetoothlegatt.GlobalService;
import com.example.android.bluetoothlegatt.Manager.CounterNotification;
import com.example.android.bluetoothlegatt.Manager.HttpManager;
import com.example.android.bluetoothlegatt.R;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements View.OnClickListener {
    private ImageButton buttonProfile, buttonMeasure, buttonHistory, buttonSetting, buttonHelp, buttonNotification;
    private TextView countNotification;
    private ArrayList<WatjaiMeasure> watjaiMeasure;
    private GetTotalMeasure getTotalMeasure;
    private Thread t;
    private int totalMeasure;
    private int countNoti;
    private SharedPreferences prefs;
    private String patId;

    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    @SuppressWarnings("UnusedParameters")
    private void init(Bundle savedInstanceState) {
        watjaiMeasure = new ArrayList<WatjaiMeasure>();
        getTotalMeasure = new GetTotalMeasure();
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences("loginStatus", MODE_PRIVATE);
        if (prefs != null) {
            patId = prefs.getString("PatID", "DEFAULT");
        }

        buttonProfile = (ImageButton) rootView.findViewById(R.id.btnProfile);
        buttonMeasure = (ImageButton) rootView.findViewById(R.id.btnMeasure);
        buttonHistory = (ImageButton) rootView.findViewById(R.id.btnHistory);
        buttonSetting = (ImageButton) rootView.findViewById(R.id.btnSetting);
        buttonHelp = (ImageButton) rootView.findViewById(R.id.btnHelp);
        buttonNotification = (ImageButton) rootView.findViewById(R.id.btnNotification);
        countNotification = (TextView) rootView.findViewById(R.id.countNotification);

        buttonMeasure.setOnClickListener(this);
        buttonProfile.setOnClickListener(this);
        buttonHistory.setOnClickListener(this);
        buttonNotification.setOnClickListener(this);
        buttonSetting.setOnClickListener(this);
        buttonHelp.setOnClickListener(this);

        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        countNoti = CounterNotification.getInstance().getCountNotification();
                        if  (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // update TextView here!
                                    if (countNoti>0) {
                                        showCountNotification();
                                    } else {
                                        hideCountNotification();
                                    }
                                    NetworkCall task = new NetworkCall();
                                    task.execute();
                                }
                            });
                        } else {
                            isInterrupted();
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (prefs != null) {
            patId = prefs.getString("PatID", "DEFAULT");
            if (patId.equalsIgnoreCase("DEFAULT")) {
                getActivity().finish();
            }
        }

        if (countNoti>0) {
            showCountNotification();
        } else {
            hideCountNotification();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonProfile) {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            startActivity(intent);
        } else if (v == buttonMeasure) {
            Intent intent = new Intent(getContext(), DeviceScanActivity.class);

            if (GlobalService.mBluetoothLeService.getDeviceAddress() != null) {
                Intent watjai = new Intent(getContext(), DeviceControlActivity.class);
                startActivity(watjai);
            } else {
                startActivity(intent);
            }
        } else if (v == buttonHistory) {
            Intent intent = new Intent(getContext(), HistoryActivity.class);
            startActivity(intent);
        } else if (v == buttonNotification) {
            if  (totalMeasure > 0 ) {
                Intent noti = new Intent(getContext(), NotificationActivity.class);
                startActivity(noti);
                countNoti = 0;
                CounterNotification.getInstance().resetCountNotification();
            } else {
                Intent blank = new Intent(getContext(), BlankNotificationActivity.class);
                startActivity(blank);
            }
        } else if (v == buttonHelp) {
            Intent help = new Intent(getContext(), HelpActivity.class);
            startActivity(help);
        } else if (v == buttonSetting) {
            Intent setting = new Intent(getContext(), SettingActivity.class);
            startActivity(setting);
        }

    }

    private void showCountNotification() {
        countNotification.setVisibility(View.VISIBLE);
        countNotification.setText(countNoti+"");
    }

    private void hideCountNotification() {
        countNotification.setVisibility(View.GONE);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance (Fragment level's variables) State here
    }

    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance (Fragment level's variables) State here
    }

    private class NetworkCall extends AsyncTask<Call, Integer, ArrayList<WatjaiMeasure>> {

        @Override
        protected void onPostExecute(ArrayList<WatjaiMeasure> result) {
            watjaiMeasure = result;
        }

        @Override
        protected ArrayList<WatjaiMeasure> doInBackground(Call... params) {

            try {
                Call<GetTotalMeasure> call = HttpManager.getInstance().getService().totalMeasureAlert(patId);
                Response<GetTotalMeasure> response = call.execute();
                getTotalMeasure = response.body();
                totalMeasure = getTotalMeasure.getTotal();
                return watjaiMeasure;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}


