package com.vmh.musicplayer.model;

import java.io.Serializable;

public class ArtistModel implements Serializable {

    public static final int RequestCode = 2;
    public static final String RequestCodeString = "SongFromArtist";

    public ArtistModel(String name, String path, int albumid, int count) {
        this.Name = name;
        this.SongCount = count;
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

}
