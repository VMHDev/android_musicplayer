package com.vmh.musicplayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.utilities.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1240;
    private static final String TAG = "SplashActivity";
    private DatabaseManager mDatabaseManager;

    private String[] appPermission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Utility.setTransparentStatusBar(this);
        if (checkPermission()) {
            initApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedCount = 0;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }
            if (deniedCount == 0) {
                initApp();
            } else {
                for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                    String perName = entry.getKey();
                    Integer perResult = entry.getValue();
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, perName)) {
                        showDialogPermission("", "Cấp quyền ", "Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                checkPermission();
                            }
                        }, "Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }, false);
                    } else {
                        showDialogPermission("", "Cấp quyền ", "Cài đặt", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }, "Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }, false);
                        break;
                    }
                }
            }
        }
    }

    /**
    * Load thông tin bài hát + Run Activity Main
    */
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

    /**
     * Kiểm tra cấp quyền
     */
    public boolean checkPermission() {
        List<String> listPermissionNeded = new ArrayList<>();
        for (String perm : appPermission) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeded.add(perm);
            }
        }
        if (!listPermissionNeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeded.toArray(new String[listPermissionNeded.size()]), PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    /**
     * Show hộp thoại xin cấp quyền
     */
    public AlertDialog showDialogPermission(String title, String message, String positiveLabel, DialogInterface.OnClickListener positiveOnclick,
                                            String negativeLabel, DialogInterface.OnClickListener negativeOnclick, boolean isCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(isCancel);
        builder.setNegativeButton(negativeLabel, negativeOnclick);
        builder.setPositiveButton(positiveLabel, positiveOnclick);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }
}
