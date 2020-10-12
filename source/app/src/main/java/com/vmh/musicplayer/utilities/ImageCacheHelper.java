package com.vmh.musicplayer.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.model.SongModel;

import java.lang.ref.WeakReference;

public class ImageCacheHelper {
    private LruCache<Long, Bitmap> mBitmapCache;
    private BitmapDrawable mPlaceholder;
    private Context mContext;
    private int mImagePlaceHolderId;

    public ImageCacheHelper(int imagePlaceHolderId) {
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Divide the maximum size by eight to get a adequate size the LRU cache should reach before it starts to evict bitmaps.
        int cacheSize = maxSize / 8;
        mBitmapCache = new LruCache<Long, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Long key, Bitmap value) {
                // returns the size of bitmaps in kilobytes.
                return value.getByteCount() / 1024;
            }
        };
        mContext = MainActivity.getMainActivity().getApplicationContext();
        mPlaceholder = (BitmapDrawable) mContext.getResources().getDrawable(imagePlaceHolderId);
        mImagePlaceHolderId = imagePlaceHolderId;
    }

    public Bitmap getBitmapCache(long albumId) {
        return mBitmapCache.get(albumId);
    }

    public void loadAlbumArt(ImageView icon, SongModel songModel) {
        // Check the current album art task if any and cancel it, if it is loading album art that doesn't match the specified album id.
        if (cancelLoadTask(icon, songModel.getAlbumId())) {
            // There was either no task running or it was loading a different image so create a new one to load the proper image.
            LoadAlbumArt loadAlbumArt = new LoadAlbumArt(icon, mContext);
            // Store the task inside of the async drawable.
            AsyncDrawable drawable = new AsyncDrawable(mContext.getResources(), mPlaceholder.getBitmap(), loadAlbumArt);
            icon.setImageDrawable(drawable);
            loadAlbumArt.execute(songModel);
        }
    }

    public boolean cancelLoadTask(ImageView icon, long albumId) {
        LoadAlbumArt loadAlbumArt = (LoadAlbumArt) getLoadTask(icon);
        // If the task is null return true because we want to try and load the album art.
        if (loadAlbumArt == null) {
            return true;
        }
        if (loadAlbumArt != null) {
            // If the album id differs cancel this task because it cannot be recycled for this imageview.
            if (loadAlbumArt.albumId != albumId) {
                loadAlbumArt.cancel(true);
                return true;
            }
        }
        return false;
    }

    private static class AsyncDrawable extends BitmapDrawable {
        WeakReference<LoadAlbumArt> loadArtworkTaskWeakReference;

        public AsyncDrawable(Resources resources, Bitmap bitmap, LoadAlbumArt task) {
            super(resources, bitmap);
            // Store the LoadArtwork task inside of a weak reference so it can still be garbage collected.
            loadArtworkTaskWeakReference = new WeakReference<LoadAlbumArt>(task);
        }

        public LoadAlbumArt getLoadArtworkTask() {
            return loadArtworkTaskWeakReference.get();
        }
    }

    public AsyncTask getLoadTask(ImageView icon) {
        LoadAlbumArt task = null;
        Drawable drawable = icon.getDrawable();
        if (drawable instanceof AsyncDrawable) {
            task = ((AsyncDrawable) drawable).getLoadArtworkTask();
        }
        return task;
    }

    private class LoadAlbumArt extends AsyncTask<SongModel, Void, Bitmap> {

        // URI that points to the AlbumArt database.
        private final Uri albumArtURI = Uri.parse("content://media/external/audio/albumart");
        public WeakReference<ImageView> mIcon;
        // Holds a publicly accessible albumId to be checked against.
        public long albumId;
        private Context mContext;
        int width, height;

        public LoadAlbumArt(ImageView icon, Context context) {
            // Store a weak reference to the imageView.
            mIcon = new WeakReference<ImageView>(icon);
            // Store the width and height of the imageview.
            // This is necessary for properly scalling the bitmap.
            width = icon.getWidth();
            height = icon.getHeight();
            mContext = context;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled() || bitmap == null) {
                return;
            }
            // Check to make sure that the imageview has not been garbage collected as well as the
            // LoadArtworkTask is the same as this one.
            if (mIcon != null && mIcon.get() != null) {
                ImageView icon = mIcon.get();
                Drawable drawable = icon.getDrawable();
                if (drawable instanceof AsyncDrawable) {
                    LoadAlbumArt task = ((AsyncDrawable) drawable).getLoadArtworkTask();
                    // Make sure that this is the same task as the one current stored inside of the ImageView's drawable.
                    if (task != null && task == this) {
                        icon.setImageBitmap(bitmap);
                    }
                }
            }
            mBitmapCache.put(albumId, bitmap);
            super.onPostExecute(bitmap);
        }

        @Override
        protected Bitmap doInBackground(SongModel... params) {
            // AsyncTask are not guaranteed to start immediately and could be cancelled somewhere in between calling doInBackground.
            if (isCancelled()) {
                return null;
            }
            albumId = params[0].getAlbumId();
            Bitmap bmp = ImageHelper.getBitmapFromPath(params[0].getPath(), mImagePlaceHolderId);
            return bmp;
        }
    }
}
