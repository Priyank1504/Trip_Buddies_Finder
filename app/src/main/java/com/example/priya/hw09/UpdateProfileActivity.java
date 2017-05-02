package com.example.priya.hw09;
/**
 * Created by priyank Verma
 */
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText Firstname, Lastname;
    Spinner gender;
    DatabaseReference updateRef;
    Uri selectedImageURI;
    String genderSelected, authId;
    ImageView img;
    int position;
    Button cancel, update;
    User usr;
    private Uri link ;
    FirebaseStorage mStorage= FirebaseStorage.getInstance();
    private StorageReference storageRef;
    String Uname,Uimg, Ugender;
    DatabaseReference Ref=FirebaseDatabase.getInstance().getReference("User");
    Map<String, Object> userUpdates = new HashMap<String, Object>();
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                selectedImageURI = data.getData();
                Picasso.with(UpdateProfileActivity.this).load(selectedImageURI).noPlaceholder().centerCrop().fit()
                        .into(img);
                storageRef = mStorage.getReferenceFromUrl("gs://homework09-c3114.appspot.com");
                StorageReference imagesRef = storageRef.child("images/"+selectedImageURI.getLastPathSegment());
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
                        Log.d("Link:",link.toString());
                        update.setEnabled(true);
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        setTitle("Update Profile");
        if(getIntent().getExtras().get("User")!=null) {
            usr =(User) getIntent().getExtras().get("User");
            authId= (String) getIntent().getExtras().get("Auth");

        }
        Firstname = (EditText) findViewById(R.id.editTextFUp);
        Firstname.setText(usr.getFname());
        Lastname = (EditText) findViewById(R.id.editTextLUp);
        Lastname.setText(usr.getLname());
        cancel = (Button) findViewById(R.id.buttonCancel);
        update = (Button) findViewById(R.id.buttonUpdate);
        update.setEnabled(false);
        gender = (Spinner) findViewById(R.id.spinnerUp);
        List<String> categories = new ArrayList<String>();
        categories.add("Male");
        categories.add("Female");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        if(usr.getGender()=="Male"){
            position=0;
        }else{
            position=1;
        }
        gender.setSelection(position);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(dataAdapter);
        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Please Select the Gender!", Toast.LENGTH_LONG).show();
            }
        });
        img = (ImageView) findViewById(R.id.imageViewUpdate);
        Glide.with(getApplicationContext()).load((usr.getImgUri()))
                .override(250, 250)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uname = Firstname.getText().toString() + " " + Lastname.getText().toString();
                Ugender = genderSelected;
                Uimg = selectedImageURI.toString();
                if(Uname.length()>0 && Firstname.getText().toString().length()>0 && Lastname.getText().toString().length()>0) {
                    Log.d("Check", Uname.toString()+Ugender.toString());
                    updateRef =Ref.child(authId);
                    userUpdates.put("/fname",Firstname.getText().toString());
                    userUpdates.put("/lname",Lastname.getText().toString());
                    userUpdates.put("/gender",Ugender);
                    userUpdates.put("/imgUri",link.toString());
                    updateRef.updateChildren(userUpdates);
                    Toast.makeText(UpdateProfileActivity.this, "Successfully Updated User", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    Toast.makeText(UpdateProfileActivity.this,"All fields must be filled correctly!",Toast.LENGTH_LONG).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                finish();
            }
        });

    }
}
