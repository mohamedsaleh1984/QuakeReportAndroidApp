package com.mega.android.quakereport.DT;

import android.net.Uri;

public class Earthquake {
    public static final String LOG_TAG = Earthquake.class.getName();
    private double fmag;
    private String strPlace;
    private long dateTime;
    private Uri uri;

    public Earthquake(double fmag, String strPlace, Long dateTime,Uri uri) {
        this.fmag = fmag;
        this.strPlace = strPlace;
        this.dateTime = dateTime;
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public double getMag() {
        return fmag;
    }

    public String getPlace() {
        return strPlace;
    }

    public Long getDateTime() {
        return dateTime;
    }
}
