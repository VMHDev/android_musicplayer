package com.vmh.musicplayer.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vmh.musicplayer.artist.ArtistViewModel;
import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.playlist.PlaylistSongModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecentModel {
    public static final String TABLE_NAME = "recent";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LINK_KEY = "link_key";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DATE = "date";

    public static final String SCRIPT_CREATE_TABLE = new StringBuilder("CREATE TABLE ")
            .append(TABLE_NAME).append("(")
            .append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(COLUMN_LINK_KEY).append(" TEXT , ")
            .append(COLUMN_TYPE).append(" INTEGER, ")
            .append(COLUMN_DATE).append(" DATETIME ")
            .append(" )")
            .toString();


    public static final int TYPE_SONG = 1;
    public static final int TYPE_ALBUM = 2;
    public static final int TYPE_ARTIST = 3;
    public static final int TYPE_PLAYLIST = 4;
    public static final int LIMIT_RECENT = 10;
    private int id;
    private String linkKey;
    private int type;
    private Date date;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLinkKey() {
        return linkKey;
    }

    public void setLinkKey(String linkKey) {
        this.linkKey = linkKey;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private static DatabaseManager mDatabaseManager = DatabaseManager.getInstance();
    private static final String TAG = "RecentModel";

    public static long addToRecent(String linkKey, int type) {
        try {
            int idRecent = getRecentId(linkKey);
            Log.d(TAG, "addToRecent: RECENT ID ADD " + idRecent);
            if (idRecent == -1) {
                SQLiteDatabase database = mDatabaseManager.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_LINK_KEY, linkKey);
                contentValues.put(COLUMN_TYPE, type);
                contentValues.put(COLUMN_DATE, getDateTimeNow());
                return database.insert(TABLE_NAME, null, contentValues);
            } else {
                return updateRecentToNow(idRecent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public static int getRecentId(String linkKey) {
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + " from " + TABLE_NAME + " WHERE " + COLUMN_LINK_KEY + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{linkKey});
        if (cursor.moveToNext()) {
            Log.d(TAG, "getRecentId: COUNT " + cursor.getCount());
            return cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        } else {
            return -1;
        }
    }

    public static long updateRecentToNow(int id) {
        SQLiteDatabase database = mDatabaseManager.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, getDateTimeNow());
        return database.update(TABLE_NAME, contentValues, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }


    public static ArrayList<SongModel> getRecentSong() {
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();


        String query = "SELECT L.* FROM " + TABLE_NAME + " R JOIN " + SongModel.TABLE_NAME + " L ON R." + COLUMN_LINK_KEY + "=L." + SongModel.COLUMN_SONG_ID +
                " WHERE R." + COLUMN_TYPE + "=" + TYPE_SONG +
                " ORDER BY R." + COLUMN_DATE + " desc " +
                " LIMIT 0," + LIMIT_RECENT;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<SongModel> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
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
                result.add(songModel);
            } while (cursor.moveToNext());

        }
        Log.d(TAG, "getRecentSong: " + result.size());
        return result;
    }

    public static ArrayList<PlaylistModel> getRecentPlaylist() {
        ArrayList<PlaylistModel> playlistModels = new ArrayList<PlaylistModel>();
        SQLiteDatabase db = DatabaseManager.getInstance().getReadableDatabase();
        String query = "SELECT P." + PlaylistModel.COLUMN_ID + ",P." + PlaylistModel.COLUMN_PLAYLIST_TITLE + ",P." + PlaylistModel.COLUMN_PATH_IMAGE + ",COUNT(PS." + PlaylistSongModel.COLUMN_ID + ") " + PlaylistModel.COLUMN_NUMBER_OF_SONG +
                " FROM (SELEcT * FROM " + PlaylistModel.TABLE_NAME + " ) P" +
                " INNER JOIN ( SELECT * from " + RecentModel.TABLE_NAME + " P WHERE " + RecentModel.COLUMN_TYPE + "=" + TYPE_PLAYLIST + " LIMIT 0," + LIMIT_RECENT + ") PR ON P." + PlaylistModel.COLUMN_ID + "=PR." + COLUMN_LINK_KEY +
                " LEFT JOIN " + PlaylistSongModel.TABLE_NAME + " PS  ON PR." + RecentModel.COLUMN_LINK_KEY + "=PS." + PlaylistSongModel.COLUMN_PLAYLIST_ID + "" +
                " GROUP BY P." + PlaylistModel.COLUMN_ID + ",P." + PlaylistModel.COLUMN_PLAYLIST_TITLE + ",P." + PlaylistModel.COLUMN_PATH_IMAGE +
                " ORDER BY PR." + RecentModel.COLUMN_DATE;
        Cursor cursor = db.rawQuery(query, null);
        Log.d(TAG, "getRecentPlaylist: " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                PlaylistModel playlist = new PlaylistModel();
                playlist.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                playlist.setTitle(cursor.getString(cursor.getColumnIndex(PlaylistModel.COLUMN_PLAYLIST_TITLE)));
                playlist.setNumberOfSongs(cursor.getInt(cursor.getColumnIndex(PlaylistModel.COLUMN_NUMBER_OF_SONG)));
                playlist.setPathImage(cursor.getString(cursor.getColumnIndex(PlaylistModel.COLUMN_PATH_IMAGE)));
                playlistModels.add(playlist);
            } while (cursor.moveToNext());
        }

        return playlistModels;
    }

    public static ArrayList<ArtistViewModel> getRecentArtist() {
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        String query = "SELECT " + SongModel.COLUMN_ARTIST + "," + SongModel.COLUMN_PATH + "," + SongModel.COLUMN_ALBUM_ID + ",COUNT(" + SongModel.COLUMN_SONG_ID + ") SongCount FROM " + TABLE_NAME + " R JOIN " + SongModel.TABLE_NAME + " L ON R." + COLUMN_LINK_KEY + "=L." + SongModel.COLUMN_ARTIST +
                " WHERE R." + COLUMN_TYPE + "=" + TYPE_ARTIST +
                " GROUP BY L." + SongModel.COLUMN_ARTIST +
                " ORDER BY R." + COLUMN_DATE + " desc " +
                " LIMIT 0," + LIMIT_RECENT;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<ArtistViewModel> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_ARTIST));
                String path = cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_PATH));
                int albumId = cursor.getInt(cursor.getColumnIndex(SongModel.COLUMN_ALBUM_ID));
                int songCount = cursor.getInt(cursor.getColumnIndex("SongCount"));

                result.add(new ArtistViewModel(name, path, albumId, songCount));
            } while (cursor.moveToNext());

        }
        return result;
    }

    private static String getDateTimeNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
