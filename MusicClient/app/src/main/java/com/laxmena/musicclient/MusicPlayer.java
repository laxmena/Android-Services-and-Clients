package com.laxmena.musicclient;

import android.media.MediaPlayer;
import android.util.Log;

public class MusicPlayer {
    private static final String TAG = "MusicPlayer";
    private static MusicPlayer instance = null;
    private MediaPlayer mediaPlayer;

    private MusicPlayer() {
    }

    public static MusicPlayer getInstance() {
        if(instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    public void stopMusic() {
        if(mediaPlayer != null) {
            Log.i(TAG, "stopping music player");
            mediaPlayer.stop();
        } else {
            Log.i(TAG, "stopMusic: NULL");
        }
    }

    public void playMusic(String URL) {
                try {
                    mediaPlayer.setDataSource(URL);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }



}
