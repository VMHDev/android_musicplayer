package com.vmh.musicplayer.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vmh.musicplayer.database.DatabaseManager;

import java.util.ArrayList;

public class PlaylistModel {
    public static final String TABLE_NAME = "playlist";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PLAYLIST_TITLE = "title";
    public static final String COLUMN_NUMBER_OF_SONG = "number_of_song";
    public static final String COLUMN_PATH_IMAGE = "path_image";


    public static final String SCRIPT_CREATE_TABLE = new StringBuilder("CREATE TABLE ")
            .append(TABLE_NAME).append("(")
            .append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(COLUMN_PLAYLIST_TITLE).append(" TEXT ,")
            .append(COLUMN_PATH_IMAGE).append(" TEXT ")
            .append(" )")
            .toString();
    private static DatabaseManager mDatabaseManager = DatabaseManager.getInstance();
    private int id;
    private String title;
    private String pathImage;
    private int numberOfSongs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public static long createPlaylist(String title) {
        SQLiteDatabase database = mDatabaseManager.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PLAYLIST_TITLE, title);
        long id = database.insert(TABLE_NAME, null, contentValues);
        database.close();
        return id;
    }

    public static ArrayList<PlaylistModel> getAllPlaylist() {
        ArrayList<PlaylistModel> playlistModels = new ArrayList<PlaylistModel>();
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        String query = "SELECT P." + COLUMN_ID + ",P." + COLUMN_PLAYLIST_TITLE + ",P." + COLUMN_PATH_IMAGE + ",COUNT(PS." + PlaylistSongModel.COLUMN_ID + ") " + COLUMN_NUMBER_OF_SONG + " from " + TABLE_NAME + " P" +
                " LEFT JOIN " + PlaylistSongModel.TABLE_NAME + " PS ON P." + COLUMN_ID + "=PS." + PlaylistSongModel.COLUMN_PLAYLIST_ID + "" +
                " group by P." + COLUMN_ID + ",P." + COLUMN_PLAYLIST_TITLE + ",P." + COLUMN_PATH_IMAGE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                PlaylistModel playlist = new PlaylistModel();
                playlist.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                playlist.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_PLAYLIST_TITLE)));
                playlist.setNumberOfSongs(cursor.getInt(cursor.getColumnIndex(COLUMN_NUMBER_OF_SONG)));
                playlist.setPathImage(cursor.getString(cursor.getColumnIndex(COLUMN_PATH_IMAGE)));
                playlistModels.add(playlist);
            } while (cursor.moveToNext());
        }
        db.close();
        return playlistModels;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

}