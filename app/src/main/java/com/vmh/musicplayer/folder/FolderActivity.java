package com.vmh.musicplayer.folder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.listsong.MultiClickAdapterListener;
import com.vmh.musicplayer.minimizeSong.MinimizeSongFragment;
import com.vmh.musicplayer.model.FolderModel;
import com.vmh.musicplayer.model.RecentModel;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.play.PlayActivity;
import com.vmh.musicplayer.play.PlayService;
import com.vmh.musicplayer.playlist.BottomSheetOptionSong;
import com.vmh.musicplayer.utilities.Utility;

import java.util.ArrayList;
import java.util.Objects;

public class FolderActivity extends AppCompatActivity implements MultiClickAdapterListener, MinimizeSongFragment.OnFragmentInteractionListener {

    private Toolbar mToolbar;
    private CoordinatorLayout mLayoutSongFolder;
    private RecyclerView mRcvSongFolder;
    private TextView mTxtSongName;
    private TextView mTxtNumberOfSong;
    private AppBarLayout mAppbarLayoutFolder;
    private ArrayList<SongModel> mSongFolderList;
    private SongFolderAdapter mSongFolderAdapter;
    private FolderModel mCurrentFolder;
    private static PlayService mPlayService;
    private MinimizeSongFragment mMinimizeSongFragment;

    private final int thresholdLoad = 10;
    private static final String TAG = "FolderActivity";
    private boolean mIsLoading;
    public static final String SENDER = "ACTIVITY_FOLDER";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mCurrentFolder = (FolderModel) bundle.getSerializable("folderModel");
        }
        Log.d(TAG, "onCreate: " + mCurrentFolder.getName());
        if (mCurrentFolder == null) {
            Toast.makeText(this, "Không tìm thấy thư mục", Toast.LENGTH_LONG).show();
            finish();
        }

        initFindViewId();
        initMimimizeSong();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    private void initFindViewId() {
        mRcvSongFolder = findViewById(R.id.rcvFolderSong);
        mToolbar = (Toolbar) findViewById(R.id.htab_toolbar);
        mTxtSongName = findViewById(R.id.txtFolderName);
        mTxtNumberOfSong = findViewById(R.id.txtNumberOfSongFolder);
        mAppbarLayoutFolder = findViewById(R.id.htab_appbar);
        mLayoutSongFolder = findViewById(R.id.layoutContentFolderSong);
        mPlayService = PlayService.newInstance();
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Utility.setTransparentStatusBar(this);
        mLayoutSongFolder.setPadding(0, Utility.getStatusbarHeight(this), 0, 0);
        mTxtSongName.setText(mCurrentFolder.getName());
        mTxtNumberOfSong.setText(mCurrentFolder.getNumberOfSong() + " bài hát");

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mSongFolderList = FolderModel.getSongsFromFolderName(mCurrentFolder.getName(), 0, thresholdLoad);
                mSongFolderAdapter = new SongFolderAdapter(FolderActivity.this, mSongFolderList, FolderActivity.this);
                mRcvSongFolder.setLayoutManager(new LinearLayoutManager(FolderActivity.this));
                mRcvSongFolder.setAdapter(mSongFolderAdapter);

                mRcvSongFolder.addOnItemTouchListener(new com.vmh.musicplayer.listsong.RecyclerItemClickListener(FolderActivity.this, mRcvSongFolder, new com.vmh.musicplayer.listsong.RecyclerItemClickListener.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onItemClick(View view, int position) {
                        SongModel songModel = mSongFolderList.get(position);
                        MainActivity _mainActivity = MainActivity.getMainActivity();
                        mPlayService.play(songModel);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mPlayService.initListPlaying(mSongFolderList);
                            }
                        }).start();
                        _mainActivity.playSongsFromFragmentListToMain(SENDER);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                RecentModel.addToRecent(mCurrentFolder.getName(), RecentModel.TYPE_SONG);
                            }
                        }).start();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));
            }
        });

        mAppbarLayoutFolder.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (Math.abs(i) - appBarLayout.getTotalScrollRange() == 0) {
                    Log.d(TAG, "onOffsetChanged: COLLPASED");
                    //Collapsed
                    getSupportActionBar().setTitle(mCurrentFolder.getName());

                } else {
                    //Expanded
                    getSupportActionBar().setTitle(" ");

                }
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mRcvSongFolder.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!mIsLoading && linearLayoutManager != null && linearLayoutManager.getItemCount() - 1 == linearLayoutManager.findLastVisibleItemPosition()) {
                    loadMore();
                    mIsLoading = true;
                }
            }
        });
    }

    private void initMimimizeSong() {
        mMinimizeSongFragment = MinimizeSongFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.frgMinimizeSong, mMinimizeSongFragment).commit();

    }

    private void loadMore() {
        mSongFolderList.add(null);
        mSongFolderAdapter.notifyItemInserted(mSongFolderList.size());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ArrayList<SongModel> tempSongs = FolderModel.getSongsFromFolderName(mCurrentFolder.getName(), mSongFolderList.size(), thresholdLoad);
                mSongFolderList.remove(mSongFolderList.size() - 1);
                mSongFolderAdapter.notifyItemRemoved(mSongFolderList.size());
                mSongFolderList.addAll(tempSongs);
                mIsLoading = false;
            }
        });
    }

    @Override
    public void optionMenuClick(View v, int position) {
        final SongModel songChose = mSongFolderList.get(position);
        showBottomSheetOptionSong(songChose);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void layoutItemClick(View v, int position) {
        final SongModel songChose = mSongFolderList.get(position);
        playSong(songChose);
    }

    @Override
    public void layoutItemLongClick(View v, int position) {
        final SongModel songChose = mSongFolderList.get(position);
        showBottomSheetOptionSong(songChose);
    }

    private void showBottomSheetOptionSong(SongModel song) {
        BottomSheetOptionSong bottomSheetDialogFragment = new BottomSheetOptionSong(song);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void playSong(com.vmh.musicplayer.model.SongModel songPlay) {
        //gọi play
        mPlayService.play(songPlay);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //tạo ds phát
                mPlayService.initListPlaying(FolderModel.getAllSongsFromFolderName(mCurrentFolder.getName()));
            }
        }).start();

        MainActivity.getMainActivity().playSongsFromFragmentListToMain(SENDER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMinimizeSongFragment.refreshControls(-1);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onFragmentRefreshNotification(int action) {
        if (MainActivity.getMainActivity() != null) {
            MainActivity.getMainActivity().refreshNotificationPlaying(action);
        }
    }

    @Override
    public void onFragmentShowPlayActivity() {
        Intent mIntentPlayActivity = new Intent(FolderActivity.this, PlayActivity.class);
        mIntentPlayActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(mIntentPlayActivity);
    }

    @Override
    public void onFragmentLoaded(final int heightLayout) {
        mRcvSongFolder.setPadding(0, 0, 0, heightLayout);
    }
}
