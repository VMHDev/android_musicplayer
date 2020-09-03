package com.vmh.musicplayer.play;

import com.vmh.musicplayer.model.SongModel;

public interface FragmentPlayInterface {

    //Fragment ListPlaying
    void updateListPlaying();

    void refreshListPlaying();

    //Fragment Playing
    void updateControlPlaying(SongModel songModel);

    void updateSeekbar(int currentDuration);

    void updateButtonPlay();
}
