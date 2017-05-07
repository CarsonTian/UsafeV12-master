package com.example.greyson.test1.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greyson.test1.R;
import com.example.greyson.test1.core.TimerListener;
import com.example.greyson.test1.ui.activity.MainActivity;
import com.example.greyson.test1.ui.base.BaseFragment;
import com.example.greyson.test1.widget.CountDownView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * This class is button function
 *
 * @author Greyson, Carson
 * @version 1.0
 */


public class SafetyButtonFragment extends BaseFragment implements View.OnClickListener{
    private static final int RESULT_PICK_CONTACT = 111;
    private static final int REQUEST_SEND_SMS = 222;
    private static final int REQUEST_READ_CONTACT = 333;
    private LinearLayout mLLStartButton;
    private LinearLayout mLLCancelButton;
    private LinearLayout mLLSettingButton;
    private TextView mTVContactName;
    private TextView mTVContactNumber;
    private CountDownView cdv;

    private boolean canSendMSM;
    private String phoneNumber;
    private String tiemrStatus;

    /**
     * Initial view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_safetybutton, container, false);
        mLLStartButton = (LinearLayout) view.findViewById(R.id.ll_startbutton);
        mLLCancelButton = (LinearLayout) view.findViewById(R.id.ll_cancelbutton);
        mLLSettingButton = (LinearLayout) view.findViewById(R.id.ll_settingbutton);
        mTVContactName = (TextView) view.findViewById(R.id.tv_contactName);
        mTVContactNumber = (TextView) view.findViewById(R.id.tv_contactPhone);
        cdv = (CountDownView) view.findViewById(R.id.countdownview);
        cdv.setInitialTime(5000); // Initial time of 5 seconds.
        cdv.setListener(new TimerListener() {
            @Override
            public void timerElapsed() {
                cdv.stop();
                //checkSMSPermission();
                if (canSendMSM == true) {
                    sendMessageToContact();
                }
                canSendMSM = false;
                ///
            }
        });
        return view;
    }

    /**
     * Check permission of message
     */
    private boolean checkSMSPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_SEND_SMS);
                return false;
            }
        }
        return true;
        //else {sendMessageToContact();}
    }

    private boolean checkReadContactPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT);
                return false;
            }
        }
        return true;
    }

    /**
     * request permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_SEND_SMS:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTimer();}
            }break;
            case REQUEST_READ_CONTACT:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    settingButton();}
            }break;
        }
    }

    /**
     * Chech contact number if is null
     * @return
     */
    private boolean checkContactNotEmpty() {
        String[] ePhoneList = mTVContactNumber.getText().toString().split(":");
        String[] eNameList = mTVContactName.getText().toString().split(":");
        if (ePhoneList.length == 1){
            Toast.makeText(mContext, "You need choose contact first" , Toast.LENGTH_LONG).show();
            return false;
        }
        saveLastContact(eNameList[1],ePhoneList[1]);
        return true;
    }

    /**
     * Save last location
     * @param name
     * @param phone
     * @return
     */
    private boolean saveLastContact(String name, String phone) {
        SharedPreferences preferences = mContext.getSharedPreferences("LastContact",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("contact", name + "," + phone);
        editor.commit();
        return true;
    }

    /**
     * This is method for sending message
     */
    private void sendMessageToContact() {
        //http://maps.google.com/maps?q=-37.886256,145.0543715

        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(SMS_DELIVERED), 0);


        BroadcastReceiver smsSent = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        //Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        mContext.registerReceiver(smsSent, new IntentFilter(SMS_SENT));

        BroadcastReceiver smsDelivered = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        mContext.registerReceiver(smsDelivered, new IntentFilter(SMS_DELIVERED));



        String[] ePhoneList = mTVContactNumber.getText().toString().split(":");
        if (checkContactNotEmpty()){
            SharedPreferences preferences = mContext.getSharedPreferences("LastLocation",MODE_PRIVATE);
            String lastLocation = preferences.getString("last location",null);
            String baseMapUrl = "http://maps.google.com/maps?q=";
            String eMessage = "This is an emergency message, please call me first, press this link to see my last location: "
                    + baseMapUrl + lastLocation;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(ePhoneList[1], null, eMessage, sentPendingIntent, deliveredPendingIntent);
            Toast.makeText(mContext, "SMS sent.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Initial data
     */
    @Override
    protected void initData() {
        canSendMSM = true;
        SharedPreferences preferences = mContext.getSharedPreferences("LastContact",MODE_PRIVATE);
        String lastContact = preferences.getString("contact",null);
        if (lastContact == null) {
            Toast.makeText(mContext, "Emergency Contact is Empty.", Toast.LENGTH_LONG).show();
            return;
        }
        mTVContactName.setText("Contact Name:" + lastContact.split(",")[0]);
        mTVContactNumber.setText("Contact Number:" + lastContact.split(",")[1]);
    }

    @Override
    protected void initEvent() {
        mLLStartButton.setOnClickListener(this);
        mLLCancelButton.setOnClickListener(this);
        mLLSettingButton.setOnClickListener(this);
    }

    /**
     * Change view listener
     * @param v
     */
    @Override
    public void onClick(View v) {
        mLLStartButton.setSelected(false);
        mLLCancelButton.setSelected(false);
        mLLSettingButton.setSelected(false);
        switch (v.getId()) {
            case R.id.ll_startbutton:
                mLLStartButton.setSelected(true);
                //mLLSettingButton.setSelected(false);
                //mLLCancelButton.setSelected(false);
                startTimer();
                break;
            case R.id.ll_cancelbutton:
                mLLCancelButton.setSelected(true);
                //mLLStartButton.setSelected(false);
                //mLLSettingButton.setSelected(false);
                resetTimer();
                break;
            case R.id.ll_settingbutton:
                mLLSettingButton.setSelected(true);
                //mLLStartButton.setSelected(false);
                //mLLCancelButton.setSelected(false);
                if (checkReadContactPermission()){
                    settingButton();
                }
                break;
        }
    }

    /**
     * reset time
     */
    private void resetTimer() {
        cdv.reset();
        canSendMSM = true;
    }

    private void startTimer() {
        if(checkContactNotEmpty() && checkSMSPermission()) {

            cdv.start();
            startNotification();
        }
    }

    /**
     * This is to set notification button
     */
    private void startNotification() {
        if(checkNotificaionExsist())
            return;
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("notification",0);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent1 = new Intent(mContext, MainActivity.class);
        intent1.putExtra("notification",1);
        PendingIntent pIntent1 = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent2 = new Intent(mContext, MainActivity.class);
        intent2.putExtra("notification",2);
        PendingIntent pIntent2 = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(R.drawable.start_24,"Start Button",pIntent1).build();

        NotificationCompat.Action action2 = new NotificationCompat.Action.Builder(R.drawable.stop_24,"Stop Button",pIntent2).build();

        Notification n  = new NotificationCompat.Builder(mContext)
                .setContentTitle("Welcome to use U-Safe")
                .setContentText("Click to enter application")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .setTicker("Start Countdown")
                .setWhen(System.currentTimeMillis())
                //.setUsesChronometer(true)
                .setAutoCancel(false)
                .addAction(action1)
                .addAction(action2)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify("usafe",666,n);
    }

    private boolean checkNotificaionExsist() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] s = notificationManager.getActiveNotifications();///Min 23 API
        int nLength = s.length;
        for (int i = 0;i< nLength; i++) {
            if(s[i].getTag().equals("usafe"))
                return true;
        }
        return false;
    }

    /**
     * Setting button
     */
    private void settingButton() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }

    /**
     * check contact number
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String contactPhone = "" ;
            String contactName = "";
            Uri uri = data.getData();
            cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            contactPhone = cursor.getString(phoneIndex);
            contactName = cursor.getString(nameIndex);
            mTVContactName.setText("Contact Name: " + contactName);
            mTVContactNumber.setText("Contact Phone: " + contactPhone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void stopView() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle b = getArguments();
        if (b != null) {
            int extra = b.getInt("notification");
            switch (extra) {
                case 1:
                    startTimer();
                    getArguments().clear();
                    break;
                case 2:
                    resetTimer();
                    getArguments().clear();
                    break;
            }
        }
    }
}
