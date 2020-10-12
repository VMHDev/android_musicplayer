package com.vmh.musicplayer.playlist;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.listsong.RecyclerItemClickListener;
import com.vmh.musicplayer.model.PlaylistModel;
import com.vmh.musicplayer.model.SongModel;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class FragmentDialogPlaylist extends DialogFragment {
    private Context mContext;
    private static RecyclerView mRecyclerViewPlaylist;
    private static PlaylistDialogAdapter mPlaylistAdapter;
    private static ArrayList<PlaylistModel> mPlaylist;
    private SongModel mCurrentSong;

    @SuppressLint("ValidFragment")
    public FragmentDialogPlaylist(SongModel currentSong) {
        mCurrentSong = currentSong;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog mDialog = new Dialog(getContext(), R.style.DialogFullScreen);
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_playlist, null);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        mRecyclerViewPlaylist = view.findViewById(R.id.rcvPlaylist2);
        mContext = MainActivity.getMainActivity();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ArrayList<PlaylistModel> playlistModels = PlaylistModel.getAllPlaylist();
                mPlaylist = playlistModels;
                mPlaylistAdapter = new PlaylistDialogAdapter(mContext, mPlaylist);
                mRecyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(mContext));
                mRecyclerViewPlaylist.setAdapter(mPlaylistAdapter);
                mRecyclerViewPlaylist.addOnItemTouchListener(new RecyclerItemClickListener(mContext, mRecyclerViewPlaylist, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                       // Toast.makeText(mContext, "Playlist", Toast.LENGTH_LONG).show();
                        PlaylistModel playlist = mPlaylist.get(position);
                        boolean isExists = PlaylistSongModel.isSongExisitPlaylist(mCurrentSong.getSongId(), playlist.getId());
                        if (isExists) {
                            Toast.makeText(mContext, "Bài hát đã tồn tại trong Playlist", Toast.LENGTH_LONG).show();
                            FragmentDialogPlaylist.this.dismiss();
                            return;
                        }
                        long result = PlaylistSongModel.addSongToPlaylist(mCurrentSong.getSongId(), playlist.getId(), mCurrentSong.getPath());

                        if (result > 0) {
                            Toast.makeText(mContext, "Thành công", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(mContext, "Thất bại", Toast.LENGTH_LONG).show();
                        }
                        FragmentPlaylist.refreshPlaylist();
                        FragmentDialogPlaylist.this.dismiss();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // TODO
                    }
                }));
            }
        });
        mDialog.setContentView(view);
        return mDialog;
    }
}
