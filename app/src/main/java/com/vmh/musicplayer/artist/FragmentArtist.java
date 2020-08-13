package com.vmh.musicplayer.artist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmh.musicplayer.R;

public class FragmentArtist extends Fragment {
    private static final String TAG = "FRAGMENT_ARTIST";
    public static final String SENDER = "FRAGMENT_ARTIST";

    public FragmentArtist() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_folder, container, false);
        return viewGroup;
    }
}
