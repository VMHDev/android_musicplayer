package com.vmh.musicplayer.listsong;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.RecyclerItemClickListener;
import com.vmh.musicplayer.model.SongModel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class FragmentListSong extends Fragment implements FragmentCallbacks {
    MainActivity _mainActivity;
    Context _context;
    LayoutInflater _inflater;
    ArrayList<SongModel> _listSong;
    NestedScrollView _layoutListSong;
    RecyclerView _listViewSong;
    ListSongRecyclerAdaper _listSongAdapter;
    Button btnCheckout;
    Button btnBottomSheet;
    NestedScrollView layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;

    private static final String TAG = "FRAGMENT_LIST_SONG";
    public static final String SENDER = "FRAGMENT_LIST_SONG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            _context = getActivity();
            _mainActivity = (MainActivity) getActivity();
        } catch (IllegalStateException e) {

        }
    }

    public static FragmentListSong newInstance() {
        FragmentListSong fragmentListSong = new FragmentListSong();
        Bundle args = new Bundle();
        args.putString("Key1", "OK");
        fragmentListSong.setArguments(args);
        return fragmentListSong;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: STARTED");
        new loadImageFromStorage().execute();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _listSong = new ArrayList<>();
        _listViewSong = (RecyclerView) view.findViewById(R.id.lsvSongs);
        _listSongAdapter = new ListSongRecyclerAdaper(_context, _listSong);
        _listViewSong.setLayoutManager(new LinearLayoutManager(_context));
        _listViewSong.setAdapter(_listSongAdapter);
        _listViewSong.addOnItemTouchListener(
                new RecyclerItemClickListener(_context, _listViewSong ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Toast.makeText(_context,"CLICK ITEM SONG"+position,Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Toast.makeText(_context,"LONG CLICK ITEM SONG"+position,Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: STARTED CREATE VIEW");
        return inflater.inflate(R.layout.fragment_list_song, container, false);
    }

    public ArrayList<SongModel> getAllAudioFromDevice(final Context context) {
        final ArrayList<SongModel> tempAudioList = new ArrayList<SongModel>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        int debugLoop = 20;
        if (c != null) {
            int count = 0;
            while (c.moveToNext()) {
                count++;
                Log.d(TAG, "getAllAudioFromDevice: " + count);
                SongModel songModel = new SongModel();
                String path = c.getString(c.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String name = c.getString(c.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                String album = c.getString(c.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
                String artist = c.getString(c.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                String duration = c.getString(c.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));

                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(path);
                InputStream inputStream = null;
                Bitmap bitmap = null;

                if (mediaMetadataRetriever.getEmbeddedPicture() != null) {
                    inputStream = new ByteArrayInputStream(mediaMetadataRetriever.getEmbeddedPicture());
                    mediaMetadataRetriever.release();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } else {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.musical_note_light_64);
                }

                songModel.setTitle(name);
                songModel.setAlbum(album);
                songModel.setArtist(artist);
                songModel.setPath(path);
                songModel.setBitmap(bitmap);
                songModel.setDuration(formateMilliSeccond(Long.valueOf(duration)));
                tempAudioList.add(songModel);
            }
            c.close();
        }
        return tempAudioList;
    }

    public static String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    @Override
    public void TestMessageFromFragmentToActivity(String message) {

    }

    private class loadImageFromStorage extends AsyncTask<Void, Integer, ArrayList<SongModel>> {
        @Override
        protected void onPostExecute(ArrayList<SongModel> songModels) {
            super.onPostExecute(songModels);
            _listSong.addAll(songModels);
            Log.i(TAG, "onPostExecute: SONGS--> " + _listSong.size());
            _listViewSong.post(new Runnable() {
                @Override
                public void run() {
                    _listSongAdapter.notifyDataSetChanged();
                }
            });
            Log.i(TAG, "onPostExecute: FINISHED");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        public ArrayList<SongModel> doInBackground(Void... voids) {
            ArrayList<SongModel> tempAudioList = SongModel.getAllSongs(MainActivity.mDatabaseHelper);
            return tempAudioList;
        }
    }
}

