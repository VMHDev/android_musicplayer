package com.vmh.musicplayer.recent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.ArtistModel;
import com.vmh.musicplayer.artist.ArtistSongsActivity;
import com.vmh.musicplayer.artist.ArtistViewModel;
import com.vmh.musicplayer.listsong.MultiClickAdapterListener;
import com.vmh.musicplayer.listsong.RecyclerItemClickListener;
import com.vmh.musicplayer.model.RecentModel;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.play.PlayService;
import com.vmh.musicplayer.playlist.BottomSheetOptionSong;
import com.vmh.musicplayer.playlist.FragmentPlaylist;
import com.vmh.musicplayer.model.PlaylistModel;
import com.vmh.musicplayer.playlist.PlaylistSongActivity;

import java.util.ArrayList;

public class FragmentRecent extends Fragment implements MultiClickAdapterListener {
    private static final String TAG = "FRAGMENT_RECENT";
    public static final String SENDER = "FRAGMENT_RECENT";

    private RecyclerView mRcvSongRecent;
    private RecyclerView mRcvPlaylistRecent;
    private RecyclerView mRcvArtistRecent;

    private ListSongRecentAdaper mListSongRecentAdapter;
    private PlaylistRecentAdapter mPlaylistRecentAdapter;
    private ArtistRecentAdapter mArtistRecentAdapter;

    private ArrayList<SongModel> mListSongRecent;
    private ArrayList<PlaylistModel> mListPlaylistRecent;
    private ArrayList<ArtistViewModel> mListArtistRecent;

    private static Context mContext;
    private static MainActivity mMainActivity;
    private static PlayService mPlayService;
    private static FragmentRecent mFragmentRecent;
    private boolean isLoadedSong, isLoadedPlaylist, isLoadedArtist;

    public FragmentRecent() {}

    public static FragmentRecent newIntance(){
        mFragmentRecent = new FragmentRecent();
        return mFragmentRecent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mContext = getActivity();
            mMainActivity = (MainActivity) getActivity();
            mPlayService = PlayService.newInstance();
        } catch (IllegalStateException e) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_recent, container, false);
        mRcvSongRecent = view.findViewById(R.id.rcvSongRecent);
        mRcvPlaylistRecent = view.findViewById(R.id.rcvPlaylistRecent);
        mRcvArtistRecent = view.findViewById(R.id.rcvArtistRecent);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mListSongRecent = RecentModel.getRecentSong();
                mListSongRecentAdapter = new ListSongRecentAdaper(mContext, mListSongRecent, FragmentRecent.this);
                mRcvSongRecent.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.VERTICAL, false));
                mRcvSongRecent.setAdapter(mListSongRecentAdapter);
                isLoadedSong = true;
            }
        });
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mListPlaylistRecent = RecentModel.getRecentPlaylist();
                mPlaylistRecentAdapter = new PlaylistRecentAdapter(mContext, mListPlaylistRecent);
                mRcvPlaylistRecent.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false));
                mRcvPlaylistRecent.setAdapter(mPlaylistRecentAdapter);
                isLoadedPlaylist = true;
            }
        });
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mListArtistRecent = RecentModel.getRecentArtist();
                mArtistRecentAdapter = new ArtistRecentAdapter(mContext, mListArtistRecent);
                mRcvArtistRecent.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false));
                mRcvArtistRecent.setAdapter(mArtistRecentAdapter);
                isLoadedArtist = true;
            }
        });

        mRcvPlaylistRecent.addOnItemTouchListener(new RecyclerItemClickListener(mContext, mRcvPlaylistRecent, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // do whatever
                Toast.makeText(mContext, "Playlist", Toast.LENGTH_SHORT).show();
                showPlaylistSongActivity(mListPlaylistRecent.get(position).getId());
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // do whatever
//                Toast.makeText(mContext, "LONG CLICK ITEM SONG" + position, Toast.LENGTH_SHORT).show();
            }
        }));
        mRcvArtistRecent.addOnItemTouchListener(new RecyclerItemClickListener(mContext, mRcvArtistRecent, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, ArtistSongsActivity.class);
                ArtistModel artistModel = mListArtistRecent.get(position).getArtistModel();
                intent.putExtra("infoArtist", artistModel);
                startActivityForResult(intent, ArtistModel.RequestCode);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void showPlaylistSongActivity(int playlistId) {
        Intent intent = new Intent(MainActivity.getMainActivity(), PlaylistSongActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("playlistId", playlistId);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onResume() {
        super.onResume();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (isLoadedSong) {
                    ArrayList<SongModel> tempSongs = RecentModel.getRecentSong();
                    mListSongRecent.clear();
                    mListSongRecent.addAll(tempSongs);
                    mRcvSongRecent.post(new Runnable() {
                        @Override
                        public void run() {
                            mListSongRecentAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        });
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (isLoadedPlaylist) {
                    ArrayList<PlaylistModel> tempPlaylists = RecentModel.getRecentPlaylist();
                    mListPlaylistRecent.clear();
                    mListPlaylistRecent.addAll(tempPlaylists);
                    mRcvPlaylistRecent.post(new Runnable() {
                        @Override
                        public void run() {
                            mPlaylistRecentAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        });
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (isLoadedArtist) {
                    ArrayList<ArtistViewModel> tempArtists = RecentModel.getRecentArtist();
                    mListArtistRecent.clear();
                    mListArtistRecent.addAll(tempArtists);
                    mRcvArtistRecent.post(new Runnable() {
                        @Override
                        public void run() {
                            mArtistRecentAdapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        });

    }

    @Override
    public void optionMenuClick(View v, int position) {
        final SongModel songChose = mListSongRecent.get(position);
        showBottomSheetOptionSong(songChose);
    }

    @Override
    public void layoutItemClick(View v, int position) {
        final SongModel songChose = mListSongRecent.get(position);
        playSong(songChose);
    }

    @Override
    public void layoutItemLongClick(View v, int position) {
        final SongModel songChose = mListSongRecent.get(position);
        showBottomSheetOptionSong(songChose);
    }

    private void playSong(final SongModel songPlay) {
        mPlayService.play(songPlay);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<SongModel> listPlaying = new ArrayList<>();
                listPlaying.add(songPlay);
                mPlayService.initListPlaying(listPlaying);
            }
        }).start();
        mMainActivity.playSongsFromFragmentListToMain(FragmentPlaylist.SENDER);
    }

    private void showBottomSheetOptionSong(SongModel song) {
        BottomSheetOptionSong bottomSheetDialogFragment = new BottomSheetOptionSong(song);
        bottomSheetDialogFragment.show(mMainActivity.getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
    }
}
