package com.audsnap.workingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.audsnap.R;
import com.audsnap.databaseoperations.InsertUsername;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SigninActivity extends AppCompatActivity {


    private EditText mEmail,mPassword;
    private String email,password;
    private Button mSignin;
    private TextView mSignup,forgotPassword;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usernameRef;
    public static String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        firebaseAuth=FirebaseAuth.getInstance();

        initUi();


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetPasswordEmail();
            }
        });

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),SignupActivity.class));
            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                email=mEmail.getText().toString();
                password=mPassword.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(SigninActivity.this,
                        new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            if(firebaseAuth.getCurrentUser()!=null) {
                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                                usernameRef = firebaseDatabase.getReference("AudSnap/" +
                                        FirebaseAuth.getInstance().getCurrentUser().getUid() + "/userinfo/USERNAME/");
                                Log.e("FRAGMENTHANDLER", "" + usernameRef);

                                usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        username=(String) dataSnapshot.getValue();
                                        insertIntoDb(SigninActivity.this,username);
                                        //Log.e("FRAGMENTHANDLER",username);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                    }
                }).addOnFailureListener(SigninActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SigninActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                });

            }
        });


    }

    private void insertIntoDb(Context ctx,String username)
    {
        InsertUsername insertUsername = new InsertUsername(ctx);
        insertUsername.insertUserName(insertUsername,username);
        stopLoading();
        startActivity(new Intent(getBaseContext(),FragementHandler.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    private void sendResetPasswordEmail() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your Email");

        // Set up the input

        final EditText input = new EditText(this);
        input.setMaxLines(1);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint(R.string.reset_password_email_hint);

        builder.setView(input);


        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String useremail=input.getText().toString();
                firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(SigninActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SigninActivity.this,R.string.password_reset_msg,Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(SigninActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SigninActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void initUi()
    {
        mEmail=(EditText) findViewById(R.id.signinemail);
        mPassword=(EditText) findViewById(R.id.signinpassword);
        mSignin=(Button) findViewById(R.id.signin);
        mSignup = (TextView) findViewById(R.id.signuptext);
        forgotPassword=(TextView) findViewById(R.id.forgotpassword);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    void showLoading()
    {
        progressDialog=new ProgressDialog(SigninActivity.this);
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
