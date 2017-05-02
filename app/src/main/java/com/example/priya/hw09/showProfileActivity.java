package com.example.priya.hw09;
/**
 * Created by priyank Verma
 */
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class showProfileActivity extends AppCompatActivity {

    private RecyclerView Recycler;
    TripAdapter adapter;
    User u, usr;
    String id;
    TextView userN;
    ImageView userPic, tripCover;
    private ProgressDialog mProgressDialog;
    private StorageReference storageRef;
    DatabaseReference myRef;
    Trip t;
    TextView disp;
    LinearLayoutManager layoutManager;
    private ArrayList<User> list=new ArrayList<>();
    private ArrayList<Trip> Tlist=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        setTitle("Profile");
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        disp=(TextView)findViewById(R.id.textViewDisptrip);
        disp.setVisibility(View.INVISIBLE);
        userN=(TextView) findViewById(R.id.Uname);
        tripCover=(ImageView) findViewById(R.id.imageViewCoverTrip);
        userPic=(ImageView) findViewById(R.id.imageViewUserPhoto);
        if(getIntent().getExtras().get("User")!=null) {
            usr =(User) getIntent().getExtras().get("User");
            id = getIntent().getStringExtra("SEND");
            Log.d("III", ""+id);
        }
        Log.d("img", usr.getImgUri());
        Glide.with(showProfileActivity.this).load((usr.getImgUri()))
                .override(250, 250)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userPic);
        userN.setText((""+usr.getFname()+" "+usr.getLname()));
        Recycler = (RecyclerView) findViewById(R.id.trip_recycler_view);
        myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("Trip").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tlist=new ArrayList<>();
                Log.d("snapTrip", dataSnapshot.toString());
                for(DataSnapshot d: dataSnapshot.getChildren()) {
                    t = d.getValue(Trip.class);
                    Tlist.add(t);
                    Log.d("Tlst:", Tlist.toString());
                }
                Log.d("Tlst1111:", Tlist.toString());
                for(int i=0; i<Tlist.size();i++){
                    t=Tlist.get(i);
                    if(usr.getUserid().equals(t.getUserid())){
                        Log.d("TlstNew:", Tlist.toString());
                        Glide.with(getApplicationContext()).load((t.getImgUri()))
                                .override(1200, 250)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(tripCover);
                        break;
                    }
                    else{
                        tripCover.setImageResource(R.drawable.vacation);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                u = null;

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    u = d.getValue(User.class);
                    list.add(u);
                    if (list.size() > 0) {
                        Log.d("snapPPP", list.toString());
                        Log.d("snapP", Tlist.toString());

                        layoutManager = new LinearLayoutManager(showProfileActivity.this) {
                            @Override
                            public boolean canScrollVertically() {
                                return false;
                            }
                        };

                        adapter = new TripAdapter(showProfileActivity.this, id, usr.getUserid(), list, Tlist, showProfileActivity.this);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                        Recycler.setLayoutManager(layoutManager);
                        Recycler.setItemAnimator(new DefaultItemAnimator());
                        Recycler.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        mProgressDialog.dismiss();
                        Log.d("demo", list.toString());
                    }
                    else{
                        disp.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
