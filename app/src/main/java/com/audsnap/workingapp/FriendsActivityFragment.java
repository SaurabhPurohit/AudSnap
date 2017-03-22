package com.audsnap.workingapp;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.audsnap.R;
import com.audsnap.adapter.SearchFriendAdapter;
import com.audsnap.adapter.SearchFriendViewItem;
import com.audsnap.workingapp.DisplayUserName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsActivityFragment extends Fragment {


    private RecyclerView recyclerView;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference friendsReference = firebaseDatabase.getReference("AudSnap/Friends/" + FirebaseAuth.getInstance()
            .getCurrentUser().getUid());

    private DatabaseReference friendsInfoReference, profilePicReference = firebaseDatabase.getReference("AudSnap/Profiles/");
    private List<SearchFriendViewItem> searchFriendViewItems = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView mNoFriendsLabel;

    public FriendsActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        progressBar = (ProgressBar) v.findViewById(R.id.friendsProgressView);
        mNoFriendsLabel = (TextView) v.findViewById(R.id.noFriendsTag);

        setUpRecyclerView(v);
        setUpDatabseListener();
        return v;
    }

    private void setUpDatabseListener() {

        friendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchFriendViewItems.clear();
                if (dataSnapshot.getChildrenCount() == 0) {
                    mNoFriendsLabel.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    mNoFriendsLabel.setVisibility(View.GONE);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        final String key = snapshot.getKey();
                        friendsInfoReference = firebaseDatabase.getReference("AudSnap/Profiles/" +
                                key + "/userinfo/USERNAME/");

                        friendsInfoReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final String userName = dataSnapshot.getValue().toString();

                                profilePicReference.child(key + "/userinfo/PICTURE/").
                                        addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                                Log.d("PROFILE REFERENCE",profilePicReference.toString());
                                                SearchFriendViewItem searchFriendViewItem = new SearchFriendViewItem();
                                                searchFriendViewItem.setUsername(userName);
                                                searchFriendViewItem.setStatus(" ");
                                                searchFriendViewItem.setUserKey(key);
                                                searchFriendViewItem.setPhotoUrl(dataSnapshot1.getValue().toString());
                                                searchFriendViewItems.add(searchFriendViewItem);
                                                recyclerView.getAdapter().notifyDataSetChanged();
                                                progressBar.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getContext(), "Error Retriving Your Friends", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setUpRecyclerView(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.friendsRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        SearchFriendAdapter searchFriendAdapter = new SearchFriendAdapter(getContext(), searchFriendViewItems,true,false);
        recyclerView.setAdapter(searchFriendAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_friend, menu);
    }

}
