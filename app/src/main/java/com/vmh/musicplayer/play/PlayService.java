package com.vmh.musicplayer.play;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.model.*;

import java.io.IOException;
import java.util.ArrayList;

public class PlayService implements PlayInterface, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private static ArrayList<PlayModel> mPlayingList;
    private static ArrayList<SongModel> mSongPlayingList;
    private static SongModel mCurrentSongPlaying;
    private static SongModel mOldSongPlaying;
    private static int mCurrentIndexSong;
    private static boolean mIsPause;

    private static MediaPlayer mMediaPlayer = null;
    private static PlayService mPlayService = null;
    private static DatabaseManager mDatabaseManager = null;
    private CountDownTimer mCountDownTimerUpdateSeekBar = null;


    public static final int ACTION_PLAY = 1;
    public static final int ACTION_PAUSE = 2;
    public static final int ACTION_RESUME = 3;
    public static final int ACTION_NEXT = 4;
    public static final int ACTION_PREV = 5;

    public static final int NONE_LOOP = 1;
    public static final int ALL_LOOP = 2;
    public static final int ONE_LOOP = 3;

    public static final int ACTION_FROM_USER = 1;
    public static final int ACTION_FROM_SYSTEM = 2;


    private static int loopType = ALL_LOOP;
    public static boolean Shuffle;
    private static final String TAG = "PLAY_SERVICE";
    public static final String SENDER = "PLAY_SERVICE";

    public static PlayService newInstance() {
        if (mPlayService == null) {
            mPlayService = new PlayService();
            mMediaPlayer = new MediaPlayer();
            mDatabaseManager = DatabaseManager.getInstance();
        }
        return mPlayService;
    }

    public static int getLoopType() {
        return loopType;
    }

    public static void setLoopType(int loopType) {
        PlayService.loopType = loopType;
    }

    public void play(final SongModel songModel) {
        mIsPause = false;
        try {
            if (mOldSongPlaying == null) {
                mOldSongPlaying = songModel;
            }
            mCurrentSongPlaying = songModel;

            setIndexSongInPlayingList();
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(songModel.getPath());
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mOldSongPlaying.getSongId() != mCurrentSongPlaying.getSongId()) {
                    boolean resultUpdateStatus = PlayModel.updateStatusPlaying(mOldSongPlaying.getSongId(), mCurrentSongPlaying.getSongId());
                    Log.d(TAG, "initListPlaying: UPDATE STATUS" + resultUpdateStatus);
                }

            }
        }).start();
    }

    public void pause() {
        mMediaPlayer.pause();
        mIsPause = true;
    }

    public void resurme() {
        mIsPause = false;
        if (mCurrentSongPlaying != null && mMediaPlayer != null) {
            mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());
            mMediaPlayer.start();
            Log.d(TAG, "resurme: RESUME SONG " + mMediaPlayer.getCurrentPosition());
        } else {
            Log.d(TAG, "resurme: NOT RESUME SONG ");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void next(int actionFrom) {
        mIsPause = false;
        resetMediaPlayer();
        if (mPlayingList == null) {
            return;
        }
        mOldSongPlaying = mCurrentSongPlaying;

        if (actionFrom == ACTION_FROM_USER) {
            setNextIndexSong();
        } else {
            if (loopType == ALL_LOOP) {
                setNextIndexSong();
            } else if (loopType == ONE_LOOP) {
                mCurrentIndexSong = mCurrentIndexSong;
            } else {
                mMediaPlayer.seekTo(0);
                if (PlayActivity.getActivity() != null) {
                    PlayActivity.getActivity().updateControlPlaying(SENDER, mCurrentSongPlaying);
                    PlayActivity.getActivity().updateButtonPlay(SENDER);
                    PlayActivity.getActivity().updateSeekbar(SENDER, mMediaPlayer.getCurrentPosition());
                }
                pause();
                return;
            }
        }

        mCurrentSongPlaying = mSongPlayingList.get(mCurrentIndexSong);
        play(mCurrentSongPlaying);

        MainActivity.getMainActivity().togglePlayingMinimize(SENDER);
        if (PlayActivity.getActivity() != null) {
            PlayActivity.getActivity().updateControlPlaying(SENDER, mCurrentSongPlaying);
        }
    }

    private void resetMediaPlayer() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
            mMediaPlayer.stop();
            mCountDownTimerUpdateSeekBar.cancel();
        }
    }

    private void setNextIndexSong() {
        if (mCurrentIndexSong == mPlayingList.size() - 1) {
            mCurrentIndexSong = 0;
        } else {
            mCurrentIndexSong++;
        }
    }

    public void prev(int actionFrom) {
        mIsPause = false;
        resetMediaPlayer();
        if (mPlayingList == null || PlayActivity.getActivity() == null) {
            return;
        }
        mOldSongPlaying = mCurrentSongPlaying;
        if (mCurrentIndexSong == 0) {
            mCurrentIndexSong = mPlayingList.size() - 1;
        } else {
            mCurrentIndexSong--;
        }
        mCurrentSongPlaying = mSongPlayingList.get(mCurrentIndexSong);
        play(mCurrentSongPlaying);

        MainActivity.getMainActivity().togglePlayingMinimize(SENDER);
        if (PlayActivity.getActivity() != null) {
            PlayActivity.getActivity().updateControlPlaying(SENDER, mCurrentSongPlaying);
        }
    }

    public static int addSongsToPlayingList(ArrayList<SongModel> songs) {
        if (songs == null)
            return -1;
        for (SongModel song : songs) {
            long result = PlayModel.addSongToPlayingList(song);
        }
        updatePlayingList();
        return 1;
    }

    public static int createPlayingList(ArrayList<SongModel> songs) {
        Log.d(TAG, "createPlayingList: " + songs.size());
        PlayModel.clearPlayingList();
        PlayModel.createPlaylistFromSongs(songs);
        updatePlayingList();
        return 1;
    }

    private static int updatePlayingList() {
        mPlayingList = PlayModel.getListPlaying();
        mSongPlayingList = PlayModel.getSongPlayingList();
        setIndexSongInPlayingList();
        Log.d(TAG, "updatePlayingList: SIZE PLAYING LIST" + mPlayingList.size());
        return mPlayingList.size();
    }

    public ArrayList<PlayModel> getPlayModelsList() {
        return mPlayingList;
    }

    public static boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public static boolean isPause() {
        return mIsPause;
    }

    public static int getCurrentDuration() {
        return mMediaPlayer.getCurrentPosition();
    }

    public static SongModel getCurrentSongPlaying() {
        return mCurrentSongPlaying;
    }

    public static void revertListSongPlaying() {
        mCurrentSongPlaying = PlayModel.getSongIsPlaying();
        updatePlayingList();
    }

    public void updateDuration(int progress) {
        mMediaPlayer.seekTo(progress * 1000);
    }

    @Override
    public void controlSong(String sender, SongModel songModel, int action) {

    }

    @Override
    public void updateControlPlaying(String sender, SongModel songModel) {

    }

    @Override
    public void updateDuration(String sender, int progress) {

    }

    @Override
    public void updateSeekbar(String sender, int duration) {
        if (PlayActivity.getActivity() != null) {
            PlayActivity.getActivity().updateSeekbar(sender, duration);
        }
    }

    @Override
    public void updateButtonPlay(String sender) {

    }

    @Override
    public void updateSongPlayingList() {

    }

    private static void setIndexSongInPlayingList() {
        if (mSongPlayingList != null) {
            for (int i = 0; i < mSongPlayingList.size(); i++) {
                if (mSongPlayingList.get(i).getSongId() == mCurrentSongPlaying.getSongId()) {
                    mCurrentIndexSong = i;
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (!mMediaPlayer.isPlaying()) {
            mCountDownTimerUpdateSeekBar = new CountDownTimer(mCurrentSongPlaying.getDuration(), 1000) {
                public void onTick(long millisUntilFinished) {
                    if (mMediaPlayer.isPlaying()) {
                        Log.d(TAG, "onTick: " + millisUntilFinished + " " + mCurrentSongPlaying.getTitle());
                        updateSeekbar(SENDER, mMediaPlayer.getCurrentPosition());
                    }
                }

                public void onFinish() {
                    Log.d(TAG, "onFinish: " + mMediaPlayer.getCurrentPosition());
                }
            }.start();
        }
        mp.start();
        if (PlayActivity.getActivity() != null) {
            PlayActivity.getActivity().updateButtonPlay(SENDER);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion: NEXT -> ");
        next(ACTION_FROM_SYSTEM);
    }

    public void initListPlaying(final ArrayList<SongModel> listPlaying) {
        PlayModel.clearPlayingList();
        PlayModel.createPlaylistFromSongs(listPlaying);
        updatePlayingList();
        if (PlayActivity.getActivity() != null) {
            PlayActivity.getActivity().updateSongPlayingList();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean resultUpdateStatus = PlayModel.updateStatusPlaying(mOldSongPlaying.getSongId(), mCurrentSongPlaying.getSongId());
                Log.d(TAG, "initListPlaying: UPDATE STATUS" + resultUpdateStatus);
            }
        }).start();
    }

    public static ArrayList<SongModel> getListPlaying() {
        return mSongPlayingList;
    }

    public static SongModel getSongIsPlaying() {
        return PlayModel.getSongIsPlaying();
    }
}