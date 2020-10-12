package com.vmh.musicplayer.playlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.PlaylistModel;

import java.util.ArrayList;

public class FragmentPlaylist extends Fragment implements MultiClickAdapterListener {
    private static final String TAG = "FRAGMENT_PLAY_LIST";
    public static final String SENDER = "FRAGMENT_PLAY_LIST";

    Context mContext;
    MainActivity mMainActivity;
    RecyclerView mRecyclerViewPlaylist;
    static PlaylistAdapter mPlaylistAdapter;
    SwipeRefreshLayout mSwpPlaylist;
    View view;
    static String searchValue = "";
    private MultiClickAdapterListener myAdapterListener;

    private final int mThreshold = 10;
    private static boolean mIsLoading;
    private static ArrayList<PlaylistModel> mPlaylist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // mContext = getActivity();
            mMainActivity = (MainActivity) getActivity();
        } catch (IllegalStateException e) {

        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //get context activity
        mContext = (MainActivity) getActivity();

        //get view from infalter
        view = inflater.inflate(R.layout.fragment_playlist, container, false);

        //get recyclerview
        mRecyclerViewPlaylist = view.findViewById(R.id.rcvPlaylist);
        mSwpPlaylist = view.findViewById(R.id.swpPlaylist);

        //get from db
        mPlaylist = PlaylistModel.getAllPlaylist(searchValue);

        // Load playlist
        mPlaylistAdapter = new PlaylistAdapter(mContext, mPlaylist, FragmentPlaylist.this);
        mRecyclerViewPlaylist.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerViewPlaylist.setAdapter(mPlaylistAdapter);

//        final Button buttonShow = view.findViewById(R.id.btnCreatePlaylist);
//        buttonShow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final BottomSheetDialog  bottomSheetDialog = new BottomSheetDialog(
//                        mMainActivity,R.style.BottomSheetDialogTheme
//                );
//                View bottomSheetView = inflater.inflate(R.layout.layout_bottom_sheet,
//                        (LinearLayout)view.findViewById(R.id.bottomSheetContainer));
////                bottomSheetView.findViewById(R.id.buttomBtn).setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        Toast.makeText(MainActivity.getMainActivity(), "Tesst....", Toast.LENGTH_SHORT).show();
////                        bottomSheetDialog.dismiss();
////                    }
////                });
//                bottomSheetDialog.setContentView(bottomSheetView);
//                bottomSheetDialog.show();
//            }
//        });

        final Button buttonShow = view.findViewById(R.id.btnCreatePlaylist);
        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mMainActivity.setContentView(R.layout.layout_playlist_create);

//                final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
////                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////                dialog.setContentView(R.layout.layout_playlist_create);
////                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
////                dialog.show();

//                Dialog mDialog = new Dialog(mContext, R.style.DialogFullScreen);
//                mDialog.setContentView(R.layout.layout_playlist_create);
//                mDialog.show();

                DialogFragment dialogCreatePlaylist = new FragmentCreatePlaylist(-1, null, null);
                dialogCreatePlaylist.show(mMainActivity.getSupportFragmentManager(), "CreatePlaylist");
            }
        });


//        mRecyclerViewPlaylist.addOnItemTouchListener(new RecyclerItemClickListener(mContext, mRecyclerViewPlaylist, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                // do whatever
//               Toast.makeText(mContext, "Playlist click " + mPlaylist.get(position).getId(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//                // do whatever
////                Toast.makeText(mContext, "LONG CLICK ITEM SONG" + position, Toast.LENGTH_SHORT).show();
//            }
//        }));

        mSwpPlaylist.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPlaylist.clear();
                mPlaylistAdapter.notifyDataSetChanged();
                ArrayList<PlaylistModel> temp = PlaylistModel.getAllPlaylist(searchValue);
                Log.d(TAG, "onRefresh: " + temp.size());
                if (temp.size() > 0) {
                    Log.d(TAG, "onRefresh: " + temp.get(temp.size() - 1).getNumberOfSongs());
                }
                mPlaylist.addAll(temp);
                mPlaylistAdapter.notifyDataSetChanged();
                mSwpPlaylist.setRefreshing(false);
            }
        });

        return view;
    }


    @Override
    public void optionMenuClick(View v, int position) {
        //      Toast.makeText(mContext, "optin cclick " + position, Toast.LENGTH_SHORT).show();
//
//        final BottomSheetDialog  bottomSheetDialog = new BottomSheetDialog(
//                        mMainActivity,R.style.BottomSheetDialogTheme
//                );
//                View bottomSheetView = LayoutInflater.from(mContext).inflate(R.layout.layout_action_playlist_bottom_sheet,
//                        (LinearLayout)view.findViewById(R.id.bottomSheetContainerActionPlaylist));
//                bottomSheetDialog.setContentView(bottomSheetView);
//                bottomSheetDialog.show();
        BottomSheetOptionPlaylist optionPlaylist = new BottomSheetOptionPlaylist(mPlaylist.get(position).getId(), null);
        optionPlaylist.show(mMainActivity.getSupportFragmentManager(), optionPlaylist.getTag());
    }

    @Override
    public void checkboxClick(View v, int position) {

    }

    @Override
    public void layoutItemClick(View v, int position) {
//        Toast.makeText(mContext, "Playlist click " + position, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.getMainActivity(), PlaylistSongActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("playlistId", mPlaylist.get(position).getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void layoutItemLongClick(View v, int position) {

    }

    public static FragmentPlaylist newInstance() {
        FragmentPlaylist fragmentPlaylist = new FragmentPlaylist();
        return fragmentPlaylist;
    }

    public static void refreshPlaylist() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mPlaylist != null) {
                    mPlaylist.clear();
                    mPlaylistAdapter.notifyDataSetChanged();
                    ArrayList<PlaylistModel> playlistModels = PlaylistModel.getAllPlaylist(searchValue);
                    mPlaylist.addAll(playlistModels);
                    mPlaylistAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void UpdateSearch(String s){
        if(s == searchValue) return;
        searchValue = s;
        mIsLoading = true;
        ArrayList<PlaylistModel> tempPlayList = PlaylistModel.getAllPlaylist(searchValue);
        mPlaylist.clear();
        mPlaylist.addAll(tempPlayList);
        mPlaylistAdapter.notifyDataSetChanged();
        mIsLoading = false;
    }
}
