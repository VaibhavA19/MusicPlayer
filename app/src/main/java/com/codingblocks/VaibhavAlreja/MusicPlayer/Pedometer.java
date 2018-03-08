package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Pedometer extends AppCompatActivity {

    DatabaseReference users;
    String token;
    FirebaseDatabase db;
    private SharedPreferences sharedPreferences;
    private Boolean started;
    private Float initSteps;
    private ToggleButton btnPedo;
    private TextView tvSteps;
    Float curSteps = Float.valueOf(0);
    private TextView tvDist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.codingblocks.VaibhavAlreja.MusicPlayer.R.layout.activity_pedometer);
        Toolbar toolbar = (Toolbar) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.toolbar);
        setSupportActionBar(toolbar);

        token = FirebaseInstanceId.getInstance().getToken();
        db = FirebaseDatabase.getInstance();
        users = db.getReference();

        tvSteps = (TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.steps);
        btnPedo = (ToggleButton) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.btnPedo);
        tvDist = (TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.dist);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Pedometer.this);
        started = sharedPreferences.getBoolean("running", false);
        initSteps = sharedPreferences.getFloat("steps", 0);
        // Toast.makeText(getApplicationContext(), "started on create" + started, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "initsteps on create" + initSteps, Toast.LENGTH_SHORT).show();
        if (started) {
            btnPedo.setChecked(true);
        }
        btnPedo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (b) {
                    started = true;
                    Boolean alreadyStarted = sharedPreferences.getBoolean("running", false);
                    // Toast.makeText(getApplicationContext(), "already " + alreadyStarted, Toast.LENGTH_SHORT).show();
                    if (!alreadyStarted) {
                        if (curSteps != 0) {
                            initSteps = curSteps;
                            editor.putFloat("steps", initSteps);
                            editor.putBoolean("running", true);
                            editor.commit();
                        }
                    }
                } else {
                    //////////////////////////////////////////////////////
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    String date = sdf.format(c.getTime());
                    String mid = getIntent().getStringExtra("uid");
                    JSONObject obj = new JSONObject();
                    try {
                        String dist = tvDist.getText().toString();
                        String st = tvSteps.getText().toString();
                        obj.put("distance", dist);
                        obj.put("steps", st);
                        obj.put("date", date);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(mid != null) {
                        users.child("users").child(mid).push().setValue(obj.toString());
                        Toast.makeText(Pedometer.this, "Updated to firebase", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Pedometer.this, "uid still null", Toast.LENGTH_SHORT).show();
                    }
                    ////////////////////////////////////////////////////////////
                    editor.putFloat("steps", 0);
                    editor.putBoolean("running", false);
                    started = false;
                    tvSteps.setText("0");
                    tvDist.setText("0");
                    editor.commit();
                }
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor pedo = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor stepDetect = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (pedo == null) {
            final AlertDialog alertDialog = new AlertDialog.Builder(Pedometer.this)
                    .setTitle("Step Counter")
                    .setMessage("Device doesn't support pedometer...")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        } else {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    //Toast.makeText(getApplicationContext(), "sense changed", Toast.LENGTH_SHORT).show();
                    if (started) {
                        if (initSteps == 0) {
                            initSteps = event.values[0];
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putFloat("steps", initSteps);
                            editor.putBoolean("running", true);
                            editor.commit();
                            // Toast.makeText(getApplicationContext(), "s changed initsteps" + initSteps, Toast.LENGTH_SHORT).show();
                        }

                        Float steps = event.values[0] - initSteps;
                        tvDist.setText(distance(steps));
                        tvSteps.setText("" + steps);
                    }
                    curSteps = event.values[0];
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            }, pedo, 100000);
        }


        if (stepDetect == null) {
            final AlertDialog alertDialog1 = new AlertDialog.Builder(Pedometer.this)
                    .setTitle("Step Counter")
                    .setMessage("Device doesn't support pedometer...")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        } else {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            }, stepDetect, 1000);
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////

        FloatingActionButton fab = (FloatingActionButton) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent i = new Intent(getApplicationContext(), TrackPedo.class);
                if (getIntent().getStringExtra("uid") != null) {
                    i.putExtra("uid", getIntent().getStringExtra("uid"));
                } else {
                    Toast.makeText(getApplicationContext(), "null uid", Toast.LENGTH_SHORT).show();
                }
                startActivity(i);
            }
        });
    }

    public String distance(Float steps) {
        String result = "";

        float d = (steps * 25);
        int km = (int)( d / 100000);
        int m = (int)(d/100);
        if (km > 0) {
            result = d + " km";
        } else if (m > 0) {
            result = d + " m";
        } else {
            result = d + " cm";
        }
        return result;
    }


}
