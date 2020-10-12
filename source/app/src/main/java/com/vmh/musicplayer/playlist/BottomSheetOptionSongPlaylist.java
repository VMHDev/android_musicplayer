package com.vmh.musicplayer.playlist;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.PlaylistModel;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.play.PlayService;
import com.vmh.musicplayer.utilities.ImageHelper;

import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class BottomSheetOptionSongPlaylist extends BottomSheetDialogFragment implements View.OnClickListener {

    private SongModel mCurrentSong;
    private PlaylistModel mCurrentPlaylist;
    private TextView mTxtTitleSong;
    private TextView mTxtArtist;
    private TableRow mTbrAddQueue;
    private TableRow mTbrAddPlaylist;
    private TableRow mTbrMakeRingTone;
    private TableRow mTbrDeleteSong;
    private TableRow mTbrDeleteSongInPlaylist;
    private ImageView mImgSong;

    private PlaylistSongActivity mSongPlaylistActivity;

    @SuppressLint("ValidFragment")
    public BottomSheetOptionSongPlaylist(SongModel songOption, PlaylistModel playlistOption, PlaylistSongActivity playlistSongActivity) {
        mCurrentSong = songOption;
        mCurrentPlaylist = playlistOption;
        mSongPlaylistActivity = playlistSongActivity;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, R.style.BottomSheetDialogTheme);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet, null, false);
        View contentView = View.inflate(getContext(), R.layout.layout_action_playlist_song_bottom_sheet, (LinearLayout) view.findViewById(R.id.bottomSheetContainerActionPlaylistSong));

        mTxtTitleSong = contentView.findViewById(R.id.txtTitleSong);
        mTxtArtist = contentView.findViewById(R.id.txtArtist);
        mTbrAddQueue = contentView.findViewById(R.id.btnAddSongToQueue);
        mTbrAddPlaylist = contentView.findViewById(R.id.btnAddSongToPlaylist);
        mTbrMakeRingTone = contentView.findViewById(R.id.btnMakeRingTone);
        //    mTbrDeleteSong = contentView.findViewById(R.id.btnDeleteSong);
        mTbrDeleteSongInPlaylist = contentView.findViewById(R.id.btnDeleteSongInPlaylist);
        mImgSong = contentView.findViewById(R.id.imgBTSong);


        mTbrAddQueue.setOnClickListener(this);
        mTbrAddPlaylist.setOnClickListener(this);
        mTbrMakeRingTone.setOnClickListener(this);
//        mTbrDeleteSong.setOnClickListener(this);
        mTbrDeleteSongInPlaylist.setOnClickListener(this);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mTxtTitleSong.setText(mCurrentSong.getTitle());
                mTxtArtist.setText(mCurrentSong.getArtist());
                mImgSong.setImageBitmap(ImageHelper.getBitmapFromPath(null, R.mipmap.ic_music));
            }
        });


        dialog.setContentView(contentView);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddSongToQueue:
                ArrayList<SongModel> songs = new ArrayList<SongModel>();
                songs.add(mCurrentSong);
                long resultAddSong = PlayService.addSongsToPlayingList(songs);
                if (resultAddSong > 0) {
                    Toast.makeText(getActivity(), "Đã thêm vào danh sách phát", Toast.LENGTH_LONG).show();
                } else if (resultAddSong == 0) {
                    Toast.makeText(getActivity(), "Bài hát đã tồn tại", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Thất bại", Toast.LENGTH_LONG).show();
                }
                BottomSheetOptionSongPlaylist.this.dismiss();
                break;
            case R.id.btnAddSongToPlaylist:
                FragmentDialogPlaylist fragmentDialogPlaylist = new FragmentDialogPlaylist(mCurrentSong);
                fragmentDialogPlaylist.show(getActivity().getSupportFragmentManager(), "ADD_SONG_TO_LIST_QUEUE");
                BottomSheetOptionSongPlaylist.this.dismiss();
                break;
            case R.id.btnMakeRingTone:
                Toast.makeText(getActivity(), "Ring tone", Toast.LENGTH_LONG).show();
                break;
            // case R.id.btnDeleteSong:
            //     break;
            case R.id.btnDeleteSongInPlaylist:
                long result = PlaylistSongModel.deleteSongInPlaylist(mCurrentSong.getSongId(), mCurrentPlaylist.getId());
                if (result > 0) {
                    Toast.makeText(getActivity(), "Đã xóa khỏi playlist", Toast.LENGTH_LONG).show();
                    mSongPlaylistActivity.refreshSongPlaylist();
                } else {
                    Toast.makeText(getActivity(), "Thất bại", Toast.LENGTH_LONG).show();
                }
                BottomSheetOptionSongPlaylist.this.dismiss();
                break;
        }
    }
}