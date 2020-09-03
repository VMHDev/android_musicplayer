package com.vmh.musicplayer.album;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmh.musicplayer.R;

public class FragmentAlbum extends Fragment {
    private static final String TAG = "FRAGMENT_ALBUM";
    public static final String SENDER = "FRAGMENT_ALBUM";

    public FragmentAlbum() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_album, container, false);
        return viewGroup;
    }
}
