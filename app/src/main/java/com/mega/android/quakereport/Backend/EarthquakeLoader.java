package com.mega.android.quakereport.Backend;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mega.android.quakereport.DT.Earthquake;
import com.mega.android.quakereport.Util.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<Earthquake>>
{
    public static final String LOG_TAG = Earthquake.class.getName();

    /** Query URL */
    private String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Earthquake> loadInBackground() {
        if (mUrl == null)
            return null;

        String jsonResponse = null;
        ArrayList<Earthquake> earthquakeArrayList = null;

        // Create URL object
        URL url = Utils.createUrl(mUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        if(url != null) {
            try {
                jsonResponse = Utils.makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error closing input stream", e);
                return null;
            }
            if(TextUtils.isEmpty(jsonResponse))
            {
                return null;
            }
            else
            {
                // Extract relevant fields from the JSON response and create an {@link Event} object
                earthquakeArrayList = new ArrayList<Earthquake>(1);
                if(!TextUtils.isEmpty(jsonResponse))
                    earthquakeArrayList = Utils.extractFeaturesFromJson(jsonResponse);
            }

            /*
            Sorting.sortMethod s =  Sorting.getURLSortType(mUrl);
            if (s == Sorting.sortMethod.magnitude)
                earthquakeArrayList = Sorting.sort(earthquakeArrayList, Sorting.sortMethod.magnitude);
            else if(s == Sorting.sortMethod.magnitude_asc)
                earthquakeArrayList = Sorting.sort(earthquakeArrayList, Sorting.sortMethod.magnitude_asc);
            else if(s == Sorting.sortMethod.Time)
                earthquakeArrayList = Sorting.sort(earthquakeArrayList, Sorting.sortMethod.Time);
            else if(s == Sorting.sortMethod.Time_asc)
                earthquakeArrayList = Sorting.sort(earthquakeArrayList, Sorting.sortMethod.Time_asc);
            */
        }
        if(earthquakeArrayList == null)
            return null;

        return earthquakeArrayList;
    }
}
