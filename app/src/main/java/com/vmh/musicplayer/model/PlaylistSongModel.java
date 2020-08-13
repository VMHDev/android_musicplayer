package com.vmh.musicplayer.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vmh.musicplayer.database.DatabaseManager;

import java.text.MessageFormat;

public class PlaylistSongModel {
    public static final String TABLE_NAME = "playlist_song";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PLAYLIST_ID = "playlist_id";
    public static final String COLUMN_SONG_ID = "song_id";

    public static final String SCRIPT_CREATE_TABLE = new StringBuilder("CREATE TABLE ")
            .append(TABLE_NAME).append("(")
            .append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(COLUMN_PLAYLIST_ID).append(" INTEGER, ")
            .append(COLUMN_SONG_ID).append(" INTEGER ")
            .append(" )")
            .toString();
    private int id;
    private int playlistId;
    private int songId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public static long addSongToPlaylist(int songId, int playlistId){
        SQLiteDatabase db = DatabaseManager.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SONG_ID, songId);
        contentValues.put(COLUMN_PLAYLIST_ID, playlistId);
        long id = db.insert(TABLE_NAME, null, contentValues);
        return id;
    }
    public static boolean isSongExisitPlaylist(int songId,int playlistId){
        SQLiteDatabase db = DatabaseManager.getInstance().getReadableDatabase();
        boolean result = false;
        String query = MessageFormat.format("SELECT {0} FROM {1} WHERE {2}={3} AND {4}={5}",
                new String[]{COLUMN_ID, TABLE_NAME, COLUMN_SONG_ID, String.valueOf(songId),COLUMN_PLAYLIST_ID,String.valueOf(playlistId)});
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            result = true;
        }
        //databaseManager.closeDatabase();
        return result;
    }
}
