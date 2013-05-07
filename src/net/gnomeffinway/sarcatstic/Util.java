package net.gnomeffinway.sarcatstic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

public class Util {

    
    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected())
            isAvailable = true;
        return isAvailable;
    }
    
    
    public static void updateQuips(JSONObject data, String[] stringArray, Context context) {
        if(data == null) {
            Toast.makeText(context, "Unable to retrieve data", Toast.LENGTH_LONG).show();
        } else {
            try {
                JSONArray jsonPosts = data.getJSONArray("sarcatstic");
                stringArray = new String[jsonPosts.length()];
                for(int x = 0; x<jsonPosts.length(); x++) {
                    JSONObject post = jsonPosts.getJSONObject(x);
                    String title = post.getString("quip");
                    title = Html.fromHtml(title).toString();
                    stringArray[x] = title;
                }
            } catch (JSONException e) {
                Log.e(MainActivity.TAG, "Exception caught", e);
            }
            saveArray(stringArray, context);
        }
    }
    
    
    public static void updateQuipsUS(JSONObject data, String[] stringArray, Context context) {
        if(data == null) {
            Toast.makeText(context, "Unable to retrieve data", Toast.LENGTH_LONG).show();
        } else {
            try {
                JSONArray jsonPosts = data.getJSONArray("user-submitted");
                stringArray = new String[jsonPosts.length()];
                for(int x = 0; x<jsonPosts.length(); x++) {
                    JSONObject post = jsonPosts.getJSONObject(x);
                    String title = post.getString("quip");
                    title = Html.fromHtml(title).toString();
                    stringArray[x] = title;
                }
            } catch (JSONException e) {
                Log.e(MainActivity.TAG, "Exception caught", e);
            }
            saveArrayUS(stringArray, context);
        }
    }
    
    
    public static void saveArray(String[] stringArray, Context context) {
        SharedPreferences storage = context.getSharedPreferences(MainActivity.PREFS_FILE, 0);
        SharedPreferences.Editor editor = storage.edit();
        
        for(int i=0; i<stringArray.length; i++) {
            editor.putString("quip_"+i, stringArray[i]);
        }
        
        editor.putInt("quip_amount", stringArray.length);
        editor.commit();
        Log.d(MainActivity.TAG, "Array Saved");
    }
    
    public static void saveArrayUS(String[] stringArray, Context context) {
        SharedPreferences storage = context.getSharedPreferences(MainActivity.PREFS_FILE, 0);
        SharedPreferences.Editor editor = storage.edit();
        
        for(int i=0; i<stringArray.length; i++) {
            editor.putString("quip_US_"+i, stringArray[i]);
        }
        
        editor.putInt("quip_US_amount", stringArray.length);
        editor.commit();
        Log.d(MainActivity.TAG, "Array SavedUS");
    }
    
    public static String[] retrieveArray(Context context) {
        SharedPreferences storage = context.getSharedPreferences(MainActivity.PREFS_FILE, 0);
        int arrayLength = storage.getInt("quip_amount", 0);
        String[] stringArray = new String[arrayLength];
        
        for(int i=0; i<arrayLength; i++) {
            stringArray[i] = storage.getString("quip_"+i, "");
        }
        Log.d(MainActivity.TAG, "Array Retrieved: " + stringArray[0]);

        return stringArray;
    }
    
    public static String[] retrieveArrayUS(Context context) {
        SharedPreferences storage = context.getSharedPreferences(MainActivity.PREFS_FILE, 0);
        int arrayLength = storage.getInt("quip_US_amount", 0);
        String[] stringArray = new String[arrayLength];
        
        for(int i=0; i<arrayLength; i++) {
            stringArray[i] = storage.getString("quip_US_"+i, "");
        }
        Log.d(MainActivity.TAG, "ArrayUS Retrieved: " + stringArray[0]);

        return stringArray;
    }
    
}
