package com.audsnap.databaseoperations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by SONI's on 6/1/2016.
 */
public class InsertUsername extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String QUERY = "CREATE TABLE " + DatabaseConstants.TABLE_NAME
            + " (" + DatabaseConstants.FIRST_COLUMN_NAME + " TEXT); ";


    public InsertUsername(Context context)
    {
        super(context,DatabaseConstants.DB_NAME,null,DATABASE_VERSION);


    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY);
    }

    public void insertUserName(InsertUsername iu,String name)
    {
        SQLiteDatabase db = iu.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConstants.FIRST_COLUMN_NAME,name);
        db.insert(DatabaseConstants.TABLE_NAME,null,cv);
        Log.e("InsertUsername","usernameinserted successfully");
    }

    public void deleteOnLogout(InsertUsername iu)
    {
        SQLiteDatabase db = iu.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConstants.FIRST_COLUMN_NAME,"");
        db.update(DatabaseConstants.TABLE_NAME,cv,null,null);

    }

    public Cursor retriveUsername(InsertUsername iu)
    {
        SQLiteDatabase db=iu.getReadableDatabase();
        String[] columns = {DatabaseConstants.FIRST_COLUMN_NAME};
        Cursor cr = db.query(DatabaseConstants.TABLE_NAME, columns, null, null, null, null, null);
        return cr;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
