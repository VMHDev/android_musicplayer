package com.vmh.musicplayer.album;

import android.graphics.Bitmap;

import com.vmh.musicplayer.model.AlbumModel;

public class AlbumViewModel extends AlbumModel {
    public AlbumViewModel(String title, String artist, String path, int albumid, int numberOfSongs) {
        super(title, artist, path, albumid, numberOfSongs);
    }

    private Bitmap bitmap = null;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public AlbumModel getAlbumModel() {
        return new AlbumModel(super.getTitle(), super.getArtist(), super.getPath(), super.getAlbumId(), super.getNumberOfSongs());
    }

}
