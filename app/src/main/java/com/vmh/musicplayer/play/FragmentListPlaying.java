package com.vmh.musicplayer.play;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vmh.musicplayer.MainActivity;
import com.vmh.musicplayer.R;
import com.vmh.musicplayer.RecyclerItemClickListener;
import com.vmh.musicplayer.model.SongModel;

import java.util.ArrayList;

public class FragmentListPlaying extends Fragment implements FragmentPlayInterface {
    private MainActivity mMainActivity;
    private PlayActivity mPlayActivity;
    private Context mContext;
    private LayoutInflater mInflater;
    private static ArrayList<SongModel> mListSong;
    private LinearLayout mLayoutListSong;
    private RecyclerView mListViewSong;
    private ListPlayingAdapter mListSongAdapter;
    private LoadListPlaying loadListPlaying;
    private TextView txtSizePlayingList;
    private static SongModel mSongPlaying = null;
    private boolean playFirst = true;
    public static final String SENDER = "FRAGMENT_PLAYING_LIST";
    private static final String TAG = "FragmentListPlaying";
    private int oldSizeListPlaying;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mContext = getActivity();
            mPlayActivity = (PlayActivity) getActivity();
            loadListPlaying = new LoadListPlaying();


        } catch (IllegalStateException e) {

        }

    }

    public static FragmentListPlaying newInstance() {
        FragmentListPlaying fragmentListPlaying = new FragmentListPlaying();
//        Bundle args = new Bundle();
//        args.putString("Key1", "OK");
//        fragmentListPlaying.setArguments(args);
        mSongPlaying = PlayService.getCurrentSongPlaying();
        return fragmentListPlaying;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: STARTED");
//        new LoadListPlaying().execute();
        updateListPlaying();

    }

    @Nullable
    @Override()
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        ViewGroup viewGroup= (ViewGroup)inflater.inflate(R.layout.fragment_playlist, container, false);
        View view = inflater.inflate(R.layout.fragment_list_playing, container, false);
        return view;
//        Log.i(TAG, "onCreateView PLAYLIST: OKOKOKO");
//        mListSong = new ArrayList<>();// getAllAudioFromDevice(_context);
//        mInflater = inflater;
//        mLayoutListSong = (LinearLayout) mInflater.inflate(R.layout.fragment_list_playing, container, false);
//        mListViewSong = mLayoutListSong.findViewById(R.id.lsvSongs);
//        mListSongAdapter = new ListSongAdapter(mContext, mListSong);
//        mListViewSong.setAdapter(mListSongAdapter);
//        mListViewSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
//        LoadListPlaying.execute();
//        return mLayoutListSong;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtSizePlayingList = mPlayActivity.findViewById(R.id.txtSizePlayingList);
        mListViewSong = (RecyclerView) view.findViewById(R.id.lsvPlaying);
        mListSong = new ArrayList<>();// getAllAudioFromDevice(_context);

//        _layoutListSong = (NestedScrollView) _inflater.inflate(R.layout.fragment_list_song, null);

        mListSongAdapter = new ListPlayingAdapter(mContext, mListSong);
        mListViewSong.setLayoutManager(new LinearLayoutManager(mContext));
        mListViewSong.setAdapter(mListSongAdapter);
        mListViewSong.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, mListViewSong, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        Toast.makeText(mContext, "CLICK ITEM SONG" + position, Toast.LENGTH_SHORT).show();
                        mSongPlaying = mListSong.get(position);
                        mPlayActivity.controlSong(FragmentListPlaying.SENDER, mSongPlaying, PlayService.ACTION_PLAY);
                        mPlayActivity.updateControlPlaying(SENDER, mSongPlaying);
                        mListSongAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                        Toast.makeText(mContext, "LONG CLICK ITEM SONG" + position, Toast.LENGTH_SHORT).show();
                    }
                })
        );
        updateListPlaying();
//        if (mSongPlaying != null && PlayService.getCurrentSongPlaying() != null) {
//            if (mSongPlaying.getSongId() == PlayService.getCurrentSongPlaying().getSongId()) {
//                playFirst = false;
//            }
//        }

//        new LoadListPlaying().execute();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: LIST PLAYING " + mListSong.size());
        outState.putSerializable("playList", mListSong);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loadListPlaying.cancel(true);
    }


    @Override
    public void updateListPlaying() {
        new LoadListPlaying().execute();
    }

    @Override
    public void refreshListPlaying() {
        mPlayActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListSongAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void updateControlPlaying(SongModel songModel) {

    }

    @Override
    public void updateSeekbar(int currentDuration) {

    }

    @Override
    public void updateButtonPlay() {

    }

    private class LoadListPlaying extends AsyncTask<Void, Integer, ArrayList<SongModel>> {

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(ArrayList<SongModel> songModels) {
            super.onPostExecute(songModels);
            if (songModels==null){
                return;
            }
            mListSong.clear();
            mListSong.addAll(songModels);
            txtSizePlayingList.setText(" (" + mListSong.size() + ") ");
            Log.i(TAG, "onPostExecute: SONGS--> " + mListSong.size());
            mListViewSong.post(new Runnable() {
                @Override
                public void run() {
                    mListSongAdapter.notifyDataSetChanged();
                }
            });
            Log.i(TAG, "onPostExecute: FINISHED");
            //play song if songPlaying !=null
//            if (playFirst) {
//                mPlayActivity.controlSong(FragmentListPlaying.SENDER, mSongPlaying, PlayService.ACTION_PLAY);
//                mPlayActivity.updateControlPlaying(SENDER, mSongPlaying);
//            }
//
//            playFirst = false;
//            if (mSongPlaying != null && !PlayService.isPlaying()) {
//                Log.d(TAG, "onPostExecute: DURATION " + PlayService.getCurrentDuration());
//                if (PlayService.getCurrentSongPlaying() != null && PlayService.getCurrentDuration() > 0) {
//                    Log.d(TAG, "onPostExecute: DURATION " + PlayService.getCurrentDuration());
//                    Log.i(TAG, "onPostExecute: RESUME PLAY IN LIST "
//                            + mSongPlaying.getSongId() + "__"
//                            + PlayService.getCurrentSongPlaying().getSongId()
//                            + mSongPlaying + "__"
//                            + PlayService.isPlaying()
//                    );
//                } else {
//                    mPlayActivity.controlSong(FragmentListPlaying.SENDER, mSongPlaying, PlayService.ACTION_PLAY);
//                    mPlayActivity.updateControlPlaying(SENDER, mSongPlaying);
//                }
////                Log.i(TAG, "onPostExecute: RESUME PLAY IN LIST " + mSongPlaying.getSongId() + "__" + PlayService.getCurrentSongPlaying().getSongId());
//            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        public ArrayList<SongModel> doInBackground(Void... voids) {

            return PlayService.getListPlaying();
        }
    }

    public static ArrayList<SongModel> getPlayingList() {
        return mListSong;
    }

}