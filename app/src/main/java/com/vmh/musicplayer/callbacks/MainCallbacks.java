package com.vmh.musicplayer.callbacks;

public interface MainCallbacks {
    void playSongsFromFragmentListToMain ();
    void togglePlayingMinimize(String sender);
    void refreshNotificationPlaying(int action);
}
