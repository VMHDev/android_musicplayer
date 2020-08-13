package com.vmh.musicplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vmh.musicplayer.callbacks.MainCallbacks;
import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.listsong.FragmentListSong;
import com.vmh.musicplayer.model.ArtistModel;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.play.PlayActivity;
import com.vmh.musicplayer.play.PlayService;
import com.vmh.musicplayer.utilities.ImageHelper;
import com.vmh.musicplayer.utilities.Utility;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MainCallbacks, View.OnClickListener {

    FragmentListSong fragmentListSong;
    FrameLayout frameLayoutContainer = null;
    Handler _mainHandle = new Handler();
    //    FragmentThread fragmentThread = new FragmentThread();
    //    @BindView(R.id.btn_bottom_sheet)
    private LinearLayout mLayoutPlayingMinimizie;
    private TextView mTextViewTitleSongMinimize;
    private TextView mTextViewArtistMinimize;
    private ImageView mImageViewSongMinimize;
    private CardView mCardViewPlayingMinimize;
    private CoordinatorLayout mLayoutMainContent;
    private static MainActivity mMainActivity;
    //    @BindView(R.id.bottom_sheet)
    BottomSheetBehavior bottomSheetBehaviorPlay;


    //    private FragmentTransaction mFragmentTransaction;
    private Intent mIntentPlayActivity;


    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private Toolbar mToolBar;
    private PlayService mPlayService;


    // region Attributes final
    private final int mIconsTabDefault[] = {
            R.mipmap.ic_action_tab_recent_default,
            R.mipmap.ic_action_tab_song_default,
            R.mipmap.ic_action_tab_playlist_default,
            R.mipmap.ic_action_tab_artist_default,
            R.mipmap.ic_action_tab_album_default,
            R.mipmap.ic_action_tab_folder_default
    };
    private final int mIconsTabActive[] = {
            R.mipmap.ic_action_tab_recent_active,
            R.mipmap.ic_action_tab_song_active,
            R.mipmap.ic_action_tab_playlist_active,
            R.mipmap.ic_action_tab_artist_active,
            R.mipmap.ic_action_tab_album_active,
            R.mipmap.ic_action_tab_folder_active
    };
    private final String mTabMainTitle[] = {"Gần đây", "Bài hát", "Playlist", "Nghệ sĩ", "Album", "Thư mục"};
    //endregion


    public static DatabaseManager mDatabaseManager;

    public static MainActivity getMainActivity() {
        return mMainActivity;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    /**
     * Khởi tạo TabLayout với icon
     */
    @SuppressLint("NewApi")
    private void initTabLayoutIcon() {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            Objects.requireNonNull(mTabLayout.getTabAt(i)).setIcon(mIconsTabDefault[i]);
        }
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setIcon(mIconsTabActive[0]);
        Objects.requireNonNull(getSupportActionBar()).setTitle(mTabMainTitle[0]);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(mTabMainTitle[tab.getPosition()]);
                tab.setIcon(mIconsTabActive[tab.getPosition()]);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(mIconsTabDefault[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private static final String TAG = "MainActivity";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initFindView();

        mLayoutPlayingMinimizie.setOnClickListener(this);
        mCardViewPlayingMinimize.setOnClickListener(this);
        mMainActivity = MainActivity.this;

//        togglePlayingMinimize("MAIN");
        mPagerAdapter = new PagerMainAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, null);
        mViewPager.setOffscreenPageLimit(1);
        mTabLayout.setupWithViewPager(mViewPager);
        mPlayService = PlayService.newInstance();

        setSupportActionBar(mToolBar);
        setupLayoutTransparent();
        initDataBaseFromDevice();
        initTabLayoutIcon();
//        initMinimizePlaying();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar, menu);


        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_main).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        return true;
    }

    /**
     * làm status bar trong suốt và set padding cho layout main
     */
    private void setupLayoutTransparent() {
        Utility.setTransparentStatusBar(MainActivity.this);
        mLayoutMainContent.setPadding(0, Utility.getStatusbarHeight(this), 0, 0);
//        mLayoutMainContent.setBackground(ImageHelper.getMainBackgroundDrawable());
    }

    /**
     * Khởi tạo View
     */
    private void initFindView() {
        mToolBar = findViewById(R.id.tool_bar_main);
        mViewPager = findViewById(R.id.pagerMainContent);
        mLayoutPlayingMinimizie = findViewById(R.id.bottomSheetPlay);
        mTextViewTitleSongMinimize = findViewById(R.id.txtTitleMinimize);
        mTextViewArtistMinimize = findViewById(R.id.txtArtistMinimize);
        mImageViewSongMinimize = findViewById(R.id.imgSongMinimize);
        mCardViewPlayingMinimize = findViewById(R.id.cardViewPlayingMinimize);
        mLayoutMainContent = findViewById(R.id.mainContent);
        mTabLayout = findViewById(R.id.tablayout_main);
    }

    /**
     * Khởi tạo data đọc từ bộ nhớ
     */
    private void initDataBaseFromDevice() {
        mDatabaseManager = DatabaseManager.newInstance(getApplicationContext());
        new intitSongFromDevice().execute();
    }

    private void initMinimizePlaying() {
        Log.d(TAG, "initMinimizePlaying: ");
        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        SongModel songPlay = null;
                        songPlay = PlayService.getCurrentSongPlaying();
                        if (songPlay == null) {
                            songPlay = PlayService.getSongIsPlaying();
                        }
                        if (songPlay != null) {
                            Log.d(TAG, "initMinimizePlaying: " + songPlay.getTitle());
                            showMinimizePlaying(songPlay);
                            if (PlayService.getCurrentSongPlaying() == null) {

                                PlayService.revertListSongPlaying();
                            }
                        } else {
                            hideMinimizePlaying();
                        }
                        Log.d(TAG, "initMinimizePlaying: null");
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SongModel songModelFromArtist = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        togglePlayingMinimize("MAIN");
        initMinimizePlaying();
    }

    /**
     * Sự kiện nhấn nút quay lại
     */
    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
//            // If the user is currently looking at the first step, allow the system to handle the
//            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
//            // Otherwise, select the previous step.

            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }

    }


    /**
     * Hiện PlayActivity
     *
     * @param Sender
     */
    @Override
    public void playSongsFromFragmentListToMain(String Sender) {
//        Log.d(TAG, "playSongsFromFragmentListToMain: " + "SONG " + songPlay.getTitle() + " LIST " + songList.size());
        handleShowPlayActivityWithSongList();
    }

    /**
     * Ẩn/ hiện mimimize playing
     *
     * @param sender
     */
    @Override
    public void togglePlayingMinimize(String sender) {
        initMinimizePlaying();
    }

    private void showMinimizePlaying(SongModel songPlaying) {
        Log.d(TAG, "togglePlayingMinimize:  SONG PLAYING " + songPlaying.getTitle());

        mTextViewTitleSongMinimize.setText(songPlaying.getTitle());
        mTextViewArtistMinimize.setText(songPlaying.getArtist());
        Bitmap bitmap = ImageHelper.getBitmapFromPath(songPlaying.getPath(), R.mipmap.music_file_128);
        mImageViewSongMinimize.setImageBitmap(bitmap);
        mLayoutPlayingMinimizie.post(new Runnable() {
            @Override
            public void run() {
                mLayoutPlayingMinimizie.measure(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                Log.d(TAG, "run: SET PADDING MINIMIZE " + mLayoutPlayingMinimizie.getMeasuredHeight());
                mViewPager.setPadding(0, 0, 0, mLayoutPlayingMinimizie.getMeasuredHeight());
                mLayoutPlayingMinimizie.setVisibility(View.VISIBLE);
            }
        });
    }


    private void hideMinimizePlaying() {
        mLayoutPlayingMinimizie.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setPadding(0, 0, 0, 0);
                mLayoutPlayingMinimizie.setVisibility(View.GONE);
                Log.d(TAG, "togglePlayingMinimize: HEIGHT" + mLayoutPlayingMinimizie.getMeasuredHeight());
            }
        });
    }

    /**
     * Hiện Play Activity
     */
    private void handleShowPlayActivityWithSongList() {
        if (mIntentPlayActivity == null) {
            mIntentPlayActivity = new Intent(MainActivity.this, PlayActivity.class);
            mIntentPlayActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        startActivity(mIntentPlayActivity);
    }


    /**
     * Sự kiện khi nhấn vào minimize play
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottomSheetPlay:
            case R.id.cardViewPlayingMinimize:

                handleShowPlayActivityWithSongList();
                break;

            default:
                break;
        }
    }

    private class intitSongFromDevice extends AsyncTask<Void, Integer, ArrayList<SongModel>> {

        @Override
        protected void onPostExecute(ArrayList<SongModel> songModels) {
            FragmentListSong fragmentListSong = (FragmentListSong) ((PagerMainAdapter) mPagerAdapter).getFragmentAtIndex(1);
            if (fragmentListSong != null) {
                fragmentListSong.updateSizeOfListSong();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        public ArrayList<SongModel> doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: SIZE AUDIOS " + SongModel.getRowsSong(mDatabaseManager));
            ArrayList<SongModel> tempAudioList = SongModel.getAllAudioFromDevice(getApplicationContext());
            Log.d(TAG, "doInBackground: AUDIO " + tempAudioList.size());
            for (SongModel song : tempAudioList) {
                long id = SongModel.insertSong(mDatabaseManager, song);
                Log.d(TAG, "onPostExecute: INSERT SONG FROM MAIN : " + id);
            }
            return tempAudioList;
        }
    }
}