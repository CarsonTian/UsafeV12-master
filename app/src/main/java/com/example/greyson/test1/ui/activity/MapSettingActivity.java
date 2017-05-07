package com.example.greyson.test1.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greyson.test1.R;
import com.example.greyson.test1.ui.base.BaseActivity;

/**
 * Created by greyson on 8/4/17.
 */

public class MapSettingActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {
    private LinearLayout mLLSavePin;
    private LinearLayout mLLAddNote;
    private LinearLayout mLLDeletePin;
    private EditText mETAddNote;
    private Intent fromIntent;
    private String toColor;
    private String toDescription;
    @Override
    protected int getLayoutRes() {
        return R.layout.act_mapsetting;
    }

    @Override
    protected void initView() {
        Toolbar toolbar = findView(R.id.toolbar);
        AppCompatTextView tvTitle = findView(R.id.tv_title);
        tvTitle.setText("Map Setting");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Spinner spinner = (Spinner) findViewById(R.id.spin_changepin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.changepin_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mLLSavePin = (LinearLayout) findViewById(R.id.ll_savepin);
        mLLDeletePin = (LinearLayout) findViewById(R.id.ll_deletepin);
        mLLAddNote = (LinearLayout) findViewById(R.id.ll_addnote);
        mETAddNote = (EditText) findViewById(R.id.et_addNote);
        mETAddNote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    toDescription = mETAddNote.getText().toString();
                    InputMethodManager im = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    handled = true;
                }
                return handled;
            }
        });
        fromIntent = getIntent();
    }

    @Override
    protected void initData() {
        Bundle b = fromIntent.getExtras();
        String note = b.getString("note");
        mETAddNote.setText(note);
    }

    @Override
    protected void initEvent() {
        mLLSavePin.setOnClickListener(this);
        mLLDeletePin.setOnClickListener(this);
        mLLAddNote.setOnClickListener(this);
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        toColor = parent.getSelectedItem().toString();
        //Toast.makeText(this, parent.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult( 0 , fromIntent);
            finish();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_savepin:
                savePin();
                break;
            case R.id.ll_deletepin:
                deletePin();
                break;
            case R.id.ll_addnote:
                break;
        }
    }

    private void deletePin() {
        Bundle b = fromIntent.getExtras();
        String tag = b.getString("tag");
        String status = b.getString("status");
        fromIntent.putExtra("tag", tag);
        fromIntent.putExtra("status", status);
        setResult( 1 , fromIntent);
        finish();
    }

    private void savePin() {
        Bundle b = fromIntent.getExtras();
        String tag = b.getString("tag");
        String status = b.getString("status");
        Double lat = b.getDouble("lat");
        Double lng = b.getDouble("lng");

        fromIntent.putExtra("color", toColor);
        fromIntent.putExtra("lat", lat);
        fromIntent.putExtra("lng", lng);
        fromIntent.putExtra("tag", tag);
        fromIntent.putExtra("status", status);
        fromIntent.putExtra("note", mETAddNote.getText().toString());

        setResult( 2 , fromIntent);
        finish();
    }
}
