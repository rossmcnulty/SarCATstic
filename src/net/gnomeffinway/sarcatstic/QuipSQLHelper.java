package net.gnomeffinway.sarcatstic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuipSQLHelper extends SQLiteOpenHelper {

    public static final String TABLE_QUIPS = "quips";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QUIP = "quip";
    public static final String COLUMN_WEBID = "webid";
    
    private static final String DATABASE_NAME = "quips.db";
    private static final int DATABASE_VERSION = 1;
    
    private static final String DATABASE_CREATE = "create table "
            + TABLE_QUIPS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_QUIP
            + " text not null, " + COLUMN_WEBID + " integer);";
    
    public QuipSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(QuipSQLHelper.class.getSimpleName(),
                "Upgrading database");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIPS);
        onCreate(db);
    }
    
}
