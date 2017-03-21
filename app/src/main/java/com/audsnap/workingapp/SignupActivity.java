package com.audsnap.workingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.audsnap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {


    private EditText mEmail,mConfirmPassword,mPassword;
    String email,confirmPassword,password;
    private Button mSignup;
    private TextView mSignin;
    FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mFirebaseAuth= FirebaseAuth.getInstance();

        initUi();

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),SigninActivity.class));
                finish();
            }
        });



        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email=mEmail.getText().toString();
                password=mPassword.getText().toString();
                confirmPassword=mConfirmPassword.getText().toString();

                if(email.equals(""))
                {
                    mEmail.setError("Field should not be empty");
                }
                else if(password.equals(""))
                {
                    mPassword.setError("Field should not be empty");
                }
                else if(!password.equals(confirmPassword))
                {
                    mConfirmPassword.setError("Password and Confirm Password is not same");
                }
                else
                {
                    try {
                        showLoading();
                        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this,
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(getBaseContext(), RegisterUsername.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            );
                                        }
                                    }
                                }).addOnFailureListener(SignupActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(SignupActivity.this,"Error Registering User",Toast.LENGTH_SHORT).show();
                    }

                    finally {
                        stopLoading();
                    }

                }

            }
        });
    }

   void initUi(){
        mEmail=(EditText) findViewById(R.id.signupemail);
        mConfirmPassword=(EditText) findViewById(R.id.signupConfirmpassword);
        mPassword=(EditText) findViewById(R.id.signuppassword);
        mSignup=(Button) findViewById(R.id.signup);
        mSignin=(TextView) findViewById(R.id.signintext);
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
        progressDialog=new ProgressDialog(SignupActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }
    void stopLoading()
    {
        if(progressDialog!=null){
            progressDialog.dismiss();
            progressDialog=null;
        }

    }
}
