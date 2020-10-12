package com.vmh.musicplayer.model;

import java.io.Serializable;

public class AlbumModel implements Serializable {
    private String title;
    private String artist;
    private int numberOfSongs;
    private String Path;

    public AlbumModel(String title, String artist, String path, int albumid, int numberOfSongs) {
        this.title = title;
        this.artist = artist;
        this.numberOfSongs = numberOfSongs;
        this.Path = path;
        this.albumId = albumid;
    }

    private int albumId;

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getPath() {
        return Path;
    }
}
