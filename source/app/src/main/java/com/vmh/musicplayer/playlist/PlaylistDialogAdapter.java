package com.vmh.musicplayer.playlist;

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
import com.vmh.musicplayer.model.PlaylistModel;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.utilities.ImageCacheHelper;
import com.vmh.musicplayer.utilities.ImageHelper;

import java.util.ArrayList;

public class PlaylistDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static ArrayList<PlaylistModel> mPlaylist;
    private static Context mContext;
    private static final String TAG = "PlaylistAdapter";
    private ImageCacheHelper mImageCacheHelper;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public PlaylistDialogAdapter(Context context, ArrayList<PlaylistModel> playlist) {
        this.mContext = context;
        this.mPlaylist = playlist;
        mImageCacheHelper = new ImageCacheHelper(R.mipmap.ic_playlist);
        Log.d(TAG, "PlaylistAdapter: Context " + mContext + "LIST " + mPlaylist.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_playlist, viewGroup, false);
//        ViewHolderRecycler viewHolder = new ViewHolderRecycler(view);
//        Log.d(TAG, "onCreateViewHolder: PLAYLIST ADAPTER " + viewGroup.getId());
//        return viewHolder;
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_playlist, viewGroup, false);
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
        Log.d(TAG, "showSongItem: PLAYLIST SIZE "+mPlaylist.size() +", PlaylistLAST ITEM "+playlistModel.getNumberOfSongs()+"__"+mPlaylist.get(mPlaylist.size()-1).getNumberOfSongs());
        Log.d(TAG, "showSongItem: " + playlistModel + " View " + viewHolder);
        if (playlistModel != null) {
            viewHolder.bindContent(playlistModel);
        }

    }

    private void showLoading(LoadingViewHolder viewHolder, int position) {

    }
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolderRecycler viewHolderRecycler, int i) {
//        SongModel songModel = mPlaylist.get(i);
//        viewHolderRecycler.bindContent(songModel);
//    }


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
            Log.d(TAG, "bindContent: PLAYLIST ITEM "+ playlistModel.getNumberOfSongs()+"__"+playlistModel.getTitle());
            this.titlePlaylist.setText(playlistModel.getTitle());
            this.numberOfSong.setText(String.valueOf(playlistModel.getNumberOfSongs()) + " bài hát");
            this.imagePlaylist.setImageBitmap(ImageHelper.getBitmapFromPath(playlistModel.getPathImage(), -1));
            SongModel tempSong = new SongModel();
            tempSong.setAlbumId(playlistModel.getId());
            tempSong.setPath(playlistModel.getPathImage());
            final Bitmap bitmap = mImageCacheHelper.getBitmapCache(tempSong.getAlbumId());//  mBitmapCache.get((long) songModel.getAlbumId());
            if (bitmap != null) {
                this.imagePlaylist.post(new Runnable() {
                    @Override
                    public void run() {
                        imagePlaylist.setImageBitmap(bitmap);
                    }
                });
            } else {
                mImageCacheHelper.loadAlbumArt(imagePlaylist, tempSong);
//                loadAlbumArt(this.imageView, songModel);
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

