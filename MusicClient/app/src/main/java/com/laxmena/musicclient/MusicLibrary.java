package com.laxmena.musicclient;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.laxmena.musiccentralservice.MusicCentralService;

import java.io.IOException;
import java.util.ArrayList;

public class MusicLibrary extends AppCompatActivity {

    private String TAG = "MusicLibrary";
    private ArrayList<String> songTitles, artistNames;
    private ArrayList<Bitmap> thumbnails;
    private MusicCentralService mService;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MusicLibraryAdapter adapter;
    private MediaPlayer musicPlayer;

    // Constructor function, initiate all values and create Recycler View
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_library);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            songTitles = bundle.getStringArrayList(Constants.TITLE_LIST);
            artistNames = bundle.getStringArrayList(Constants.ARTIST_LIST);
            thumbnails = bundle.getParcelableArrayList(Constants.BITMAP_LIST);
        }

        mService = MainActivity.getService();
        RVClickListener listener = (view, position) -> {
            try {
                if(musicPlayer != null) musicPlayer.stop();

                String URL = (String) mService.getSongUrl(position);
                playMusic(URL);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        };

        recyclerView = findViewById(R.id.musicLibraryRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MusicLibraryAdapter(songTitles, artistNames, thumbnails, listener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    // Method to Play Music
    private void playMusic(String audioUrl) {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        musicPlayer = new MediaPlayer();
        try {
            musicPlayer.setDataSource(audioUrl);
            musicPlayer.prepare();
            musicPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}