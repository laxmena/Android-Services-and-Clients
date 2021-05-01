package com.laxmena.musicclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.laxmena.musiccentralservice.MusicCentralService;

import java.io.ByteArrayOutputStream;

import static com.laxmena.musicclient.Constants.*;
import static com.laxmena.musicclient.Constants.TITLE_LIST;

public class MainActivity extends AppCompatActivity {
    private boolean mBound = false;
    private static MusicCentralService mService;
    private String TAG = "MainActivity";
    private Bundle musicBundle = null;
    private Button bind, unbind, showMusicLib;

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

        updateDisplay();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void unbindService() {
        if(mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        updateDisplay();
    }

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

    private Bundle setAllMusicDetails(Bundle mBundle) {
        Bundle bundle = new Bundle();

        String[] title = mBundle.getStringArray(TITLE_LIST);
        String[] artist = mBundle.getStringArray(ARTIST_LIST);
        Bitmap[] image = new Bitmap[title.length];

        bundle.putStringArray(TITLE_LIST, title);
        bundle.putStringArray(ARTIST_LIST, artist);

        for (int i = 0; i < title.length; i++) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image[i] = mBundle.getParcelable(title[i]);
            image[i].compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            bundle.putByteArray(title[i], byteArray);
        }

        return bundle;
    }

    public void updateDisplay() {
        bind.setEnabled(!mBound);
        unbind.setEnabled(mBound);
        showMusicLib.setEnabled(mBound);
        int bindBG = R.color.enabled;
        int unbindBG = R.color.disabled;
        if(mBound) {
            bindBG = R.color.disabled;
            unbindBG = R.color.enabled;
        }
        bind.setBackgroundResource(bindBG);
        bind.setBackgroundResource(unbindBG);
    }
}