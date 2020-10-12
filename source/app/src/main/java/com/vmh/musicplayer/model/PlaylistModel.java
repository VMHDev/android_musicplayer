package com.vmh.musicplayer.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.playlist.PlaylistSongModel;

import java.util.ArrayList;

public class PlaylistModel {
    public static final String TABLE_NAME = "playlist";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PLAYLIST_TITLE = "title";
    public static final String COLUMN_NUMBER_OF_SONG = "number_of_song";
    public static final String COLUMN_PATH_IMAGE = "path_image";
    private static final String TAG = "PlaylistModel";

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

    /*phong*/
    public static long createPlaylist(String title) {
        SQLiteDatabase database = mDatabaseManager.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PLAYLIST_TITLE, title);
        long id = database.insert(TABLE_NAME, null, contentValues);
        database.close();
        return id;
    }

    public static ArrayList<PlaylistModel> getAllPlaylist() {
        ArrayList<PlaylistModel> playlistModels = new ArrayList<PlaylistModel>();
        SQLiteDatabase db = DatabaseManager.getInstance().getReadableDatabase();
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

        return playlistModels;
    }


    public static ArrayList<PlaylistModel> getAllPlaylist(String value) {
        ArrayList<PlaylistModel> playlistModels = new ArrayList<PlaylistModel>();
        SQLiteDatabase db = mDatabaseManager.getInstance().getReadableDatabase();

        String whereClause =  "WHERE ? = '' OR " + COLUMN_PLAYLIST_TITLE +" LIKE ?";
        String[] whereArgs = new String[]{value ,"%" + value + "%"};
        String query = "SELECT P." + COLUMN_ID + ",P." + COLUMN_PLAYLIST_TITLE + ",P." + COLUMN_PATH_IMAGE + ",COUNT(PS." + PlaylistSongModel.COLUMN_ID + ") " + COLUMN_NUMBER_OF_SONG + " from " + TABLE_NAME + " P" +
                " LEFT JOIN " + PlaylistSongModel.TABLE_NAME + " PS ON P." + COLUMN_ID + "=PS." + PlaylistSongModel.COLUMN_PLAYLIST_ID + " "
                + whereClause +
                " group by P." + COLUMN_ID + ",P." + COLUMN_PLAYLIST_TITLE + ",P." + COLUMN_PATH_IMAGE;

        Cursor cursor = db.rawQuery(query, whereArgs);
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
        return playlistModels;
    }


    public static ArrayList<PlaylistModel> getAllPlaylist(int skip, int take) {
        ArrayList<PlaylistModel> playlistModels = new ArrayList<PlaylistModel>();
        SQLiteDatabase db = DatabaseManager.getInstance().getReadableDatabase();
        String query = "SELECT P." + COLUMN_ID + ",P." + COLUMN_PLAYLIST_TITLE + ",P." + COLUMN_PATH_IMAGE + ",COUNT(PS." + PlaylistSongModel.COLUMN_ID + ") " + COLUMN_NUMBER_OF_SONG +
                " FROM ( SELECT * from " + TABLE_NAME + " P LIMIT " + skip + "," + take + ") P " +
                " LEFT JOIN " + PlaylistSongModel.TABLE_NAME + " PS  ON P." + COLUMN_ID + "=PS." + PlaylistSongModel.COLUMN_PLAYLIST_ID + "" +
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

        return playlistModels;
    }

    public static PlaylistModel getPlaylistById(int playlistId) {

        SQLiteDatabase db = DatabaseManager.getInstance().getReadableDatabase();
        String query = "SELECT P." + COLUMN_ID + ",P." + COLUMN_PLAYLIST_TITLE + ",P." + COLUMN_PATH_IMAGE + ",COUNT(PS." + PlaylistSongModel.COLUMN_ID + ") " + COLUMN_NUMBER_OF_SONG + " from " + TABLE_NAME + " P" +
                " LEFT JOIN " + PlaylistSongModel.TABLE_NAME + " PS ON P." + COLUMN_ID + "=PS." + PlaylistSongModel.COLUMN_PLAYLIST_ID + "  " +
                " WHERE P." + COLUMN_ID + " = " + playlistId + "  " +

                " group by P." + COLUMN_ID + ",P." + COLUMN_PLAYLIST_TITLE + ",P." + COLUMN_PATH_IMAGE;
        Log.d(TAG, "getPlaylistById: " + query);
        Cursor cursor = db.rawQuery(query, null);
        Log.d(TAG, "getPlaylistById: " + cursor.getCount());
        if (cursor != null) {
            cursor.moveToFirst();
            PlaylistModel playlist = new PlaylistModel();
            playlist.setId(cursor.getInt(cursor.getColumnIndex(PlaylistModel.COLUMN_ID)));
            playlist.setTitle(cursor.getString(cursor.getColumnIndex(PlaylistModel.COLUMN_PLAYLIST_TITLE)));
            playlist.setNumberOfSongs(cursor.getInt(cursor.getColumnIndex(PlaylistModel.COLUMN_NUMBER_OF_SONG)));
            playlist.setPathImage(cursor.getString(cursor.getColumnIndex(PlaylistModel.COLUMN_PATH_IMAGE)));
            return playlist;

        }

        return null;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public static long updateImageCoverPlaylist(int playlistId, String pathImage) {
        SQLiteDatabase db = DatabaseManager.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlaylistModel.COLUMN_PATH_IMAGE, pathImage);

        long id = db.update(PlaylistModel.TABLE_NAME, contentValues, PlaylistModel.COLUMN_ID + " =? ", new String[]{String.valueOf(playlistId)});
        return id;
    }

    public static PlaylistModel getInfoPlaylistById(int playlistId) {
        SQLiteDatabase db = DatabaseManager.getInstance().getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + playlistId;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            PlaylistModel playlist = new PlaylistModel();
            playlist.setId(cursor.getInt(cursor.getColumnIndex(PlaylistModel.COLUMN_ID)));
            playlist.setTitle(cursor.getString(cursor.getColumnIndex(PlaylistModel.COLUMN_PLAYLIST_TITLE)));
            playlist.setPathImage(cursor.getString(cursor.getColumnIndex(PlaylistModel.COLUMN_PATH_IMAGE)));
            return playlist;

        }

        return null;
    }

    public static long updateTitlePlaylist(PlaylistModel playlistModel) {
        SQLiteDatabase db = DatabaseManager.getInstance().getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlaylistModel.COLUMN_PLAYLIST_TITLE, playlistModel.getTitle());
        long id = db.update(PlaylistModel.TABLE_NAME, contentValues, PlaylistModel.COLUMN_ID + " =? ", new String[]{String.valueOf(playlistModel.getId())});
        return id;
    }

    public static long deletePlaylist(int playlistId) {
        SQLiteDatabase db = DatabaseManager.getInstance().getWritableDatabase();
        long result = db.delete(PlaylistModel.TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(playlistId)});
        return result;
    }
}
