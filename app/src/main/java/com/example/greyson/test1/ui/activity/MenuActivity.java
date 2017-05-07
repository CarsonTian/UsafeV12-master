package com.example.greyson.test1.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.greyson.test1.R;
import com.example.greyson.test1.ui.base.BaseActivity;

/**
 * Created by greyson on 5/5/17.
 */

public class MenuActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout mLLEmergencyMenu;
    private LinearLayout mLLPanicButtonMenu;
    private LinearLayout mLLSafetyTrackMenu;
    private LinearLayout mLLSafetyMapMenu;
    private LinearLayout mLLSafetyPinMenu;
    private LinearLayout mLLSettingMenu;


    private static final int REQUEST_COARSE_LOCATION = 000;
    private static final int REQUEST_FINE_LOCATION = 001;
    private static final int REQUEST_SEND_SMS = 002;
    private static final int REQUEST_READ_CONTACT = 003;
    private static final int RESULT_PICK_CONTACT = 111;

    @Override
    protected int getLayoutRes() {
        return R.layout.act_menu;
    }

    @Override
    protected void initView() {
        mLLEmergencyMenu = (LinearLayout) findViewById(R.id.ll_emergencyCallMenu);
        mLLPanicButtonMenu = (LinearLayout) findViewById(R.id.ll_panicButtonMenu);
        mLLSafetyTrackMenu = (LinearLayout) findViewById(R.id.ll_startTrackMenu);
        mLLSafetyMapMenu = (LinearLayout) findViewById(R.id.ll_safetyMapMenu);
        mLLSafetyPinMenu = (LinearLayout) findViewById(R.id.ll_safetyPinMenu);
        mLLSettingMenu = (LinearLayout) findViewById(R.id.ll_settingMenu);
    }

    @Override
    protected void initData() {
        checkCoarseLocationPermission();
        checkFineLocationPermission();
        checkSMSPermission();
        checkReadContactPermission();
    }

    @Override
    protected void initEvent() {
        mLLEmergencyMenu.setOnClickListener(this);
        mLLPanicButtonMenu.setOnClickListener(this);
        mLLSafetyTrackMenu.setOnClickListener(this);
        mLLSafetyMapMenu.setOnClickListener(this);
        mLLSafetyPinMenu.setOnClickListener(this);
        mLLSettingMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        switch (v.getId()) {
            case R.id.ll_emergencyCallMenu:
                intent.putExtra("menu","emergency");///
                startActivity(intent);
                break;
            case R.id.ll_panicButtonMenu:
                intent.putExtra("menu","button");
                startActivity(intent);
                break;
            case R.id.ll_startTrackMenu:
                intent.putExtra("menu","track");
                startActivity(intent);
                break;
            case R.id.ll_safetyMapMenu:
                intent.putExtra("menu","map");
                startActivity(intent);
                break;
            case R.id.ll_safetyPinMenu:
                intent.putExtra("menu","pin");
                startActivity(intent);
                break;
            case R.id.ll_settingMenu:
                Intent intent1 = new Intent(MenuActivity.this, UserSettingActivity.class);
                startActivity(intent1);/////
                break;
        }
    }

    @Override
    protected void destroyView() {

    }

    private boolean checkCoarseLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                return false;
            }
        }
        return true;
    }

    private boolean checkFineLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
                return false;
            }
        }
        return true;
    }

    private boolean checkSMSPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
                return false;
            }
        }
        return true;
    }

    private boolean checkReadContactPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT);
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_COARSE_LOCATION:{
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Safety Map function can not work properly.",Toast.LENGTH_SHORT).show();
                }
            }break;
            case REQUEST_FINE_LOCATION:{
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Safety Map function can not work properly.",Toast.LENGTH_SHORT).show();
                }
            }break;

            case REQUEST_SEND_SMS:{
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Emergency Contact function can not work properly.",Toast.LENGTH_SHORT).show();
                }
            }break;
            case REQUEST_READ_CONTACT:{
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Emergency Contact function can not work properly.",Toast.LENGTH_SHORT).show();
                }
            }break;
        }
    }

}
