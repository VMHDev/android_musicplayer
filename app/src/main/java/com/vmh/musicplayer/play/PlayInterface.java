package com.vmh.musicplayer.play;

import com.vmh.musicplayer.model.SongModel;

public interface PlayInterface {
    void controlSong(String sender, SongModel songModel, int action);

    void updateControlPlaying(String sender, SongModel songModel);

    void updateDuration(String sender, int progress);

    void updateSeekbar(String sender, int duration);

    void updateButtonPlay(String sender);

    void updateSongPlayingList();
}
