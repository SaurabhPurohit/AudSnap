package com.audsnap;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.audsnap.databaseoperations.InsertUsername;
import com.audsnap.workingapp.DisplayUserName;
import com.audsnap.workingapp.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class FragementHandler extends AppCompatActivity {


    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Friends","Inbox"};
    int NumbOfTabs=2;
    File dir;
    static boolean b=false;
    public static String username="";
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usernameRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragement_handler);
        firebaseAuth=FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseUser==null){
                    startActivity(new Intent(getBaseContext(),SigninActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }

            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);

        Log.e("Usernamebefore",username);
        if(firebaseUser!=null) {
            if (username.equals("")) {
                InsertUsername insertUsername = new InsertUsername(this);
                Cursor cr = insertUsername.retriveUsername(insertUsername);
                cr.moveToLast();
                username = cr.getString(0);
//                Log.e("Username", username);
            }
        }

        if(b==false) {
            dir = new File(Environment.getExternalStorageDirectory() + "/AudSnap/");
            if (!dir.exists()) {
                b=dir.mkdir();
            }
        }

        adapter = new ViewPagerAdapter(getSupportFragmentManager(),Titles,NumbOfTabs);

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs=(SlidingTabLayout)findViewById(R.id.tabs);


        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                   return getResources().getColor(R.color.colorAccent);
            }

            @Override
            public int getDividerColor(int position) {
                return 0;
            }

        });
        tabs.setViewPager(pager);
    }

    @Override
    protected void onStart() {
        super.onStart();
       // firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.mainactivitymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                username="";
                startActivity(new Intent(getBaseContext(),SigninActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            case R.id.action_profile:
                startActivity(new Intent(getBaseContext(),ProfileActivity.class));
                break;

            case R.id.menu_add_friend:
                startActivity(new Intent(getBaseContext(),SearchFriendActivity.class));
                break;

            case R.id.action_added_me:
                startActivity(new Intent(getBaseContext(),AddedMeActivity.class));
                break;
        }
        return true;
    }


}
