/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mega.android.quakereport.Util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.mega.android.quakereport.DT.Earthquake;
import com.mega.android.quakereport.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility class with methods to help perform the HTTP request, parse the response, handler UI
 * date and time presentations.
 */
public final class Utils {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = Utils.class.getSimpleName();
    /**
     * Sample JSON response for a USGS query

    //public static  String SAMPLE_JSON_RESPONSE ="https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=10";
    public static String SAMPLE_JSON_RESPONSE = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-01-02&limit=10";
    public static String GEN_JSON_RESPONSE = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmagnitude=5&limit=10";
    public static String YYY = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2016-01-01&endtime=2018-01-01&orderby=time&minmagnitude=5&limit=10";
    public static String ZZZZ = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-01-01&endtime=2018-08-01&orderby=magnitude-asc&minmagnitude=4&limit=100";
*/

    /**
     * Convert Aug 11,2012 => 2012,08,11
     */
    public static int[] getDatePartsFromUI(String string) {
        int[] array = new int[3];

        try {
            Calendar mydate = new GregorianCalendar();
            Date date = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).parse(string);
            mydate.setTime(date);

            array[0] = mydate.get(Calendar.YEAR);
            array[1] = mydate.get(Calendar.MONTH);
            array[2] = mydate.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return array;
    }

    /**
     * Get Today's Date in JSON Format
     * 2014-01-02
     */
    public static String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        String date = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + 1 + "-" + cal.get(Calendar.DAY_OF_MONTH);
        return date;
    }
    /**
     * Get Today's Date in JSON Format
     * 2014-01-02
     */
    public static String getTodayDateForUI() {
        Calendar cali = Calendar.getInstance();

        Calendar cal = new GregorianCalendar(cali.get(Calendar.YEAR),
                                            cali.get(Calendar.MONTH),
                                            cali.get(Calendar.DAY_OF_MONTH));
        DateFormat df = new SimpleDateFormat("MMM d, yyyy");
        String date = df.format(cal.getTime());
        return date;
    }
    /**
     * Get Date Format for UI
     * 2014-01-02
     */
    public static String getDate(int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        DateFormat df = new SimpleDateFormat("MMM d, yyyy");
        String date = df.format(cal.getTime());
        return date;
    }

    /**
     * Get Date in Format for JSON
     */
    public static String getDateJSON(String strGivenDate) throws ParseException {
        // This is the format date we want
        DateFormat mSDF = new SimpleDateFormat("yyyy-MM-dd");
        // This format date is actually present
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");
        String strNewDateFormat = mSDF.format(formatter.parse(strGivenDate));
        return strNewDateFormat;
    }

    /**
     * Get Unix Server Time From Date UI format
     * */
    public static long getUnixTimeFromUIFormat(String strDate)
    {
        long millis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
         millis = date.getTime();
        return millis;
    }
    /**
     * Get Date from Linux Server Time.
     **/
    public static String getDate(long timeInMilliseconds) {
        Date dateObject = new Date(timeInMilliseconds);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d, yyyy");
        return dateFormatter.format(dateObject);
    }

    /**
     * Get Time from Linux Server Time.
     */
    public static String getTime(long timeInMilliseconds) {
        Date date = new Date(timeInMilliseconds);
        DateFormat formatter = new SimpleDateFormat("hh:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatted = formatter.format(date);
        return dateFormatted;
    }

    /**
     * Split the Location into 2 array elements.
     */
    public static String[] splitter(String strData) {

        String[] strings = new String[2];

        int spaceCounter = 0;
        int i = 0;

        for (i = 0; i < strData.length(); i++) {
            if (strData.charAt(i) == ' ')
                ++spaceCounter;

            if (spaceCounter == 3)
                break;
        }

        if (spaceCounter == 3) {
            strings[0] = strData.substring(0, i);
            strings[1] = strData.substring(i + 1, strData.length());
        } else {
            strings[0] = "";
            strings[1] = strData;
        }

        return strings;
    }

    /**
     * Get float format for UI presentation.
     */
    public static String getFormatterFloat(double f) {
        DecimalFormat formatter = new DecimalFormat("0.0");
        String output = formatter.format(f);
        return output;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                return null;
            }
        }
        catch (SocketTimeoutException e)
        {
            Log.e(LOG_TAG, "Timeout Exception.", e);
            return "Timeout";
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    public static String readFromStream(InputStream inputStream) {
        StringBuilder output = new StringBuilder();
        try {
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
        } catch (Exception ex) {
            return null;
        }
        return output.toString();
    }

    /**
     * Return an {@link Earthquake} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */
    public static ArrayList<Earthquake> extractFeaturesFromJson(String earthquakeJSON) {
        ArrayList<Earthquake> earthquakeArrayList = new ArrayList<Earthquake>();
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray featureArray = baseJsonResponse.getJSONArray("features");
            // If there are results in the features array
            if (featureArray.length() > 0) {
                // Extract out the first feature (which is an earthquake)
                for (int i = 0; i < featureArray.length(); i++) {
                    JSONObject firstFeature = featureArray.getJSONObject(i);
                    JSONObject properties = firstFeature.getJSONObject("properties");
                    // Extract out the title, number of people, and perceived strength values
                    double mag = properties.getDouble("mag");
                    String place = properties.getString("place");
                    long time = properties.getLong("time");
                    String strURL = properties.getString("url");

                    Earthquake e = new Earthquake(mag,place,time,Uri.parse(strURL));
                    // Create a new {@link Event} object
                    if(!earthquakeArrayList.contains(e)) {
                        earthquakeArrayList.add(e);
                    }
                    //earthquakeArrayList.add(new Earthquake(mag, place, time, Uri.parse(properties.getString("url"))));
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
            Log.i(LOG_TAG, "Problem parsing the earthquake JSON results", e);
            return null;
        }
        return earthquakeArrayList;
    }

    /**
     * Get Magnitude Color Based on Earth Quack Magnitude.
     */
    public static int getMagnitudeColor(double earthQuackMagnitude, Context cnn) {

        if (earthQuackMagnitude >= 10)
            return ContextCompat.getColor(cnn, R.color.magnitude10plus);
        else if (earthQuackMagnitude >= 9 && earthQuackMagnitude < 10)
            return ContextCompat.getColor(cnn, R.color.magnitude9);
        else if (earthQuackMagnitude >= 8 && earthQuackMagnitude < 9)
            return ContextCompat.getColor(cnn, R.color.magnitude8);
        else if (earthQuackMagnitude >= 7 && earthQuackMagnitude < 8)
            return ContextCompat.getColor(cnn, R.color.magnitude8);
        else if (earthQuackMagnitude >= 6 && earthQuackMagnitude < 7)
            return ContextCompat.getColor(cnn, R.color.magnitude7);
        else if (earthQuackMagnitude >= 5 && earthQuackMagnitude < 6)
            return ContextCompat.getColor(cnn, R.color.magnitude6);
        else if (earthQuackMagnitude >= 4 && earthQuackMagnitude < 5)
            return ContextCompat.getColor(cnn, R.color.magnitude5);
        else if (earthQuackMagnitude >= 3 && earthQuackMagnitude < 4)
            return ContextCompat.getColor(cnn, R.color.magnitude4);
        else if (earthQuackMagnitude >= 2 && earthQuackMagnitude < 3)
            return ContextCompat.getColor(cnn, R.color.magnitude3);
        else if (earthQuackMagnitude >= 1 && earthQuackMagnitude < 2)
            return ContextCompat.getColor(cnn, R.color.magnitude2);
        else if (earthQuackMagnitude < 1)
            return ContextCompat.getColor(cnn, R.color.magnitude1);
        else
            return 0;
    }

    /*
     * Check Internet Connectivity.
     * */
    public static boolean checkInternet(Activity activity) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected())
            return false;
        return networkInfo.isConnected();
    }
}
