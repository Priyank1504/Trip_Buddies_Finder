package com.example.priya.hw09;

/**
 * Created by priyank Verma
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.R.drawable.ic_media_play;

/**
 * HW09-A
 * Group#31
 * Created by Priyank Verma and Henry DeJong
 */
public class GridFriendsAdapter extends RecyclerView.Adapter<GridFriendsAdapter.ViewHolder> {
    private List<User> list;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private List<String> Show=null;
    UserActivity activity;
    User user;
    final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    String id;
    DatabaseReference ref;
    static boolean check=false;
    boolean send ;
    // data is passed into the constructor
    public GridFriendsAdapter(String id, ArrayList<User> list, ArrayList<String> Show, UserActivity activity) {
        this.id=id;
        this.Show=Show;
        this.activity=activity;
        this.list = list;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_friends, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView TitleG;
        public ImageView logoG;
        public ViewHolder(View itemView) {
            super(itemView);
            TitleG = (TextView) itemView.findViewById(R.id.textViewFriends);
            logoG = (ImageView) itemView.findViewById(R.id.imageViewFriends);
        }
    }
    // total number of cells
    @Override
    public int getItemCount() {
        Log.d("Checkcheck", ""+check);
        if(check){
            return Show.size()-1;
        }
        else {
            return Show.size();
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String fa = Show.get(position);
        sharedpreferences =  holder.itemView.getContext().getSharedPreferences(id, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        for (int j = 0; j < list.size(); j++) {
            user=list.get(j);
            Log.d("valye:", Show.toString());
            if (fa.equals(user.getUserid())) {
                send = sharedpreferences.getBoolean(user.getUserid(), false);
                holder.TitleG.setText(user.getFname() + " " + user.getLname());
                holder.TitleG.setTextColor(Color.BLACK);
                Log.d("valyeeeee:", user.toString());
                Glide.with(holder.itemView.getContext()).load((user.getImgUri()))
                        .override(300, 300)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.logoG);
                final int finalJ = j;
                holder.logoG.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user = list.get(finalJ);
                        Intent i = new Intent(holder.itemView.getContext(), showProfileActivity.class);
                        i.putExtra("User", user);
                        i.putExtra("SEND", id);
                        activity.startActivity(i);
                    }
                });
                holder.logoG.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        user = list.get(finalJ);
                        check=true;
                        ref = mDatabase.getInstance().getReference("FriendAccepted").child(id).child(user.getUserid());;
                        editor.putBoolean(user.getUserid(), false);
                        editor.apply();
                        ref.setValue(null);
                        Toast.makeText(holder.itemView.getContext(), user.getFname() +" is deleted from your friend list",
                                Toast.LENGTH_LONG).show();
                        holder.logoG.setOnClickListener(null);

                        return true;
                    }
                });
                break;
            }
        }

    }


}

