package com.vmh.musicplayer.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.vmh.musicplayer.DatabaseHelper;
import com.vmh.musicplayer.database.DatabaseManager;

import java.io.File;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;

public class SongModel implements Serializable {
    public static final String TABLE_NAME = "songs";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SONG_ID = "song_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ALBUM = "album";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_FOLDER = "folder";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_ALBUM_ID = "album_id";

    public static final String SCRIPT_CREATE_TABLE = new StringBuilder("CREATE TABLE ")
            .append(TABLE_NAME).append("(")
            .append(COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(COLUMN_SONG_ID).append(" INTEGER , ")
            .append(COLUMN_TITLE).append(" TEXT,")
            .append(COLUMN_ALBUM).append(" TEXT,")
            .append(COLUMN_ARTIST).append(" TEXT,")
            .append(COLUMN_DURATION).append(" INTEGER,")
            .append(COLUMN_FOLDER).append(" TEXT ,")
            .append(COLUMN_PATH).append(" TEXT ,")
            .append(COLUMN_ALBUM_ID).append(" INTEGER ")
            .append(" )")
            .toString();
    private static final String TAG = "SONG_MODEL";

    private String path;
    private String title;
    private String album;
    private String artist;
    private Bitmap bitmap;
    private Long duration;
    private int id;
    private int songId;
    private String folder;
    private int albumId;

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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
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
                MediaStore.Audio.AudioColumns.ALBUM_ID,

        };
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";
        Cursor c = context.getContentResolver().query(uri, projection, selection, null, sortOrder);//select .. from audio
        int debugLoop = 40;
        if (c != null) {
            int count = 0;

            while (c.moveToNext()) {// && count++<debugLoop
                count++;
//                Log.d(TAG, "getAllAudioFromDevice: " + count);
                SongModel songModel = new SongModel();
                String path = c.getString(0);//c.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)
                String name = c.getString(1);//c.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)
                String album = c.getString(2);//c.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)
                String artist = c.getString(3);//c.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)
                Long duration = c.getLong(4);//c.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)
                int songId = c.getInt(5);
                int albumId = c.getInt(c.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                Log.d(TAG, "getAllAudioFromDevice: ALBUM ID" + albumId);
                String parentPath = new File(path).getParent();
                String folder = parentPath.substring(parentPath.lastIndexOf('/') + 1);
//                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//                mediaMetadataRetriever.setDataSource(path);
//                InputStream inputStream;
//                Bitmap bitmap;
//
//
//                if (mediaMetadataRetriever.getEmbeddedPicture() != null) {
//                    inputStream = new ByteArrayInputStream(mediaMetadataRetriever.getEmbeddedPicture());
//                    mediaMetadataRetriever.release();
//                    bitmap = BitmapFactory.decodeStream(inputStream);
//                } else {
//                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.musical_note_light_64);
//                }
                songModel.setTitle(name);
                songModel.setAlbum(album);
                songModel.setArtist(artist);
                songModel.setPath(path);
                songModel.setBitmap(null);
                songModel.setDuration(duration);
                songModel.setSongId(songId);
                songModel.setFolder(folder);
                songModel.setAlbumId(albumId);
//                Log.e("Name :" + name, " Album :" + album);
//                Log.e("Path :" + path, " artist :" + artist);

                tempAudioList.add(songModel);
            }
            c.close();
        }


        return tempAudioList;
    }

    public static String formateMilliSeccond(long milliseconds) {
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

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
        return finalTimerString;
    }

    public static long insertSong(DatabaseManager databaseManager, SongModel songModel) {

        if (!isSongExsist(databaseManager, songModel)) {
            SQLiteDatabase database = databaseManager.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(SongModel.COLUMN_SONG_ID, songModel.getSongId());
            contentValues.put(SongModel.COLUMN_TITLE, songModel.getTitle());
            contentValues.put(SongModel.COLUMN_ARTIST, songModel.getArtist());
            contentValues.put(SongModel.COLUMN_ALBUM, songModel.getAlbum());
            contentValues.put(SongModel.COLUMN_DURATION, songModel.getDuration());
            contentValues.put(SongModel.COLUMN_FOLDER, songModel.getFolder());
            contentValues.put(SongModel.COLUMN_PATH, songModel.getPath());
            contentValues.put(SongModel.COLUMN_ALBUM_ID, songModel.getSongId());
            long id = database.insert(SongModel.TABLE_NAME, null, contentValues);
//            database.close();
            return id;
        }
        return 0;
    }

    public static void deleteAllSong(DatabaseManager databaseManager) {
        SQLiteDatabase database = databaseManager.getWritableDatabase();
        database.delete(TABLE_NAME, null, null);
    }

    public static boolean isSongExsist(DatabaseManager databaseManager, SongModel song) {
        SQLiteDatabase db = databaseManager.getReadableDatabase();
        boolean result = false;
        String query = MessageFormat.format("SELECT {0} FROM {1} WHERE {2}={3} ",
                new String[]{SongModel.COLUMN_ID, SongModel.TABLE_NAME, SongModel.COLUMN_SONG_ID, String.valueOf(song.getSongId())});
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            result = true;
        }
        //databaseManager.closeDatabase();
        return result;
    }

    public static SongModel getSongFromSongId(DatabaseManager databaseManager, int id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = databaseManager.getReadableDatabase();
        String[] projection = {
                SongModel.COLUMN_ID,
                SongModel.COLUMN_SONG_ID,
                SongModel.COLUMN_TITLE,
                SongModel.COLUMN_ALBUM,
                SongModel.COLUMN_DURATION,
                SongModel.COLUMN_FOLDER,
                SongModel.COLUMN_ARTIST,
                SongModel.COLUMN_PATH,
                SongModel.COLUMN_ALBUM_ID
        };
        String sortOrder = SongModel.COLUMN_ID + " ASC";
        String selection = SongModel.COLUMN_SONG_ID + " =  " + id;// ;


        Cursor cursor = db.query(SongModel.TABLE_NAME,
                projection,
                selection, null, null, null, null
        );
        SongModel songModel = new SongModel();
        if (cursor != null) {
            cursor.moveToFirst();
            songModel.setId(cursor.getInt(cursor.getColumnIndex(SongModel.COLUMN_ID)));
            songModel.setSongId(cursor.getInt(cursor.getColumnIndex(SongModel.COLUMN_SONG_ID)));
            songModel.setTitle(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_TITLE)));
            songModel.setAlbum(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_ALBUM)));
            songModel.setArtist(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_ARTIST)));
            songModel.setFolder(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_FOLDER)));
            songModel.setDuration(cursor.getLong(cursor.getColumnIndex(SongModel.COLUMN_DURATION)));
            songModel.setPath(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_PATH)));
            songModel.setAlbumId(cursor.getInt(cursor.getColumnIndex(SongModel.COLUMN_ALBUM_ID)));
            //databaseManager.closeDatabase();

            return songModel;
        } else {
            return null;
        }
        //Log.d(TAG, "getSong: " + songModel.getSongId() + " _ " + songModel.getTitle() + " _ " + songModel.getAlbum());
        // close the db connection
    }


    public static ArrayList<SongModel> getAllSongs(DatabaseManager databaseManager) {
        ArrayList<SongModel> songModelList = new ArrayList<>();
        SQLiteDatabase db = databaseManager.getReadableDatabase();
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
                songModel.setDuration(cursor.getLong(cursor.getColumnIndex(SongModel.COLUMN_DURATION)));
                songModel.setPath(cursor.getString(cursor.getColumnIndex(SongModel.COLUMN_PATH)));
                songModelList.add(songModel);
            } while (cursor.moveToNext());

        }
        //databaseManager.closeDatabase();
        return songModelList;
    }

    public static long getRowsSong(DatabaseManager databaseManager) {
        SQLiteDatabase db = databaseManager.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);

        return count;
    }

    public static ArrayList<SongModel> getSongsWithThreshold(DatabaseManager databaseManager, int skip, int count) {
        ArrayList<SongModel> songModelList = new ArrayList<>();
        SQLiteDatabase db = databaseManager.getReadableDatabase();
        String query = "SELECT * FROM " + SongModel.TABLE_NAME + " ORDER BY "+SongModel.COLUMN_TITLE+" ASC  LIMIT " + skip + "," + count;
        Cursor cursor = db.rawQuery(query, null);
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
                songModel.setAlbumId(cursor.getInt(cursor.getColumnIndex(SongModel.COLUMN_ALBUM_ID)));
                Log.d(TAG, "getSongsWithThreshold: HOLD ALBUMID" + songModel.getAlbumId());
                songModelList.add(songModel);
            } while (cursor.moveToNext());

        }
        //databaseManager.closeDatabase();
        return songModelList;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
}