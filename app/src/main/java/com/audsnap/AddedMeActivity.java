package com.audsnap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.audsnap.adapter.AddedMeAdapter;
import com.audsnap.adapter.AddedMeViewItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SONI's on 3/15/2017.
 */

public class AddedMeActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference addedFriendsDatabaseReference, userReference;
    private RecyclerView recyclerView;
    private TextView noRequestText;
    private List<AddedMeViewItem> addedMeViewItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_me);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addedFriendToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Friend Request");

        noRequestText = (TextView) findViewById(R.id.noPendingRequestsLabel);
        setUpRecylerView();
        firebaseDatabase = FirebaseDatabase.getInstance();
        addedFriendsDatabaseReference = firebaseDatabase.getReference("/AudSnap/AddedFriends/" + FirebaseAuth
                .getInstance().getCurrentUser().getUid());

        addedFriendsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount()==0) {
                    noRequestText.setVisibility(View.VISIBLE);
                }
                else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final String key = snapshot.getKey();
                        userReference = firebaseDatabase.getReference("/AudSnap/Profiles/" + key + "/userinfo");
                        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String username = dataSnapshot.child("USERNAME").getValue().toString();
                                AddedMeViewItem addedMeViewItem = new AddedMeViewItem();
                                addedMeViewItem.setmKey(key);
                                addedMeViewItem.setmName(username);
                                addedMeViewItems.add(addedMeViewItem);
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void setUpRecylerView() {
        recyclerView = (RecyclerView) findViewById(R.id.addedMeRecylerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddedMeActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        AddedMeAdapter addedMeAdapter = new AddedMeAdapter(AddedMeActivity.this,addedMeViewItems);
        recyclerView.setAdapter(addedMeAdapter);
    }
}
