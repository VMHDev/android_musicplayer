package com.vmh.musicplayer.playlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.RecyclerItemClickListener;
import com.vmh.musicplayer.model.PlaylistModel;

import java.util.ArrayList;

public class FragmentPlaylist extends Fragment {
    private static final String TAG = "FRAGMENT_PLAY_LIST";
    public static final String SENDER = "FRAGMENT_PLAY_LIST";
    private Context mContext;
    private MainActivity mMainActivity;
    private static RecyclerView mRecyclerViewPlaylist;
    private static PlaylistAdapter mPlaylistAdapter;
    private FloatingActionButton mButtonCreatePlaylist;

    private static ArrayList<PlaylistModel> mPlaylist;

    public FragmentPlaylist() {

    }

    public static FragmentPlaylist newInstance() {
        FragmentPlaylist fragmentPlaylist = new FragmentPlaylist();
//        Bundle args = new Bundle();
//        args.putString("Key1", "OK");
//        fragmentListSong.setArguments(args);
        return fragmentPlaylist;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mContext = getActivity();
            mMainActivity = (MainActivity) getActivity();
        } catch (IllegalStateException e) {

        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_playlist, container, false);
        return viewGroup;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerViewPlaylist = view.findViewById(R.id.rcvPlaylist);
        mButtonCreatePlaylist = view.findViewById(R.id.btnCreatePlaylist);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mPlaylist = PlaylistModel.getAllPlaylist();
                mPlaylistAdapter = new PlaylistAdapter(mContext, mPlaylist);
                mRecyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(mContext));
                mRecyclerViewPlaylist.setAdapter(mPlaylistAdapter);
            }
        });

        mRecyclerViewPlaylist.addOnItemTouchListener(new RecyclerItemClickListener(mContext, mRecyclerViewPlaylist, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // do whatever
                Toast.makeText(mContext, "Playlist", Toast.LENGTH_SHORT).show();
                showPlaylistSongActivity();
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // do whatever
                Toast.makeText(mContext, "LONG CLICK ITEM SONG" + position, Toast.LENGTH_SHORT).show();
            }
        }));
        mButtonCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogCreatePlaylist = new FragmentDialogCreatePlaylist();
                dialogCreatePlaylist.show(mMainActivity.getSupportFragmentManager(), "CreatePlaylist");
            }
        });
//        refreshPlaylist();
    }

    private void showPlaylistSongActivity() {
        Intent intent = new Intent(MainActivity.getMainActivity(), PlaylistSongActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public synchronized static void refreshPlaylist() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<PlaylistModel> playlistModels = PlaylistModel.getAllPlaylist();
                Log.d(TAG, "run: REFRESH PLAYLIST SIZE " + playlistModels.get(playlistModels.size() - 1).getNumberOfSongs());
                mPlaylist.clear();
                mPlaylist.addAll(playlistModels);
                Log.d(TAG, "run: SIZE PLAYLIST 1 - " + mPlaylist.get(mPlaylist.size() - 1).getNumberOfSongs());
//                mPlaylistAdapter.notifyDataSetChanged();
                mRecyclerViewPlaylist.post(new Runnable() {
                    @Override
                    public void run() {
                        mPlaylistAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();



    }
}