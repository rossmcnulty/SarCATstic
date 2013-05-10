package net.gnomeffinway.sarcatstic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

public class Util {

    public static final String TAG = Util.class.getSimpleName();
    
    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected())
            isAvailable = true;
        return isAvailable;
    }
    
    
    public static boolean updateQuips(JSONObject data, Quip[] quipArray, Context context, QuipsDataSource datasource) {
        if(data == null) {
            Toast.makeText(context, "Unable to retrieve data", Toast.LENGTH_LONG).show();
            return false;
        } else {
            try {
                JSONArray jsonPosts = data.getJSONArray("sarcatstic");
                quipArray = new Quip[jsonPosts.length()];
                for(int x = 0; x<jsonPosts.length(); x++) {
                    JSONObject post = jsonPosts.getJSONObject(x);
                    String title = post.getString("quip");
                    title = Html.fromHtml(title).toString();
                    long webId = post.getInt("id");
                    quipArray[x] = new Quip(title, webId);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Exception caught", e);
                return false;
            }
            saveArray(quipArray, context, datasource);
            return true;
        }
    }
    
    
/*    public static void updateQuipsUS(JSONObject data, String[] stringArray, Context context) {
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
                Log.e(TAG, "Exception caught", e);
            }
            saveArrayUS(stringArray, context);
        }
    }*/
    
    
    public static void saveArray(Quip[] quipArray, Context context, QuipsDataSource datasource) {
        for(int i=0; i<quipArray.length; i++) {
            Quip quip = quipArray[i];
            datasource.createQuip(quip.getQuip(), quip.getWebId());
        }
    }
    
/*    public static void saveArrayUS(String[] stringArray, Context context) {
        SharedPreferences storage = context.getSharedPreferences(MainActivity.PREFS_FILE, 0);
        SharedPreferences.Editor editor = storage.edit();
        
        for(int i=0; i<stringArray.length; i++) {
            editor.putString("quip_US_"+i, stringArray[i]);
        }
        
        editor.putInt("quip_US_amount", stringArray.length);
        editor.commit();
        Log.d(TAG, "Array SavedUS");
    }*/
    
/*    public static Quip[] retrieveArray(Context context, QuipsDataSource datasource) {
        
        
        
        
        SharedPreferences storage = context.getSharedPreferences(MainActivity.PREFS_FILE, 0);
        int arrayLength = storage.getInt("quip_amount", 0);
        String[] stringArray = new String[arrayLength];
        
        for(int i=0; i<arrayLength; i++) {
            stringArray[i] = storage.getString("quip_"+i, "");
        }
        
        Log.d(TAG, "Array Retrieved: " + stringArray[0]);

        return stringArray;
    }*/
    
/*    public static String[] retrieveArrayUS(Context context) {
        SharedPreferences storage = context.getSharedPreferences(MainActivity.PREFS_FILE, 0);
        int arrayLength = storage.getInt("quip_US_amount", 0);
        String[] stringArray = new String[arrayLength];
        
        for(int i=0; i<arrayLength; i++) {
            stringArray[i] = storage.getString("quip_US_"+i, "");
        }
        Log.d(TAG, "ArrayUS Retrieved: " + stringArray[0]);

        return stringArray;
    }*/
    
}
