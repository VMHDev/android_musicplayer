package com.vmh.musicplayer.listsong;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.SongModel;

import java.util.ArrayList;

public class ListSongRecyclerAdaper extends RecyclerView.Adapter<ListSongRecyclerAdaper.ViewHolderRecycler> {

    private ArrayList<SongModel> mListSong;
    private Context mContext;

    public ListSongRecyclerAdaper(Context context, ArrayList<SongModel> listSong){
        this.mContext = context;
        this.mListSong = listSong;
    }
    @NonNull
    @Override
    public ViewHolderRecycler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_song,viewGroup,false);
        ViewHolderRecycler viewHolder=new ViewHolderRecycler(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderRecycler viewHolderRecycler, int i) {
        SongModel songModel=mListSong.get(i);
        viewHolderRecycler.bindContent(songModel);
    }


    @Override
    public int getItemCount() {
        return mListSong.size();
    }

    @Override
    public long getItemId(int position) {
        return  position;
    }

    public   class  ViewHolderRecycler extends RecyclerView.ViewHolder{
        TextView titleSong;
        TextView album;
        TextView artist;
        TextView duration;
        ImageView imageView;
        public ViewHolderRecycler(@NonNull View itemView) {
            super(itemView);
            this.titleSong=(TextView)itemView.findViewById(R.id.txtTitle);
//            this.album=album;
            this.artist=(TextView)itemView.findViewById(R.id.txtArtist);
            this.imageView=(ImageView)itemView.findViewById(R.id.imgSong);
            this.duration=(TextView)itemView.findViewById(R.id.txtDuration);

        }
        public  void bindContent(SongModel songModel){
            this.titleSong.setText(songModel.getTitle());
            this.artist.setText(songModel.getArtist());
            this.duration.setText(songModel.getDuration());
        }
    }
}