package com.audsnap.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.audsnap.R;
import com.audsnap.camera.DemoCamera;
import com.audsnap.workingapp.ReceivedImagePreview;
import com.audsnap.workingapp.SearchFriendActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by SONI's on 8/18/2016.
 */
public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.MyViewHolder>{

    List<SearchFriendViewItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private View view;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String uid;
    private boolean flagLayout=false,previewLayout=false;
    public static String receiverId="";

    public SearchFriendAdapter(Context context, List<SearchFriendViewItem> data) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        firebaseDatabase=FirebaseDatabase.getInstance();
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference=firebaseDatabase.getReference("AudSnap/AddedFriends/"+uid);
    }

    public SearchFriendAdapter(Context context, List<SearchFriendViewItem> data,boolean flagLayout,boolean previewLayout) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        firebaseDatabase=FirebaseDatabase.getInstance();
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference=firebaseDatabase.getReference("AudSnap/AddedFriends/"+uid);
        this.flagLayout=flagLayout;
        this.previewLayout=previewLayout;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.cardview_search_friend, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final SearchFriendViewItem searchFriendViewItem = data.get(position);

        boolean flag = searchFriendViewItem.isAdded();

        if(searchFriendViewItem.getPhotoUrl().equals("a")){
            holder.mProfilePicture.setImageDrawable(context.getResources().getDrawable(R.drawable.default_avatar));
        }
        else {
            Picasso.with(context).load(searchFriendViewItem.getPhotoUrl()).into(holder.mProfilePicture);
        }
        if(flagLayout)
        {
            holder.mMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    receiverId=searchFriendViewItem.getUserKey();
                    context.startActivity(new Intent(context, DemoCamera.class));
                }
            });
        }

        if(previewLayout)
        {
            holder.mAddedButton.setVisibility(View.GONE);
            holder.mAddButton.setVisibility(View.GONE);
            holder.mMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ReceivedImagePreview.class)
                            .putExtra("image",searchFriendViewItem.getReceivedImageUrl())
                            .putExtra("audio",searchFriendViewItem.getReceivedAudioUrl()));
                }
            });
        }

        if(!flagLayout && !previewLayout){

            if(flag)
            {
                holder.mAddedButton.setVisibility(View.VISIBLE);
            }
            else {
                holder.mAddButton.setVisibility(View.VISIBLE);
            }
        }else {
            holder.mAddedButton.setVisibility(View.GONE);
            holder.mAddButton.setVisibility(View.GONE);
        }

        holder.mUsername.setText(searchFriendViewItem.getUsername());
        holder.mStatus.setText("Hello There, I'm using AudSnap");

        holder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference mAddedFriendReferenceFriend=firebaseDatabase.getReference("AudSnap/AddedFriends/"+
                        searchFriendViewItem.getUserKey()+"/");

                holder.mAddButton.setVisibility(View.GONE);
                holder.mAddingButton.setVisibility(View.VISIBLE);

                mAddedFriendReferenceFriend.child(uid).setValue("true").
                        addOnSuccessListener((SearchFriendActivity) context, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                               // mAddedFriendReferenceFriend.child(uid).setValue("true");
                                holder.mAddingButton.setVisibility(View.GONE);
                                holder.mAddedButton.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener((SearchFriendActivity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                        holder.mAddingButton.setVisibility(View.GONE);
                        holder.mAddButton.setVisibility(View.VISIBLE);
                    }
                });

            }
        });

        holder.mAddedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference mAddedFriendReferenceFriend=firebaseDatabase.getReference("AudSnap/AddedFriends/"+
                        searchFriendViewItem.getUserKey()+"/");

                holder.mAddedButton.setVisibility(View.GONE);
                holder.mAddingButton.setVisibility(View.VISIBLE);
                holder.mAddingButton.setText("Removing ");

                mAddedFriendReferenceFriend.child(uid).removeValue().
                        addOnSuccessListener((SearchFriendActivity) context, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //mAddedFriendReferenceFriend.child(uid).removeValue();
                                holder.mAddingButton.setVisibility(View.GONE);
                                holder.mAddButton.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener((SearchFriendActivity) context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                        holder.mAddingButton.setVisibility(View.GONE);
                        holder.mAddedButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mStatus,mUsername;
        FancyButton mAddButton,mAddedButton,mAddingButton;
        CircleImageView mProfilePicture;
        LinearLayout mMainLayout;

        public MyViewHolder(final View itemView) {
            super(itemView);
            mStatus=(TextView) itemView.findViewById(R.id.statusSearchFriend);
            mUsername=(TextView) itemView.findViewById(R.id.usernameSearchFriend);
            mAddButton=(FancyButton) itemView.findViewById(R.id.addFriendButton);
            mAddedButton=(FancyButton) itemView.findViewById(R.id.addedFriendButton);
            mAddingButton=(FancyButton) itemView.findViewById(R.id.addFriendProgressButton);
            mProfilePicture=(CircleImageView) itemView.findViewById(R.id.profile_image);
            mMainLayout=(LinearLayout) itemView.findViewById(R.id.searchMainLayout);
        }
    }
}
