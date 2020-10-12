package com.vmh.musicplayer.callbacks;

public interface MainCallbacks {
    void playSongsFromFragmentListToMain(String Sender);

    void togglePlayingMinimize(String sender);

    void refreshNotificationPlaying(int action);
}