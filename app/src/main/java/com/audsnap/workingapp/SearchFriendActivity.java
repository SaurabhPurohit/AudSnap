package com.audsnap.workingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.audsnap.R;
import com.audsnap.adapter.SearchFriendAdapter;
import com.audsnap.adapter.SearchFriendViewItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendActivity extends AppCompatActivity {

    private EditText mSearchFriend;
    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, addedFriendReference, profilePicReference;
    private List<SearchFriendViewItem> searchFriendViewItems = new ArrayList<>();
    private List<SearchFriendViewItem> searchEditedFriendViewItems = new ArrayList<>();
    private SearchFriendAdapter searchFriendAdapter;
    private ValueEventListener valueEventListener, updatedValueEventListener;

    private ArrayList<String> addedFreinds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSearchFriend = (EditText) findViewById(R.id.search_friend);
        recyclerView = (RecyclerView) findViewById(R.id.searchFriendRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("/AudSnap/UserNames");
        profilePicReference = firebaseDatabase.getReference("/AudSnap/Profiles/");
        addedFriendReference = firebaseDatabase.getReference("AudSnap/Friends/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        addedFriendReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    addedFreinds.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                searchFriendViewItems.clear();
                for (DataSnapshot searchDataSnapShot : dataSnapshot.getChildren()) {
                    SearchFriendViewItem searchFriendViewItem = new SearchFriendViewItem();

                    searchFriendViewItem.setUsername(searchDataSnapShot.getValue().toString());
                    searchFriendViewItem.setStatus(" ");
                    searchFriendViewItems.add(searchFriendViewItem);

                }

                if (getBaseContext() != null) {
                    searchFriendAdapter = new SearchFriendAdapter(SearchFriendActivity.this, searchFriendViewItems);
                    recyclerView.setAdapter(searchFriendAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };*/

        mSearchFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.equals("")) {
                    updatedValueEventListener = valueEventListener(s);
                    databaseReference.addValueEventListener(updatedValueEventListener);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private ValueEventListener valueEventListener(final CharSequence usernameSubString) {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                searchFriendViewItems.clear();
                for (DataSnapshot searchDataSnapShot : dataSnapshot.getChildren()) {
                    final SearchFriendViewItem searchFriendViewItem = new SearchFriendViewItem();
                    String username = searchDataSnapShot.getValue().toString();
                    if (username.contains(usernameSubString)) {
                        if (addedFreinds.contains(searchDataSnapShot.getKey())) {
                            searchFriendViewItem.setAdded(true);
                        } else {
                            searchFriendViewItem.setAdded(false);
                        }
                        searchFriendViewItem.setUsername(username);
                        searchFriendViewItem.setStatus(" ");
                        searchFriendViewItem.setUserKey(searchDataSnapShot.getKey());
                        profilePicReference.child(searchDataSnapShot.getKey() + "/userinfo/PICTURE/").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        searchFriendViewItem.setPhotoUrl(dataSnapshot.getValue().toString());
                                        searchFriendViewItems.add(searchFriendViewItem);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }

                }

                if (getBaseContext() != null) {
                    searchFriendAdapter = new SearchFriendAdapter(SearchFriendActivity.this, searchFriendViewItems);
                    recyclerView.setAdapter(searchFriendAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getBaseContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        return valueEventListener;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}