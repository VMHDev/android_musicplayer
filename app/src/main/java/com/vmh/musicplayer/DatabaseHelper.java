package com.vmh.musicplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vmh.musicplayer.model.SongModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mDatabaseInstance = null;
    private Context mContext;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "mp20_db.db";

    public static DatabaseHelper newInstance(Context context) {
        if (mDatabaseInstance == null) {
            mDatabaseInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mDatabaseInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SongModel.SCRIPT_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SongModel.TABLE_NAME);
        onCreate(db);
    }
}