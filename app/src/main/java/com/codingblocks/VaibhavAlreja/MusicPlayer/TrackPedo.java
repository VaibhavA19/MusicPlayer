package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TrackPedo extends AppCompatActivity {

    RecyclerView rvPedo;
    TrackPedoAdapter adapter;
    DatabaseReference users;
    String token;
    String mid ;
    FirebaseDatabase db;
    ArrayList<PedoUser> pedoUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.codingblocks.VaibhavAlreja.MusicPlayer.R.layout.activity_track_pedo);
        Toolbar toolbar = (Toolbar) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.toolbar);
        setSupportActionBar(toolbar);

        mid = getIntent().getStringExtra("uid");
        Toast.makeText(this, "in track pedo mid "+mid, Toast.LENGTH_SHORT).show();
        pedoUsers = new ArrayList<>();
        pedoUsers.add(new PedoUser("100","111","12/12/12"));
        rvPedo = (RecyclerView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.rvPedo);
        rvPedo.setLayoutManager(new LinearLayoutManager(TrackPedo.this));
        //set adapter
        adapter = new TrackPedoAdapter(getApplicationContext(), pedoUsers);
        rvPedo.setAdapter(adapter);

        token = FirebaseInstanceId.getInstance().getToken();
        db = FirebaseDatabase.getInstance();
        users = db.getReference();
        ChildEventListener cel = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String p = dataSnapshot.getValue(String.class);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(p);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "invalid data type ", Toast.LENGTH_SHORT).show();
                }
                try {
                    pedoUsers.add(new PedoUser(obj.getString("steps"), obj.getString("distance"), obj.getString("date")));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "invalid data type 2", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("tag", "onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdf.format(c.getTime());

        JSONObject obj = new JSONObject();
        try {
            obj.put("distance", "100km");
            obj.put("steps", "100");
            obj.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(mid != null) {
            users.child("users").child(mid).getRef().addChildEventListener(cel);
            users.child("users").child(mid).push().setValue(obj.toString());
        }else{
            Toast.makeText(this, "mid is still null", Toast.LENGTH_SHORT).show();
        }
    }

}
