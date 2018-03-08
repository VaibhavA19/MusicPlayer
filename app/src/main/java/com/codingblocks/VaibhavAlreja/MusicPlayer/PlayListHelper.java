package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


public class PlayListHelper {

    public static Cursor queryPlaylists(ContentResolver resolver) {
        Uri media = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
        String sort = MediaStore.Audio.Playlists.NAME;
        return resolver.query(media, projection, null, null, sort);
    }

    public static ArrayList<Playlist> getAllPlayLists(Context context) {
        Cursor cursor = queryPlaylists(context.getContentResolver());
        ArrayList<Playlist> playlistArrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long id = cursor.getLong(0);
            String name = cursor.getString(1);
            playlistArrayList.add(new Playlist(id, name));
        }
        cursor.close();
        return playlistArrayList;
    }

    public static ArrayList<Audio> getAllSongsOfPlaylist(Context context, Long pid) {
        ArrayList<Audio> audioArrayList = new ArrayList<>();
        final String[] PROJECTION = new String[]{
                MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.Playlists.Members.DATA,
                MediaStore.Audio.Playlists.Members.TITLE,
                MediaStore.Audio.Playlists.Members.ALBUM,
                MediaStore.Audio.Playlists.Members.ARTIST,
                MediaStore.Audio.Playlists.Members.DURATION,
                MediaStore.Audio.Playlists.Members.PLAY_ORDER,
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);
        try {
            ContentResolver contentResolver = context.getContentResolver() ;
            Cursor cursor = contentResolver.query(uri, PROJECTION, null, null,  MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));
                    Long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION));
                    Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
                    audioArrayList.add(new Audio(id, data, title, album, artist, duration));
                }
            } else {
                Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }catch (Exception e){
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
        }

        Toast.makeText(context, audioArrayList.size() + "", Toast.LENGTH_SHORT).show();
        return audioArrayList;
    }

    public static long getPlaylist(ContentResolver resolver, String name) {
        long id = -1;

        Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[]{name}, null);

        if (cursor != null) {
            if (cursor.moveToNext())
                id = cursor.getLong(0);
            cursor.close();
        }

        return id;
    }

    public static long createPlaylist(ContentResolver resolver, String name) {
        long id = getPlaylist(resolver, name);

        if (id == -1) {
            // We need to create a new playlist.
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, name);
            Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
            id = Long.parseLong(uri.getLastPathSegment());
        } else {
            return -1 ;
        }

        return id;
    }


    public static void addToPlaylist(ContentResolver resolver, Long audioId, Long YOUR_PLAYLIST_ID) {

        String[] cols = new String[]{
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", YOUR_PLAYLIST_ID);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + audioId);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(uri, values);
    }

    public static void removeFromPlaylist(ContentResolver resolver, int audioId, Long YOUR_PLAYLIST_ID) {
        Log.v("made it to add", "" + audioId);
        String[] cols = new String[]{
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", YOUR_PLAYLIST_ID);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();

        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID + " = " + audioId, null);
    }

    public static void deletePlaylist(ContentResolver resolver, long id) {
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
        resolver.delete(uri, null, null);
    }


    public static void renamePlaylist(ContentResolver resolver, long id, String newName) {
        long existingId = getPlaylist(resolver, newName);
        // We are already called the requested name; nothing to do.
        if (existingId == id)
            return;
        // There is already a playlist with this name. Kill it.
        if (existingId != -1)
            deletePlaylist(resolver, existingId);

        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, newName);
        resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, "_id=" + id, null);
    }
}
