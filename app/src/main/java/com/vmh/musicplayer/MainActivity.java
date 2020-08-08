package com.vmh.musicplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.vmh.musicplayer.model.SongModel;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private CoordinatorLayout mLayoutMainContent;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private Toolbar mToolBar;

    public static DatabaseHelper mDatabaseHelper;

    private String mSearchValue = "";

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

        mViewPager = (ViewPager) findViewById(R.id.pagerMainContent);
        mPagerAdapter = new PagerMainAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, null);
        mViewPager.setOffscreenPageLimit(1);
        mToolBar = findViewById(R.id.tool_bar_main);
        setSupportActionBar(mToolBar);
        mTabLayout = findViewById(R.id.tablayout_main);
        mTabLayout.setupWithViewPager(mViewPager);

        initTabLayoutIcon();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        mDatabaseHelper = DatabaseHelper.newInstance(getApplicationContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!getApplicationContext().getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()) {
                    Log.d(TAG, "run: NOT EXIST DATABASE: ");

                    new intitSongFromDevice().execute();
                }
            }
        }).run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_main).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Tìm kiếm ...");
        return true;
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
    //endregion

    //region Functions
    /**
     * Khởi tạo Search View
     */
    private void initFindView() {
        mToolBar = findViewById(R.id.tool_bar_main);
        mViewPager = findViewById(R.id.pagerMainContent);

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

    private class intitSongFromDevice extends AsyncTask<Void, Integer, ArrayList<SongModel>> {
        @Override
        protected void onPostExecute(ArrayList<SongModel> songModels) {
            super.onPostExecute(songModels);
            for (SongModel song : songModels) {
                long id = SongModel.insertSong(mDatabaseHelper, song);
                Log.d(TAG, "onPostExecute: INSERT SONG FROM MAIN : " + id);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        public ArrayList<SongModel> doInBackground(Void... voids) {
            ArrayList<SongModel> tempAudioList = SongModel.getAllAudioFromDevice(getApplicationContext());
            return tempAudioList;
        }
    }
    //endregion
}