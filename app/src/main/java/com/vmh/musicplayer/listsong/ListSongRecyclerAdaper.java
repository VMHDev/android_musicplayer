package com.vmh.musicplayer.listsong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.utilities.ImageCacheHelper;

import java.util.ArrayList;

public class ListSongRecyclerAdaper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ListSongRecyclerAdaper";
    private static ArrayList<SongModel> mListSong;
    private static Context mContext;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private LruCache<Long, Bitmap> mBitmapCache;
    private BitmapDrawable mPlaceholder;
    private ImageCacheHelper mImageCacheHelper;

    public ListSongRecyclerAdaper(Context context, ArrayList<SongModel> listSong) {
        mContext = context;
        mListSong = listSong;
        mImageCacheHelper = new ImageCacheHelper(R.mipmap.ic_music_file);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_song, viewGroup, false);
            return new ViewHolderRecycler(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progressbar_circle, viewGroup, false);
            return new LoadingViewHolder(view);
        }
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
        SongModel songModel = mListSong.get(position);
        viewHolder.bindContent(songModel);
    }

    private void showLoading(LoadingViewHolder viewHolder, int position) {

    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemViewType(int position) {
        return mListSong.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mListSong == null ? 0 : mListSong.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolderRecycler extends RecyclerView.ViewHolder {

        TextView titleSong;
        TextView album;
        TextView artist;
        TextView duration;
        ImageView imageView;
        ImageButton btnOptionSong;

        ViewHolderRecycler(@NonNull View itemView) {
            super(itemView);
            this.titleSong = itemView.findViewById(R.id.txtTitle);
            this.artist = itemView.findViewById(R.id.txtArtist);
            this.imageView = itemView.findViewById(R.id.imgSong);
            this.duration = itemView.findViewById(R.id.txtDuration);
            this.btnOptionSong = itemView.findViewById(R.id.btnOptionSong);
        }

        @SuppressLint("SetTextI18n")
        void bindContent(SongModel songModel) {
            Log.d(TAG, "bindContent: BIND CONTENT");
            this.titleSong.setText(songModel.getTitle());
            this.artist.setText(songModel.getArtist());

            final Bitmap bitmap = mImageCacheHelper.getBitmapCache(songModel.getAlbumId());
            if (bitmap != null) {
                this.imageView.setImageBitmap(bitmap);
            } else {
                mImageCacheHelper.loadAlbumArt(imageView, songModel);
            }
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBarCircle);
        }
    }
}