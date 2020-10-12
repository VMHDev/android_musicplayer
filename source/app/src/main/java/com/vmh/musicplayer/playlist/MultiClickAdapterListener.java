package com.vmh.musicplayer.playlist;

import android.view.View;


public interface MultiClickAdapterListener {

    void optionMenuClick(View v, int position);

    void checkboxClick(View v, int position);

    void layoutItemClick(View v, int position);

    void layoutItemLongClick(View v, int position);
}