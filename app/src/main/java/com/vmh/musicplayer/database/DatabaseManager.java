package com.vmh.musicplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vmh.musicplayer.model.PlayModel;
import com.vmh.musicplayer.model.PlaylistModel;
import com.vmh.musicplayer.model.RecentModel;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.playlist.PlaylistSongModel;

public class DatabaseManager extends SQLiteOpenHelper {

    private static DatabaseManager mDatabaseInstance = null;

    private SQLiteDatabase mDatabase;
    private Context mContext;
    private int mOpenCounter;

    public static DatabaseManager getInstance() {
        return mDatabaseInstance;
    }

    // Database Version
    private static final int DATABASE_VERSION = 10;

    // Database Name
    public static final String DATABASE_NAME = "mp20_db.db";

    public static DatabaseManager newInstance(Context context) {
        if (mDatabaseInstance == null) {
            mDatabaseInstance = new DatabaseManager(context.getApplicationContext());
        }
        return mDatabaseInstance;
    }

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SongModel.SCRIPT_CREATE_TABLE);
        db.execSQL(PlayModel.SCRIPT_CREATE_TABLE);
        db.execSQL(PlaylistModel.SCRIPT_CREATE_TABLE);
        db.execSQL(PlaylistSongModel.SCRIPT_CREATE_TABLE);
        db.execSQL(RecentModel.SCRIPT_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SongModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlayModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlaylistModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlaylistSongModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecentModel.TABLE_NAME);
        onCreate(db);
    }

    public void resetDB() {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + SongModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlayModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlaylistModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlaylistSongModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecentModel.TABLE_NAME);
        onCreate(db);
    }
}