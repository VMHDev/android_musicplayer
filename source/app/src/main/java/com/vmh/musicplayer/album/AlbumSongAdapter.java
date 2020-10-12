package com.vmh.musicplayer.album;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.SongModel;

import java.util.List;

public class AlbumSongAdapter extends RecyclerView.Adapter<AlbumSongAdapter.AlbumSongViewHolder> {

    private List<SongModel> mylist;
    private Context myContext;

    public AlbumSongAdapter(Context context, List<SongModel> list) {
        myContext = context;
        mylist = list;
    }

    @NonNull
    @Override
    public AlbumSongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_artist_song, viewGroup, false);
        AlbumSongViewHolder albumSongViewHolder = new AlbumSongViewHolder(view);
        return albumSongViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumSongViewHolder albumSongViewHolder, int i) {
        albumSongViewHolder.BindData(i);
    }

    @Override
    public int getItemCount() {
        return mylist.size();
    }

    public class AlbumSongViewHolder extends RecyclerView.ViewHolder {
        TextView TVNumber;
        TextView TVNameSong;
        TextView TVNameArtist;
        TextView TVDuration;

        public AlbumSongViewHolder(@NonNull View itemView) {
            super(itemView);
            TVNumber = (TextView) itemView.findViewById(R.id.artistSongNumber);
            TVNameSong = (TextView) itemView.findViewById(R.id.artistSongNameSong);
            TVNameArtist = (TextView) itemView.findViewById(R.id.artistSongNameArtist);
            TVDuration = (TextView) itemView.findViewById(R.id.artistSongDuration);
        }

        public void BindData(int position) {
            SongModel albumSongsModel = mylist.get(position);
            TVNumber.setText(position + 1 + "");
            String title = albumSongsModel.getTitle().length() > 35
                    ? albumSongsModel.getTitle().substring(0, 35) + "..."
                    : albumSongsModel.getTitle();
            TVNameSong.setText(title);
            TVNameArtist.setText(albumSongsModel.getArtist());
            TVDuration.setText(SongModel.formateMilliSeccond(albumSongsModel.getDuration()));
        }
    }
}
