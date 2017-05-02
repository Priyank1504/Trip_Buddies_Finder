package com.example.priya.hw09;
/**
 * Created by priyank Verma
 */
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText EmailId, Pass;
    String email, password;
    Button Login, SignUp;
    FirebaseUser user;
    DatabaseReference myRef;
    User u;
    private ArrayList<User> Ulist=new ArrayList<>();
    GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Trip");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("540993086777-gkh2n9ue23djg6gs2btepkp23euda4bj.apps.googleusercontent.com")
                /*.requestIdToken(MainActivity.this.getResources()
                        .getString(R.string.default_web_client_id))*/
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        ImageView GS = (ImageView) findViewById(R.id.buttonGS);
        GS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        EmailId = (EditText) findViewById(R.id.editTextEmailUpdate);
        Pass = (EditText) findViewById(R.id.editTextPassword);
        Login=(Button) findViewById(R.id.buttonLogIn);
        SignUp=(Button) findViewById(R.id.buttonSignUp);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    user = mAuth.getCurrentUser();
                    myRef = FirebaseDatabase.getInstance().getReference();
                    myRef.child("User").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Ulist = new ArrayList<>();
                            Log.d("snapTrip", dataSnapshot.toString());
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                u = d.getValue(User.class);
                                Ulist.add(u);
                            }
                            for(int i=0; i<Ulist.size(); i++) {
                                u=null;
                                u=Ulist.get(i);
                                if (user.getEmail().equals(u.getEmail())) {
                                    Log.d("whatIS", u.getFname());
                                    Intent intExpList = new Intent(MainActivity.this, UserActivity.class);
                                    intExpList.putExtra("ID", user.getEmail());
                                    intExpList.putExtra("IDONE", u.getUserid());
                                    intExpList.putExtra("AUTH", user.getUid());
                                    startActivity(intExpList);

                                }
                            }
                            Toast.makeText(MainActivity.this, "Sign in Done",
                                    Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }

            }
        };
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = EmailId.getText().toString();
                password = Pass.getText().toString();
                if (email.length() > 0 && password.toString().length() > 0) {

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        Log.d("Demo", "signInWithEmail:onComplete:" + task.isSuccessful());
                                        Toast.makeText(MainActivity.this, "Sign in Done",
                                                Toast.LENGTH_SHORT).show();
                                        user = mAuth.getCurrentUser();
                                        myRef = FirebaseDatabase.getInstance().getReference();
                                        myRef.child("User").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Ulist = new ArrayList<>();
                                                Log.d("snapTrip", dataSnapshot.toString());
                                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                                    u = d.getValue(User.class);
                                                    Ulist.add(u);
                                                }
                                                for(int i=0; i<Ulist.size(); i++) {
                                                    u=null;
                                                    u=Ulist.get(i);
                                                    if (user.getEmail().equals(u.getEmail())) {
                                                        Log.d("whatIS", u.getFname());
                                                        Intent intExpList = new Intent(MainActivity.this, UserActivity.class);
                                                        intExpList.putExtra("ID", user.getEmail());
                                                        intExpList.putExtra("IDONE", u.getUserid());
                                                        intExpList.putExtra("AUTH", user.getUid());
                                                        startActivity(intExpList);

                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else {
                                        Log.w("Demo", "signInWithEmail:failed", task.getException());
                                        Toast.makeText(MainActivity.this, "Sign in Failed:" + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(MainActivity.this, "Enter all the Credentials", Toast.LENGTH_LONG).show();
                }

            }

        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 100) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(MainActivity.this, "Google Sign In Successful !",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Google Sign In failed !",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Tag", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Authentication Succeeded",
                                    Toast.LENGTH_SHORT).show();
                            Intent intExpList = new Intent(MainActivity.this, UserActivity.class);
                            startActivity(intExpList);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Tag", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
