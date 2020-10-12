package com.vmh.musicplayer.play;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.model.*;
import com.vmh.musicplayer.utilities.Utility;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity implements PlayInterface {

    private ViewPager mPager;

    private DatabaseManager mDatabaseManager;
    private CoordinatorLayout mLayoutPlay;
    private PagerAdapter mPagerAdapter;
    private PlayService mPlayService;
    private ImageView imageViewBackgroundMain;
    private MainActivity mMainActivity;
    private ArrayList<SongModel> mPlayList;

    public static final String SENDER = "PLAY_ACTIVITY";
    private static final String TAG = "PLAY_ACTIVITY";
    public static final String EXTRA_PLAYING_LIST = "EXTRA_PLAYING_LIST";

    public static final int TYPE_SHOW_NEW = 1;
    public static final int TYPE_SHOW_RESUME = 2;

    private SongModel mSongPlaying = null;
    private static PlayActivity mPlayActivity;

    public static PlayActivity getActivity() {
        return mPlayActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mLayoutPlay = findViewById(R.id.layoutPlayActivity);
        mPager = (ViewPager) findViewById(R.id.pager);

        Utility.setTranslucentStatusBar(PlayActivity.this);
        mLayoutPlay.setPadding(0, Utility.getStatusbarHeight(this), 0, 0);

        mDatabaseManager = DatabaseManager.newInstance(getApplicationContext());
        mPlayActivity = this;
        mPlayService = PlayService.newInstance();

        mSongPlaying = PlayService.getCurrentSongPlaying();
        mPagerAdapter = new FragmentPlayAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: RESTORE " + savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tabIndex", 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedPref = PlayActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("TEST", 1);
        editor.commit();
    }

    public void hidePlayActivity(View view) {
        onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void controlSong(String sender, SongModel songModel, int action) {
        switch (action) {
            case PlayService.ACTION_PLAY:
                Log.d(TAG, "controlSong: PLAY " + sender + " " + songModel.getTitle());
                mPager.setCurrentItem(1);
                mPlayService.play(songModel);
                Log.d(TAG, "controlSong: ");
                break;
            case PlayService.ACTION_PAUSE:
                mPlayService.pause();
                break;
            case PlayService.ACTION_RESUME:
                mPlayService.resurme();
                break;
            case PlayService.ACTION_PREV:
                mPlayService.prev(PlayService.ACTION_FROM_USER);
                refreshListPlaying();
                break;
            case PlayService.ACTION_NEXT:
                mPlayService.next(PlayService.ACTION_FROM_USER);
                refreshListPlaying();
                break;
            default:
                break;
        }
    }

    public void refreshListPlaying() {
        FragmentListPlaying fragmentListPlaying = ((FragmentPlayAdapter) mPagerAdapter).getFragmentListPlaying();
        if (fragmentListPlaying != null) {
            fragmentListPlaying.refreshListPlaying();
        }
    }

    @Override
    public void updateControlPlaying(String sender, SongModel songModel) {
        FragmentPlaying fragmentPlaying = ((FragmentPlayAdapter) mPagerAdapter).getFragmentPlaying();
        if (fragmentPlaying != null) {
            fragmentPlaying.updateControlPlaying(songModel);
        }
        MainActivity.getMainActivity().togglePlayingMinimize(sender);
    }

    @Override
    public void updateDuration(String sender, int progress) {
        mPlayService.updateDuration(progress);
    }

    @Override
    public void updateSeekbar(String sender, int duration) {
        FragmentPlaying fragmentPlaying = ((FragmentPlayAdapter) mPagerAdapter).getFragmentPlaying();
        if (fragmentPlaying != null) {
            fragmentPlaying.updateSeekbar(duration);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void updateButtonPlay(String sender) {
        FragmentPlaying fragmentPlaying = ((FragmentPlayAdapter) mPagerAdapter).getFragmentPlaying();
        if (fragmentPlaying != null) {
            fragmentPlaying.updateButtonPlay();
        }
    }

    @Override
    public void updateSongPlayingList() {
        FragmentListPlaying fragmentListPlaying = ((FragmentPlayAdapter) mPagerAdapter).getFragmentListPlaying();
        if (fragmentListPlaying != null) {
            fragmentListPlaying.updateListPlaying();
        }
    }
}