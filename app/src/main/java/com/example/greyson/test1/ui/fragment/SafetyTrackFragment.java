package com.example.greyson.test1.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.example.greyson.test1.R;
import com.example.greyson.test1.core.TimerListener;
import com.example.greyson.test1.ui.base.BaseFragment;
import com.example.greyson.test1.widget.CountDownView2;

import java.text.SimpleDateFormat;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;


/**
 * Tis class is about tracker function
 *
 * @author Greyson, Carson
 * @version 1.0
 */
public class SafetyTrackFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_GET_DEVICEID = 222;

    private String id, tStamp, cusTime, number, Contact1, Contact2, Contact3, cLatitude, cLngtitude;

    private Button buttonStartTime, buttonStopTime;
    private EditText edtTimerValue;
    private WebView upWeb;
    private long totalTimeCountInMilliseconds;

    private Runnable wTimer;
    private Handler mHandler;
    private MediaPlayer mp;
    private CountDownView2 cdv;

    private SharedPreferences preferences;
    private AlertDialog alertDialog;

    private String aa="";


    /**
     * This method is used to initialize the map view and request the current location
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_safetytrack, container, false);

        mHandler = new Handler();
        upWeb = (WebView) view.findViewById(R.id.upWeb);
        edtTimerValue = (EditText) view.findViewById(R.id.edtTimerValue);
        buttonStartTime = (Button) view.findViewById(R.id.btnStartTime);
        buttonStopTime = (Button) view.findViewById(R.id.btnStopTime);

        buttonStartTime.setOnClickListener(this);
        buttonStopTime.setOnClickListener(this);

        cdv = (CountDownView2) view.findViewById(R.id.countdownview2);
        cdv.setInitialTime(0); // Initial time of 5 seconds.

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("timeResume", MODE_PRIVATE);
        edtTimerValue.setText(sharedPreferences.getString("time",""));


        if (!sharedPreferences.getString("time", "").trim().equals("")) {
            aa = edtTimerValue.getText().toString().trim();
            buttonStartTime.setVisibility(View.GONE);
            buttonStopTime.setVisibility(View.VISIBLE);
            edtTimerValue.setVisibility(View.GONE);
            startTimer();
            cdv.start();
            tStamp = sharedPreferences.getString("tId", "");
        }


        wTimer = new Runnable() {
            @Override
            public void run() {
                warningDialog();
            }
        };
        return view;
    }

    @Override
    protected void initData() {
        getCurrentLocation();
        getContactList();
        getName();
        checkDeviceIDPermission();
        getMobileIMEI();
        preferences = mContext.getSharedPreferences("UserSetting",MODE_PRIVATE);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void stopView() {
    }

    @Override
    protected void destroyView() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("timeResume", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("time", aa);
        editor.putString("tId", tStamp);
        editor.commit();
    }

    /**
     * request permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_GET_DEVICEID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMobileIMEI();
                }
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnStartTime) {
            if (setTimer() && setNamCan()) {
                aa = edtTimerValue.getText().toString().trim();
                buttonStartTime.setVisibility(View.GONE);
                buttonStopTime.setVisibility(View.VISIBLE);
                edtTimerValue.setVisibility(View.GONE);
                startUpload();
                startTimer();
                cdv.start();
            } else {
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Notice")
                        .setContentText("Please complete your user setting before you start the trail tracker.")
                        .setConfirmText("OK")
                        .show();
            }
        } else if (v.getId() == R.id.btnStopTime) {
            aa = "";
            buttonStartTime.setVisibility(View.VISIBLE);
            buttonStopTime.setVisibility(View.GONE);
            edtTimerValue.setVisibility(View.VISIBLE);
            edtTimerValue.setText("");
            finishUpload();
            mHandler.removeCallbacks(wTimer);
            cdv.reset();
        }
    }

    private void checkMediaPlayerPermission() {
        try {
            mp.setDataSource(mContext, RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkDeviceIDPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_PHONE_STATE)) {

            } else {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_GET_DEVICEID);
            }
        } else {
            getMobileIMEI();
        }
    }

    private void getMobileIMEI() {
        TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        id = tManager.getDeviceId();
        number = tManager.getLine1Number();

    }

    private void getCurrentLocation() {
        SharedPreferences preferences1 = mContext.getSharedPreferences("LastLocation", MODE_PRIVATE);
        String[] array = preferences1.getString("last location", "0,0").split(",");
        cLatitude = array[0];
        cLngtitude = array[1];
    }

    private void getContactList() {

    }

    private void getName() {

    }

    private boolean setNamCan() {
        if (preferences.getString("contact1", "").trim().equals("") ||
                preferences.getString("contact2", "").trim().equals("") ||
                preferences.getString("contact3", "").trim().equals("") ||
                preferences.getString("userName", "").trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private boolean setTimer() {
        if (edtTimerValue.getText().toString().trim().equals("")) {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Notice")
                    .setContentText("Please enter a time")
                    .setConfirmText("OK")
                    .show();
            return false;
        } else if (Integer.parseInt(edtTimerValue.getText().toString().trim()) < 5 && Integer.parseInt(edtTimerValue.getText().toString().trim()) > 1){
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Notice")
                    .setContentText("Please make sure time is longer than 5 min.")
                    .setConfirmText("OK")
                    .show();
            return false;
        } else if (Integer.parseInt(edtTimerValue.getText().toString().trim()) > 30) {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Notice")
                    .setContentText("please make sure time is shorter than 30 min")
                    .setConfirmText("OK")
                    .show();
            return false;
        } else {
            totalTimeCountInMilliseconds = 60 * Integer.parseInt(edtTimerValue.getText().toString().trim()) * 1000;
            cusTime = edtTimerValue.getText().toString().trim();
            return true;
        }
    }

    private void startTimer() {
        cdv.setInitialTime(totalTimeCountInMilliseconds); // Initial time of 5 seconds.
        cdv.setListener(new TimerListener() {
            @Override
            public void timerElapsed() {
                cdv.stop();
                dialog();
                mp = new MediaPlayer();
                checkMediaPlayerPermission();
                mp.start();
                mHandler.postDelayed(wTimer, 60000);
            }
        });
    }

    private void dialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you safe?\nYou have 1min to confirm");
        builder.setTitle("Alarm");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cdv.setInitialTime(totalTimeCountInMilliseconds);
                cdv.start();
                mHandler.removeCallbacks(wTimer);
                mp.stop();
                uploadData();
                dialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void warningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("We have sent warning messages, please contact your friends");
        builder.setTitle("Alarm");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                alertDialog.dismiss();
                aa = "";
                buttonStartTime.setVisibility(View.VISIBLE);
                buttonStopTime.setVisibility(View.GONE);
                edtTimerValue.setVisibility(View.VISIBLE);
                edtTimerValue.setText("");
                mHandler.removeCallbacks(wTimer);
                cdv.reset();
            }
        });
        AlertDialog wAlertDialog = builder.create();
        wAlertDialog.setCanceledOnTouchOutside(false);
        wAlertDialog.show();
    }

    private void timeStamp() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long time = Long.valueOf(System.currentTimeMillis());
        tStamp = format.format(time);
    }

    private void uploadData() {
        //upWeb.loadUrl("http://usafe.epnjkefarc.us-west-2.elasticbeanstalk.com/trailtrack/update/?deviceid=" + id + tStamp + "&status=safe&lat=" + cLatitude + "&lng=" + cLngtitude);
    }

    private void startUpload() {
        timeStamp();
        //upWeb.loadUrl("http://usafe.epnjkefarc.us-west-2.elasticbeanstalk.com/trailtrack/create/?deviceid=" + id + tStamp + "&name=" + preferences.getString("userName" , "") + "&uphone=1234567&c1=" + preferences.getString("contact1", "").trim().split(";")[1] + "&c2=" + preferences.getString("contact2", "").trim().split(";")[1] + "&c3=" + preferences.getString("contact3", "").trim().split(";")[1] + "&status=start&period=" + cusTime + "&lat=" + cLatitude + "&lng=" + cLngtitude);
    }

    private void finishUpload() {
        //upWeb.loadUrl("http://usafe.epnjkefarc.us-west-2.elasticbeanstalk.com/trailtrack/update/?deviceid=" + id + tStamp + "&status=reached&lat=" + cLatitude + "&lng=" + cLngtitude);
    }
}
