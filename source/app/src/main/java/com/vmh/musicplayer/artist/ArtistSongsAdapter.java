package com.vmh.musicplayer.artist;

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

public class ArtistSongsAdapter extends RecyclerView.Adapter<ArtistSongsAdapter.ArtistSongViewHolder> {
    private List<SongModel> mylist;
    private Context myContext;

    public ArtistSongsAdapter(Context context, List<SongModel> list){
        myContext = context;
        mylist = list;
    }
    @NonNull
    @Override
    public ArtistSongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_artist_song, viewGroup, false);
        ArtistSongViewHolder artistViewHolder = new ArtistSongViewHolder(view);
        return artistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistSongViewHolder artistSongViewHolder, int i) {
        artistSongViewHolder.BindData(i);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mylist.size();
    }

    public class ArtistSongViewHolder extends RecyclerView.ViewHolder{
        TextView TVNumber;
        TextView TVNameSong;
        TextView TVNameArtist;
        TextView TVDuration;
        public ArtistSongViewHolder(@NonNull View itemView) {
            super(itemView);
             TVNumber = (TextView) itemView.findViewById(R.id.artistSongNumber);
             TVNameSong = (TextView) itemView.findViewById(R.id.artistSongNameSong);
             TVNameArtist = (TextView) itemView.findViewById(R.id.artistSongNameArtist);
             TVDuration = (TextView) itemView.findViewById(R.id.artistSongDuration);
        }
        public void BindData(int position){
            SongModel artistSongsModel = mylist.get(position);
            TVNumber.setText(position + 1 + "");
            String title = artistSongsModel.getTitle().length() > 35
                    ? artistSongsModel.getTitle().substring(0,35) + "..."
                    : artistSongsModel.getTitle();

            TVNameSong.setText(title);
            TVNameArtist.setText(artistSongsModel.getArtist());
            TVDuration.setText(SongModel.formateMilliSeccond(artistSongsModel.getDuration()));
        }
    }
}
