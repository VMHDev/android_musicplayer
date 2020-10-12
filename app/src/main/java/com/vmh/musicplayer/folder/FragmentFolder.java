package com.vmh.musicplayer.folder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.vmh.musicplayer.listsong.RecyclerItemClickListener;
import com.vmh.musicplayer.model.FolderModel;

import java.io.Serializable;
import java.util.ArrayList;

public class FragmentFolder extends Fragment {
    private static final String TAG = "FRAGMENT_FOLDER";
    public static final String SENDER = "FRAGMENT_FOLDER";

    private RecyclerView mRcvFolder;
    private FolderAdapter mFolderAdapter;
    private ArrayList<FolderModel> mListFolder;

    private Context mContext;

    String searchValue = "";
    static boolean mIsLoading;


    public FragmentFolder() {
        mContext = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_folder, container, false);
        mRcvFolder = viewGroup.findViewById(R.id.rcvFolder);
        //get data
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mListFolder = FolderModel.getAllFolders();
                mFolderAdapter = new FolderAdapter(mContext, mListFolder);
                mRcvFolder.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                mRcvFolder.setAdapter(mFolderAdapter);
            }
        });
        mRcvFolder.addOnItemTouchListener(new RecyclerItemClickListener(mContext, mRcvFolder, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showSongFolderActivity(mListFolder.get(position));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        return viewGroup;
    }

    private void showSongFolderActivity(FolderModel folderChose) {
        Intent intent = new Intent(MainActivity.getMainActivity(), FolderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("folderModel", (Serializable) folderChose); //folder model
        intent.putExtras(bundle);
        startActivity(intent);

    }

    public void UpdateSearch(String s) {
        if (s == searchValue || mListFolder == null) return;
        searchValue = s;
        mIsLoading = true;
        ArrayList<FolderModel> tempFolderList = FolderModel.getAllFolders(searchValue);
        mListFolder.clear();
        mListFolder.addAll(tempFolderList);
        mFolderAdapter.notifyDataSetChanged();
        mIsLoading = false;
    }
}
