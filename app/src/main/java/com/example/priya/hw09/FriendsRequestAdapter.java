package com.example.priya.hw09;

/**
 * Created by Priyank Verma
 */
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.Tasks;
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
import java.util.List;


public class FriendsRequestAdapter extends RecyclerView.Adapter<FriendsRequestAdapter.MyViewHolder> {

    SharedPreferences sharedpreferences;
    private ArrayList<User> list;
    private ArrayList<String> Flist=null;
    FriendRequestActivity activity;
    User user;
    SharedPreferences.Editor editor;
    String id, delId;
    final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference ref, myRef;
    FriendsAccepted fr, frRef;
    boolean send ;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView UserImg, Accept, Decline;

        public MyViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.textViewName);
            Decline= (ImageView) view.findViewById(R.id.imageViewDecline);
            UserImg = (ImageView) view.findViewById(R.id.imageViewUser);
            Accept= (ImageView) view.findViewById(R.id.imageViewAccept);

        }
    }
    public FriendsRequestAdapter( String id, ArrayList<User> list, ArrayList<String> Flist,FriendRequestActivity activity) {
        this.list = list;
        this.activity= activity;
        this.Flist=Flist;
        this.id=id;
        Log.d("CheckReq:", list.toString());
        Log.d("CheckReqqq:", Flist.toString());

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_adapter, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        sharedpreferences =  holder.itemView.getContext().getSharedPreferences(id, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        final String f = Flist.get(position);
        for(int j=0;j<list.size();j++){
            user=list.get(j);
            if (f.equals(user.getUserid())) {
                Log.d("Userur", user.getFname());
                send = sharedpreferences.getBoolean(user.getUserid(), false);
                holder.username.setText(user.getFname() + " " + user.getLname());
                holder.username.setTextColor(Color.BLACK);
                Glide.with(holder.itemView.getContext()).load((user.getImgUri()))
                        .override(300, 300)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.UserImg);
                final int finalJ = j;
                holder.Accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user=list.get(finalJ);
                        Log.d("Useruser", user.getFname());
                        Toast.makeText(holder.itemView.getContext(), ""+user.getFname() + " added as Friend", Toast.LENGTH_LONG).show();
                        ref = mDatabase.getInstance().getReference("FriendAccepted");
                        DatabaseReference mUserRef = ref.child(id).child(user.getUserid());
                        DatabaseReference RelatedUserRef = ref.child(user.getUserid()).child(id);
                        fr = new FriendsAccepted();
                        fr.frinedIs=user.getUserid();
                        fr.friendWhom=id;
                        frRef= new  FriendsAccepted();
                        frRef.frinedIs=id;
                        frRef.friendWhom=user.getUserid();
                        delId=user.getUserid();
                        editor.putBoolean(user.getUserid(), false);
                        editor.apply();
                        mUserRef.setValue(fr);
                        RelatedUserRef.setValue(frRef);
                        list.remove(delId);
                        myRef=FirebaseDatabase.getInstance().getReference("Friends").child(id).child(delId);
                        myRef.setValue(null);
                        holder.Accept.setEnabled(false);
                        holder.Decline.setEnabled(false);
                    }
                });
                holder.Decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        user=list.get(finalJ);
                        editor.putBoolean(user.getUserid(), false);
                        editor.apply();
                        Toast.makeText(holder.itemView.getContext(), "Friend request Decline", Toast.LENGTH_LONG).show();
                        list.remove(user.getUserid());
                        holder.Accept.setEnabled(false);
                        holder.Decline.setEnabled(false);
                    }
                });
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        Log.d("SizeOf", ""+Flist.size());
        return Flist.size() ;

    }
}
