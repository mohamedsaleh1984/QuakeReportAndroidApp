package com.mega.android.quakereport.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.mega.android.quakereport.DT.Earthquake;
import com.mega.android.quakereport.R;
import com.mega.android.quakereport.Util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private Preference btnStartDate;
    private Preference btnEndDate;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_main);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        //*******************************************************************************
        btnStartDate = findPreference("start_date");
        btnEndDate = findPreference("end_date");
        //*******************************************************************************
        if (sharedPreferences.getString("start_date", null) != null)
            btnStartDate.setSummary(sharedPreferences.getString("start_date", null));

        if (sharedPreferences.getString("end_date", null) != null)
            btnEndDate.setSummary(sharedPreferences.getString("end_date", null));

        //Set Handler for Start Date Picker Dialog.
        btnStartDate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDateDialog(preference, "Choose Start Date");
                return true;
            }
        });

        //Set Handler for End Date Picker Dialog.
        btnEndDate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDateDialog(preference, "Choose End Date");
                return true;
            }
        });
    }

    private void showDateDialog(final Preference preference, final String strDialogTitle) {
        int year = 0, month = 0, day = 0;
        String string = preference.getSharedPreferences().getString(preference.getKey(), null);
        //Get today's date and show the Date Picker.
        if (string.isEmpty()) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            //Get saved date and show the Date Picker.
            try {
                int[] array = Utils.getDatePartsFromUI(string);
                year = array[0];
                month = array[1];
                day = array[2];
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        //Initialize DatePickerDialog and show DatePickerDialog
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String strDate = Utils.getDate(year, month, dayOfMonth);
                preference.setSummary(strDate);
                preference.getSharedPreferences().edit().putString(preference.getKey(), strDate).apply();
            }
        }, year, month, day);
        //Set Dialog Title
        datePickerDialog.setMessage(strDialogTitle);
        //When User try to set the Date Picker
        if (preference.getKey().equals("end_date")) {
            //Check Start Date=>if NULL
            if (TextUtils.isEmpty(sharedPreferences.getString("start_date", null))) {
                //Set The Min Date to Today
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            } else {
                //Set The Min Date to Start Date
                datePickerDialog.getDatePicker().setMinDate(Utils.getUnixTimeFromUIFormat(sharedPreferences.getString("start_date", null)));
            }
        } else {
            //Set Min Date to be 1 January 1970
            datePickerDialog.getDatePicker().setMinDate(0);
        }

        //Set Max Date to be today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        //Show Date Picker Dialog
        datePickerDialog.show();
    }
}