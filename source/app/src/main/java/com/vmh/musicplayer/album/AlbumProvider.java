package com.vmh.musicplayer.album;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.model.SongModel;

import java.text.MessageFormat;
import java.util.ArrayList;

public class AlbumProvider {
    public static ArrayList<AlbumViewModel> getListAlbum(Context context) {
        ArrayList<AlbumViewModel> arr = new ArrayList<AlbumViewModel>();
        SQLiteDatabase db = DatabaseManager.getInstance().getReadableDatabase();

        String query = MessageFormat.format("select {0},{1},{2},{3},COUNT({4}) from {5} group by {0}"
                , SongModel.COLUMN_ALBUM,
                SongModel.COLUMN_ARTIST,
                SongModel.COLUMN_PATH,
                SongModel.COLUMN_ALBUM_ID,
                SongModel.COLUMN_ID,
                SongModel.TABLE_NAME);

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                arr.add(new AlbumViewModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4)));
            } while (cursor.moveToNext());
        }
        return arr;
    }

    public static ArrayList<AlbumViewModel> getAlbumModelPaging(Context context, String value, int skip, int take) {
        ArrayList<AlbumViewModel> result = new ArrayList<AlbumViewModel>();
        DatabaseManager databaseManager = DatabaseManager.newInstance(context);
        SQLiteDatabase db = databaseManager.getReadableDatabase();
        String[] tableColumns = new String[]{
                SongModel.COLUMN_ALBUM,
                SongModel.COLUMN_ARTIST,
                SongModel.COLUMN_PATH,
                SongModel.COLUMN_ALBUM_ID,
                "COUNT(" + SongModel.COLUMN_ID + ") AS c"
        };
        String whereClause = "? = '' OR " + SongModel.COLUMN_ALBUM + " LIKE ?";
        String[] whereArgs = new String[]{value, "%" + value + "%"};
        String groupBy = String.format("%s LIMIT %d,%d", SongModel.COLUMN_ALBUM, skip, take);
        Cursor cursor = db.query(SongModel.TABLE_NAME, tableColumns, whereClause, whereArgs, groupBy, null, null);

        if (cursor.moveToFirst()) {
            do {
                result.add(new AlbumViewModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4)));
            } while (cursor.moveToNext());
        }
        return result;
    }

    public static ArrayList<SongModel> getALbumSongs(Context context, String album) {
        ArrayList<SongModel> arr = new ArrayList<SongModel>();
        SQLiteDatabase db = DatabaseManager.getInstance().getReadableDatabase();
        String query = MessageFormat.format("select {0},{1},{2},{3},{4},{5},{6},{7},{8} from {9} where {10} = ?"
                , SongModel.COLUMN_ID,
                SongModel.COLUMN_SONG_ID,
                SongModel.COLUMN_TITLE,
                SongModel.COLUMN_ALBUM,
                SongModel.COLUMN_DURATION,
                SongModel.COLUMN_FOLDER,
                SongModel.COLUMN_ARTIST,
                SongModel.COLUMN_PATH,
                SongModel.COLUMN_ALBUM_ID,
                SongModel.TABLE_NAME,
                SongModel.COLUMN_ALBUM);
        String[] whereArgs = new String[]{
                album,
        };
        Cursor cursor = db.rawQuery(query, whereArgs);
        if (cursor.moveToFirst()) {
            do {
                SongModel songModel = new SongModel();
                songModel.setId(cursor.getInt(0));
                songModel.setSongId(cursor.getInt(1));
                songModel.setTitle(cursor.getString(2));
                songModel.setAlbum(cursor.getString(3));
                songModel.setDuration(cursor.getLong(4));
                songModel.setFolder(cursor.getString(5));
                songModel.setArtist(cursor.getString(6));
                songModel.setPath(cursor.getString(7));
                songModel.setAlbumId(cursor.getInt(8));
                arr.add(songModel);
            } while (cursor.moveToNext());
        }
        return arr;
    }
}
