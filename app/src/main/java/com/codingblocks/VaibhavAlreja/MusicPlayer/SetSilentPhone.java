package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SetSilentPhone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.codingblocks.VaibhavAlreja.MusicPlayer.R.layout.activity_set_silent_phone);
        ((Button)findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.start)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext() , SilentPhone.class);
                startService(i);
            }
        });
        ((Button)findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.stop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext() , SilentPhone.class);
                stopService(i);
            }
        });
    }
}
