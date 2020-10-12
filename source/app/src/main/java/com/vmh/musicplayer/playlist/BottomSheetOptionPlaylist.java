package com.vmh.musicplayer.playlist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.PlaylistModel;
import com.vmh.musicplayer.utilities.ImageHelper;

public class BottomSheetOptionPlaylist extends BottomSheetDialogFragment implements View.OnClickListener {

    private int _idPlaylist;

    private TableRow mTbrAddQueue;
    private TableRow mDelete;
    private ImageView mImgSong;
    private TextView txtPlaylistBSTitlePlaylist;
    PlaylistSongActivity mPlaylistSongActivity;

    public BottomSheetOptionPlaylist() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//
//        //   Inflate the layout for this fragment
//        // return inflater.inflate(R.layout.layout_action_playlist_bottom_sheet, container, false);
//
//        View view = inflater.inflate(R.layout.layout_action_playlist_bottom_sheet, null, false);
//        LinearLayout layout = view.findViewById(R.id.pl_bts_edit);
//        layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Your Text Here!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }


//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        super.onCreateDialog(savedInstanceState);
//
//        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
//                getActivity(), R.style.BottomSheetDialogTheme
//        );
//
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_action_playlist_bottom_sheet, null, false);
//
//        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.layout_action_playlist_bottom_sheet,
//                (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet, null).findViewById(R.id.bottomSheetContainerActionPlaylist));
//        bottomSheetDialog.setContentView(bottomSheetView);
//
//        mTbrAddQueue = view.findViewById(R.id.btnAddSongToQueue);
//        mTbrAddQueue.setOnClickListener(this);
//
//        return bottomSheetDialog;
//    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, R.style.BottomSheetDialogTheme);

        //View  view = LayoutInflater.from(getContext()).inflate(R.layout.layout_action_playlist_bottom_sheet, null, false);
        View contentView = View.inflate(getContext(), R.layout.layout_action_playlist_bottom_sheet, null);
        mTbrAddQueue = contentView.findViewById(R.id.btnPlaylistUpdate);
        mTbrAddQueue.setOnClickListener(this);

        mDelete = contentView.findViewById(R.id.btnPlaylistDelete);
        mDelete.setOnClickListener(this);

        mImgSong = contentView.findViewById(R.id.imgBtPlaylist);
        txtPlaylistBSTitlePlaylist = contentView.findViewById((R.id.txtPlaylistBSTitlePlaylist));

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mImgSong.setImageBitmap(ImageHelper.getBitmapFromPath(null, R.mipmap.ic_playlist));
                txtPlaylistBSTitlePlaylist.setText(PlaylistModel.getPlaylistById(_idPlaylist).getTitle());
            }
        });
        dialog.setContentView(contentView);
    }

    @SuppressLint("ValidFragment")
    public BottomSheetOptionPlaylist(int idPlaylist, PlaylistSongActivity playlistSongActivity) {
        _idPlaylist = idPlaylist;
        mPlaylistSongActivity = playlistSongActivity;
        // Toast.makeText(getActivity(), "Your Text Here!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlaylistUpdate:
                DialogFragment dialogCreatePlaylist = new FragmentCreatePlaylist(_idPlaylist, this, mPlaylistSongActivity);
                dialogCreatePlaylist.show(getActivity().getSupportFragmentManager(), "CreatePlaylist");
                break;
            case R.id.btnPlaylistDelete:
                long result = PlaylistModel.deletePlaylist(_idPlaylist);
                if (result > 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "Xóa thành công", Toast.LENGTH_LONG).show();
                    FragmentPlaylist.refreshPlaylist();
                    if(mPlaylistSongActivity != null)
                    {
                        mPlaylistSongActivity.onBackPressed();
                    }
                    this.dismiss();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Thất bại", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    public void refreshTitle() {
        txtPlaylistBSTitlePlaylist.setText(PlaylistModel.getPlaylistById(_idPlaylist).getTitle());
    }

}
