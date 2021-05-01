package com.laxmena.musiccentral;

import java.util.ArrayList;

public class MusicDataProvider {
    static MusicDataProvider mSingleton = null;
    private ArrayList<Music> mMusicList = new ArrayList<Music>();

    public static MusicDataProvider getInstance() {
        if(mSingleton == null) {
            mSingleton = new MusicDataProvider();
        }
        return mSingleton;
    }

    private MusicDataProvider() {

        mMusicList.add(new Music("Song 6",
                "SoundHelix",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
                R.drawable.ic_launcher_background));
        mMusicList.add(new Music("Song 8",
                "SoundHelix",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
                R.drawable.ic_launcher_background));
        mMusicList.add(new Music("Song 10",
                "SoundHelix",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
                R.drawable.ic_launcher_background));
        mMusicList.add(new Music("Song 12",
                "SoundHelix",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3",
                R.drawable.ic_launcher_background));
        mMusicList.add(new Music("Song 14",
                "SoundHelix",
                "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3",
                R.drawable.ic_launcher_background));
    }

    public Music getMusicDetails(int index) {
        return mMusicList.get(index);
    }

    public String getMusicUrl(int index) {
        return mMusicList.get(index).getMusicUrl();
    }

    public ArrayList<Music> getAllMusic() {
        return mMusicList;
    }
}
