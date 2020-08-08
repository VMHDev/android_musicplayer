package com.vmh.musicplayer.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.vmh.musicplayer.DatabaseHelper;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ArtistModel {

    public ArtistModel(String name,String path,int count)
    {
        Name = name;
        Path = path;
        SongCount = count;
    }
    private Bitmap bitmap = null;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private String Path;

    public void setPath(String path) {
        Path = path;
    }

    public String getPath() {
        return Path;
    }

    private String Name;

    public void setName(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    private int SongCount;

    public void setSongCount(int songCount) {
        SongCount = songCount;
    }

    public int getSongCount() {
        return SongCount;
    }

    public static ArrayList<ArtistModel> getArtistModel(Context context){
        ArrayList<ArtistModel> result = new ArrayList<ArtistModel>();
        DatabaseHelper databaseHelper = DatabaseHelper.newInstance(context);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = MessageFormat.format("select {0},{1},COUNT({2}) from {3} group by {0}"
                ,new String[] {SongModel.COLUMN_ARTIST,SongModel.COLUMN_PATH,SongModel.COLUMN_ID,SongModel.TABLE_NAME});
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            do {
                result.add(new ArtistModel(cursor.getString(0),cursor.getString(1),cursor.getInt(2)));
            } while (cursor.moveToNext());
        }
        db.close();
        return result;
    }
}