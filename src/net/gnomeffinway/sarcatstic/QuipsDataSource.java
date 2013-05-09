package net.gnomeffinway.sarcatstic;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class QuipsDataSource {
    
    private SQLiteDatabase database;
    private QuipSQLHelper dbHelper;
    private String[] allColumns = { QuipSQLHelper.COLUMN_ID, 
            QuipSQLHelper.COLUMN_QUIP, QuipSQLHelper.COLUMN_WEBID };

    public QuipsDataSource(Context context) {
        dbHelper = new QuipSQLHelper(context);
    }
    
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    
    public void close() {
        dbHelper.close();
    }
    
    public Quip createQuip(String quip, long webId) {
        ContentValues values = new ContentValues();
        values.put(QuipSQLHelper.COLUMN_QUIP, quip);
        values.put(QuipSQLHelper.COLUMN_WEBID, webId);
        long insertId = database.insert(QuipSQLHelper.TABLE_QUIPS, null, values);
        Cursor cursor = database.query(QuipSQLHelper.TABLE_QUIPS,
                allColumns, QuipSQLHelper.COLUMN_ID + " = " + insertId, null, 
                null, null, null);
        cursor.moveToFirst();
        Quip newQuip = cursorToQuip(cursor);
        cursor.close();
        return newQuip;
    }
    
    public void deleteQuip(long webId) {
        long id = webId;
        database.delete(QuipSQLHelper.TABLE_QUIPS, QuipSQLHelper.COLUMN_WEBID
                + " = " + id, null);
    }
    
    public List<Quip> getAllQuips() {
        List<Quip> quips = new ArrayList<Quip>();
        
        Cursor cursor = database.query(QuipSQLHelper.TABLE_QUIPS, allColumns,
                null, null, null, null, null);
        
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Quip quip = cursorToQuip(cursor);
            quips.add(quip);
            cursor.moveToNext();
        }
        
        cursor.close();
        return quips;
    }
    
    private Quip cursorToQuip(Cursor cursor) {
        Quip quip = new Quip();
        quip.setId(cursor.getLong(0));
        quip.setQuip(cursor.getString(1));
        quip.setWebId(cursor.getLong(2));
        return quip;
    }
}
