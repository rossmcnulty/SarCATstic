package net.gnomeffinway.sarcatstic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends Activity {
    // Set the display time, in milliseconds (or extract it out as a configurable parameter)
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private static final String TAG = SplashActivity.class.getSimpleName();
    public static final String PREFS_FILE = "quip-prefs";
    protected JSONObject sarcatsticData;
    private Handler getQuipsHandler;
    private Runnable getQuipsRunnable;
    private GetQuipsTask getQuips;
    Quip[] quips;
    SharedPreferences prefs;

 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        prefs = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        
        // Obtain the sharedPreference, default to true if not available
        boolean isSplashEnabled = prefs.getBoolean("isSplashEnabled", true);
        
        boolean hasStoredQuips = prefs.getBoolean("stored-quips", false);
        boolean forceUpdateCache = prefs.getBoolean("update-cache", false);
        
        if(!hasStoredQuips || forceUpdateCache) {
            if(Util.isNetWorkAvailable(this)) {
                Log.d(TAG, "Retrieving posts");
                getQuips = new GetQuipsTask();
                getQuips.execute();
                prefs.edit().putBoolean("pause-update", false).commit();
            } else {
                prefs.edit().putBoolean("pause-update", true).commit();
                Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Has stored quips and cache not forced to update");
        }
        
        getQuipsHandler = new Handler();
        getQuipsRunnable = new Runnable() {
            @Override
            public void run() {
                //Finish the splash activity so it can't be returned to.
                SplashActivity.this.finish();
                // Create an Intent that will start the main activity.
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
            }
        };
        
        if (isSplashEnabled) {
            getQuipsHandler.postDelayed(getQuipsRunnable, SPLASH_DISPLAY_LENGTH);
        } else {
            // if the splash is not enabled, then finish the activity immediately and go to main.
            finish();
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(mainIntent);
        }
    }
    
    @Override 
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Removing callbacks");
        getQuipsHandler.removeCallbacks(getQuipsRunnable);
    }

    private class GetQuipsTask extends AsyncTask<Object, Void, JSONObject> {
        
        QuipsDataSource datasource;

        @Override
        protected JSONObject doInBackground(Object... arg0) {
            datasource = new QuipsDataSource(getApplicationContext());
            datasource.open();
            
            int responseCode = -1;
            JSONObject jsonResponse = null;
            
            try {
                URL blogFeedURL = new URL("https://dl.dropboxusercontent.com/u/38067805/Cat%20Quips.js");
                HttpURLConnection connection = (HttpURLConnection) blogFeedURL.openConnection();
                connection.connect();
                
                responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);
                    char[] charArray = new char[connection.getContentLength()];
                    reader.read(charArray);
                    String responseData = new String(charArray);
                    
                    jsonResponse = new JSONObject(responseData);
                } else {
                    Log.i(TAG, "Unsuccessful Http response code: " + responseCode);
                }
            } catch(MalformedURLException e) {
                Log.e(TAG, "Malformed URL Exception caught", e);
            } catch(IOException e) {
                Log.e(TAG, "IO Exception caught", e);
            } catch(Exception e) {
                Log.e(TAG, "Generic Exception caught", e);
            }
            
            return jsonResponse;
        }
        
        @Override
        protected void onPostExecute(JSONObject result) {
            sarcatsticData = result;
            boolean successful = Util.updateQuips(sarcatsticData, quips, getApplicationContext(), datasource);
            if(successful) {
                Log.d(TAG, "Logging stored-quips into prefs");
                prefs.edit().putBoolean("stored-quips", true).commit();
            }
        }
        
    }
}