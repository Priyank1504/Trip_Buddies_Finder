package com.example.priya.hw09;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.app.SearchManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * HW09-A
 * Group#31
 * Created by Priyank and William
 */
import static java.lang.System.load;
/**
 * Created by priyank Verma
 */
public class UserActivity extends AppCompatActivity {
    SearchView search;
    private ProgressDialog mProgressDialog;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<User> list=new ArrayList<>();
    private ArrayList<Trip> Tlist=new ArrayList<>();
    private ArrayList<String> fToShow=new ArrayList<>();
    private ArrayList<User> SearchList=new ArrayList<>();
    TextView Username, TripName, tripCom, Nofriends;
    ImageView trip, userImg;
    DatabaseReference myRef;
    int uindex=0, col;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FriendsAccepted fa;
    private String id, authId;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    User u, ur, utemp;
    AlertDialog.Builder builder;
    Trip t=null;
    private RecyclerView GridrecyclerView;
    GridFriendsAdapter Gadapter;
    private ArrayList<FriendsAccepted> friendShow=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("Trips Awaited!");
        GridrecyclerView = (RecyclerView) findViewById(R.id.grid_View);
        Intent i = getIntent();
        TripName = (TextView) findViewById(R.id.textViewTripname);
        tripCom = (TextView) findViewById(R.id.textViewComment);
        final String email = i.getStringExtra("ID");
        id=i.getStringExtra("IDONE");
        authId=i.getStringExtra("AUTH");
        Log.d("autID", authId.toString());
        Nofriends=(TextView) findViewById(R.id.textView12);
        Nofriends.setVisibility(View.INVISIBLE);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        myRef = FirebaseDatabase.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        // Friend request
        readTrip();
        myRef.child("FriendAccepted").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fToShow=new ArrayList<>();
                Log.d("enter", id);
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    fa = d.getValue(FriendsAccepted.class);
                    Log.d("friendToShow",""+ dataSnapshot.toString());
                    friendShow.add(fa);
                }
                for(int i=0;i<friendShow.size();i++){
                    fa=friendShow.get(i);
                    fToShow.add(fa.getFrinedIs());
                }
                if (fToShow.size() > 0 && u != null) {

                    GridrecyclerView.setLayoutManager(new GridLayoutManager(UserActivity.this, 3));
                    Gadapter = new GridFriendsAdapter(id, list, fToShow, UserActivity.this);
                    Gadapter.notifyDataSetChanged();
                    GridrecyclerView.setAdapter(Gadapter);

                    Log.d("demo", list.toString());
                }
                else{
                    Nofriends.setVisibility(View.VISIBLE);
                }
                displayPYMK();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userImg = (ImageView) findViewById(R.id.imageViewUser);
        Username = (TextView) findViewById(R.id.textViewusername);
        Log.d("FTO", fToShow.toString());
        myRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                u = null;
                Log.d("snap", dataSnapshot.toString());
                int index = 0;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    utemp = d.getValue(User.class);
                    Log.d("email", utemp.getEmail());
                    if (utemp.getEmail().equals(email)) {
                        uindex = index;
                        u = utemp;
                        Username.setTextColor(Color.BLACK);
                        Username.setText(u.getFname() + " " + u.getLname());
                        Glide.with(getApplicationContext())
                                .load((u.getImgUri()))
                                .override(250, 250)
                                .centerCrop()
                                .into(userImg);

                        for (int i = 0; i < Tlist.size(); i++) {
                            t = Tlist.get(i);
                            if (id.equals(t.getUserid())) {

                                TripName.setText(t.getTripName());
                                tripCom.setText(t.getComment());
                            }
                        }
                        index++;
                    }
                    list.add(utemp);
                }
                if (list.size() > 0 && u != null) {
                    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                    ArrayList<User> tempList = list;
                    tempList.remove(u);
                    Log.d("fsize", friendShow.toString());
                    mAdapter = new FriendsAdapter(authId,id, tempList, Tlist, UserActivity.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mRecyclerView.setAdapter(mAdapter);
                    mProgressDialog.dismiss();
                    mAdapter.notifyDataSetChanged();

                    Log.d("demo", list.toString());
                }
                search = (SearchView) findViewById(R.id.searchFriends);
                search.setQueryHint("Search Friends");
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Search(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (TextUtils.isEmpty(newText)) {
                            Search("");
                        }
                        return true;
                    }

                    public void Search(String query) {
                        ArrayList<User> sl = new ArrayList<User>();
                        for (int i = 0; i < list.size(); i++) {
                            ur = list.get(i);
                            SearchList = new ArrayList<>();
                            Log.d("query", query.toString());
                            Log.d("ur", ur.getFname());
                            if (query.toString().equals(ur.getFname()) || query.toString().equals(ur.getFname() + " " + ur.getLname()) || query.toString().equals(ur.getLname())) {
                                Log.d("ur inside:", ur.toString());
                                sl.add(ur);
                                Log.d("ur new:", ur.toString());
                            }
                        }
                        Display(sl);
                    }
                    public void Display(ArrayList<User> sl1) {
                        if (sl1.size() > 0) {
                            mRecyclerView.setAdapter(null);
                            Log.d("Search", sl1.toString());
                            mAdapter = new FriendsAdapter(authId,id, sl1, Tlist, UserActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(UserActivity.this, "No person found with this name ", Toast.LENGTH_SHORT).show();
                            Toast.makeText(UserActivity.this, "search is case Sensitive. Please, Try again! ", Toast.LENGTH_LONG).show();
                            mAdapter = new FriendsAdapter(authId,id, list, Tlist, UserActivity.this);
                            mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            mRecyclerView.setLayoutManager(mLayoutManager);
                            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

                trip = (ImageView) findViewById(R.id.imageViewAddTrip);
                trip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("utemp", id);
                        if (t != null && t.getUserid().equals(id)) {
                            builder = new AlertDialog.Builder(UserActivity.this);
                            builder.setTitle("Trip!!").setMessage("Sure, you want to delete the existing trip and crete a new one?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            myRef = FirebaseDatabase.getInstance().getReference("Trip");
                                            myRef.child(t.getUserid()).removeValue();
                                            Toast.makeText(UserActivity.this, "Trip Deleted!", Toast.LENGTH_LONG).show();
                                            Intent i = new Intent(UserActivity.this, AddTripActivity.class);
                                            i.putExtra("USER", u.getUserid());
                                            startActivity(i);
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(UserActivity.this, "Your old trip is still here!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Intent i = new Intent(UserActivity.this, AddTripActivity.class);
                            if (u != null) {
                                Log.d("uid", "" + u.getUserid());
                            }
                            i.putExtra("USER", u.getUserid());
                            startActivity(i);
                        }
                    }
                });
                userImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("IIIDdd", id);
                        Intent i = new Intent(UserActivity.this, showProfileActivity.class);
                        i.putExtra("User", u);
                        i.putExtra("SEND", id);
                        startActivity(i);
                        /*for(int k=0;k<list.size();k++){
                            if(id.equals(list.get(k).getUserid())){
                                Log.d("gettingSomething", list.get(k).toString());
                            }

                        }*/

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(UserActivity.this, "Sign Out!", Toast.LENGTH_LONG).show();
                super.onBackPressed();
                finish();
                return true;

            case R.id.request:
                Intent intent=new Intent(this, FriendRequestActivity.class);
                intent.putExtra("ID",u.getUserid());
                intent.putExtra("AUTH",authId);
                intent.putExtra("LIST", list);
                startActivity(intent);
                return true;

            case R.id.refresh:
                readTrip();
                return true;

            case R.id.profile:
                Intent i=new Intent(this, UpdateProfileActivity.class);
                Log.d("list", list.toString());
                i.putExtra("User", u);
                i.putExtra("Auth", authId);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayPYMK(){
        if (list.size() > 0 && u != null) {
            ArrayList<User> tempList = new ArrayList<>();
            tempList.remove(u);
            Log.d("fsize1", friendShow.toString());
            for(int y=0; y<list.size(); y++){
                boolean found=false;
                for(int x=0; x<friendShow.size(); x++){
                   if(friendShow.get(x).getFrinedIs().equals(list.get(y).getUserid()))
                   {
                       found=true;
                   }
               }
                if(!found)
                    tempList.add(list.get(y));
            }
            tempList.remove(u);
            mAdapter = new FriendsAdapter(authId ,id, tempList, Tlist, UserActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(mAdapter);
            mProgressDialog.dismiss();
            mAdapter.notifyDataSetChanged();
            Log.d("demo", list.toString());
        }
    }

    @Override
    public void onBackPressed() {

    }

    public void readTrip(){
        myRef.child("Trip").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tlist = new ArrayList<>();
                Log.d("snapTrip", dataSnapshot.toString());
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    t = d.getValue(Trip.class);
                    Tlist.add(t);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        for (int i = 0; i < Tlist.size(); i++) {
            t = Tlist.get(i);
            if (id.equals(t.getUserid())) {
                TripName.setText(t.getTripName());
                tripCom.setText(t.getComment());
                Log.d("trip t", t.toString());
            }
        }


    }

}


