package com.mega.android.quakereport.Activities;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mega.android.quakereport.DT.Earthquake;
import com.mega.android.quakereport.Backend.EarthquakeAdapter;
import com.mega.android.quakereport.Backend.EarthquakeLoader;
import com.mega.android.quakereport.R;
import com.mega.android.quakereport.UI.AboutBox;
import com.mega.android.quakereport.UI.ErrorDialog;
import com.mega.android.quakereport.Util.Utils;
import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Earthquake>> {
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final int EARTHQUAKE_LOADER_ID = 100;
    private SharedPreferences sharedPreferences;
    private LoaderManager loaderManager;

    //region UI Elements
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private ListView earthquakeListView;
    private EarthquakeAdapter earthquakeAdapter;
    //endregion UI Elements

    //region Activity Override Methods
    /**
     * onCreate Method
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EarthquakeActivity.this);
        //Bug Fix for Default/First Installation Crash//////////////////////////////////////////////
        if(TextUtils.isEmpty(sharedPreferences.getString("start_date", null)))
            sharedPreferences.edit().putString("start_date",Utils.getTodayDateForUI()).commit();
        if(TextUtils.isEmpty(sharedPreferences.getString("end_date", null)))
            sharedPreferences.edit().putString("end_date",Utils.getTodayDateForUI()).commit();
        ////////////////////////////////////////////////////////////////////////////////////////////
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.VISIBLE);
        textViewEmpty = (TextView) findViewById(R.id.tvempty);
        earthquakeListView = (ListView) findViewById(R.id.list);
        earthquakeListView.setEmptyView(findViewById(R.id.tvempty));
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(earthquakeAdapter.getItem(position).getUri());
                startActivity(intent);
            }
        });
        fetchData();
    }

    /**
     * Inflate the main menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    /**
     * Menu Items Handlers.
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            //Refresh UI

            //Set the list to invisible.////////////////////////////////////////////////////////////
            earthquakeListView.setVisibility(View.INVISIBLE);
            //Destroy the current loader.///////////////////////////////////////////////////////////
            loaderManager.destroyLoader(EARTHQUAKE_LOADER_ID);
            //Show Loading Again.../////////////////////////////////////////////////////////////////
            progressBar.setVisibility(View.VISIBLE);
            //Start Fetching the Data from the Server.//////////////////////////////////////////////
            fetchData();
        } else if (id == R.id.action_settings) {
            //Need to be handled Properly, through using Preference Fragment.
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            //pass the saved info to the fragment
            settingsIntent.putExtra("start_date", sharedPreferences.getString("start_date", null));
            settingsIntent.putExtra("end_date", sharedPreferences.getString("end_date", null));
            //Show the activity.
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.action_about) {
            //Show About Box
            AboutBox.Show(EarthquakeActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion Activity Override

    //region Loader Calls

    /**
     * Trigger the server hit.
     */
    private void fetchData() {
        //Set TextView inside the ListView to Empty String./////////////////////////////////////
        textViewEmpty.setText("");
        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
    }

    /**
     * Create the loader and hit the server and return the earth quack objects.
     */
    @Override
    public Loader<ArrayList<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, generateJSONfromPreferences());
        return new EarthquakeLoader(EarthquakeActivity.this, generateJSONfromPreferences());
    }

    /**
     * Call back after Loader Finished Execution.
     * */
    @Override
    public void onLoadFinished(Loader<ArrayList<Earthquake>> loader, ArrayList<Earthquake> earthquakes) {
        // Clear the adapter of previous earthquake data
        if (earthquakeAdapter != null)
            earthquakeAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        progressBar.setVisibility(View.GONE);

        if (earthquakes != null && !earthquakes.isEmpty()) {
            earthquakeListView.setVisibility(View.VISIBLE);
            earthquakeAdapter = new EarthquakeAdapter(EarthquakeActivity.this, earthquakes);
            earthquakeListView.setAdapter(earthquakeAdapter);
        } else {
            if (Utils.checkInternet(this)==false)
                textViewEmpty.setText(R.string.nointernet);

            if(earthquakeAdapter == null || earthquakes == null)
                textViewEmpty.setText(R.string.noearthquakes);

            earthquakeListView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * OnloaderRest Handler.
     */
    @Override
    public void onLoaderReset(Loader<ArrayList<Earthquake>> loader) {
        // Loader reset, so we can clear out our existing data.
        if (earthquakeAdapter != null)
            earthquakeAdapter.clear();
    }

    //endregion Loader Calls

    /**
     * Generate JSON Query from SharedPreferences.
     * */
    private String generateJSONfromPreferences() {
        StringBuilder stringBuilder = new StringBuilder("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson");

        String strMinMag = sharedPreferences.getString("min_magnitude", null);
        String strStart = sharedPreferences.getString("start_date", null);
        String strEnd = sharedPreferences.getString("end_date", null);
        String strLimit = sharedPreferences.getString("maximumno", null);
        String strOrderby = sharedPreferences.getString("orderby", null);

      //  Toast.makeText(EarthquakeActivity.this, strMinMag + "\n" + strStart + "\n" + strEnd + "\n" + strLimit, Toast.LENGTH_LONG).show();

        try {
            if (strStart != null)
                stringBuilder.append("&starttime=" + Utils.getDateJSON(strStart));
            else
                stringBuilder.append("&starttime=" + Utils.getTodayDate());

            if (strEnd != null)
                stringBuilder.append("&endtime=" + Utils.getDateJSON(strEnd));
            else
                stringBuilder.append("&endtime=" + Utils.getTodayDate());

            if (strLimit != null)
                stringBuilder.append("&limit=" + strLimit);
            else
                stringBuilder.append("&limit=10");

            if (strOrderby != null)
                stringBuilder.append("&orderby=" + strOrderby);
            else
                stringBuilder.append("&orderby=time");

            if (strMinMag != null)
                stringBuilder.append("&minmagnitude=" + strMinMag);
            else
                stringBuilder.append("&minmagnitude=1");

            Log.i(LOG_TAG, String.format("%s ** %s", "generateJSONfromPreferences", stringBuilder.toString()));

        } catch (Exception edx) {
         //   Toast.makeText(EarthquakeActivity.this, edx.getMessage(), Toast.LENGTH_LONG).show();
            ErrorDialog.Show(this, "Error", edx.getMessage(), R.drawable.about);
        }
        return stringBuilder.toString();
    }
}
//region unused Commented Code.
/*
private void fetchUserPreferences() throws ParseException {
        try {
            String strMinMag = sharedPreferences.getString("min_magnitude", null);
            String strStart = sharedPreferences.getString("start_date", null);
            String strEnd = sharedPreferences.getString("end_date", null);
            String iLimit = sharedPreferences.getString("maximumno", null);
            String strOrder = sharedPreferences.getString("orderby", null);
            Toast.makeText(EarthquakeActivity.this, strMinMag + "\n" + strStart + "\n" + strEnd + "\n" + iLimit + "\n" + strOrder, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(EarthquakeActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

        if (bUserAppliedChanges) {
        //
            return new EarthquakeLoader(EarthquakeActivity.this, Utils.XXX);
        } else {
            return new EarthquakeLoader(EarthquakeActivity.this, Utils.XXX);
        }

        //Fully Dynamic JSON Generator.
        //return new EarthquakeLoader(EarthquakeActivity.this, Utils.GEN_JSON_RESPONSE);
        //return new EarthquakeLoader(EarthquakeActivity.this, Utils.ZZZZ);*/
//endregion unused