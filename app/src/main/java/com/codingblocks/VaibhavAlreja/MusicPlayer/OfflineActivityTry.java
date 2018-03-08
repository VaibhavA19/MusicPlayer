package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OfflineActivityTry extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
    StorageReference storageReference;
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.codingblocks.VaibhavAlreja.MusicPlayer.R.layout.activity_offline_try);
        if (getIntent().getStringExtra("uid") != null) {
            StorageUtil.uid = getIntent().getStringExtra("uid");
            Toast.makeText(getApplicationContext(), StorageUtil.uid, Toast.LENGTH_SHORT).show();
        }
        if (getIntent().getStringExtra("username") != null) {
            StorageUtil.username = getIntent().getStringExtra("username");
        }
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.codingblocks.VaibhavAlreja.MusicPlayer.R.string.navigation_drawer_open, com.codingblocks.VaibhavAlreja.MusicPlayer.R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.uname);
        name.setText(StorageUtil.username);

        ///////////////////////////////////////////////////////////////////////////////
        listView = (ListView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.lvTrackList);
        constraintLayout = (ConstraintLayout) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.cLayout);
        ////////////////////////////////////////////////////////////////////////
        Log.d(TAG, "onCreate: Before loadaudio");
        Long pid = getIntent().getLongExtra("playlistID", -1);
        if (pid == -1) {
            loadAudio(getApplicationContext());
        } else {
            audioList = PlayListHelper.getAllSongsOfPlaylist(OfflineActivityTry.this, pid);
        }
        ///////////////////////////////////
        if(audioList != null) {
            adapter = new mediaAdapterSwipe(OfflineActivityTry.this, audioList);
            ////////////////////////////////////////
            Log.d(TAG, "onCreate: after loadaudio");
            Collections.sort(audioList, new Comparator<Audio>() {
                public int compare(Audio audio1, Audio audio2) {
                    return audio1.getTitle().compareToIgnoreCase(audio2.getTitle());
                }
            });
        }else{
            Toast.makeText(OfflineActivityTry.this,"No Songs",Toast.LENGTH_SHORT);
        }
        /////////////////////////////////////////////////////////

        int permCode = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
        if (permCode == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate Permission Granted");
            Log.d(TAG, "+++++++++++++++++++++++++++++++");
            Toast.makeText(getApplicationContext(),"granted",Toast.LENGTH_SHORT).show();
        }
        if (permCode == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(),"denied",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreate Permission Denied");
            Log.d(TAG, "+++++++++++++++++++++++++++++++");
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 345);//Request code can be anything
        }

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
                adapter.closeAllItems();
                switch (view.getId()) {
                    case com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.lLayout:
                        Log.d(TAG, "onCreate: inside onclick");
                        Intent i = new Intent(OfflineActivityTry.this, MediaActivity.class);
                        i.putExtra("songId", itemId);
                        i.putExtra("audioList", audioList);
                        startActivity(i);
                        break;
                    case com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.del:
                        String root = Environment.getExternalStorageDirectory().toString();
                        String path = audioList.get(itemId).getData();
                        Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT).show();
                        File file = new File(path);
                        Toast.makeText(getApplicationContext(), file.canWrite() + "", Toast.LENGTH_SHORT).show();
                        Boolean deleted = file.delete();
                        if (file.exists()) {
                            try {
                                file.getCanonicalFile().delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(OfflineActivityTry.this, "del" + deleted, Toast.LENGTH_SHORT).show();
                        break;
                    case com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.fav:
                        Toast.makeText(OfflineActivityTry.this, "fav", Toast.LENGTH_SHORT).show();
                        PlayListHelper
                                .addToPlaylist(
                                        OfflineActivityTry.this.getContentResolver()
                                        , audioList.get(itemId).getId()
                                        , PlayListHelper.getPlaylist(getApplicationContext().getContentResolver(), "mFavourites"));
                        break;
                    case com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.fbupload:
                        progressDialog.setMessage("uploading to firebase");
                        progressDialog.show();
                        Toast.makeText(getApplicationContext(), "share", Toast.LENGTH_SHORT).show();
                        uploadFile(itemId);
                        break;
                }
            }
        });

        tvTotalSongs = (TextView) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.tvTotalSongs);
        tvTotalSongs.setText("Total Songs : " + audioList.size());
    }

    private void uploadFile(int itemId) {
        final StorageReference filepath = storageReference.child(StorageUtil.uid).child(audioList.get(itemId).getTitle()+".mp3");
        Uri uri = Uri.fromFile(new File(audioList.get(itemId).getData()));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String share = uri.toString();
                        Intent si = new Intent();
                        si.setAction(Intent.ACTION_SEND);
                        si.putExtra(Intent.EXTRA_TEXT,share);
                        si.setType("text/plain");
                        startActivity(si);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OfflineActivityTry.this, "failed to get download url", Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"failed"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.codingblocks.VaibhavAlreja.MusicPlayer.R.menu.offline_activity_try, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.sortbydu) {
            Collections.sort(audioList, new Comparator<Audio>() {
                @Override
                public int compare(Audio audio, Audio t1) {
                    Long a1 = audio.getDuration();
                    Long a2 = t1.getDuration();
                    return a1.compareTo(a2);
                }
            });
            item.setChecked(true);
            adapter.updateMedia(audioList);
            return true;
        } else if (id == com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.sortbyname) {
            Collections.sort(audioList, new Comparator<Audio>() {
                public int compare(Audio audio1, Audio audio2) {
                    return audio1.getTitle().compareToIgnoreCase(audio2.getTitle());
                }
            });
            item.setChecked(true);
            adapter.updateMedia(audioList);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadAudio(Context context) {
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
                //Toast.makeText(context,data,Toast.LENGTH_SHORT).show();
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                // Save to audioList
                audioList.add(new Audio(id, data, title, album, artist, duration));
            }
        }
        cursor.close();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.Playlists) {
            Intent playlist = new Intent(getApplicationContext(), PlayLists.class);
            startActivity(playlist);
        } else if (id == com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.Settings) {
                Intent i = new Intent(getApplicationContext(),SetSilentPhone.class);
                startActivity(i);
        } else if (id == com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.Youtube) {

        } else if (id == com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.Pedometer) {
            Intent i = new Intent(OfflineActivityTry.this, Pedometer.class);
           i.putExtra("uid", StorageUtil.uid);
            startActivity(i);

        } else if (id == com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.Online) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
