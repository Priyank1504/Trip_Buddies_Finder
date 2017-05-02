package com.example.priya.hw09;
/**
 * Created by Priyank Verma
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
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendRequestActivity extends AppCompatActivity {
    DatabaseReference myRef;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    Button Finish;
    private ProgressDialog mProgressDialog;
    private ArrayList<User> list=new ArrayList<>();
    String id, authId;
    private ArrayList<Friends> friendList=new ArrayList<>();
    private ArrayList<String> friendListShow=new ArrayList<>();
    Friends fr;
    User ur;
    boolean go=true;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        setTitle("Friends Request");
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        if(getIntent().getExtras().get("ID")!=null) {
            id=getIntent().getExtras().getString("ID");
            list= (ArrayList<User>) getIntent().getExtras().getSerializable("LIST");
            authId=getIntent().getExtras().getString("AUTH");
        }
        Finish=(Button) findViewById(R.id.buttonBack);
        Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("Friends").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendList = new ArrayList<>();
                Log.d("friendListBefore", dataSnapshot.toString());
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    fr = d.getValue(Friends.class);
                    friendList.add(fr);
                    Log.d("friendList:", friendList.toString());
                }
                    for (int j = 0; j < list.size(); j++) {
                        ur = list.get(j);
                        for (int i = 0; i < friendList.size(); i++) {
                            fr = friendList.get(i);
                            Log.d("friinside:", fr.getReqSentBy());

                            if (ur.getUserid().equals(fr.getReqSentBy())) {
                                friendListShow.add(fr.getReqSentBy());
                            }
                        }
                    }
                    Log.d("friendListShow:", friendListShow.toString());
                mRecyclerView = (RecyclerView) findViewById(R.id.friends_recycler_view);
                mAdapter = new FriendsRequestAdapter(id, list, friendListShow, FriendRequestActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAdapter);
                mProgressDialog.dismiss();
                mAdapter.notifyDataSetChanged();
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menulog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(FriendRequestActivity.this, "Sign Out!", Toast.LENGTH_LONG).show();
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
