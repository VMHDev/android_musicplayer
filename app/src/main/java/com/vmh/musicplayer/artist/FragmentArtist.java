package com.vmh.musicplayer.artist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.RecyclerItemClickListener;
import com.vmh.musicplayer.model.ArtistModel;

import java.util.ArrayList;

public class FragmentArtist extends Fragment {
    private static final String TAG = "FRAGMENT_ARTIST";
    public static final String SENDER="FRAGMENT_ARTIST";

    View view;
    ArrayList<ArtistModel> arrArtist;
    RecyclerView LVArtist;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //get context activity
        context = (MainActivity)getActivity();

        //get view from infalter
        view = inflater.inflate(R.layout.fragment_artist,container,false);

        //get recyclerview
        LVArtist = (RecyclerView) view.findViewById(R.id.lvArtistList);

        //get list artist from db
        arrArtist = ArtistModel.getArtistModel(context);

        //map layout with adapter
        ListArtistAdapter listArtistAdapter = new ListArtistAdapter(context,arrArtist);
        LVArtist.setLayoutManager(new LinearLayoutManager(context));
        LVArtist.setAdapter(listArtistAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LVArtist.addOnItemTouchListener(new RecyclerItemClickListener(context, LVArtist, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context,ArtistSongsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    public static FragmentArtist newInstance() {
        FragmentArtist fragmentArtist = new FragmentArtist();
        Bundle args = new Bundle();
        args.putString("Key2", "OK");
        fragmentArtist.setArguments(args);
        return fragmentArtist;
    }
}
