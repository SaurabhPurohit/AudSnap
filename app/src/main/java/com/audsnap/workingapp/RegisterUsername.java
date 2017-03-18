package com.audsnap.workingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.audsnap.FragementHandler;
import com.audsnap.R;
import com.audsnap.databaseoperations.InsertUsername;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterUsername extends AppCompatActivity {

    private EditText mUsername;
    private Button mRegisterUsername;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,usernameReference;
    private FirebaseUser firebaseUser;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_username);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseDatabase=FirebaseDatabase.getInstance();

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        uid=firebaseUser.getUid();

        databaseReference=firebaseDatabase.getReference("AudSnap/Profiles/"+firebaseUser.getUid()+"/userinfo");
        usernameReference=firebaseDatabase.getReference("AudSnap/UserNames/");

        mUsername=(EditText) findViewById(R.id.registerusername);
        mRegisterUsername=(Button) findViewById(R.id.b_username);

        mRegisterUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoading();
                Map<String,String> usernameMap=new HashMap<String, String>();
                usernameMap.put("USERNAME",mUsername.getText().toString());
                usernameMap.put("PROVIDER",firebaseUser.getProviderId());
                databaseReference.setValue(usernameMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError==null)
                        {
                            usernameReference.child(uid).setValue(mUsername.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    insertIntoDb(RegisterUsername.this,mUsername.getText().toString());
                                }
                            });


                        }
                        else
                        {
                            Toast.makeText(RegisterUsername.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }

    private void insertIntoDb(Context ctx, String username)
    {
        InsertUsername insertUsername = new InsertUsername(ctx);
        insertUsername.insertUserName(insertUsername,username);
        stopLoading();
        Toast.makeText(RegisterUsername.this,R.string.username_created_successfully,Toast.LENGTH_LONG).show();
        startActivity(new Intent(getBaseContext(),FragementHandler.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

        finish();
    }

    void showLoading()
    {
        progressDialog=new ProgressDialog(RegisterUsername.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

    }
    void stopLoading()
    {
        progressDialog.dismiss();

    }

}
