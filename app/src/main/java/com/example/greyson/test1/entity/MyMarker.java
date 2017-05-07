package com.example.greyson.test1.entity;

import java.io.Serializable;

/**
 * Created by greyson on 15/4/17.
 */

public class MyMarker implements Serializable {
    private String mkTag;
    private Double mkLat;
    private Double mkLnt;
    private String mkColor;
    private String mkDescription;

    public MyMarker(String mkTag, Double mkLat, Double mkLnt, String mkColor, String mkDescription) {
        this.mkTag = mkTag;
        this.mkLat = mkLat;
        this.mkLnt = mkLnt;
        this.mkColor = mkColor;
        this.mkDescription = mkDescription;
    }

    public String getMkTag() {
        return mkTag;
    }

    public void setMkTag(String mkTag) {
        this.mkTag = mkTag;
    }

    public Double getMkLat() {
        return mkLat;
    }

    public void setMkLat(Double mkLat) {
        this.mkLat = mkLat;
    }

    public Double getMkLnt() {
        return mkLnt;
    }

    public void setMkLnt(Double mkLnt) {
        this.mkLnt = mkLnt;
    }

    public String getMkColor() {
        return mkColor;
    }

    public void setMkColor(String mkColor) {
        this.mkColor = mkColor;
    }

    public String getMkDescription() {
        return mkDescription;
    }

    public void setMkDescription(String mkDescription) {
        this.mkDescription = mkDescription;
    }
}
