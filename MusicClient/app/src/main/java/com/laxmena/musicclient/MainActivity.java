package com.laxmena.musicclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.laxmena.musiccentralservice.MusicCentralService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.laxmena.musicclient.Constants.*;
import static com.laxmena.musicclient.Constants.TITLE_LIST;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private boolean mBound = false, mPlaying = false;
    private static MusicCentralService mService;
    private String TAG = "MainActivity";
    private Bundle musicBundle = null;
    private Button bind, unbind, showMusicLib;
    private Spinner spinner;
    MediaPlayer mPlayer;
    private String nowPlaying;
    private TextView nowPlayingComponent, status;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service: " + service);
            mService = MusicCentralService.Stub.asInterface(service);
            mBound = true;
            updateDisplay();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service Disconnected");
            mService = null;
            mBound = false;
            updateDisplay();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bind = findViewById(R.id.bind);
        unbind = findViewById(R.id.unbind);
        showMusicLib = findViewById(R.id.show_music);
        spinner = (Spinner) findViewById(R.id.planets_spinner);
        nowPlayingComponent = findViewById(R.id.nowPlaying);
        status = findViewById(R.id.status);

        // Update UI based on mBound status
        updateDisplay();

        // Setup Spinner to list Songs in the UI
        ArrayAdapter<CharSequence> spinnerArrayAdapter =
                ArrayAdapter.createFromResource(this,
                        R.array.songTitles,
                        R.layout.spinner_item);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(this);

    }

    // Method to stop Music Player
    private void stopMusicPlayer() {
        if(mPlayer != null)
            mPlayer.stop();
    }

    // Method to Start Music Player
    private void startMusicPlayer(String URL) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(URL);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Stop Music Player, if the activity stops
    @Override
    protected void onStop() {
        super.onStop();
        stopMusicPlayer();
    }

    // Method to Unbind from service
    private void unbindService() {
        if(mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        updateDisplay();
    }

    // Logic to bind to the service
    private void bindService() {
        if(!mBound) {
            Intent serviceIntent = new Intent(MusicCentralService.class.getName());
            Log.i(TAG, "bindService: " + MusicCentralService.class.getName());

            ServiceInfo serviceInfo = getPackageManager().resolveService(serviceIntent, 0).serviceInfo;
            serviceIntent.setComponent(new ComponentName(serviceInfo.packageName, serviceInfo.name));
            Log.i(TAG, "bindService: " + serviceInfo.packageName + " " + serviceInfo.name);

            boolean result = bindService(serviceIntent, this.mServiceConnection, Context.BIND_AUTO_CREATE);
            if (result) {
                Log.i(TAG, "bindService successful");
                updateDisplay();
            } else {
                Log.i(TAG, "bindService unsuccessful");
            }
        }
    }

    public void bindButtonOnClick(View view) {
        System.out.println("Binding Service");
        bindService();
    }

    public void unbindButtonOnClick(View view) {
        System.out.println("Unbinding Service");
        unbindService();
    }

    public void showMusicOnClick(View view) throws RemoteException {
        if(musicBundle == null) {
            musicBundle = mService.getMusicList();
        }
        Intent intent = new Intent(this, MusicLibrary.class);
        intent.putExtras(musicBundle);
        startActivity(intent);
    }

    public static MusicCentralService getService() {
        return mService;
    }

    public void updateDisplay() {
        bind.setEnabled(!mBound);
        unbind.setEnabled(mBound);
        showMusicLib.setEnabled(mBound);
        int bindBG, unbindBG;

        if(mBound) {
            status.setText("Status: Binded to Service");
            bindBG = R.color.disabled;
            unbindBG = R.color.enabled;
            spinner.setVisibility(View.VISIBLE);

        } else {
            status.setText("Status: Not Binded");
            bindBG = R.color.enabled;
            unbindBG = R.color.disabled;
            spinner.setVisibility(View.INVISIBLE);
        }
        bind.setBackgroundResource(bindBG);
        bind.setBackgroundResource(unbindBG);

        // Make UI Changes if Song is playing now
        if(mPlaying) {
            nowPlayingComponent.setVisibility(View.VISIBLE);
            nowPlayingComponent.setText("Now Playing:");
        } else {
            nowPlayingComponent.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        stopMusicPlayer();
        if (mBound) {
            if (position == 0) {
                mPlaying = false;
            } else {
                Bundle bundle;
                try {
                    bundle = mService.getSongInfo(position - 1);
                    if(bundle != null) {
                        nowPlaying = bundle.getString(TITLE);
                        nowPlayingComponent.setText(nowPlaying);
                        System.out.println(bundle.getString(TITLE));
                        mPlaying = true;
                        startMusicPlayer(mService.getSongUrl(position - 1));
                    } else {
                        Toast.makeText(this, "Please wait for few seconds, before trying again.", Toast.LENGTH_LONG).show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        updateDisplay();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void playMusic(String URL) {
        startMusicPlayer(URL);
    }


}