package com.vmh.musicplayer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.utilities.Utility;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1240;
    private static final String TAG = "SplashActivity";
    private DatabaseManager mDatabaseManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Utility.setTransparentStatusBar(this);
        initApp();
    }

    public void initApp() {
        mDatabaseManager = DatabaseManager.newInstance(getApplicationContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "doInBackground: SIZE AUDIOS " + SongModel.getRowsSong(mDatabaseManager));
                ArrayList<SongModel> tempAudioList = SongModel.getAllAudioFromDevice(getApplicationContext());
                Log.d(TAG, "doInBackground: AUDIO " + tempAudioList.size());
                if (SongModel.getRowsSong(mDatabaseManager) != tempAudioList.size()) {
                    for (SongModel song : tempAudioList) {
                        long id = SongModel.insertSong(mDatabaseManager, song);
                        Log.d(TAG, "onPostExecute: INSERT SONG FROM MAIN : " + id);
                    }
                }
            }
        }).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 1000);
    }
}
