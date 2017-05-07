package com.example.greyson.test1.entity;

import android.support.v4.util.ArraySet;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by greyson on 13/4/17.
 */

public class UserPinHistory implements Serializable{
    private String username;
    private ArrayList<MyMarker> mmk;

    public UserPinHistory(String username, ArrayList<MyMarker> mmk) {
        this.username = username;
        this.mmk = mmk;
    }

    public UserPinHistory() {
        this.username = "admin";
        this.mmk = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<MyMarker> getMmk() {
        return mmk;
    }

    public void setMmk(ArrayList<MyMarker> mmk) {
        this.mmk = mmk;
    }
}
