package com.vmh.musicplayer.playlist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.PlaylistModel;

public class FragmentCreatePlaylist extends DialogFragment implements View.OnClickListener {

//    EditText txtTitlePlaylist;
////    Button btnCancel;
////    Button btnSubmit;
////
////    @Override
////    public Dialog onCreateDialog(Bundle savedInstanceState) {
//////        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullScreen);
//////        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//////        // Get the layout inflater
//////        LayoutInflater inflater = getActivity().getLayoutInflater();
//////
//////        View view = inflater.inflate(R.layout.layout_playlist_create, null);
//////        txtTitlePlaylist = view.findViewById(R.id.txtTilePlaylistCreate);
//////        btnCancel = view.findViewById(R.id.btnCancelCreatePlaylist);
//////        btnSubmit = view.findViewById(R.id.btnSubmitCreatePlaylist);
//////
//////        btnSubmit.setOnClickListener(this);
//////        btnCancel.setOnClickListener(this);
//////
//////        txtTitlePlaylist.requestFocus();
//////        builder.setView(view);
//////        return builder.create();
////
////        Dialog mDialog = new Dialog(getContext(), R.style.DialogFullScreen);
////        mDialog.setContentView(R.layout.layout_playlist_create);
////
////        LayoutInflater inflater = getActivity().getLayoutInflater();
////        View view = inflater.inflate(R.layout.layout_playlist_create, null);
////        btnCancel = view.findViewById(R.id.btnCancelCreatePlaylist);
////        btnSubmit = view.findViewById(R.id.btnSubmitCreatePlaylist);
////
////        btnSubmit.setOnClickListener(this);
////        btnCancel.setOnClickListener(this);
////        txtTitlePlaylist.requestFocus();
////
////        return mDialog;
////    }
////
////    @Override
////    public void onClick(View v) {
////        switch (v.getId()) {
////            case R.id.btnCancelCreatePlaylist:
////                Toast.makeText(getContext(),"1243435", Toast.LENGTH_LONG);
////                break;
////            case R.id.btnSubmitCreatePlaylist:
////                String title = txtTitlePlaylist.getText().toString().trim();
////                Toast.makeText(getContext(),txtTitlePlaylist.getText(), Toast.LENGTH_LONG);
////                break;
////            default:
////                break;
////        }
////    }


    EditText txtTitlePlaylist;
    EditText txtPlaylistBSTitlePlaylist;
    Button btnCancel;
    Button btnSubmit;
    PlaylistModel mCurrentPlaylist;
    private Toolbar mToolbar;
    private int STATUS = -1;
    BottomSheetOptionPlaylist mBottomSheetOptionPlaylist;
    PlaylistSongActivity mPlaylistSongActivity;

    @SuppressLint("ValidFragment")
    public FragmentCreatePlaylist(int status, BottomSheetOptionPlaylist bottomSheetOptionPlaylist, PlaylistSongActivity playlistSongActivity) {
        STATUS = status;
        mBottomSheetOptionPlaylist = bottomSheetOptionPlaylist;
        mPlaylistSongActivity = playlistSongActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_playlist_create, container, false);

        btnCancel = view.findViewById(R.id.btnCancelCreatePlaylist);
        btnSubmit = view.findViewById(R.id.btnSubmitCreatePlaylist);
        txtTitlePlaylist = view.findViewById(R.id.txtTilePlaylistCreate);
        mToolbar = (Toolbar) view.findViewById(R.id.tbplaylistCreate);
        txtPlaylistBSTitlePlaylist = view.findViewById(R.id.txtPlaylistBSTitlePlaylist);

        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        initToolBarParalax();

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCreatePlaylist.this.getDialog().cancel();
            }
        });

        if (STATUS != -1) {
            mCurrentPlaylist = PlaylistModel.getInfoPlaylistById(STATUS);

            //  txtPlaylistBSTitlePlaylist.setText("hábdsa");
            txtTitlePlaylist.setText(mCurrentPlaylist.getTitle() + "");
            txtTitlePlaylist.requestFocus();
            txtTitlePlaylist.setSelection(txtTitlePlaylist.length());
        }
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancelCreatePlaylist:
                FragmentCreatePlaylist.this.getDialog().cancel();
                break;
            case R.id.btnSubmitCreatePlaylist:
                if (STATUS == -1) {
                    String title = txtTitlePlaylist.getText().toString().trim();
                    if (!title.isEmpty()) {
                        long result = PlaylistModel.createPlaylist(title);
                        if (result > 0) {
                            FragmentPlaylist.refreshPlaylist();
                            Toast.makeText(getActivity().getApplicationContext(), "Đã tạo mới playlist", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Thất bại", Toast.LENGTH_LONG).show();

                        }
                        FragmentCreatePlaylist.this.getDialog().cancel();
                    }
                } else {
                    String titleEdit = txtTitlePlaylist.getText().toString();
                    if (titleEdit.isEmpty()) {
                        break;
                    }
                    mCurrentPlaylist.setTitle(titleEdit);
                    long result = PlaylistModel.updateTitlePlaylist(mCurrentPlaylist);
                    if (result > 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "Sửa tiêu đề thành công", Toast.LENGTH_LONG).show();
                        if(mPlaylistSongActivity != null) {
                            mPlaylistSongActivity.refreshTitlePlaylist();
                        }
                        mBottomSheetOptionPlaylist.refreshTitle();
                        FragmentPlaylist.refreshPlaylist();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Thất bại", Toast.LENGTH_LONG).show();
                    }
                    FragmentCreatePlaylist.this.getDialog().cancel();
                }
                break;
            default:
                break;
        }
    }


    private void initToolBarParalax() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
    }
}


