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
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.utilities.Utility;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity implements PlayInterface {

    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private DatabaseManager mDatabaseManager;
    private CoordinatorLayout mLayoutPlay;
    private PagerAdapter mPagerAdapter;
    private PlayService mPlayService;
    private ImageView imageViewBackgroundMain;
    private MainActivity mMainActivity;
    private ArrayList<SongModel> mPlayList;

    private static final String TAG = "PlayActivity";
    public static final String EXTRA_PLAYING_LIST = "EXTRA_PLAYING_LIST";
    public static final String SENDER = "PLAY_ACTIVITY";

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

//        mLayoutPlay.setBackground(ImageHelper.getMainBackgroundDrawable());


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
//                if (i==0){//Page list playing
//                    ((FragmentPlayAdapter) mPagerAdapter).getFragmentListPlaying().updateListPlaying();
//                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//
//
//        int typeShow = -1;
//        typeShow = bundle.getInt("TYPE_SHOW");
//        Log.d(TAG, "onCreate: TYPE SHOW" + typeShow);
//        if (typeShow == TYPE_SHOW_NEW) {
//            if (bundle.getSerializable("PLAY_LIST") != null) {
//                mPlayList = (ArrayList<SongModel>) bundle.getSerializable("PLAY_LIST");
//            } else {
//                mPlayList = (ArrayList<SongModel>) intent.getSerializableExtra(PlayActivity.EXTRA_PLAYING_LIST);
//            }
//            Log.d(TAG, "onCreate: " + "PLAY LIST " + mPlayList.size());
//            if (bundle.getSerializable("PLAY_SONG") != null) {
//                mSongPlaying = (SongModel) bundle.getSerializable("PLAY_SONG");
//            } else {
//                if (mPlayList.size() > 0) {
//                    mSongPlaying = mPlayList.get(0);
//                }
//            }
//            new InitPlaylist().execute(mPlayList);
//        } else if (typeShow == TYPE_SHOW_RESUME) {
//            Log.d(TAG, "onCreate: RESUME " + PlayService.getCurrentSongPlaying());
//            mSongPlaying = PlayService.getCurrentSongPlaying();
//            mPagerAdapter = new FragmentPlayAdapter(getSupportFragmentManager(), mSongPlaying);
//            mPager.setAdapter(mPagerAdapter);
//            ((FragmentPlayAdapter) mPagerAdapter).getFragmentPlaying().updateButtonPlay();
//            mPager.setCurrentItem(1);
//        }


//        Log.d(TAG, "onCreate: " + "PLAY SONG " + mSongPlaying.getTitle());

//        TextView textView=findViewById(R.id.txtTest);
//        Log.d(TAG, "onCreate: " + songs.size());
//        for (SongModel song : songs
//        ) {
//            Log.d(TAG, "onCreate: " + song.getSongId());
//        }
//        Toast.makeText(PlayActivity.this, songs.size()+"", Toast.LENGTH_SHORT).show();
//        mPlayService = PlayService.newInstance(PlayActivity.this.getApplicationContext(), this);
        // Instantiate a ViewPager and a PagerAdapter.


//        if (PlayService.getCurrentSongPlaying() != null) {
//            Log.d(TAG, "onCreate: SONG PLAYING " + PlayService.getCurrentSongPlaying().getTitle()
//                    + " mSONGPLAYING " + mSongPlaying.getTitle());
//        }

        //set animation for slide page
//        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
//        Log.d(TAG, "onCreate: SAVE INSTANCE STATE" + savedInstanceState);
//        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
////        int defaultValue = getResources().getInteger("TEST");
//        int highScore = sharedPref.getInt("TEST", 0);
//        Log.d(TAG, "onCreate: TEST SHARE "+ highScore);
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


//        super.onPause();
//        if (mPager.getCurrentItem() == 0) {
//            // If the user is currently looking at the first step, allow the system to handle the
//            // Back button. This calls finish() on this activity and pops the back stack.
//            super.onBackPressed();
//        } else {
//            // Otherwise, select the previous step.
//            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
//        }
    }

    public void hidePlayActivity(View view) {
//        Intent i=new Intent(this, MainActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);
        onBackPressed();
//        finish();
    }


    @Override
    public void controlSong(String sender, SongModel songModel, int action) {

        switch (action) {
            case PlayService.ACTION_PLAY:
//                if (sender.equals(FragmentListPlaying.SENDER)) {
//
//                }
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

    private void refreshListPlaying() {
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
//        ((FragmentPlayAdapter) mPagerAdapter).getFragmentPlaying().updateControlPlaying(songModel);
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
//        ((FragmentPlayAdapter) mPagerAdapter).getFragmentPlaying().updateButtonPlay();
    }

    @Override
    public void updateSongPlayingList() {
        FragmentListPlaying fragmentListPlaying = ((FragmentPlayAdapter) mPagerAdapter).getFragmentListPlaying();
        if (fragmentListPlaying != null) {
            fragmentListPlaying.updateListPlaying();
        }
    }
}