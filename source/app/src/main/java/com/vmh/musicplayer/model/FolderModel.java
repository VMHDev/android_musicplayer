package com.vmh.musicplayer.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vmh.musicplayer.database.DatabaseManager;

import java.io.Serializable;
import java.util.ArrayList;

public class FolderModel implements Serializable {
    private String name;
    private int numberOfSong;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfSong() {
        return numberOfSong;
    }

    public void setNumberOfSong(int numberOfSong) {
        this.numberOfSong = numberOfSong;
    }

    private static DatabaseManager mDatabaseManager = DatabaseManager.getInstance();
    private static final String TAG = "FolderModel";

    public static ArrayList<FolderModel> getAllFolders() {
        ArrayList<FolderModel> folderModels = new ArrayList<>();
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        String query = "SELECT " + SongModel.COLUMN_FOLDER + ",COUNT(" + SongModel.COLUMN_FOLDER + ") songCount  FROM " + SongModel.TABLE_NAME + " GROUP BY " + SongModel.COLUMN_FOLDER;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                FolderModel folderModel = new FolderModel();
                folderModel.setName(cursor.getString(0));
                folderModel.setNumberOfSong(cursor.getInt(1));
                folderModels.add(folderModel);
            } while (cursor.moveToNext());
        }
        return folderModels;
    }

    public static ArrayList<FolderModel> getAllFolders(String value) {
        ArrayList<FolderModel> folderModels = new ArrayList<>();
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        String[] tableColumns = new String[]{
                SongModel.COLUMN_FOLDER,
                "COUNT(" + SongModel.COLUMN_FOLDER + ") as songCount"
        };
        String whereClause = "? = '' OR " + SongModel.COLUMN_FOLDER + " LIKE ?";
        String[] whereArgs = new String[]{value, "%" + value + "%"};
        String groupBy = SongModel.COLUMN_FOLDER;
        Cursor cursor = db.query(SongModel.TABLE_NAME, tableColumns, whereClause, whereArgs, groupBy, null, null);
        if (cursor.moveToFirst()) {
            do {
                FolderModel folderModel = new FolderModel();
                folderModel.setName(cursor.getString(0));
                folderModel.setNumberOfSong(cursor.getInt(1));
                folderModels.add(folderModel);
            } while (cursor.moveToNext());
        }
        return folderModels;
    }

    public static ArrayList<SongModel> getSongsFromFolderName(String folderName, int skip, int take) {
        Log.d(TAG, "getSongsFromFolderName: NAME FOLDER " + folderName);
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        String query = "SELECT * FROM " + SongModel.TABLE_NAME + " WHERE " + SongModel.COLUMN_FOLDER + "='" + folderName + "' LIMIT " + skip + "," + take;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<SongModel> resultSongs = new ArrayList<>();
        Log.d(TAG, "getSongsFromFolderName: " + cursor.getCount());
        if (cursor.moveToNext()) {
            do {
                SongModel songModel = new SongModel();
                songModel.setId(cursor.getInt(cursor.getColumnIndex(SongModel.COLUMN_ID)));
                songModel.setSongId(cursor.getInt(cursor.getColumnIndex(SongModel.COLUMN_SONG_ID)));
                songModel.setTitle(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_TITLE)));
                songModel.setAlbum(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_ALBUM)));
                songModel.setArtist(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_ARTIST)));
                songModel.setFolder(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_FOLDER)));
                songModel.setDuration(cursor.getLong(cursor.getColumnIndex(SongModel.COLUMN_DURATION)));
                songModel.setPath(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_PATH)));
                resultSongs.add(songModel);
            } while (cursor.moveToNext());
        }
        Log.d(TAG, "getSongsFromFolderName: " + resultSongs.size());
        return resultSongs;
    }

    public static ArrayList<SongModel> getAllSongsFromFolderName(String folderName) {
        Log.d(TAG, "getSongsFromFolderName: NAME FOLDER " + folderName);
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        String query = "SELECT * FROM " + SongModel.TABLE_NAME + " WHERE " + SongModel.COLUMN_FOLDER + "='" + folderName + "'";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<SongModel> resultSongs = new ArrayList<>();
        Log.d(TAG, "getSongsFromFolderName: " + cursor.getCount());
        if (cursor.moveToNext()) {
            do {
                SongModel songModel = new SongModel();
                songModel.setId(cursor.getInt(cursor.getColumnIndex(SongModel.COLUMN_ID)));
                songModel.setSongId(cursor.getInt(cursor.getColumnIndex(SongModel.COLUMN_SONG_ID)));
                songModel.setTitle(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_TITLE)));
                songModel.setAlbum(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_ALBUM)));
                songModel.setArtist(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_ARTIST)));
                songModel.setFolder(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_FOLDER)));
                songModel.setDuration(cursor.getLong(cursor.getColumnIndex(SongModel.COLUMN_DURATION)));
                songModel.setPath(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_PATH)));
                resultSongs.add(songModel);
            } while (cursor.moveToNext());
        }
        Log.d(TAG, "getSongsFromFolderName: " + resultSongs.size());
        return resultSongs;
    }
}
