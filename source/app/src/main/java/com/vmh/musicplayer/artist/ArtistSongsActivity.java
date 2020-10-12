package com.vmh.musicplayer.artist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.listsong.RecyclerItemClickListener;
import com.vmh.musicplayer.minimizeSong.MinimizeSongFragment;
import com.vmh.musicplayer.model.ArtistModel;
import com.vmh.musicplayer.model.RecentModel;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.play.PlayActivity;
import com.vmh.musicplayer.play.PlayService;
import com.vmh.musicplayer.playlist.FragmentPlaylist;
import com.vmh.musicplayer.utilities.ImageHelper;
import com.vmh.musicplayer.utilities.Utility;

import java.util.ArrayList;

public class ArtistSongsActivity extends AppCompatActivity implements MinimizeSongFragment.OnFragmentInteractionListener {
    RecyclerView RVListArtist;
    ImageView ImgProfile;
    TextView TVNameArtist;
    TextView TVSongcount;
    ArtistModel artistModel;
    Toolbar mToolbarArtistSong;
    CoordinatorLayout layoutContentArtistSong;
    PlayService mPlayService;
    AppBarLayout mAppbarLayoutArtist;
    private MinimizeSongFragment mMinimizeSongFragment;
    private static final String TAG = "ArtistSongsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_songs);

        Intent intent = getIntent();
        artistModel = (ArtistModel) intent.getSerializableExtra("infoArtist");
        InitControl();
        BindData();
        setupLayoutTransparent();
        initMimimizeSong();
    }

    private void InitControl() {
        RVListArtist = (RecyclerView) findViewById(R.id.rcvartistSong);
        ImgProfile = (ImageView) findViewById(R.id.artistSongImgProfile);
        TVNameArtist = (TextView) findViewById(R.id.artistSongNameArtist);
        TVSongcount = (TextView) findViewById(R.id.artistSongcount);
        layoutContentArtistSong = (CoordinatorLayout) findViewById(R.id.layoutContentArtistSong);
        mToolbarArtistSong = (Toolbar) findViewById(R.id.artisthtab_toolbar);
        mAppbarLayoutArtist = (AppBarLayout) findViewById(R.id.artisthtab_appbar);
        mPlayService = PlayService.newInstance();
    }

    private void setupLayoutTransparent() {
        Utility.setTransparentStatusBar(ArtistSongsActivity.this);
        Bitmap bitmapBg = null;
        Log.d(TAG, "setupLayoutTransparent: PATH " + artistModel.getPath());
        bitmapBg = ImageHelper.getBitmapFromPath(artistModel.getPath(), R.drawable.gradient_bg);
        if (bitmapBg != null) {
            Bitmap bitmapBgBlur = ImageHelper.blurBitmap(bitmapBg, 1.0f, 50);
            Bitmap bitmapOverlay = ImageHelper.createImage(bitmapBgBlur.getWidth(), bitmapBgBlur.getHeight(), getResources().getColor(R.color.colorBgPrimaryOverlay));
            Bitmap bitmapBgOverlay = ImageHelper.overlayBitmapToCenter(bitmapBgBlur, bitmapOverlay);
            layoutContentArtistSong.setBackground(ImageHelper.getMainBackgroundDrawableFromBitmap(bitmapBgOverlay));
        }

        layoutContentArtistSong.setPadding(0, Utility.getStatusbarHeight(this), 0, 0);
        setSupportActionBar(mToolbarArtistSong);
        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mToolbarArtistSong.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void BindData() {
        if (artistModel != null) {
            TVNameArtist.setText(artistModel.getName());
            TVSongcount.setText(artistModel.getSongCount() + " Bài hát");
            ImgProfile.setImageBitmap(ImageHelper.getBitmapFromPath(artistModel.getPath(), R.mipmap.ic_microphone));
            final ArrayList<SongModel> artistSongsList = ArtistProvider.getArtistSongs(this, artistModel.getName());
            ArtistSongsAdapter artistSongsAdapter = new ArtistSongsAdapter(this, artistSongsList);
            RVListArtist.setLayoutManager(new LinearLayoutManager(this));
            RVListArtist.setHasFixedSize(true);
            RVListArtist.setAdapter(artistSongsAdapter);
            RVListArtist.addOnItemTouchListener(new RecyclerItemClickListener(this, RVListArtist, new RecyclerItemClickListener.OnItemClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onItemClick(View view, int position) {
                    SongModel songModel = artistSongsList.get(position);
                    MainActivity _mainActivity = MainActivity.getMainActivity();
                    mPlayService.play(songModel);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mPlayService.initListPlaying(artistSongsList);
                        }
                    }).start();
                    _mainActivity.playSongsFromFragmentListToMain(FragmentPlaylist.SENDER);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RecentModel.addToRecent(artistModel.getName(), RecentModel.TYPE_ARTIST);
                        }
                    }).start();
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
            mAppbarLayoutArtist.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                    if (Math.abs(i) - appBarLayout.getTotalScrollRange() == 0) {
                        getSupportActionBar().setTitle(artistModel.getName());

                    } else {
                        getSupportActionBar().setTitle(" ");

                    }
                }
            });
        }
    }

    private void initMimimizeSong() {
        mMinimizeSongFragment = MinimizeSongFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.frgMinimizeSong, mMinimizeSongFragment).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMinimizeSongFragment.refreshControls(-1);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentRefreshNotification(int action) {
        if (MainActivity.getMainActivity() != null) {
            return;
        }
    }

    @Override
    public void onFragmentShowPlayActivity() {
        Intent mIntentPlayActivity = new Intent(ArtistSongsActivity.this, PlayActivity.class);
        mIntentPlayActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(mIntentPlayActivity);
    }

    @Override
    public void onFragmentLoaded(int heightLayout) {

    }
}
