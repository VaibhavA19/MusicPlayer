package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by ITCONTROLLER on 12/1/2017.
 */

public class SilentPhone extends Service implements SensorEventListener {

    boolean DEBUG = false;
    boolean music;
    boolean ringtone;
    boolean alarm;
    boolean flag = true;
    boolean alarm_recovery = true;
    boolean music_recover;
    public int ringVolume;
    public int alarmVolume;
    public int musicVolume;
    AudioManager audioManager;
    boolean alarmFlipActive = false;
    boolean musicFlipActive = false;
    boolean ringFlipActive = false;
    long duration;
    long current;
    long alarmCurrent;
    long snoozeDur;


    public static final String TAG = "TAG";
    BroadcastReceiver broadcastReceiver;
    SensorManager sensorManager;

    long currentTime;

    public SilentPhone() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Toast.makeText(screenOff.this, "Silent Flip ON", Toast.LENGTH_SHORT).show();
        music =  true;
        ringtone = true;
        alarm =  true;
        alarm_recovery = true;
        music_recover =  true;
        duration = 300000;
        snoozeDur =300000;

        flag = true;


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        AudioManager audioManager;

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        ringVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);


        currentTime = SystemClock.uptimeMillis();
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if ((SystemClock.uptimeMillis() - currentTime) > 1000) {
            currentTime = SystemClock.uptimeMillis();
            if (DEBUG) {
                Log.d("x : ", "" + event.values[0]);
                Log.d("y : ", "" + event.values[1]);
                Log.d("z : ", "" + event.values[2]);
                Log.d("flag: ", "" + flag);
            }
        }


        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        if ((event.values[2] < -9) && flag && (event.values[1] > -3) && (event.values[1] < 3)) {
            //Log.d(TAG," on sensor changed z less than -8");
//            Log.d("music :", "" + music);
//            Log.d("ringtone :", "" + ringtone);
//            Log.d("alarm :", "" + alarm);
            if (music == true && musicFlipActive == false) {
                if (DEBUG) Log.d("MUSIC", "MUSIC IN SENSOR OFF ");
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
                    musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                }
                musicFlipActive = true;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
            if (ringtone == true && ringFlipActive == false) {

                if (DEBUG) Log.d("RINGTONE", "Ringtone");
                if (audioManager.getStreamVolume(AudioManager.STREAM_RING) > 0) {
                    ringVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                    current = SystemClock.uptimeMillis();
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    ringFlipActive = true;

                }


            }
            if (alarm == true && alarmFlipActive == false) {
                if (DEBUG) Log.d("ALARM", "ALARM");
                if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) > 0) {
                    alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                }
                alarmFlipActive = true;
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
        }

        if (event.values[2] > 7 && flag && alarmFlipActive && alarm_recovery) {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVolume, 0);
            alarmCurrent = SystemClock.uptimeMillis();
            alarmFlipActive = false;

        }
        if (event.values[2] > 7 && flag && musicFlipActive && music_recover) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolume, 0);
            musicFlipActive = false;
        }
        if (((SystemClock.uptimeMillis() - current) > duration) && ringFlipActive && flag) {

            audioManager.setStreamVolume(AudioManager.STREAM_RING, ringVolume, 0);
            if (DEBUG) Log.d("Duration:", "" + duration);
            ringFlipActive = false;
        }

        if (event.values[2] < -9 && alarm == true && flag && ((SystemClock.uptimeMillis() - alarmCurrent) > snoozeDur) && alarmFlipActive == true && alarm_recovery) {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarmVolume, 0);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Service Destroyed");
        // Toast.makeText(screenOff.this, "Silent Flip OFF ", Toast.LENGTH_SHORT).show();
        flag = false;
        super.onDestroy();

    }
}
