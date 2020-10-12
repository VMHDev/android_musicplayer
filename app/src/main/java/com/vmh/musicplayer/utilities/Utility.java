package com.vmh.musicplayer.utilities;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class Utility {
    //    private static  Activity  mActivity;
    public Utility(){

    }
    public static void setTransparentStatusBar(Activity activity){

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            Window window= activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
    public static void setTranslucentStatusBar(Activity activity){

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            Window window= activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
    public static int getStatusbarHeight(Activity activity){
        // status bar height
        int statusBarHeight = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return  statusBarHeight;
    }

    public static int getDisplayHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int height = display.getHeight();

        return height;
    }
}
