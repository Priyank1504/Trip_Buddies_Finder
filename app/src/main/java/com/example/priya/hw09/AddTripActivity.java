package com.example.priya.hw09;
/**
 * Created by Priyank Verma
 */
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddTripActivity extends AppCompatActivity {
    EditText startDate, comment, tripName, group;
    EditText placeAdd;
    Button cancel, save;
    Button add;
    ListView places;
    ImageView imgTrip;
    Uri selectedImageURI;
    String sDate, eDate, dFrom, dTo, tripN, tGroup, tcomm;
    private Uri link;
    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    Trip trip;
    String id;
    ArrayAdapter<String> itemsAdapter;
    ArrayList<String> sLocs;
    ArrayList<Location> locs;
    int selected=-1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                selectedImageURI = data.getData();
                Picasso.with(AddTripActivity.this).load(selectedImageURI).noPlaceholder().centerCrop().fit()
                        .into(imgTrip);
                storageRef = mStorage.getReferenceFromUrl("gs://homework09-c3114.appspot.com");
                StorageReference imagesRef = storageRef.child("images/" + selectedImageURI.getLastPathSegment());
                UploadTask uploadTask = imagesRef.putFile(selectedImageURI);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        link = taskSnapshot.getDownloadUrl();
                        Log.d("Link:", link.toString());
                        save.setEnabled(true);
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        setTitle("Save Trip");
        locs = new ArrayList<>();
        places = (ListView) findViewById(R.id.places);
        add = (Button) findViewById(R.id.textView5);
        placeAdd = (EditText) findViewById(R.id.editTextFrom);
        cancel = (Button) findViewById(R.id.buttonCancel);
        save = (Button) findViewById(R.id.buttonTripSave);
        startDate = (EditText) findViewById(R.id.editTextStartingDate);
        imgTrip = (ImageView) findViewById(R.id.imageViewOfTrip);
        sLocs = new ArrayList<String>();
        itemsAdapter =
                new ArrayAdapter<String>(AddTripActivity.this, android.R.layout.simple_list_item_1, sLocs);
        places.setAdapter(itemsAdapter);

        comment = (EditText) findViewById(R.id.editTextComment);
        comment.setMovementMethod(new ScrollingMovementMethod());
        tripName = (EditText) findViewById(R.id.editTextTripName);
        group = (EditText) findViewById(R.id.editTextGroup);
        id = (String) getIntent().getExtras().get("USER");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });
        save.setEnabled(false);
        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(locs.size()>1) {
                    trip = new Trip();
                    tripN = tripName.getText().toString();
                    trip.setLocList(locs);
                    sDate = startDate.getText().toString();
                    Log.d("date:", sDate + "," + eDate);
                    Log.d("id:", id + "");
                    //dFrom=from.getText().toString();
                    //dTo=to.getText().toString();
                    tGroup = group.getText().toString();
                    tcomm = comment.getText().toString();
                    trip.comment = tcomm;
                    trip.startDate = sDate;
                    trip.imgUri = link.toString();
                    trip.tripName = tripN;
                    trip.group = tGroup;
                    trip.userid = id;
                    ref = mDatabase.getInstance().getReference("Trip");
                    DatabaseReference mUserRef = ref.child(id);
                    mUserRef.setValue(trip);
                    Toast.makeText(AddTripActivity.this, "Trip Successfully Created", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(AddTripActivity.this, "Trip needs at least 2 places total!", Toast.LENGTH_LONG).show();
                }

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("add", "blah");
                if(onSearch(placeAdd.getText().toString())!=null)
                {
                    if(locs.size()<5)
                    {
                        Location loc = onSearch(placeAdd.getText().toString());
                        Toast.makeText(AddTripActivity.this, loc.getLocName() + " Added!", Toast.LENGTH_LONG).show();
                        placeAdd.setText("");

                        locs.add(loc);

                            sLocs.add(loc.getLocName());


                        itemsAdapter.notifyDataSetChanged();


                    }
                    else {
                        Toast.makeText(AddTripActivity.this, "Number of places limited reached!", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(AddTripActivity.this, "Please enter valid place!", Toast.LENGTH_LONG).show();
                }

            }
        });

        places.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(AddTripActivity.this, locs.get(position).getLocName() + " removed from trip!", Toast.LENGTH_LONG).show();

                locs.remove(position);
                sLocs.remove(position);

                itemsAdapter.notifyDataSetChanged();
                return false;
            }
        });


    }

    public Location onSearch(String location) {


        //location = search get text
        List<Address> addressList = null;

        if (location != null || location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addressList.size()>0) {
                Address address = addressList.get(0);
                Location loc = new Location();
                loc.setLocName(address.getFeatureName());
                loc.setLat(address.getLatitude());
                loc.setLng(address.getLongitude());
                return loc;
            }
        }

        return null;
    }

}
