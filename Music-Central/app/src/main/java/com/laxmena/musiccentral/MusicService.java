package com.laxmena.musiccentral;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.laxmena.musiccentralservice.MusicCentralService;

import java.util.ArrayList;

import static com.laxmena.musiccentral.Constants.ARTIST;
import static com.laxmena.musiccentral.Constants.ARTIST_LIST;
import static com.laxmena.musiccentral.Constants.BITMAP;
import static com.laxmena.musiccentral.Constants.BITMAP_LIST;
import static com.laxmena.musiccentral.Constants.TITLE;
import static com.laxmena.musiccentral.Constants.TITLE_LIST;

public class MusicService extends Service {

    MusicDataProvider dataProvider = null;

    // Constructor for Music Service
    public MusicService() {
        dataProvider = MusicDataProvider.getInstance();

        // Start Foreground Service
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setComponent(new ComponentName("com.laxmena.musicclient", "com.laxmena.musicclient.MainActivity"));
//        startForegroundService(intent);
    }


    // Helper Method
    private Bitmap getImageFromResource(int resId) {
        return BitmapFactory.decodeResource(getResources(), resId);
    }

    // Defenition of the AIDL Methods
    private final MusicCentralService.Stub mBinder = new MusicCentralService.Stub() {
        @Override
        public Bundle getMusicList() throws RemoteException {
            Bundle bundle = new Bundle();
            ArrayList<Music> musicList = dataProvider.getAllMusic();

            ArrayList<String> musicTitles = new ArrayList();
            ArrayList<String> musicArtistNames = new ArrayList();
            ArrayList<Bitmap> musicThumbnails = new ArrayList();

            for(Music musicData: musicList) {
                musicTitles.add(musicData.getTitle());
                musicArtistNames.add(musicData.getArtistName());
                musicThumbnails.add(getImageFromResource(musicData.getThumbnail()));
            }

            bundle.putStringArrayList(TITLE_LIST, musicTitles);
            bundle.putStringArrayList(ARTIST_LIST, musicArtistNames);
            bundle.putParcelableArrayList(BITMAP_LIST, musicThumbnails);

            return bundle;
        }

        // Method returns song info for a particular song, given the song id
        @Override
        public Bundle getSongInfo(int songId) throws RemoteException {
            System.out.println("Querying Song info for: " + songId);
            Bundle bundle = new Bundle();
            Music musicData = dataProvider.getMusicDetails(songId);

            bundle.putString(TITLE, musicData.getTitle());
            bundle.putString(ARTIST, musicData.getArtistName());
            bundle.putParcelable(BITMAP, getImageFromResource(musicData.getThumbnail()));

            return bundle;
        }

        // Given the song id, method returns the url
        @Override
        public String getSongUrl(int songId) throws RemoteException {
            return dataProvider.getMusicDetails(songId).getMusicUrl();
        }

        // Method returns Thumbnail image for the given song id
        @Override
        public Bundle getSongThumbnail(int songId) throws RemoteException {
            Bundle bundle = new Bundle();
            int imgResId = dataProvider.getMusicDetails(songId).getThumbnail();
            bundle.putParcelable(BITMAP, getImageFromResource(imgResId));
            return bundle;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}