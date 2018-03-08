package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayLists extends AppCompatActivity {

    ArrayList<Playlist> playlistArrayList;
    ListView listView;
    PlaylistAdapter playlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.codingblocks.VaibhavAlreja.MusicPlayer.R.layout.activity_play_lists);
        Toolbar toolbar = (Toolbar) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long inserted = PlayListHelper.createPlaylist(getApplicationContext().getContentResolver(), "mFavourites");
                if (inserted != -1) {
                    playlistArrayList.add(new Playlist(inserted, "mFavourites"));
                    playlistAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Playlist already\n exits", Toast.LENGTH_SHORT).show();
                }
            }
        });
        playlistArrayList = PlayListHelper.getAllPlayLists(PlayLists.this);
        listView = (ListView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.lvPlaylists);
        playlistAdapter = new PlaylistAdapter(PlayLists.this, playlistArrayList, new OnPlaylistClickedListener() {
            @Override
            public void onPlaylistClicked(int itemid, Context context, Long pid) {
                if (itemid != -1) {
                    PlayListHelper.deletePlaylist(context.getContentResolver(), pid);
                    playlistArrayList.remove(itemid);
                    playlistAdapter.closeAllItems();
                    playlistAdapter.notifyDataSetChanged();
                } else {
                    try {

                        Intent offlineAct = new Intent(context, OfflineActivityTry.class);
                        offlineAct.putExtra("playlistID", pid);
                        startActivity(offlineAct);
                    } catch (Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, null);
        listView.setAdapter(playlistAdapter);
    }

}
