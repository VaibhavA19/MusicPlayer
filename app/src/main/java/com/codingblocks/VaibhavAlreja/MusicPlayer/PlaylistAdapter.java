package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;


public class PlaylistAdapter extends BaseSwipeAdapter {
    Context context;
    ArrayList<Playlist> playlistArrayList;
    OnPlaylistClickedListener onPlaylistClickedListener;
    OnItemClickListener onItemClickListener;

    public PlaylistAdapter(Context context, ArrayList<Playlist> playlistArrayList, OnPlaylistClickedListener onPlaylistClickedListener, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.playlistArrayList = playlistArrayList;
        this.onPlaylistClickedListener = onPlaylistClickedListener;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return playlistArrayList.size();
    }

    @Override
    public Playlist getItem(int i) {
        return playlistArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.PlaylistSwipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(com.codingblocks.VaibhavAlreja.MusicPlayer.R.layout.play_list_item, null);
    }

    @Override
    public void fillValues(final int position, final View convertView) {
        ((TextView) convertView.findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.tvPlaylistName)).setText(getItem(position).getName());
        convertView.findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.lLayoutPlaylist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlaylistClickedListener.onPlaylistClicked(-1, context, playlistArrayList.get(position).getId());
            }
        });
        convertView.findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.delPlaylist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlaylistClickedListener.onPlaylistClicked(position, context, playlistArrayList.get(position).getId());
            }
        });

    }
}
