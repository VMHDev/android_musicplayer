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
import android.support.annotation.RequiresApi;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vmh.musicplayer.callbacks.MainCallbacks;
import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.listsong.FragmentListSong;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.play.PlayActivity;
import com.vmh.musicplayer.play.PlayService;
import com.vmh.musicplayer.utilities.ImageHelper;
import com.vmh.musicplayer.utilities.Utility;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MainCallbacks, View.OnClickListener {
    private CoordinatorLayout mLayoutMainContent;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private Toolbar mToolBar;

    // region Widget miniplaying
    private LinearLayout mLayoutPlayingMinimizie;
    private CardView mCardViewPlayingMinimize;
    private TextView mTextViewTitleSongMinimize;
    private TextView mTextViewArtistMinimize;
    private ImageView mImageViewSongMinimize;

    private ImageButton mButtonPlayMinimize;
    private ImageButton mButtonNextMinimize;
    private ImageButton mButtonPrevMinimize;
    //endregion

    private Intent mIntentPlayActivity;
    private PlayService mPlayService;

    public static DatabaseManager mDatabaseManager;

    private static MainActivity mMainActivity;
    public static MainActivity getMainActivity() {
        return mMainActivity;
    }

    private static final String TAG = "MainActivity";

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

    //region Methods Override
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFindView();

        // region Event Playing Minimize
        mLayoutPlayingMinimizie.setOnClickListener(this);
        mCardViewPlayingMinimize.setOnClickListener(this);
        mButtonPlayMinimize.setOnClickListener(this);
        mButtonNextMinimize.setOnClickListener(this);
        mButtonPrevMinimize.setOnClickListener(this);
        // endregion

        mMainActivity = MainActivity.this;
        mPlayService = PlayService.newInstance();

        mViewPager = (ViewPager) findViewById(R.id.pagerMainContent);
        mPagerAdapter = new PagerMainAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, null);
        mViewPager.setOffscreenPageLimit(1);
        mToolBar = findViewById(R.id.tool_bar_main);
        setSupportActionBar(mToolBar);
        mTabLayout = findViewById(R.id.tablayout_main);
        mTabLayout.setupWithViewPager(mViewPager);

        initDataBaseFromDevice();
        initTabLayoutIcon();
    }

    /**
     * Xử lý GUI Title - Search
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_main).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Tìm kiếm ...");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMinimizePlaying();
    }

    /**
     * Xử lý khi nhấn nút back
     */
    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    /**
     * Hiện Activity Play để run bài hát
     */
    @Override
    public void playSongsFromFragmentListToMain() {
        handleShowPlayActivityWithSongList();
    }

    /**
     * Ẩn/ hiện mimimize playing
     */
    @Override
    public void togglePlayingMinimize(String sender) {
        initMinimizePlaying();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Sự kiện khi nhấn vào minimize play
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottomSheetPlay:
            case R.id.cardViewPlayingMinimize:
                handleShowPlayActivityWithSongList();
                break;
            case R.id.btnPlaySong:
                SongModel songPlay = null;
                songPlay = PlayService.getCurrentSongPlaying();
                if (songPlay == null) {
                    songPlay = PlayService.getSongIsPlaying();
                }
                if (songPlay == null) {
                    Toast.makeText(MainActivity.this, "Không tìm thấy bài hát.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (PlayService.isPlaying()) {
                    mPlayService.pause();
                    mButtonPlayMinimize.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_play_circle_outline_black_small));
                } else if (PlayService.isPause()) {
                    mPlayService.resurme();
                    mButtonPlayMinimize.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_pause_circle_outline_black_small));
                } else {
                    mPlayService.play(songPlay);
                    mButtonPlayMinimize.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_pause_circle_outline_black_small));
                }
                break;
            case R.id.btnNextSong:
                mPlayService.next(PlayService.ACTION_FROM_USER);
                break;
            case R.id.btnPrevSong:
                mPlayService.prev(PlayService.ACTION_FROM_USER);
                break;
            default:
                break;
        }
    }
    //endregion

    //region Functions
    /**
     * Khởi tạo data đọc từ bộ nhớ
     */
    private void initDataBaseFromDevice() {
        mDatabaseManager = DatabaseManager.newInstance(getApplicationContext());
        new intitSongFromDevice().execute();
    }

    /**
     * Khởi tạo Widget View
     */
    private void initFindView() {
        mToolBar = findViewById(R.id.tool_bar_main);
        mViewPager = findViewById(R.id.pagerMainContent);

        //region Widget Playing Minimizie
        mLayoutPlayingMinimizie = findViewById(R.id.bottomSheetPlay);
        mCardViewPlayingMinimize = findViewById(R.id.cardViewPlayingMinimize);
        mTextViewTitleSongMinimize = findViewById(R.id.txtTitleMinimize);
        mTextViewArtistMinimize = findViewById(R.id.txtArtistMinimize);
        mImageViewSongMinimize = findViewById(R.id.imgSongMinimize);
        mButtonPlayMinimize = findViewById(R.id.btnPlaySong);
        mButtonNextMinimize = findViewById(R.id.btnNextSong);
        mButtonPrevMinimize = findViewById(R.id.btnPrevSong);
        //endregion

        mLayoutMainContent = findViewById(R.id.mainContent);
        mTabLayout = findViewById(R.id.tablayout_main);
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

    /**
     *  Khởi tạo danh sách bài hát từ thiết bị
     * */
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

    /**
     * Run bài hát
     */
    private void handleShowPlayActivityWithSongList() {
        if (mIntentPlayActivity == null) {
            mIntentPlayActivity = new Intent(MainActivity.this, PlayActivity.class);
            mIntentPlayActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        startActivity(mIntentPlayActivity);
    }

    /**
     * Ẩn/ hiện mimimize playing
     */
    private void initMinimizePlaying() {
        Log.d(TAG, "initMinimizePlaying: ");
        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    /**
     * Hiện mimimize playing
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showMinimizePlaying(SongModel songPlaying) {
        Log.d(TAG, "togglePlayingMinimize:  SONG PLAYING " + songPlaying.getTitle());

        mTextViewTitleSongMinimize.setText(songPlaying.getTitle());
        mTextViewArtistMinimize.setText(songPlaying.getArtist());
        Bitmap bitmap = ImageHelper.getBitmapFromPath(songPlaying.getPath(), R.mipmap.ic_music_file);
        mImageViewSongMinimize.setImageBitmap(bitmap);

        //region Cập icon widget playing minimize
        if (PlayService.isPlaying()) {
            mButtonPlayMinimize.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_pause_circle_outline_black_small));
        } else {
            mButtonPlayMinimize.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_play_circle_outline_black_small));
        }
        //endregion

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

    /**
     * Ẩn mimimize playing
     */
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
    //endregion
}