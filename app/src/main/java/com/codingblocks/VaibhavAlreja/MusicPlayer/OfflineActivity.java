package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OfflineActivity extends AppCompatActivity {
    public static final String TAG = "offline";
    ///////////////////////////////////////////////////////
    ConstraintLayout constraintLayout;
    mediaAdapterSwipe adapter;
    ListView listView;
    //////////////////////////////////////////////////////////
    //RecyclerView rvTrackList;
    //MediaAdapter mediaAdapter;
    ArrayList<Audio> audioList;
    TextView tvTotalSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.codingblocks.VaibhavAlreja.MusicPlayer.R.layout.activity_offline);
        ///////////////////////////////////////////////////////////////////////////////
        listView = (ListView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.lvTrackList1);
        constraintLayout = (ConstraintLayout) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.cLayout);
        ////////////////////////////////////////////////////////////////////////
        Log.d(TAG, "onCreate: Before loadaudio");
        loadAudio();
        ///////////////////////////////////
        adapter = new mediaAdapterSwipe(OfflineActivity.this, audioList);
        ////////////////////////////////////////
        Log.d(TAG, "onCreate: after loadaudio");
        Collections.sort(audioList, new Comparator<Audio>() {
            public int compare(Audio audio1, Audio audio2) {
                return audio1.getTitle().compareToIgnoreCase(audio2.getTitle());
            }
        });

        Log.d(TAG, "onCreate: Before rv");
        //rvTrackList = (RecyclerView) findViewById(R.id.rvTrackList);
        //rvTrackList.setLayoutManager(new LinearLayoutManager(this));
        //mediaAdapter = new MediaAdapter(this, audioList);
        // rvTrackList.setAdapter(mediaAdapter);
        listView.setAdapter(adapter);
        Log.d(TAG, "onCreate: after rv");
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void itemClick(int itemId, View view) {
                adapter.closeItem(itemId);
                switch (view.getId()) {
                    case com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.lLayout:
                        adapter.closeAllItems();
                        Log.d(TAG, "onCreate: inside onclick");
                        Intent i = new Intent(OfflineActivity.this, MediaActivity.class);
                        i.putExtra("songId", itemId);
                        i.putExtra("audioList", audioList);
                        startActivity(i);
                        break;
                    case com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.del:
                        Toast.makeText(OfflineActivity.this, "del", Toast.LENGTH_SHORT).show();
                        break;
                    case com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.fav:
                        Toast.makeText(OfflineActivity.this, "fav", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        tvTotalSongs = (TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.tvTotalSongs);
        tvTotalSongs.setText("Total Songs : " + audioList.size());
    }

    private void loadAudio() {
        Log.d(TAG, "onCreate: inside loadaudio");
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                // Save to audioList
              //  audioList.add(new Audio(0,data, title, album, artist, duration));
            }
        }
        cursor.close();
    }
}
