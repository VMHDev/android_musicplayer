package com.vmh.musicplayer;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vmh.musicplayer.album.FragmentAlbum;
import com.vmh.musicplayer.artist.FragmentArtist;
import com.vmh.musicplayer.folder.FragmentFolder;
import com.vmh.musicplayer.listsong.FragmentListSong;
import com.vmh.musicplayer.playlist.FragmentPlaylist;
import com.vmh.musicplayer.recent.FragmentRecent;

public class PagerMainAdapter extends FragmentStatePagerAdapter {
    private static final int NUM_PAGES = 6;
    private Fragment mFragmentListSong, mFragmentPlaylist, mFragmentRecent, mFragmentArtist, mFragmentAlbum, mFragmentFolder;
    FragmentManager mFragmentManager;

    public PagerMainAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i) {
            case 0:
                fragment = mFragmentRecent == null ? mFragmentRecent = new FragmentRecent() : mFragmentRecent;
                break;
            case 1:
                fragment = mFragmentListSong == null ? mFragmentListSong = FragmentListSong.newInstance() : mFragmentListSong;
                break;
            case 2:
                fragment = mFragmentPlaylist == null ? mFragmentPlaylist = new FragmentPlaylist() : mFragmentPlaylist;
                break;
            case 3:
                fragment = mFragmentArtist == null ? mFragmentArtist = new FragmentArtist() : mFragmentArtist;
                break;
            case 4:
                fragment = mFragmentAlbum == null ? mFragmentAlbum = new FragmentAlbum() : mFragmentAlbum;
                break;
            case 5:
                fragment = mFragmentFolder == null ? mFragmentFolder = new FragmentFolder() : mFragmentFolder;
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    public Fragment getFragmentAtIndex(int index){
        Fragment fragment = null;
        switch (index) {
            case 0:
                fragment = mFragmentRecent;
                break;
            case 1:

                fragment = mFragmentListSong;
                break;
            case 2:
                fragment = mFragmentPlaylist;
                break;
            case 3:
                fragment = mFragmentArtist;
                break;
            case 4:
                fragment = mFragmentAlbum;

                break;
            case 5:
                fragment = mFragmentFolder;
                break;

            default:
                break;
        }
        return fragment;
    }
}