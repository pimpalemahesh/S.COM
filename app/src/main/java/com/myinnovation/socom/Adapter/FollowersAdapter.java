package com.myinnovation.socom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Model.Follow;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.SampleFollowersBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.myviewholder> {

    ArrayList<Follow> list;
    Context context;

    public FollowersAdapter() {

    }

    public FollowersAdapter(ArrayList<Follow> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_followers, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Follow follow = list.get(position);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(follow.getFollowedBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserClass user = snapshot.getValue(UserClass.class);
                        Picasso.get()
                                .load(user.getProfile_image())
                                .placeholder(R.drawable.user_logo)
                                .into(holder.binding.profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {

        SampleFollowersBinding binding;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            binding = SampleFollowersBinding.bind(itemView);
        }
    }
}

