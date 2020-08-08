package com.vmh.musicplayer.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.vmh.musicplayer.DatabaseHelper;

import java.io.File;
import java.util.ArrayList;

public class SongModel {
    public static final String TABLE_NAME = "songs";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ALBUM = "album";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_FOLDER = "folder";
    public static final String COLUMN_PATH = "path";

    public static final String SCRIPT_CREATE_TABLE = new StringBuilder("CREATE TABLE ")
            .append(TABLE_NAME).append("(")
            .append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(COLUMN_SONG_ID).append(" INTEGER , ")
            .append(COLUMN_TITLE).append(" TEXT,")
            .append(COLUMN_ALBUM).append(" TEXT,")
            .append(COLUMN_ARTIST).append(" TEXT,")
            .append(COLUMN_DURATION).append(" TEXT,")
            .append(COLUMN_FOLDER).append(" TEXT ,")
            .append(COLUMN_PATH).append(" TEXT ")
            .append(" )")
            .toString();
    private static final String TAG = "SONG_MODEL";

    private String path;
    private String title;
    private String album;
    private String artist;
    private Bitmap bitmap;
    private String duration;
    private int id;
    private int songId;
    private String folder;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public static ArrayList<SongModel> getAllAudioFromDevice(final Context context) {
        final ArrayList<SongModel> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.AudioColumns.DATA,//path
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns._ID,
        };

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";
        Cursor c = context.getContentResolver().query(uri, projection, selection, null, sortOrder);//select .. from audio
        int debugLoop = 40;
        if (c != null) {
            int count = 0;

            while (c.moveToNext()) {
                count++;
                SongModel songModel = new SongModel();
                String path = c.getString(0);//c.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)
                String name = c.getString(1);//c.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)
                String album = c.getString(2);//c.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)
                String artist = c.getString(3);//c.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)
                String duration = c.getString(4);//c.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)
                int songId = c.getInt(5);
                String parentPath = new File(path).getParent();
                String folder = parentPath.substring(parentPath.lastIndexOf('/') + 1);

                songModel.setTitle(name);
                songModel.setAlbum(album);
                songModel.setArtist(artist);
                songModel.setPath(path);
                songModel.setBitmap(null);
                songModel.setDuration(formateMilliSeccond(Long.valueOf(duration)));
                songModel.setSongId(songId);
                songModel.setFolder(folder);

                tempAudioList.add(songModel);
            }
            c.close();
        }

        return tempAudioList;
    }

    private static String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    public static long insertSong(DatabaseHelper databaseHelper, SongModel songModel) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SongModel.COLUMN_SONG_ID, songModel.getSongId());
        contentValues.put(SongModel.COLUMN_TITLE, songModel.getTitle());
        contentValues.put(SongModel.COLUMN_ARTIST, songModel.getArtist());
        contentValues.put(SongModel.COLUMN_ALBUM, songModel.getAlbum());
        contentValues.put(SongModel.COLUMN_DURATION, songModel.getDuration());
        contentValues.put(SongModel.COLUMN_FOLDER, songModel.getFolder());
        contentValues.put(SongModel.COLUMN_PATH, songModel.getPath());

        long id = database.insert(SongModel.TABLE_NAME, null, contentValues);
        database.close();
        return id;
    }

    public static ArrayList<SongModel> getAllSongs(DatabaseHelper databaseHelper) {
        ArrayList<SongModel> songModelList = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {
                SongModel.COLUMN_ID,
                SongModel.COLUMN_SONG_ID,
                SongModel.COLUMN_TITLE,
                SongModel.COLUMN_ALBUM,
                SongModel.COLUMN_DURATION,
                SongModel.COLUMN_FOLDER,
                SongModel.COLUMN_ARTIST,
                SongModel.COLUMN_PATH
        };
        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                SongModel songModel = new SongModel();
                songModel.setId(cursor.getInt(cursor.getColumnIndex(SongModel.COLUMN_ID)));
                songModel.setSongId(cursor.getInt(cursor.getColumnIndex(SongModel.COLUMN_SONG_ID)));
                songModel.setTitle(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_TITLE)));
                songModel.setAlbum(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_ALBUM)));
                songModel.setArtist(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_ARTIST)));
                songModel.setFolder(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_FOLDER)));
                songModel.setDuration(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_DURATION)));
                songModel.setPath(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_PATH)));
                songModelList.add(songModel);
            } while (cursor.moveToNext());

        }
        cursor.close();
        return songModelList;
    }
}
