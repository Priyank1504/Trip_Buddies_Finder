package com.example.priya.hw09;

/**
 * Created by Priyank Verma
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.ExpandedMenuView;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder> {
    public static final String MyPREFERENCES = "MyPrefs";
    private ArrayList<User> list;
    private ArrayList<Trip> Tlist;
    UserActivity activity;
    String Userid, authId;
    Friends fr;
    Trip t;
    DatabaseReference myRef;
    DatabaseReference HashRef;
    ArrayList<String> Friendreq;
    final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference refSendTo;
    AlertDialog.Builder builder;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Map<String, Object> userUpdates = new HashMap<String, Object>();
    boolean send ;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView Trip, Username;
        public ImageView UserImg;
        public ImageView FriendReq;
        public MyViewHolder(View view) {
            super(view);
            Username = (TextView) view.findViewById(R.id.textViewUName);
            Trip = (TextView) view.findViewById(R.id.textViewTrip);
            UserImg = (ImageView) view.findViewById(R.id.imageUser);
            FriendReq= (ImageView) view.findViewById(R.id.imageViewAdd);

        }
    }
    public FriendsAdapter(String authId,String id, ArrayList<User> list, ArrayList<Trip> Tlist, UserActivity activity) {
        this.list = list;
        this.activity= activity;
        this.Userid=id;
        this.authId=authId;
        this.Tlist=Tlist;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_recycler_adapter, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final User user = list.get(position);
        sharedpreferences = holder.itemView.getContext().getSharedPreferences(Userid, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        holder.Trip.setVisibility(View.INVISIBLE);
        holder.Username.setText(user.getFname() + " " + user.getLname());
        holder.Username.setTextColor(Color.BLACK);
        for (int i = 0; i < Tlist.size(); i++) {
            t = Tlist.get(i);
            if (user.getUserid().equals(t.getUserid())) {
                holder.Trip.setVisibility(View.VISIBLE);
                Log.d("tripName", t.getTripName());
                holder.Trip.setText(t.getTripName());
            }
        }
        Glide.with(holder.itemView.getContext()) .load((user.getImgUri()))
                .override(250, 250)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.UserImg);
        holder.UserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(holder.itemView.getContext(), showProfileActivity.class);
                i.putExtra("User", user);
                i.putExtra("SEND", Userid);
                activity.startActivity(i);
            }
        });
        send = sharedpreferences.getBoolean(user.getUserid(), false);
        if(send){
            holder.FriendReq.setImageResource(R.drawable.accepted);
        }
        else{
            holder.FriendReq.setImageResource(R.drawable.add);
        }
        holder.FriendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean send  = sharedpreferences.getBoolean(user.getUserid(), false);
                if(send){
                    builder= new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Friend Request Invitation").setMessage("Sure, you want to Undo the sent request!")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    HashRef = FirebaseDatabase.getInstance().getReference("Friends");
                                    DatabaseReference updateRef =HashRef.child(user.getUserid()).child(Userid);
                                    updateRef.setValue(null);
                                    editor.putBoolean(user.getUserid(), false);
                                    editor.apply();
                                    notifyDataSetChanged();
                                    Toast.makeText(holder.itemView.getContext(), "Firend Request Cancel", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(holder.itemView.getContext(), "Friend request Pending", Toast.LENGTH_LONG).show();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    builder= new AlertDialog.Builder(holder.itemView.getContext());
                    builder.setTitle("Friend Request Invitation").setMessage("Sure, you want to send the request!")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    HashRef = FirebaseDatabase.getInstance().getReference("Friends");
                                    DatabaseReference updateRef =HashRef.child(user.getUserid()).child(Userid);
                                    fr = new Friends();
                                    fr.reqSentTo=user.getUserid();
                                    fr.reqSentBy=Userid;
                                    updateRef.setValue(fr);
                                    editor.putBoolean(user.getUserid(), true);
                                    editor.apply();
                                    notifyDataSetChanged();
                                    Toast.makeText(holder.itemView.getContext(), "Invitation sent. Pending acceptance!", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(holder.itemView.getContext(), "Invitation not sent", Toast.LENGTH_LONG).show();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            }
        });
    }
    @Override
    public int getItemCount() {

        return list.size();
    }
}