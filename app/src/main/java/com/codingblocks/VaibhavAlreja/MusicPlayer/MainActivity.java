package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "offline";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.codingblocks.VaibhavAlreja.MusicPlayer.R.layout.activity_main);

        ((Button) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.btnOffline)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OfflineActivity.class));
            }
        });

//        ((Button) findViewById(R.id.btnOnline)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, SearchOnlineActivity.class));
//            }
//        });
    }
}
