package com.vmh.musicplayer.recent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.model.PlaylistModel;
import com.vmh.musicplayer.utilities.ImageCacheHelper;
import com.vmh.musicplayer.utilities.ImageHelper;

import java.util.ArrayList;

public class PlaylistRecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static ArrayList<PlaylistModel> mPlaylist;
    private static Context mContext;
    private static final String TAG = "PlaylistAdapter";
    private ImageCacheHelper mImageCacheHelper;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public PlaylistRecentAdapter(Context context, ArrayList<PlaylistModel> playlist) {
        this.mContext = context;
        this.mPlaylist = playlist;
        mImageCacheHelper = new ImageCacheHelper(R.mipmap.ic_playlist);
        Log.d(TAG, "PlaylistAdapter: Context " + mContext + "LIST " + mPlaylist.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_playlist_recent, viewGroup, false);
        return new ViewHolderRecycler(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ViewHolderRecycler) {
            showSongItem((ViewHolderRecycler) viewHolder, i);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoading((LoadingViewHolder) viewHolder, i);
        }
    }

    private void showSongItem(ViewHolderRecycler viewHolder, int position) {
        PlaylistModel playlistModel = mPlaylist.get(position);
        Log.d(TAG, "showSongItem: " + playlistModel + " View " + viewHolder);
        if (playlistModel != null) {
            viewHolder.bindContent(playlistModel);
        }

    }

    private void showLoading(LoadingViewHolder viewHolder, int position) {
        // Todo something
    }


    @Override
    public int getItemCount() {
        return mPlaylist == null ? 0 : mPlaylist.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPlaylist.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolderRecycler extends RecyclerView.ViewHolder {
        TextView titlePlaylist;
        TextView numberOfSong;
        ImageView imagePlaylist;

        public ViewHolderRecycler(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolderRecycler: " + itemView.getId());
            this.titlePlaylist = (TextView) itemView.findViewById(R.id.txtPlaylistTitle);
            this.numberOfSong = (TextView) itemView.findViewById(R.id.txtNumberSongPlaylist);
            this.imagePlaylist = (ImageView) itemView.findViewById(R.id.imgPlaylist);
        }

        @SuppressLint("SetTextI18n")
        public void bindContent(PlaylistModel playlistModel) {
            this.titlePlaylist.setText(playlistModel.getTitle());
            this.numberOfSong.setText(String.valueOf(playlistModel.getNumberOfSongs()) + " bài hát");
            this.imagePlaylist.setImageBitmap(ImageHelper.getBitmapFromPath(playlistModel.getPathImage(), -1));
            SongModel tempSong = new SongModel();
            tempSong.setAlbumId(playlistModel.getId());
            tempSong.setPath(playlistModel.getPathImage());
            final Bitmap bitmap = mImageCacheHelper.getBitmapCache(tempSong.getAlbumId());
            if (bitmap != null) {
                this.imagePlaylist.post(new Runnable() {
                    @Override
                    public void run() {
                        imagePlaylist.setImageBitmap(bitmap);
                    }
                });
            } else {
                mImageCacheHelper.loadAlbumArt(imagePlaylist, tempSong);
            }
        }


    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBarCircle);
        }
    }


}

