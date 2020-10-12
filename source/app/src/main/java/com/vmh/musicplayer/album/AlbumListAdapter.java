package com.vmh.musicplayer.album;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.utilities.ImageCacheHelper;

import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder> {
    private Context myContext;
    private List<AlbumViewModel> albumList;
    private ImageCacheHelper mImageCacheHelper;

    public AlbumListAdapter(Context context, List<AlbumViewModel> list) {
        myContext = context;
        albumList = list;
        mImageCacheHelper = new ImageCacheHelper(R.mipmap.ic_album);
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_album, viewGroup, false);
        AlbumViewHolder albumViewHolder = new AlbumViewHolder(view);
        return albumViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder albumViewHolder, int i) {
        albumViewHolder.BindData(albumList, i);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView TVAlbumName;
        TextView TVAlbumCount;
        TextView TVAlbumArtist;
        ImageView IVAlbum;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            TVAlbumName = (TextView) itemView.findViewById(R.id.txtAlbumName);
            TVAlbumCount = (TextView) itemView.findViewById(R.id.txtAlbumSongCount);
            TVAlbumArtist = (TextView) itemView.findViewById(R.id.txtAlbumArtist);
            IVAlbum = (ImageView) itemView.findViewById(R.id.imgAlbum);
        }

        public void BindData(List<AlbumViewModel> albumList, int position) {
            AlbumViewModel albumViewModel = albumList.get(position);

            String title = albumViewModel.getTitle().length() > 35
                    ? albumViewModel.getTitle().substring(0, 35) + "..."
                    : albumViewModel.getTitle();

            TVAlbumName.setText(title);
            TVAlbumCount.setText(albumViewModel.getNumberOfSongs() + " Bài hát");
            TVAlbumArtist.setText(albumViewModel.getArtist());

            SongModel songModel = new SongModel();
            songModel.setAlbumId(albumViewModel.getAlbumId());
            songModel.setPath(albumViewModel.getPath());
            final Bitmap bitmap = mImageCacheHelper.getBitmapCache(songModel.getAlbumId());
            if (bitmap != null) {
                this.IVAlbum.setImageBitmap(bitmap);
            } else {
                mImageCacheHelper.loadAlbumArt(IVAlbum, songModel);
            }
        }
    }
}

