package com.audsnap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.audsnap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SONI's on 3/15/2017.
 */

public class AddedMeAdapter extends RecyclerView.Adapter<AddedMeAdapter.ViewHolder> {

    private Context context;
    private List<AddedMeViewItem> addedMeViewItems;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mFriendReference,mAddedFriendReference,mAddedFriendReferenceFriend,mFriendKeyReference;

    public AddedMeAdapter(Context context,List<AddedMeViewItem> addedMeViewItems)
    {
        this.context=context;
        this.addedMeViewItems=addedMeViewItems;
        firebaseDatabase = FirebaseDatabase.getInstance();
        mFriendReference = firebaseDatabase.getReference("AudSnap/Friends/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/");

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_view_added_me,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final AddedMeViewItem addedMeViewItem = addedMeViewItems.get(position);

        //TODO:set image resource

        holder.mUserName.setText(addedMeViewItem.getmName());
        holder.mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddedFriendReference=firebaseDatabase.getReference("AudSnap/AddedFriends/"+
                        FirebaseAuth.getInstance().getCurrentUser().getUid()+"/");
                //mAddedFriendReferenceFriend=firebaseDatabase.getReference("AudSnap/AddedFriends/"+addedMeViewItem.getmKey()+"/");
                mFriendKeyReference=firebaseDatabase.getReference("AudSnap/Friends/"+addedMeViewItem.getmKey()+"/");

                mAddedFriendReference.child(addedMeViewItem.getmKey()).removeValue();
                //mAddedFriendReferenceFriend.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                mFriendReference.child(addedMeViewItem.getmKey()).setValue("true");
                mFriendKeyReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("true");
                addedMeViewItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
            }
        });
        holder.mReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddedFriendReference=firebaseDatabase.getReference("AudSnap/AddedFriends/"+addedMeViewItem.getmKey()+"/");
                mAddedFriendReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                addedMeViewItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
            }
        });
    }

    @Override
    public int getItemCount() {
        return addedMeViewItems.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView mUserName;
        private ImageButton mAccept,mReject;

        public ViewHolder(View itemView) {
            super(itemView);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.profileImageAddedMe);
            mUserName=(TextView) itemView.findViewById(R.id.usernameAddedMe);
            mAccept=(ImageButton) itemView.findViewById(R.id.acceptRequest);
            mReject=(ImageButton) itemView.findViewById(R.id.rejectRequest);
        }
    }
}
