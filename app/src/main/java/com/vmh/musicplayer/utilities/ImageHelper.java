package com.vmh.musicplayer.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ImageHelper {
    private static Context mContext = MainActivity.getMainActivity().getApplicationContext();
    private static final String TAG = "ImageHelper";


    public static Bitmap getBitmapFromPath(String pathImage, int resourceDefaultId) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        if (pathImage != null && !pathImage.isEmpty()) {
            mediaMetadataRetriever.setDataSource(pathImage);
            InputStream inputStream;
            try {

                if (mediaMetadataRetriever.getEmbeddedPicture() != null) {
                    inputStream = new ByteArrayInputStream(mediaMetadataRetriever.getEmbeddedPicture());
                    mediaMetadataRetriever.release();
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inPreferredConfig = Bitmap.Config.RGB_565;

                    bitmap = BitmapFactory.decodeStream(inputStream);
                } else {
                    bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceDefaultId);
                    Log.d(TAG, "getBitmapFromPath: " + bitmap.getByteCount());
                }
            } catch (Exception ex) {
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceDefaultId);
            }

        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), resourceDefaultId);
        }
//        Bitmap thumbnail= ThumbnailUtils.extractThumbnail(bitmap,128,128,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        mediaMetadataRetriever.release();

        return bitmap;
    }
    public static Bitmap getBitmapFromPath(String pathImage) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        if (pathImage != null && !pathImage.isEmpty()) {
            mediaMetadataRetriever.setDataSource(pathImage);
            InputStream inputStream;
            try {

                if (mediaMetadataRetriever.getEmbeddedPicture() != null) {
                    inputStream = new ByteArrayInputStream(mediaMetadataRetriever.getEmbeddedPicture());
                    mediaMetadataRetriever.release();
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inPreferredConfig = Bitmap.Config.RGB_565;

                    bitmap = BitmapFactory.decodeStream(inputStream);
                } else {
                    bitmap = null;
                    Log.d(TAG, "getBitmapFromPath: " + bitmap.getByteCount());
                }
            } catch (Exception ex) {
                bitmap = null;//BitmapFactory.decodeResource(mContext.getResources(), resourceDefaultId);
            }

        } else {
            bitmap = null;
        }
//        Bitmap thumbnail= ThumbnailUtils.extractThumbnail(bitmap,128,128,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        mediaMetadataRetriever.release();

        return bitmap;
    }
    public static BitmapDrawable getMainBackgroundDrawable() {
        Bitmap bitmap = ImageHelper.createImage(480,960, Color.argb(100,71,72,81));//ImageHelper.drawableToBitmap(R.drawable.highcompress_background_test);//highcompress_background_test

//        Bitmap bitmapBg = ImageHelper.blurBitmap(bitmap, 1.0f, 10);


        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        return bitmapDrawable;
    }

    public static BitmapDrawable getMainBackgroundDrawableFromBitmap(Bitmap bitmap) {
//        Bitmap bitmap = ImageHelper.drawableToBitmap(R.drawable.highcompress_background_test);

        Bitmap bitmapBg = ImageHelper.blurBitmap(bitmap, 1.0f, 20);


        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmapBg);
        return bitmapDrawable;
    }

   // public static BitmapDrawable getMimimizeBackgroundDrawable() {

//        Bitmap bitmap = ImageHelper.drawableToBitmap(R.drawable.highcompress_background_test);
//        Bitmap bitmapWhiteGlass = ImageHelper.createImage(bitmap.getWidth(), bitmap.getHeight(), Color.argb(50, 255, 255, 255));
//        Bitmap bitmapBg = ImageHelper.blurBitmap(bitmap, 1.0f, 20);
//
//        Bitmap bitmapOverlay = ImageHelper.overlayBitmapToCenter(bitmapBg, bitmapWhiteGlass);
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmapWhiteGlass);
//        return bitmapDrawable;
    //}

    public static Bitmap drawableToBitmap(int drawableId) {

        Drawable drawable = mContext.getResources().getDrawable(drawableId);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Stack Blur v1.0 from
     * http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
     * Java Author: Mario Klingemann <mario at quasimondo.com>
     * http://incubator.quasimondo.com
     * <p>
     * created Feburary 29, 2004
     * Android port : Yahel Bouaziz <yahel at kayenko.com>
     * http://www.kayenko.com
     * ported april 5th, 2012
     * <p>
     * This is a compromise between Gaussian Blur and Box blur
     * It creates much better looking blurs than Box Blur, but is
     * 7x faster than my Gaussian Blur implementation.
     * <p>
     * I called it Stack Blur because this describes best how this
     * filter works internally: it creates a kind of moving stack
     * of colors whilst scanning through the image. Thereby it
     * just has to add one new block of color to the right side
     * of the stack and remove the leftmost color. The remaining
     * colors on the topmost layer of the stack are either added on
     * or reduced by one, depending on if they are on the right or
     * on the left side of the stack.
     * <p>
     * If you are using this algorithm in your code please add
     * the following line:
     * Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
     */

    public static Bitmap blurBitmap(Bitmap sentBitmap, float scale, int radius) {

        int width = sentBitmap == null ? Resources.getSystem().getDisplayMetrics().widthPixels : Math.round(sentBitmap.getWidth() * scale);
        int height = sentBitmap == null ? Resources.getSystem().getDisplayMetrics().heightPixels : Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();



        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static boolean isDarkBitmap(Bitmap bitmap) {
        boolean dark = false;

        float darkThreshold = bitmap.getWidth() * bitmap.getHeight() * 0.45f;
        int darkPixels = 0;

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int pixel : pixels) {
            int color = pixel;
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            double luminance = (0.299 * r + 0.0f + 0.587 * g + 0.0f + 0.114 * b + 0.0f);
            if (luminance < 150) {
                darkPixels++;
            }
        }

        if (darkPixels >= darkThreshold) {
            dark = true;
        }
//        long duration = System.currentTimeMillis() - s;
        return dark;
    }

    public static Bitmap overlayBitmapToCenter(Bitmap bitmap1, Bitmap bitmap2) {
        int bitmap1Width = bitmap1.getWidth();
        int bitmap1Height = bitmap1.getHeight();
        int bitmap2Width = bitmap2.getWidth();
        int bitmap2Height = bitmap2.getHeight();

        float marginLeft = (float) (bitmap1Width * 0.5 - bitmap2Width * 0.5);
        float marginTop = (float) (bitmap1Height * 0.5 - bitmap2Height * 0.5);

        Bitmap overlayBitmap = Bitmap.createBitmap(bitmap1Width, bitmap1Height, bitmap1.getConfig());
        Canvas canvas = new Canvas(overlayBitmap);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, marginLeft, marginTop, null);
        return overlayBitmap;
    }

    public static Bitmap createImage(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
        return bitmap;
    }
}