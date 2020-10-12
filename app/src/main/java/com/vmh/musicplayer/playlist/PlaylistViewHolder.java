package com.vmh.musicplayer.playlist;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.PlaylistModel;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.utilities.ImageCacheHelper;
import com.vmh.musicplayer.utilities.ImageHelper;

public class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView titlePlaylist;
    TextView numberOfSong;
    ImageView imagePlaylist;
    String oldPathImage = "";
    private ImageButton btnOptionSong;
    private CardView layoutItemSong;
    private MultiClickAdapterListener mListener;
    ImageCacheHelper mImageCacheHelper;

    public PlaylistViewHolder(@NonNull View itemView, MultiClickAdapterListener listenerCustom) {
        super(itemView);
        //Log.d(TAG, "ViewHolderRecycler: " + itemView.getId());
        this.titlePlaylist = (TextView) itemView.findViewById(R.id.txtPlaylistTitle);
        this.numberOfSong = (TextView) itemView.findViewById(R.id.txtNumberSongPlaylist);
        this.imagePlaylist = (ImageView) itemView.findViewById(R.id.imgPlaylist);
        this.btnOptionSong = (ImageButton) itemView.findViewById(R.id.btnOptionSong);
        layoutItemSong = (CardView) itemView.findViewById(R.id.layoutItemPlaylist);
        mListener = listenerCustom;

        btnOptionSong.setOnClickListener(this);
        layoutItemSong.setOnClickListener(this);

        mImageCacheHelper = new ImageCacheHelper(R.mipmap.ic_playlist);
    }

    @SuppressLint("SetTextI18n")
    public void BindData(PlaylistModel playlistModel) {
        this.titlePlaylist.setText(playlistModel.getTitle());
        this.numberOfSong.setText(String.valueOf(playlistModel.getNumberOfSongs()) + " bài hát");
        this.imagePlaylist.setImageBitmap(ImageHelper.getBitmapFromPath(playlistModel.getPathImage(), -1));

        SongModel tempSong = new SongModel();
        tempSong.setAlbumId(playlistModel.getId());
        tempSong.setPath(playlistModel.getPathImage());
        final Bitmap bitmap = mImageCacheHelper.getBitmapCache(tempSong.getAlbumId());
        if (bitmap != null && oldPathImage == playlistModel.getPathImage()) {
            this.imagePlaylist.post(new Runnable() {
                @Override
                public void run() {
                    imagePlaylist.setImageBitmap(bitmap);
                }
            });
        } else {
            mImageCacheHelper.loadAlbumArt(imagePlaylist, tempSong);
        }
        oldPathImage = playlistModel.getPathImage();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOptionSong) {
            mListener.optionMenuClick(v, getAdapterPosition());
        } else {
            mListener.layoutItemClick(v, getAdapterPosition());
        }
    }
}
