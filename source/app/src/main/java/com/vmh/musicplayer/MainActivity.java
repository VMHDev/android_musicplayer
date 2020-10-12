package com.vmh.musicplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.vmh.musicplayer.artist.FragmentArtist;
import com.vmh.musicplayer.callbacks.MainCallbacks;
import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.folder.FragmentFolder;
import com.vmh.musicplayer.listsong.FragmentListSong;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.play.PlayActivity;
import com.vmh.musicplayer.play.PlayService;
import com.vmh.musicplayer.playlist.FragmentPlaylist;
import com.vmh.musicplayer.utilities.ImageHelper;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MainCallbacks, View.OnClickListener {
    // region Widget main
    private CoordinatorLayout mLayoutMainContent;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private Toolbar mToolBar;
    //endregion

    // region Widget mini playing
    private LinearLayout mLayoutPlayingMinimizie;
    private CardView mCardViewPlayingMinimize;
    private TextView mTextViewTitleSongMinimize;
    private TextView mTextViewArtistMinimize;
    private ImageView mImageViewSongMinimize;

    private ImageButton mButtonPlayMinimize;
    private ImageButton mButtonNextMinimize;
    private ImageButton mButtonPrevMinimize;
    private int mCurrentFragmentActive;
    private String mSearchValue = "";
    //endregion

    //region Notification
    private static RemoteViews mNotificationlayoutPlaying;
    //endregion

    //region Play
    private Intent mIntentPlayActivity;
    private PlayService mPlayService;
    //endregion

    public static DatabaseManager mDatabaseManager;

    private static MainActivity mMainActivity;
    public static MainActivity getMainActivity() {
        return mMainActivity;
    }

    private static final String TAG = "MainActivity";

    // region Attributes final
    //region Notification
    public static final Integer PLAY_CHANEL_ID = 102;
    public static final Integer PLAY_NOTIFICATION_ID = 102;
    //endregion

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
    // region Override GUI
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

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mCurrentFragmentActive = i;
                SearchByFragment(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        initDataBaseFromDevice();
        initTabLayoutIcon();
        initMinimizePlaying();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMinimizePlaying();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Xử lý GUI Title - Search
        getMenuInflater().inflate(R.menu.top_toolbar, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_main).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Tìm kiếm ...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mSearchValue = s;
                SearchByFragment(mCurrentFragmentActive);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mSearchValue = s;
                SearchByFragment(mCurrentFragmentActive);
                return false;
            }
        });

        return true;
    }
    //endregion

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        // Xử lý khi nhấn nút back
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        //Sự kiện khi nhấn vào minimize play
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

    //region Override Callbacks
    /**
     * Hiện Activity Play để run bài hát
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void playSongsFromFragmentListToMain(String Sender) {
        refreshNotificationPlaying(PlayService.ACTION_PLAY);
        handleShowPlayActivityWithSongList();
    }

    /**
     * Ẩn/ hiện mimimize playing
     */
    @Override
    public void togglePlayingMinimize(String sender) {
        initMinimizePlaying();
    }

    /**
     * Làm mới Notification
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void refreshNotificationPlaying(int action) {
        SongModel songPlaying = PlayService.getCurrentSongPlaying();
        Log.d(TAG, "initNotificationPlay: " + songPlaying);
        if (songPlaying == null) {
            return;
        }
        createNotificationChanel();

        // Create layout notification
        mNotificationlayoutPlaying = new RemoteViews(getPackageName(), R.layout.layout_notificatoin_play);

        // Set content notification
        Bitmap bitmapBg = ImageHelper.getBitmapFromPath(songPlaying.getPath(), R.mipmap.ic_music_file);
        Bitmap bitmapBgBlur = ImageHelper.blurBitmap(bitmapBg, 2.0f, 4);
        Bitmap bitmapOverlay = ImageHelper.createImage(bitmapBgBlur.getWidth(), bitmapBgBlur.getHeight(), getResources().getColor(R.color.colorBgPrimaryOverlay));
        Bitmap bitmapBgOverlay = ImageHelper.overlayBitmapToCenter(bitmapBgBlur, bitmapOverlay);
        mNotificationlayoutPlaying.setImageViewBitmap(R.id.imgSongMinimize, bitmapBgOverlay);
        mNotificationlayoutPlaying.setTextViewText(R.id.txtTitleMinimize, songPlaying.getTitle());
        mNotificationlayoutPlaying.setTextViewText(R.id.txtArtistMinimize, songPlaying.getArtist());
        if (action != PlayService.ACTION_PAUSE) {
            mNotificationlayoutPlaying.setImageViewResource(R.id.btnPlaySong, R.drawable.ic_pause_circle_outline_black_small);
        } else {
            mNotificationlayoutPlaying.setImageViewResource(R.id.btnPlaySong, R.drawable.ic_play_circle_outline_black_small);
        }

        // Playback activity
        Intent mainIntent = new Intent(this, MainActivity.class);//mới change
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PlayActivity.class);
        stackBuilder.addNextIntent(mainIntent);


        // Intent back to activity
        PendingIntent pendingIntentPlay = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationlayoutPlaying.setOnClickPendingIntent(R.id.notificationLayout, pendingIntentPlay);

        // Intent play song
        Intent playButtonIntent = new Intent(this, NotifyBroadcastReceiver.class);
        playButtonIntent.setAction(NotifyBroadcastReceiver.ACTION_PLAY_NOTIFY);
        playButtonIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent playButtonPending = PendingIntent.getBroadcast(this, 0, playButtonIntent, 0);

        Intent nextButtonIntent = new Intent(this, NotifyBroadcastReceiver.class);
        nextButtonIntent.setAction(NotifyBroadcastReceiver.ACTION_NEXT_NOTIFY);
        nextButtonIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent nextButtonPending = PendingIntent.getBroadcast(this, 0, nextButtonIntent, 0);

        Intent prevButtonIntent = new Intent(this, NotifyBroadcastReceiver.class);
        prevButtonIntent.setAction(NotifyBroadcastReceiver.ACTION_PREV_NOTIFY);
        prevButtonIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent prevButtonPending = PendingIntent.getBroadcast(this, 0, prevButtonIntent, 0);

        mNotificationlayoutPlaying.setOnClickPendingIntent(R.id.btnPlaySong, playButtonPending);
        mNotificationlayoutPlaying.setOnClickPendingIntent(R.id.btnNextSong, nextButtonPending);
        mNotificationlayoutPlaying.setOnClickPendingIntent(R.id.btnPrevSong, prevButtonPending);

        // Intent play song
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PLAY_CHANEL_ID.toString())
                .setSmallIcon(R.drawable.ic_album_black_small)
                .setDefaults(0)
                .setContentIntent(pendingIntentPlay)
                .setContentIntent(playButtonPending)
                .setContentIntent(nextButtonPending)
                .setContentIntent(prevButtonPending)
                .setCustomContentView(mNotificationlayoutPlaying);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(PLAY_NOTIFICATION_ID, builder.build());
        refreshMinimizePlaying(action);
    }
    //endregion

    //endregion

    //region Functions
    //region GUI
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

    public void SearchByFragment(int fragmentIndex) {
        switch (fragmentIndex) {
            case 1:
                FragmentListSong fragmentListSong = (FragmentListSong) ((PagerMainAdapter) mPagerAdapter).getFragmentAtIndex(fragmentIndex);
                if (fragmentListSong != null) {
                    fragmentListSong.UpdateSearch(mSearchValue);
                }
                break;
            case 2:
                FragmentPlaylist fragmentPlaylist = (FragmentPlaylist) ((PagerMainAdapter) mPagerAdapter).getFragmentAtIndex(fragmentIndex);
                if (fragmentPlaylist != null) {
                    fragmentPlaylist.UpdateSearch(mSearchValue);
                }
                break;
            case 3:
                FragmentArtist fragmentArtist = (FragmentArtist) ((PagerMainAdapter) mPagerAdapter).getFragmentAtIndex(fragmentIndex);
                if (fragmentArtist != null) {
                    fragmentArtist.UpdateSearch(mSearchValue);
                }
                break;
            case 5:
                FragmentFolder fragmentFolder = (FragmentFolder) ((PagerMainAdapter) mPagerAdapter).getFragmentAtIndex(fragmentIndex);
                if (fragmentFolder != null) {
                    fragmentFolder.UpdateSearch(mSearchValue);
                }
                break;
            default:
                break;
        }
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
    //endregion

    //region Load Song
    /**
     * Khởi tạo data đọc từ bộ nhớ
     */
    private void initDataBaseFromDevice() {
        mDatabaseManager = DatabaseManager.newInstance(getApplicationContext());
        new intitSongFromDevice().execute();
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
    //endregion

    //region Run Song
    private void handleShowPlayActivityWithSongList() {
        if (mIntentPlayActivity == null) {
            mIntentPlayActivity = new Intent(MainActivity.this, PlayActivity.class);
            mIntentPlayActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        startActivity(mIntentPlayActivity);
    }
    //endregion

    // region Mini playing
    /**
     * Ẩn/ hiện Mini playing
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
                            PlayService.revertListSongPlaying();
                            initNotificationPlay();
                        } else {
                            hideMinimizePlaying();
                        }

                        Log.d(TAG, "initMinimizePlaying: " + songPlay);
                    }
                }
        );
    }

    /**
     * Hiện Mini playing
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
     * Ẩn Mini playing
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

    //region Notification
    private void initNotificationPlay() {
        SongModel songPlaying = PlayService.getCurrentSongPlaying();
        Log.d(TAG, "initNotificationPlay: " + songPlaying);
        if (songPlaying == null) {
            return;
        }
        createNotificationChanel();

        // Create layout notification
        mNotificationlayoutPlaying = new RemoteViews(getPackageName(), R.layout.layout_notificatoin_play);

        // Set content notification
        Bitmap bitmapBg = ImageHelper.getBitmapFromPath(songPlaying.getPath(), R.mipmap.ic_music_file);
        Bitmap bitmapBgBlur = ImageHelper.blurBitmap(bitmapBg, 2.0f, 4);
        Bitmap bitmapOverlay = ImageHelper.createImage(bitmapBgBlur.getWidth(), bitmapBgBlur.getHeight(), getResources().getColor(R.color.colorBgPrimaryOverlay));
        Bitmap bitmapBgOverlay = ImageHelper.overlayBitmapToCenter(bitmapBgBlur, bitmapOverlay);
        mNotificationlayoutPlaying.setImageViewBitmap(R.id.imgSongMinimize, bitmapBgOverlay);
        mNotificationlayoutPlaying.setTextViewText(R.id.txtTitleMinimize, songPlaying.getTitle());
        mNotificationlayoutPlaying.setTextViewText(R.id.txtArtistMinimize, songPlaying.getArtist());
        if (PlayService.isPlaying()) {
            mNotificationlayoutPlaying.setImageViewResource(R.id.btnPlaySong, R.drawable.ic_pause_circle_outline_black_small);
        } else {
            mNotificationlayoutPlaying.setImageViewResource(R.id.btnPlaySong, R.drawable.ic_play_circle_outline_black_small);
        }

        // Playback activity
        Intent mainIntent = new Intent(this, MainActivity.class);//mới change
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PlayActivity.class);
        stackBuilder.addNextIntent(mainIntent);

        // Intent back to activity
        PendingIntent pendingIntentPlay = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationlayoutPlaying.setOnClickPendingIntent(R.id.notificationLayout, pendingIntentPlay);

        // Intent play song
        Intent playButtonIntent = new Intent(this, NotifyBroadcastReceiver.class);
        playButtonIntent.setAction(NotifyBroadcastReceiver.ACTION_PLAY_NOTIFY);
        playButtonIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent playButtonPending = PendingIntent.getBroadcast(this, 0, playButtonIntent, 0);

        Intent nextButtonIntent = new Intent(this, NotifyBroadcastReceiver.class);
        nextButtonIntent.setAction(NotifyBroadcastReceiver.ACTION_NEXT_NOTIFY);
        nextButtonIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent nextButtonPending = PendingIntent.getBroadcast(this, 0, nextButtonIntent, 0);

        Intent prevButtonIntent = new Intent(this, NotifyBroadcastReceiver.class);
        prevButtonIntent.setAction(NotifyBroadcastReceiver.ACTION_PREV_NOTIFY);
        prevButtonIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent prevButtonPending = PendingIntent.getBroadcast(this, 0, prevButtonIntent, 0);

        mNotificationlayoutPlaying.setOnClickPendingIntent(R.id.btnPlaySong, playButtonPending);
        mNotificationlayoutPlaying.setOnClickPendingIntent(R.id.btnNextSong, nextButtonPending);
        mNotificationlayoutPlaying.setOnClickPendingIntent(R.id.btnPrevSong, prevButtonPending);

        // Intent play song
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PLAY_CHANEL_ID.toString())
                .setSmallIcon(R.drawable.ic_album_black_small)
                .setDefaults(0)
                .setContentIntent(pendingIntentPlay)//mới change
                .setContentIntent(playButtonPending)
                .setContentIntent(nextButtonPending)
                .setContentIntent(prevButtonPending)
                .setCustomContentView(mNotificationlayoutPlaying);//mới change

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(PLAY_NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence chanelName = PLAY_CHANEL_ID.toString();
            String description = "TEST NOTIFICATION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel chanel = new NotificationChannel(PLAY_CHANEL_ID.toString(), chanelName, importance);
            chanel.setDescription(description);
            chanel.setSound(null, null);
            chanel.setVibrationPattern(new long[]{0});
            chanel.enableVibration(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(chanel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void refreshMinimizePlaying(int action) {
        SongModel songPlay = PlayService.getCurrentSongPlaying();
        if (songPlay != null) {
            mTextViewTitleSongMinimize.setText(songPlay.getTitle());
            mTextViewArtistMinimize.setText(songPlay.getArtist());
            Bitmap bitmap = ImageHelper.getBitmapFromPath(songPlay.getPath(), R.mipmap.ic_music_file);
            mImageViewSongMinimize.setImageBitmap(bitmap);

            //update controls play
            if (action != PlayService.ACTION_PAUSE) {
                mButtonPlayMinimize.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_pause_circle_outline_black_small));
            } else {
                mButtonPlayMinimize.setImageDrawable(MainActivity.this.getDrawable(R.drawable.ic_play_circle_outline_black_small));
            }

            mLayoutPlayingMinimizie.post(new Runnable() {
                @Override
                public void run() {
                    mLayoutPlayingMinimizie.measure(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    Log.d(TAG, "run: SET PADDING MINIMIZE " + mLayoutPlayingMinimizie.getMeasuredHeight());
                    mViewPager.setPadding(0, 0, 0, mLayoutPlayingMinimizie.getMeasuredHeight() + 16);
                    mLayoutPlayingMinimizie.setVisibility(View.VISIBLE);
                }
            });
        }
    }
    //endregion

    //endregion
}