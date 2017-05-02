package com.example.priya.hw09;

/**
 * Created by priyank Verma
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.ExpandedMenuView;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.lang.reflect.Array;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder> {

    private ArrayList<User> list;
    private ArrayList<Trip> Tlist;
    showProfileActivity activity;
    Context context;
    String id, userID;
    DatabaseReference myRef;
    private ArrayList<FriendsAccepted> friendShow=new ArrayList<>();
    User user;
    FriendsAccepted fa;
    private ArrayList<String> fToShow=new ArrayList<>();
    DatabaseReference TripRef;
    ArrayList<String> Friendreq;
    final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference refSendTo;
    SharedPreferences.Editor editor;
    static boolean check=false;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView TripName, comment, range, date;
        public ListView FromTo;
        public Button Delete, route;
        public ImageView GoChat;



        public MyViewHolder(View view) {
            super(view);
            comment = (TextView) view.findViewById(R.id.textViewC);
            FromTo = (ListView) view.findViewById(R.id.FromTo);
            range = (TextView) view.findViewById(R.id.textViewRange);
            TripName = (TextView) view.findViewById(R.id.textViewTip);
            date = (TextView) view.findViewById(R.id.textViewdate);
            Delete = (Button) view.findViewById(R.id.buttonDelete);
            GoChat = (ImageView) view.findViewById(R.id.imageViewGoChat);
            route = (Button) view.findViewById(R.id.route);
        }

    }

    public TripAdapter(Context context, String LogInId, String userID, ArrayList<User> list, ArrayList<Trip> Tlist, showProfileActivity activity) {
        this.list = list;
        this.activity = activity;
        this.userID = userID;
        this.Tlist = Tlist;
        this.id=LogInId;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_recycler_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Log.d("OwnerID",""+ id);
        holder.Delete.setVisibility(View.INVISIBLE);
        for(int i=0;i<Tlist.size();i++) {
            final Trip trip = Tlist.get(i);
            if (userID.equals(trip.getUserid())) {
                check=true;
                Log.d("Onelast", ""+check);
                Log.d("Show T", trip.toString());
                holder.itemView.setBackgroundColor(ContextCompat.getColor((holder.itemView.getContext()), R.color.common_google_signin_btn_text_light_disabled));
                holder.comment.setText("Comment: " + trip.getComment().toString());
                holder.comment.setTextColor(Color.BLACK);
                holder.TripName.setText("Trip Name: " + trip.getTripName());
                final String tripName = trip.getTripName();
                holder.TripName.setTextColor(Color.BLACK);
                ArrayList<String> locals = new ArrayList<>();
                for(int x=0; x<trip.getLocList().size(); x++)
                {
                    locals.add(trip.getLocList().get(x).getLocName());
                }
                Log.d("locals", locals.toString());
                ArrayAdapter<String> itemsAdapter =
                        new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,locals );
                holder.FromTo.setAdapter(itemsAdapter);
                holder.date.setText("Start Date: " + trip.getStartDate());
                holder.range.setText("People Require: " + trip.getGroup());
                holder.date.setTextColor(Color.BLACK);
                holder.range.setTextColor(Color.BLACK);
                Glide.with(holder.itemView.getContext()).load((trip.getImgUri()))
                        .override(250, 250)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.GoChat);

                holder.GoChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef = FirebaseDatabase.getInstance().getReference();
                        myRef.child("FriendAccepted").child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                fToShow=new ArrayList<>();
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    fa = d.getValue(FriendsAccepted.class);
                                    Log.d("friendToShow",""+ dataSnapshot.toString());
                                    friendShow.add(fa);
                                }
                                for(int i=0;i<friendShow.size();i++){
                                    fa=friendShow.get(i);
                                    fToShow.add(fa.getFrinedIs());
                                }
                                for(int z=0;z<fToShow.size();z++) {
                                    Log.d("userIII",fToShow.toString());
                                    Log.d("userI", userID);
                                    if(fToShow.get(z).equals(userID) || userID.equals(id)) {
                                        Intent i = new Intent(holder.itemView.getContext(), ChatRoomActivity.class);
                                        i.putExtra("User", user);
                                        i.putExtra("TN", tripName);
                                        Log.d("Tripname", tripName);
                                        activity.startActivity(i);
                                        break;
                                    }}
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });}});
                if(userID.equals(id)) {
                    holder.Delete.setVisibility(View.VISIBLE);
                    holder.Delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TripRef = FirebaseDatabase.getInstance().getReference("Trip");
                            TripRef.child(id).setValue(null);
                        }
                    });
                }
                else{
                    holder.Delete.setVisibility(View.INVISIBLE);
                }

                holder.route.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), MapActivity.class);
                        i.putExtra("trips", trip.getLocList());
                        v.getContext().startActivity(i);
                    }
                });

            }
        }
    }
    @Override
    public int getItemCount() {
        Log.d("REturnCh", ""+check);
        if(check) {
            return 1;
        }
        else{
            return 1;
        }
    }
}
