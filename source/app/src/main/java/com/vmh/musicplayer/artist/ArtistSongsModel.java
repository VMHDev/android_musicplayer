package com.vmh.musicplayer.artist;

public class ArtistSongsModel {

    public ArtistSongsModel(int songId, String song, String artist, String duration) {
        SongId = songId;
        NameSong = song;
        NameSongArtist = artist;
        Duration = duration;
    }

    private int SongId;

    public void setSongId(int songId) {
        SongId = songId;
    }

    public int getSongId() {
        return SongId;
    }

    private String NameSong;

    public void setNameSong(String nameSong) {
        NameSong = nameSong;
    }

    public String getNameSong() {
        return NameSong;
    }

    private String NameSongArtist;

    public void setNameSongArtist(String nameSongArtist) {
        NameSongArtist = nameSongArtist;
    }

    public String getNameSongArtist() {
        return NameSongArtist;
    }

    private String Duration;

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getDuration() {
        return Duration;
    }

}
