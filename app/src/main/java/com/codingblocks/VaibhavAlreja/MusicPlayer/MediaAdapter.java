package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    Context context;
    ArrayList<Audio> audioList;
    OnItemClickListener onItemClickListener;

    public MediaAdapter(Context context, ArrayList<Audio> audioList) {
        this.context = context;
        this.audioList = audioList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updateMedia(ArrayList<Audio> newAudioArrayList) {
        this.audioList = newAudioArrayList;
        notifyDataSetChanged();
    }

    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(com.codingblocks.VaibhavAlreja.MusicPlayer.R.layout.list_item_tracks, parent, false);
        return new MediaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MediaViewHolder holder, final int position) {
        Audio audio = audioList.get(position);
        holder.tvTitle.setText(audio.getTitle());
        holder.thisView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.itemClick(position, view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    class MediaViewHolder extends RecyclerView.ViewHolder {
        View thisView;
        TextView tvTitle;

        public MediaViewHolder(View itemView) {
            super(itemView);
            thisView = itemView;
            tvTitle = (TextView) itemView.findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.tvTitle);
        }
    }
}
