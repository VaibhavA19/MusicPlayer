package com.codingblocks.VaibhavAlreja.MusicPlayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ITCONTROLLER on 11/30/2017.
 */

public class TrackPedoAdapter extends RecyclerView.Adapter<TrackPedoAdapter.UserViewHolder> {
    Context context;
    ArrayList<PedoUser> pedoUsers;

    public TrackPedoAdapter(Context context, ArrayList<PedoUser> pedoUsers) {
        this.context = context;
        this.pedoUsers = pedoUsers;
    }
    public void  updateArrayList(ArrayList<PedoUser> arrayList){
        this.pedoUsers = arrayList ;
        notifyDataSetChanged();
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(com.codingblocks.VaibhavAlreja.MusicPlayer.R.layout.list_item_fbpedo, parent, false);
        UserViewHolder uvh = new UserViewHolder(view);
        return uvh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        Toast.makeText(context, "position", Toast.LENGTH_SHORT).show();
        PedoUser thisuser = pedoUsers.get(position);
        holder.date.setText(thisuser.getDate());
        holder.steps.setText(thisuser.getSteps());
        holder.distance.setText(thisuser.getDistance());
    }

    @Override
    public int getItemCount() {
        Toast.makeText(context,"in adapter size"+pedoUsers.size(),Toast.LENGTH_SHORT).show();
        return pedoUsers.size();
    }


    public  class UserViewHolder extends RecyclerView.ViewHolder {
        TextView steps, distance, date;

        public UserViewHolder(View itemView) {
            super(itemView);
            this.distance = (TextView) itemView.findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.fbDistance);
            this.steps = (TextView) itemView.findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.fbsteps);
            this.date = (TextView) itemView.findViewById(com.codingblocks.VaibhavAlreja.MusicPlayer.R.id.fbDate);
        }
    }
}
