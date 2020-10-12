package com.vmh.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.vmh.musicplayer.play.PlayActivity;
import com.vmh.musicplayer.play.PlayService;

public class NotifyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "NotifyBroadcastReceiver";
    public static final String ACTION_PLAY_NOTIFY = "Play_Button_Notification";
    public static final String ACTION_NEXT_NOTIFY = "Next_Button_Notification";
    public static final String ACTION_PREV_NOTIFY = "Prev_Button_Notification";
    private final PlayService mPlayService = PlayService.newInstance();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_PLAY_NOTIFY:
                if (PlayService.isPlaying()) {
                    mPlayService.pause();
                    MainActivity.getMainActivity().refreshNotificationPlaying(PlayService.ACTION_PAUSE);
                } else if (PlayService.isPause()) {
                    mPlayService.resurme();
                    MainActivity.getMainActivity().refreshNotificationPlaying(PlayService.ACTION_PLAY);
                } else {
                    mPlayService.play(PlayService.getCurrentSongPlaying());
                    MainActivity.getMainActivity().refreshNotificationPlaying(PlayService.ACTION_PLAY);
                }
                break;
            case ACTION_NEXT_NOTIFY:
                mPlayService.next(PlayService.ACTION_FROM_SYSTEM);
                MainActivity.getMainActivity().refreshNotificationPlaying(PlayService.ACTION_PLAY);
                PlayActivity.getActivity().refreshListPlaying();
                break;
            case ACTION_PREV_NOTIFY:
                mPlayService.prev(PlayService.ACTION_FROM_SYSTEM);
                MainActivity.getMainActivity().refreshNotificationPlaying(PlayService.ACTION_PLAY);
                PlayActivity.getActivity().refreshListPlaying();
                break;
            default:
                break;
        }
    }
}
