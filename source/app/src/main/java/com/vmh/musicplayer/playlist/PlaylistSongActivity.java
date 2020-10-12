package com.vmh.musicplayer.playlist;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.model.PlaylistModel;
import com.vmh.musicplayer.model.RecentModel;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.play.PlayService;
import com.vmh.musicplayer.utilities.Utility;

import java.util.ArrayList;
import java.util.Objects;

public class PlaylistSongActivity extends AppCompatActivity implements MultiClickAdapterListener{

    private static PlayService mPlayService;
    private int mCurrentPlaylistId;
    private PlaylistModel mCurrentPlaylist;
    private TextView mTxtTitlePlaylist;
    private TextView mTxtNumberOfSongPlaylist;
    private Toolbar mToolbar;
    private RecyclerView mRecylerViewListSong;
    private ArrayList<SongModel> mListSong;
    private PlaylistSongAdapter mSongPlaylistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_song);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mCurrentPlaylistId = bundle.getInt("playlistId");
        }

        mCurrentPlaylist = PlaylistModel.getPlaylistById(mCurrentPlaylistId);
        mPlayService = PlayService.newInstance();
        initFindViewId();
        initToolBarParalax();
        initRecyclerViewListSong();

        mTxtTitlePlaylist.setText(mCurrentPlaylist.getTitle());
        mTxtNumberOfSongPlaylist.setText((mCurrentPlaylist.getNumberOfSongs() + "") + " bài hát");
        Toast.makeText(this, "bai" + mCurrentPlaylist.getNumberOfSongs(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void optionMenuClick(View v, int position) {
        final SongModel songChose = mListSong.get(position);
   //     Toast.makeText(this, "option" + position,Toast.LENGTH_SHORT).show();
        showBottomSheetOptionSong(songChose);
    }

    @Override
    public void checkboxClick(View v, int position) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void layoutItemClick(View v, int position) {

     //   Toast.makeText(this, "layout" + position,Toast.LENGTH_SHORT).show();
        final com.vmh.musicplayer.model.SongModel songPlay = mListSong.get(position);
        mPlayService.play(songPlay);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPlayService.initListPlaying(com.vmh.musicplayer.model.SongModel.getAllSongs(DatabaseManager.getInstance()));
                RecentModel.addToRecent(String.valueOf(mCurrentPlaylistId), RecentModel.TYPE_PLAYLIST);
                Log.d("TAG", "run: ");
            }
        }).start();

        MainActivity.getMainActivity().playSongsFromFragmentListToMain(FragmentPlaylist.SENDER);
    }

    @Override
    public void layoutItemLongClick(View v, int position) {

    }


    private void initFindViewId() {
        mRecylerViewListSong = findViewById(R.id.rcvListSongPlaylist);
        mToolbar = (Toolbar) findViewById(R.id.htab_toolbar);
        mTxtTitlePlaylist = findViewById(R.id.txtListsongTitlePlaylist);
        mTxtNumberOfSongPlaylist = findViewById(R.id.txtNumberOfSongPlaylist2);

        mTxtTitlePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTxtTitlePlaylist.setCursorVisible(true);
                mTxtTitlePlaylist.setFocusableInTouchMode(true);
                mTxtTitlePlaylist.setInputType(InputType.TYPE_CLASS_TEXT);
                mTxtTitlePlaylist.requestFocus(); //to trigger the soft input
            }
        });

    }

    private void initToolBarParalax() {
        setSupportActionBar(mToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        Utility.setTranslucentStatusBar(this);
    }

    private void initRecyclerViewListSong() {

        mListSong = PlaylistSongModel.getAllSongFromPlaylistId(mCurrentPlaylistId);
//        mListSong = new ArrayList<>();
//        for (int i = 0; i<10;i++)
//        {
//            com.vmh.musicplayer.listsong.SongModel songModel = new com.vmh.musicplayer.listsong.SongModel();
//            songModel.setId(i);
//            songModel.setSongId(i);
//            songModel.setTitle("title " + i);
//            songModel.setAlbum(i + "");
//            songModel.setArtist(i + "");
//            songModel.setFolder(i + "");
//            songModel.setDuration(12345678910L);
//            songModel.setPath(i + "");
//            songModel.setAlbumId(i);
//            mListSong.add(songModel);
//        }
  //      mListSong = SongModel.getAllSongs(MainActivity.mDatabaseManager);
        mSongPlaylistAdapter = new PlaylistSongAdapter(this, mListSong, this);
        mRecylerViewListSong.setLayoutManager(new LinearLayoutManager(this));
        mRecylerViewListSong.setHasFixedSize(true);
        mRecylerViewListSong.setAdapter(mSongPlaylistAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlist_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu1:
                //code xử lý khi bấm menu1
             //   Toast.makeText(getApplicationContext(), "menu1", Toast.LENGTH_SHORT).show();

                final BottomSheetDialog  bottomSheetDialog = new BottomSheetDialog(
                        this,R.style.BottomSheetDialogTheme
                );

//              View  view = LayoutInflater.from(this).inflate(R.layout.fragment_playlist, null, false);
//                View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.layout_action_playlist_bottom_sheet,
//                        (LinearLayout)view.findViewById(R.id.bottomSheetContainerActionPlaylist));
//                bottomSheetDialog.setContentView(bottomSheetView);
//                bottomSheetDialog.show();
                BottomSheetOptionPlaylist optionPlaylist = new  BottomSheetOptionPlaylist(mCurrentPlaylist.getId(), this);
                optionPlaylist.show( this.getSupportFragmentManager(), optionPlaylist.getTag());
                break;
//            case R.id.menu2:
//                //code xử lý khi bấm menu2
//                Toast.makeText(getApplicationContext(), "menu2", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.menu3:
//                //code xử lý khi bấm menu3
//                Toast.makeText(getApplicationContext(), "menu3", Toast.LENGTH_SHORT).show();
//                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showBottomSheetOptionSong(SongModel song) {
        BottomSheetOptionSongPlaylist bottomSheetDialogFragment = new BottomSheetOptionSongPlaylist(song, mCurrentPlaylist, this);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
    }

    public  void refreshTitlePlaylist(){
        mTxtTitlePlaylist.setText(PlaylistModel.getPlaylistById(mCurrentPlaylistId).getTitle());
    }

    public void refreshSongPlaylist() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ArrayList<SongModel> songsPlaylist = PlaylistSongModel.getAllSongFromPlaylistId(mCurrentPlaylistId);
                mListSong.clear();
                mSongPlaylistAdapter.notifyDataSetChanged();
                mListSong.addAll(songsPlaylist);
                mSongPlaylistAdapter.notifyDataSetChanged();
                mTxtNumberOfSongPlaylist.setText((mCurrentPlaylist.getNumberOfSongs() + "") + " bài hát");
            }
        });
    }
}
