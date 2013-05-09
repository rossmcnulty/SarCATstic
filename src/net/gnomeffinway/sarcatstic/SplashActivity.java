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
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends Activity {
    // Set the display time, in milliseconds (or extract it out as a configurable parameter)
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    protected JSONObject sarcatsticData;
    Quip[] quips;
    SharedPreferences sp;

 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Obtain the sharedPreference, default to true if not available
        boolean isSplashEnabled = sp.getBoolean("isSplashEnabled", true);
        
        boolean hasStoredQuips = sp.getBoolean("stored-quips", false);
        boolean forceUpdateCache = sp.getBoolean("update-cache", false);
        
        if(!hasStoredQuips || forceUpdateCache) {
            if(Util.isNetWorkAvailable(this)) {
                GetQuipsTask getPosts = new GetQuipsTask();
                getPosts.execute();
                sp.edit().putBoolean("pause_update", false).commit();
            } else {
                sp.edit().putBoolean("pause_update", true).commit();
                Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
            }
        }
        
        if (isSplashEnabled) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Finish the splash activity so it can't be returned to.
                    SplashActivity.this.finish();
                    // Create an Intent that will start the main activity.
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                }
            }, SPLASH_DISPLAY_LENGTH);
        }
        else {
            // if the splash is not enabled, then finish the activity immediately and go to main.
            finish();
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(mainIntent);
        }
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
                    Log.i(MainActivity.TAG, "Unsuccessful Http response code: " + responseCode);
                }
            } catch(MalformedURLException e) {
                Log.e(MainActivity.TAG, "Malformed URL Exception caught", e);
            } catch(IOException e) {
                Log.e(MainActivity.TAG, "IO Exception caught", e);
            } catch(Exception e) {
                Log.e(MainActivity.TAG, "Generic Exception caught", e);
            }
            
            return jsonResponse;
        }
        
        @Override
        protected void onPostExecute(JSONObject result) {
            sarcatsticData = result;
            Util.updateQuips(sarcatsticData, quips, getApplicationContext(), datasource);
            sp.edit().putBoolean("stored-quips", true).commit();
        }
        
    }
}