package com.vmh.musicplayer.listsong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.RecyclerItemClickListener;
import com.vmh.musicplayer.callbacks.FragmentCallbacks;
import com.vmh.musicplayer.database.DatabaseManager;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.play.PlayService;
import com.vmh.musicplayer.playlist.BottomSheetOptionSong;
import com.vmh.musicplayer.playlist.FragmentPlaylist;

import java.util.ArrayList;

public class FragmentListSong extends Fragment implements FragmentCallbacks, RecyclerItemClickListener.OnItemClickListener {
    MainActivity _mainActivity;
    Context _context;
    ArrayList<SongModel> _listSong;
    RecyclerView _listViewSong;
    TextView _txtSizeOfListSong;
    ListSongRecyclerAdaper _listSongAdapter;
    private static final String TAG = "FRAGMENT_LIST_SONG";
    public static final String SENDER = "FRAGMENT_LIST_SONG";
    private static final int mThreshHold = 10;
    private static boolean mIsLoading;
    private static PlayService mPlayService;

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
        mPlayService = PlayService.newInstance();
        return fragmentListSong;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                _txtSizeOfListSong.setText("Tìm thấy " + String.valueOf(SongModel.getRowsSong(MainActivity.mDatabaseManager)) + " bài hát");
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: STARTED CREATE VIEW");
        View view = inflater.inflate(R.layout.fragment_list_song, container, false);
        _txtSizeOfListSong = view.findViewById(R.id.txtSizeOfListSong);
        _listViewSong = view.findViewById(R.id.lsvSongs);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                _listSong = SongModel.getSongsWithThreshold(MainActivity.mDatabaseManager, 0, mThreshHold);
                _listSongAdapter = new ListSongRecyclerAdaper(_context, _listSong);
                _listViewSong.setLayoutManager(new LinearLayoutManager(_context));
                _listViewSong.setAdapter(_listSongAdapter);
                _txtSizeOfListSong.setText("Tìm thấy " + String.valueOf(SongModel.getRowsSong(MainActivity.mDatabaseManager)) + " bài hát");
            }
        });
        _listViewSong.addOnItemTouchListener(new RecyclerItemClickListener(_context, _listViewSong, this));
        _listViewSong.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null) {
                    Log.d(TAG, "onScrolled: " + dx + "_" + dy + "___" + linearLayoutManager.getItemCount() + "_" + linearLayoutManager.findLastVisibleItemPosition());
                }

                if (!mIsLoading && linearLayoutManager != null && linearLayoutManager.getItemCount() - 1 <= linearLayoutManager.findLastVisibleItemPosition()) {
                    loadMore();
                    mIsLoading = true;
                }
            }
        });
        return view;

    }

    @Override
    public void updateSizeOfListSong() {
        _txtSizeOfListSong.setText("Tìm thấy " + String.valueOf(SongModel.getRowsSong(MainActivity.mDatabaseManager)) + " bài hát");
    }

    private void loadMore() {
        _listSong.add(null);
        _listSongAdapter.notifyItemInserted(_listSong.size() - 1);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                _listSong.remove(_listSong.size() - 1);
                int scollPosition = _listSong.size();
                _listSongAdapter.notifyItemRemoved(scollPosition);
                ArrayList<SongModel> tempAudioList = SongModel.getSongsWithThreshold(MainActivity.mDatabaseManager, _listSong.size(), mThreshHold);
                _listSong.addAll(tempAudioList);
                mIsLoading = false;
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        final SongModel songPlay = _listSong.get(position);
        mPlayService.play(songPlay);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPlayService.initListPlaying(SongModel.getAllSongs(DatabaseManager.getInstance()));
                Log.d(TAG, "run: ");
            }
        }).start();

        _mainActivity.playSongsFromFragmentListToMain(FragmentPlaylist.SENDER);
    }

    @Override
    public void onLongItemClick(View view, int position) {
        Toast.makeText(_context, "LONG CLICK ITEM SONG" + position, Toast.LENGTH_SHORT).show();
        final SongModel songPlay = _listSong.get(position);
        BottomSheetOptionSong bottomSheetDialogFragment = new BottomSheetOptionSong(songPlay);
        bottomSheetDialogFragment.show(_mainActivity.getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");

    }

    private class loadImageFromStorage extends AsyncTask<Void, Integer, ArrayList<SongModel>> {

        @Override
        protected void onPostExecute(ArrayList<SongModel> songModels) {
            super.onPostExecute(songModels);
            _listSong.remove(_listSong.size() - 1);
            final int positionStart = _listSong.size() + 1;
            _listSong.addAll(songModels);
            Log.i(TAG, "onPostExecute: SONGS--> " + _listSong.size());
            _listViewSong.post(new Runnable() {
                @Override
                public void run() {
                    _listSongAdapter.notifyItemRangeChanged(positionStart, _listSong.size());
                    _listSongAdapter.notifyItemRemoved(positionStart);
                    _listSongAdapter.notifyItemChanged(positionStart);
                    _listSongAdapter.notifyItemInserted(positionStart);
                }
            });
            Log.i(TAG, "onPostExecute: FINISHED");
            mIsLoading = false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        public ArrayList<SongModel> doInBackground(Void... voids) {
            _listSong.add(null);
            _listSongAdapter.notifyItemInserted(_listSong.size());
            ArrayList<SongModel> tempAudioList = SongModel.getSongsWithThreshold(MainActivity.mDatabaseManager, _listSong.size(), mThreshHold);
            return tempAudioList;
        }
    }
}