package com.audsnap.workingapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.audsnap.R;
import com.audsnap.adapter.InboxItem;
import com.audsnap.adapter.SearchFriendAdapter;
import com.audsnap.adapter.SearchFriendViewItem;
import com.audsnap.camera.DemoCamera;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class InboxActivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<SearchFriendViewItem> searchFriendViewItems = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mChatReference = firebaseDatabase.getReference("/AudSnap/chat/"+
            FirebaseAuth.getInstance().getCurrentUser().getUid()+"/received/");
    private ProgressBar progressBar;
    private TextView textView;

    public InboxActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        textView=(TextView) view.findViewById(R.id.noItemToDisplay);
        progressBar=(ProgressBar) view.findViewById(R.id.inboxProgressBar);
        setUpRecyclerView(view);
        setUpChatDbReference();
        return view;

    }

    private void setUpChatDbReference() {
        mChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0)
                {
                    textView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    textView.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final InboxItem inboxItem = snapshot.getValue(InboxItem.class);
                        DatabaseReference databaseReference = firebaseDatabase.getReference("/AudSnap/Profiles/" +
                                inboxItem.getReceiver() + "/userinfo/");

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                com.audsnap.adapter.UserInfo userInfo = dataSnapshot.getValue(com.audsnap.adapter.UserInfo.class);
                                SearchFriendViewItem searchFriendViewItem = new SearchFriendViewItem();
                                searchFriendViewItem.setUserKey(inboxItem.getReceiver());
                                searchFriendViewItem.setReceivedImageUrl(inboxItem.getImage());
                                searchFriendViewItem.setReceivedAudioUrl(inboxItem.getAudio());
                                searchFriendViewItem.setPhotoUrl(userInfo.getPICTURE());
                                searchFriendViewItem.setUsername(userInfo.getUSERNAME());
                                searchFriendViewItems.add(searchFriendViewItem);
                                recyclerView.getAdapter().notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpRecyclerView(View view) {
        recyclerView=(RecyclerView) view.findViewById(R.id.inboxRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SearchFriendAdapter searchFriendAdapter = new SearchFriendAdapter(getContext(),searchFriendViewItems,false,true);
        recyclerView.setAdapter(searchFriendAdapter);
    }
}
