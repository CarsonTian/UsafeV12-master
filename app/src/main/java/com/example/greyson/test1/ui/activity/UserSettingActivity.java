package com.example.greyson.test1.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greyson.test1.R;
import com.example.greyson.test1.ui.base.BaseActivity;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.group.Group;
import com.onegravity.contactpicker.picture.ContactPictureType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by greyson on 5/5/17.
 */

public class UserSettingActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout mLLUserName;
    private LinearLayout mLLUserContact;
    private LinearLayout mLLsettingContact;

    private TextView mTVUserContactName1;
    private TextView mTVUserContactPhone1;
    private TextView mTVUserContactName2;
    private TextView mTVUserContactPhone2;
    private TextView mTVUserContactName3;
    private TextView mTVUserContactPhone3;
    private List<TextView> nameList;
    private List<TextView> phoneList;


    private TextView mTVAddContact;
    private TextView mTVSaveContact;
    private EditText mETUSername;

    private SharedPreferences preferences;
    private String userName;
    private static final int REQUEST_CONTACT = 011;
    private static final int RESULT_PICK_CONTACT = 012;

    @Override
    protected int getLayoutRes() {
        return R.layout.act_setting;
    }

    @Override
    protected void initView() {
        Toolbar toolbar = findView(R.id.toolbar);
        AppCompatTextView tvTitle = findView(R.id.tv_title);
        tvTitle.setText("User Setting");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mLLUserName = (LinearLayout) findViewById(R.id.ll_userName);
        mLLUserContact = (LinearLayout) findViewById(R.id.ll_userContact);
        mLLsettingContact = (LinearLayout) findViewById(R.id.ll_settingContacts);

        mTVUserContactName1 = (TextView) findViewById(R.id.tv_userContactName);
        mTVUserContactName2 = (TextView) findViewById(R.id.tv_userContactName2);
        mTVUserContactName3 = (TextView) findViewById(R.id.tv_userContactName3);
        mTVUserContactPhone1 = (TextView) findViewById(R.id.tv_userContactPhone);
        mTVUserContactPhone2 = (TextView) findViewById(R.id.tv_userContactPhone2);
        mTVUserContactPhone3 = (TextView) findViewById(R.id.tv_userContactPhone3);


        mTVAddContact = (TextView) findViewById(R.id.tv_addContact);
        mTVSaveContact = (TextView) findViewById(R.id.tv_saveContact);

        mETUSername = (EditText) findViewById(R.id.et_typeName);
        mETUSername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    userName = mETUSername.getText().toString();
                    InputMethodManager im = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //mETUSername.setText(userName);
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    protected void initData() {
        nameList = new ArrayList<>();
        phoneList = new ArrayList<>();

        nameList.add(mTVUserContactName1);
        nameList.add(mTVUserContactName2);
        nameList.add(mTVUserContactName3);
        phoneList.add(mTVUserContactPhone1);
        phoneList.add(mTVUserContactPhone2);
        phoneList.add(mTVUserContactPhone3);

        preferences = getSharedPreferences("UserSetting",MODE_PRIVATE);

        loadEmergencyContact();
    }

    @Override
    protected void initEvent() {
        mTVAddContact.setOnClickListener(this);
        mTVSaveContact.setOnClickListener(this);
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_addContact:
                mTVAddContact.setSelected(true);
                addContacts1();
                break;
            case R.id.tv_saveContact:
                mTVSaveContact.setSelected(true);
                saveContacts();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void loadEmergencyContact() {
        String userName = preferences.getString("userName",null);
        String contact1 = preferences.getString("contact1",null);
        String contact2 = preferences.getString("contact2",null);
        String contact3 = preferences.getString("contact3",null);
        if (userName != null && !userName.trim().isEmpty()) {
            mETUSername.setText(userName);
        }
        if (contact1 != null && contact1.contains(";")) {
            mTVUserContactName1.setText(contact1.split(";")[0]);
            mTVUserContactPhone1.setText(contact1.split(";")[1]);
        }
        if (contact2 != null && contact2.contains(";")) {
            mTVUserContactName2.setText(contact2.split(";")[0]);
            mTVUserContactPhone2.setText(contact2.split(";")[1]);
        }
        if (contact3 != null && contact3.contains(";")) {
            mTVUserContactName3.setText(contact3.split(";")[0]);
            mTVUserContactPhone3.setText(contact3.split(";")[1]);
        }
    }

    private void saveContacts() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", mETUSername.getText().toString());
        editor.putString("contact1", mTVUserContactName1.getText().toString() + ";" + mTVUserContactPhone1.getText().toString() );
        editor.putString("contact2", mTVUserContactName2.getText().toString() + ";" + mTVUserContactPhone2.getText().toString() );
        editor.putString("contact3", mTVUserContactName3.getText().toString() + ";" + mTVUserContactPhone3.getText().toString() );
        editor.commit();
        Toast.makeText(this,"Emergency Contacts Saved",Toast.LENGTH_SHORT).show();
    }

    private void addContacts1() {

        Intent intent = new Intent(this, ContactPickerActivity.class)
                .putExtra(ContactPickerActivity.EXTRA_THEME, R.style.ContactPicker_Theme_Light)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                .putExtra(ContactPickerActivity.EXTRA_SHOW_CHECK_ALL, true)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name())
                .putExtra(ContactPickerActivity.EXTRA_SELECT_CONTACTS_LIMIT,3)
                .putExtra(ContactPickerActivity.EXTRA_LIMIT_REACHED_MESSAGE,"Sorry, You can't pick more than 3 contacts.");
        startActivityForResult(intent, REQUEST_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK &&
                data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {
            List<Contact> contacts = (List<Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
            Iterator<Contact> iterator = contacts.iterator();
            Iterator<TextView> iterator1 = nameList.iterator();
            Iterator<TextView> iterator2 = phoneList.iterator();
            mTVUserContactName1.setText("");
            mTVUserContactName2.setText("");
            mTVUserContactName3.setText("");
            mTVUserContactPhone1.setText("");
            mTVUserContactPhone2.setText("");
            mTVUserContactPhone3.setText("");


            while (iterator.hasNext()) {
                Contact contact = iterator.next();
                String name = contact.getFirstName() + " " + contact.getLastName();
                String phone = contact.getPhone(2);
                if (iterator1.hasNext()) {

                    iterator1.next().setText(name);
                }
                if (iterator2.hasNext()) {
                    iterator2.next().setText(phone);
                }
            }
            List<Group> groups = (List<Group>) data.getSerializableExtra(ContactPickerActivity.RESULT_GROUP_DATA);
            for (Group group : groups) {
            }
        }
    }
}
