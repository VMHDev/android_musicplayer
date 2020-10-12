package com.vmh.musicplayer.playlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.PlaylistModel;
import com.vmh.musicplayer.utilities.ImageCacheHelper;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static List<PlaylistModel> mPlaylist;
    private static Context myContext;
    private static final String TAG = "PlaylistAdapter";
    private ImageCacheHelper mImageCacheHelper;
    public MultiClickAdapterListener mListener;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public PlaylistAdapter(Context context, List<PlaylistModel> list, MultiClickAdapterListener listener) {
        myContext = context;
        mPlaylist = list;
        mImageCacheHelper = new ImageCacheHelper(R.mipmap.ic_microphone);
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_playlist, viewGroup, false);
        PlaylistViewHolder artistViewHolder = new PlaylistViewHolder(view, mListener);
        return artistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder artistViewHolder, int i) {
        ((PlaylistViewHolder)artistViewHolder).BindData(mPlaylist.get(i));
    }

    @Override
    public int getItemCount() {
        return mPlaylist.size();
    }
}