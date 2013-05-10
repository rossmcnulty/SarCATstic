package net.gnomeffinway.sarcatstic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    public static final String TAG = MainActivity.class.getSimpleName();
    final int MENU_CLOSE_DELAY = 5000;
    
    Quip[] quips;
    protected JSONObject sarcatsticData;

    public Respondent respondent = new Respondent();
    OutlineTextView topText;
    OutlineTextView bottomText;
    ImageButton favButton;
    ImageButton galleryButton;
    ImageButton configButton;
    //TODO: updateOnPause -> sharedPrefs
    boolean updateOnPause = false;
    boolean inUserSubmitted = false;
    boolean firstQuipShown = false;
    ProgressBar loading;
    SharedPreferences prefs;
    
    QuipsDataSource datasource;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        prefs = getSharedPreferences(SplashActivity.PREFS_FILE, MODE_PRIVATE);

        datasource = new QuipsDataSource(this);
        datasource.open();
        
        Log.v(TAG, "Contains stored-quips: " + prefs.contains("stored-quips"));
                
        boolean hasStoredQuips = prefs.getBoolean("stored-quips", false);
        // boolean forceUpdateCache = prefs.getBoolean("update-cache", false);
        
        if(hasStoredQuips) {
            Log.d(TAG, "Generating quips from storage");
            Object[] objArray = datasource.getAllQuips().toArray();
            Quip[] retrievedArray = Arrays.copyOf(objArray, objArray.length, Quip[].class);
            respondent.setQuips(retrievedArray);
        } else {
            Log.d(TAG, "No quips stored");
        }
        
        Button faceButton = (Button) findViewById(R.id.button1);
        
        favButton = (ImageButton) findViewById(R.id.favorite);
        galleryButton = (ImageButton) findViewById(R.id.gallery);
        configButton = (ImageButton) findViewById(R.id.config);
                        
        topText = (OutlineTextView) findViewById(R.id.textView1);
        bottomText = (OutlineTextView) findViewById(R.id.textView2);
        
        loading = (ProgressBar) findViewById(R.id.progressBar1);
        loading.setVisibility(View.GONE);
        
        Typeface impact = Typeface.createFromAsset(getAssets(), "fonts/impact.ttf");
        topText.setTypeface(impact);
        bottomText.setTypeface(impact);
        
        faceButton.setVisibility(View.VISIBLE);
        faceButton.setBackgroundColor(Color.TRANSPARENT);
        faceButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                handleNextResponse();
            }
        });
        
        favButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });
        
/*        galleryButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                toggleUserSubmitted();
            }
        });*/
    }
    
    @SuppressWarnings("deprecation")
    private void toggleFavorite() {
        if(!firstQuipShown)
            return;
        
        if(respondent.getCurrentQuip() == null) {
            Log.e(TAG, "Current respondent quip null, can't denote favorite status");
            return;
        }
        
        long currentId = respondent.getCurrentQuip().getWebId();
        
        if(currentId == -1)
            return;
        
        Set<String> favList = new HashSet<String>(prefs.getStringSet("favs", new HashSet<String>()));
                
        boolean favorited = favList.contains(String.valueOf(currentId));
        
        if(favorited) {
            favList.remove(String.valueOf(currentId));
            prefs.edit().putStringSet("favs", favList).commit();
            Log.d(TAG, prefs.getStringSet("favs", new HashSet<String>()).toString());
            favButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.fav_false_button));
        } else {
            favList.add(String.valueOf(currentId));
            prefs.edit().putStringSet("favs", favList).commit();
            Log.d(TAG, prefs.getStringSet("favs", new HashSet<String>()).toString());
            favButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.fav_true_button));
        }
    }
    
    @SuppressWarnings("deprecation")
    public void handleNextResponse() {
        if(!firstQuipShown)
            firstQuipShown = true;
        respondent.nextResponse(topText, bottomText);
        
        if(respondent.getCurrentQuip() == null) {
            Log.e(TAG, "Current respondent quip null, can't determine favorite status");
            return;
        }
        
        long currentId = respondent.getCurrentQuip().getWebId();
                
        Set<String> favSet = new HashSet<String>(prefs.getStringSet("favs", new HashSet<String>()));
        
        if(favSet.contains(String.valueOf(currentId))) {
            favButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.fav_true_button));
        } else {
            favButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.fav_false_button));
        }
                    
/*        boolean favorited = prefs.getBoolean("fav-" + currentId, false);
        
        if(favorited) {
            favButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.fav_true_button));
        } else {
            favButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.fav_false_button));
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateOnPause = prefs.getBoolean("pause-update", false);
        if(!updateOnPause)
            return;
        if(Util.isNetWorkAvailable(this)) {
            GetQuipsTask getPosts = new GetQuipsTask();
            getPosts.execute(false);
            updateOnPause = false;
        } else {
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }
    
    public void clearDisplay() {
        topText.setText("");
        bottomText.setText("");
    }
    
    @Override protected void onPause() {
        datasource.close();
        super.onPause();
    }
    
/*    private void toggleUserSubmitted() {
        if(!inUserSubmitted) {
            inUserSubmitted = true;
            boolean cachedUS = (getSharedPreferences(PREFS_FILE, 0).getString("quip_US_0", null) != null);
            boolean forceRefresh = getSharedPreferences(PREFS_FILE, 0).getBoolean("force_US_refresh", false);
            if(forceRefresh || !cachedUS) {
                GetQuipsTask getPostsUS = new GetQuipsTask();
                getPostsUS.execute(true);
            } else {
                respondent.setQuips(Util.retrieveArrayUS(this));
            }
            respondent.clearIndex();
            clearDisplay();
        } else {
            inUserSubmitted = false;
            respondent.setQuips(Util.retrieveArray(this));
            respondent.clearIndex();
            clearDisplay();
        }
    }*/

    public class GetQuipsTask extends AsyncTask<Boolean, Void, JSONObject> {

        boolean userSubmitted = false;
        
        @Override
        protected JSONObject doInBackground(Boolean... arg0) {
            userSubmitted = arg0[0];
            Log.v(TAG, String.valueOf(userSubmitted));
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
            if(userSubmitted) {
/*                Util.updateQuipsUS(sarcatsticData, quips, getApplicationContext());
                respondent.setQuips(Util.retrieveArrayUS(getApplicationContext()));
                respondent.clearIndex();
                clearDisplay();*/
            } else {
                Util.updateQuips(sarcatsticData, quips, getApplicationContext(), datasource);
                Object[] objArray = datasource.getAllQuips().toArray();
                Quip[] retrievedArray = Arrays.copyOf(objArray, objArray.length, Quip[].class);
                respondent.setQuips(retrievedArray);
            }
        }
        
    }

}
