package com.vmh.musicplayer.minimizeSong;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vmh.musicplayer.R;
import com.vmh.musicplayer.model.SongModel;
import com.vmh.musicplayer.play.PlayService;
import com.vmh.musicplayer.utilities.ImageHelper;


public class MinimizeSongFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageButton mButtonPlayMinimize;
    private ImageButton mButtonNextMinimize;
    private ImageButton mButtonPrevMinimize;
    private TextView mTextViewTitleSongMinimize;
    private TextView mTextViewArtistMinimize;
    private ImageView mImageViewSongMinimize;
    private PlayService mPlayService;
    private static Context mContext;
    private Animation mAnimationPlay;
    private int mHeightLayout;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private CardView mLayoutPlayingMinimizie;
    private SongModel mCurrentSongPlaying;

    public MinimizeSongFragment() {

    }

    public static MinimizeSongFragment newInstance() {
        MinimizeSongFragment fragment = new MinimizeSongFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mContext = getContext();
        mCurrentSongPlaying = PlayService.getCurrentSongPlaying();
        mPlayService = PlayService.newInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minimize_song, container, false);
        mLayoutPlayingMinimizie = view.findViewById(R.id.cardViewPlayingMinimize);
        mButtonPlayMinimize = view.findViewById(R.id.btnPlaySong);
        mButtonNextMinimize = view.findViewById(R.id.btnNextSong);
        mButtonPrevMinimize = view.findViewById(R.id.btnPrevSong);
        mImageViewSongMinimize = view.findViewById(R.id.imgSongMinimize);
        mTextViewTitleSongMinimize = view.findViewById(R.id.txtTitleMinimize);
        mTextViewArtistMinimize = view.findViewById(R.id.txtArtistMinimize);
        mAnimationPlay = AnimationUtils.loadAnimation(mContext, R.anim.playing_image);
        if (mCurrentSongPlaying != null) {
            mLayoutPlayingMinimizie.setVisibility(View.VISIBLE);
            mTextViewArtistMinimize.setText(mCurrentSongPlaying.getArtist());
            mTextViewTitleSongMinimize.setText(mCurrentSongPlaying.getTitle());
            Bitmap bitmap = ImageHelper.getBitmapFromPath(mCurrentSongPlaying.getPath(), R.mipmap.ic_music);
            mImageViewSongMinimize.setImageBitmap(bitmap);
            mButtonPlayMinimize.setOnClickListener(this);
            mButtonNextMinimize.setOnClickListener(this);
            mButtonPrevMinimize.setOnClickListener(this);
            mLayoutPlayingMinimizie.setOnClickListener(this);
        } else {
            mLayoutPlayingMinimizie.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayoutPlayingMinimizie.post(new Runnable() {
            //                @Override
            public void run() {
                mLayoutPlayingMinimizie.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                mHeightLayout = mLayoutPlayingMinimizie.getMeasuredHeight() + 16;
                mListener.onFragmentLoaded(mHeightLayout);

            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void refreshControls(int action) {
        mCurrentSongPlaying = PlayService.getCurrentSongPlaying();
        if (mCurrentSongPlaying == null) {
            mLayoutPlayingMinimizie.setVisibility(View.GONE);
        } else {
            if (PlayService.isPlaying() || action == PlayService.ACTION_PLAY) {
                setPlayControls();
            } else {
                setPauseControls();
            }
            mTextViewArtistMinimize.setText(mCurrentSongPlaying.getArtist());
            mTextViewTitleSongMinimize.setText(mCurrentSongPlaying.getTitle());
            Bitmap bitmap = ImageHelper.getBitmapFromPath(mCurrentSongPlaying.getPath(), R.mipmap.ic_music);
            mImageViewSongMinimize.setImageBitmap(bitmap);
            mLayoutPlayingMinimizie.setVisibility(View.VISIBLE);
        }
        mListener.onFragmentLoaded(mHeightLayout);
    }

    private void setPlayControls() {
        mImageViewSongMinimize.startAnimation(mAnimationPlay);
        mButtonPlayMinimize.setImageDrawable(mContext.getDrawable(R.drawable.ic_pause_circle_outline_black_32dp));
    }

    private void setPauseControls() {
        mImageViewSongMinimize.clearAnimation();
        mButtonPlayMinimize.setImageDrawable(mContext.getDrawable(R.drawable.ic_play_circle_outline_black_32dp));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardViewPlayingMinimize:
                mListener.onFragmentShowPlayActivity();
                break;
            case R.id.btnPlaySong:
                SongModel songPlay = null;
                songPlay = PlayService.getCurrentSongPlaying();
                if (songPlay == null) {
                    songPlay = PlayService.getSongIsPlaying();
                }
                if (songPlay == null) {
                    Toast.makeText(mContext, "Không tìm thấy bài hát.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (PlayService.isPlaying()) {
                    mListener.onFragmentRefreshNotification(PlayService.ACTION_PAUSE);
                    mPlayService.pause();
                    setPauseControls();
                } else if (PlayService.isPause()) {
                    mListener.onFragmentRefreshNotification(PlayService.ACTION_RESUME);
                    mPlayService.resurme();
                    setPlayControls();
                } else {
                    mListener.onFragmentRefreshNotification(PlayService.ACTION_PLAY);
                    mPlayService.play(songPlay);
                    setPlayControls();
                }
                break;
            case R.id.btnNextSong:
                mPlayService.next(PlayService.ACTION_FROM_USER);
                refreshControls(PlayService.ACTION_PLAY);
                mListener.onFragmentRefreshNotification(PlayService.ACTION_PLAY);
                break;
            case R.id.btnPrevSong:
                mPlayService.prev(PlayService.ACTION_FROM_USER);
                refreshControls(PlayService.ACTION_PLAY);
                mListener.onFragmentRefreshNotification(PlayService.ACTION_PLAY);
                break;
            default:
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void onFragmentRefreshNotification(int action);

        void onFragmentShowPlayActivity();

        void onFragmentLoaded(int heightLayout);
    }
}
