package com.vmh.musicplayer.artist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.listsong.RecyclerItemClickListener;
import com.vmh.musicplayer.model.ArtistModel;

import java.util.ArrayList;

public class FragmentArtist extends Fragment {
    private static final String TAG = "FRAGMENT_ARTIST";
    public static final String SENDER = "FRAGMENT_ARTIST";

    View view;
    ArrayList<ArtistViewModel> arrArtist;
    ListArtistAdapter listArtistAdapter;
    RecyclerView LVArtist;
    Context context;
    SwipeRefreshLayout mSwpListArtist;
    static boolean mIsLoading;
    static int take = 10;
    static String searchValue = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //get context activity
        context = (MainActivity) getActivity();

        //get view from infalter
        view = inflater.inflate(R.layout.fragment_artist, container, false);

        //get recyclerview
        LVArtist = (RecyclerView) view.findViewById(R.id.lvArtistList);

        //get list artist from db
        arrArtist = ArtistProvider.getArtistModelPaging(context, searchValue, 0, 20);

        mSwpListArtist = view.findViewById(R.id.swpListArtist);

        //map layout with adapter
        listArtistAdapter = new ListArtistAdapter(context, arrArtist);
        LVArtist.setLayoutManager(new LinearLayoutManager(context));
        LVArtist.setAdapter(listArtistAdapter);
        LVArtist.addOnItemTouchListener(new RecyclerItemClickListener(context, LVArtist, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, ArtistSongsActivity.class);
                ArtistModel artistModel = arrArtist.get(position).getArtistModel();
                intent.putExtra("infoArtist", artistModel);
                startActivityForResult(intent, ArtistModel.RequestCode);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        LVArtist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!mIsLoading && linearLayoutManager != null && linearLayoutManager.getItemCount() - 1 <= linearLayoutManager.findLastVisibleItemPosition()) {
                    loadMore();
                    mIsLoading = true;
                }
            }
        });

        mSwpListArtist.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prefreshListArtist();
            }
        });
        return view;
    }

    private void prefreshListArtist() {

        arrArtist.clear();
        listArtistAdapter.notifyDataSetChanged();
        ArrayList<ArtistViewModel> temp = ArtistProvider.getArtistModelPaging(context, searchValue, 0, 20);
        arrArtist.addAll(temp);
        listArtistAdapter.notifyDataSetChanged();
        mSwpListArtist.setRefreshing(false);
    }

    private void loadMore() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<ArtistViewModel> tempAudioList = ArtistProvider.getArtistModelPaging(context, searchValue, arrArtist.size(), take);
                arrArtist.addAll(tempAudioList);
                listArtistAdapter.notifyDataSetChanged();
                mIsLoading = false;
            }
        });
    }

    public static FragmentArtist newInstance() {
        FragmentArtist fragmentArtist = new FragmentArtist();
        return fragmentArtist;
    }

    public void UpdateSearch(String s) {
        if (s == searchValue) return;
        searchValue = s;
        mIsLoading = true;
        ArrayList<ArtistViewModel> tempAudioList = ArtistProvider.getArtistModelPaging(context, searchValue, 0, 20);
        arrArtist.clear();
        arrArtist.addAll(tempAudioList);
        listArtistAdapter.notifyDataSetChanged();
        mIsLoading = false;
    }
}
