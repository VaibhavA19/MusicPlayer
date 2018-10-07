package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import co.mobiwise.library.InteractivePlayerView;
import co.mobiwise.library.OnActionClickedListener;

public class MediaActivity extends AppCompatActivity implements OnActionClickedListener, GestureDetector.OnGestureListener {
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.codingblocks.vaibhavalreja.testmusic";
    ArrayList<Audio> audioList;
    boolean serviceBound = false;
    private MediaPlayerService player;
    //////////////////Swipe
    RelativeLayout swipeRLayout;
    ImageView swipeImageView ;
    GestureDetectorCompat gestureDetectorCompat;
    ////////////////
    ///////////////////////////////////////////////////
    private ShakeListener shakeListener;
    /////////////////////////////////////////////
    int songIdReceived;
    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            Toast.makeText(MediaActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.codingblocks.VaibhavAlreja.MusicPlayer.R.layout.activity_media);
        audioList = (ArrayList<Audio>) getIntent().getSerializableExtra("audioList");
        songIdReceived = getIntent().getIntExtra("songId", 0);

        ((TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.musicArtistName)).setText(audioList.get(songIdReceived).getArtist());
        ((TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.musicTitle)).setText(audioList.get(songIdReceived).getTitle());

        ((TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.tvNextSong1Title)).setText(audioList.get((songIdReceived + 1) % audioList.size()).getTitle());
        ((TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.tvNextSong2Title)).setText(audioList.get((songIdReceived + 2) % audioList.size()).getTitle());
        ((TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.tvNextSong3Title)).setText(audioList.get((songIdReceived + 3) % audioList.size()).getTitle());
        ((TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.tvNextSong1Artist)).setText(audioList.get((songIdReceived + 1) % audioList.size()).getArtist());
        ((TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.tvNextSong2Artist)).setText(audioList.get((songIdReceived + 2) % audioList.size()).getArtist());
        ((TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.tvNextSong3Artist)).setText(audioList.get((songIdReceived + 3) % audioList.size()).getArtist());

        ////////////////////////swipe
        swipeRLayout = (RelativeLayout) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.songPlayerTopLayout);
        swipeImageView = (ImageView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.imageView);
        swipeImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        });
        /*swipeRLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        });*/
        gestureDetectorCompat = new GestureDetectorCompat(this, this);
        ///////////////////////////////////////////////

        Log.d("aaa", "1");
        final InteractivePlayerView mInteractivePlayerView = (InteractivePlayerView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.interactivePlayerView);
        long duration = audioList.get(songIdReceived).getDuration();
        mInteractivePlayerView.setMax(250);
        mInteractivePlayerView.setProgress(0);
        mInteractivePlayerView.setOnActionClickedListener(this);

        final ImageView imageView = (ImageView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.control);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mInteractivePlayerView.isPlaying()) {
                    playAudio(songIdReceived);
                    mInteractivePlayerView.start();
                    imageView.setBackgroundResource(com.codingblocks.VaibhavAlreja.MusicPlayer.R.drawable.ic_action_pause);
                } else {
                    mInteractivePlayerView.stop();
                    imageView.setBackgroundResource(com.codingblocks.VaibhavAlreja.MusicPlayer.R.drawable.ic_action_play);
                }
            }
        });
        ////////////////////////////////////////////////////////////
        shakeListener = new ShakeListener(MediaActivity.this);
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                Log.d("abc", "onShake");
                stopServiceIfBound();
                playSong(songIdReceived + 1);
                finish();
            }
        });
        ///////////////////////////////////////////////////////////

    }

    ////////////////////////////////////
    private void playSong(int playIndex) {
        Intent i = new Intent(MediaActivity.this, MediaActivity.class);
        i.putExtra("songId", (audioList.size() + playIndex) % audioList.size());
        i.putExtra("audioList", audioList);
        startActivity(i);
    }

    private void stopServiceIfBound() {
        if (serviceBound) {
            Intent stopservice = new Intent(MediaActivity.this, MediaPlayerService.class);
            stopService(stopservice);
        }
    }
    /////////////////////////////////////////

    ////////////////////////////////////////////////////swipe

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                if (diffX > 0) {
                    Toast.makeText(this, "right", Toast.LENGTH_SHORT).show();
                    stopServiceIfBound();
                    playSong(songIdReceived - 1);
                    finish();
                } else {
                    Toast.makeText(this, "left", Toast.LENGTH_SHORT).show();
                    stopServiceIfBound();
                    playSong(songIdReceived + 1);
                    finish();
                }
            }
        } else {
            if (Math.abs(diffY) > 100 && Math.abs(velocityY) > 100) {
                if (diffY > 0) {
                    Toast.makeText(this, "down", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "up", Toast.LENGTH_SHORT).show();
                }
            }

        }
        return true;
    }

    //////////////////////////////
    ///////////////////////////////////////////////////////////////////////
    @Override
    protected void onPause() {
        Log.d("aaa", "4");
        shakeListener.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("aaa", "5");
        super.onResume();
        shakeListener.resume();
    }

    ///////////////////////////////////////////////////////////////////////
    @Override
    public void onActionClicked(int id) {
        switch (id) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
    }

//    private void playAudio(String media) {
//        //Check is service is active
//        if (!serviceBound) {
//            Intent playerIntent = new Intent(this, MediaPlayerService.class);
//            playerIntent.putExtra("media", media);
//            startService(playerIntent);
//            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//        } else {
//            //Service is active
//            //Send media with BroadcastReceiver
//        }
//    }

    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(audioList);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }


}
