package com.myinnovation.socom.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.myinnovation.socom.databinding.SampleViewUserDataBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.myviewholder> {

    ArrayList<Follow> list;
    Context context;
    Activity activity;
    View view;
    ViewGroup viewGroup;

    public FollowersAdapter() {

    }

    public FollowersAdapter(ArrayList<Follow> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
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
                                .placeholder(R.drawable.ic_user)
                                .into(holder.binding.profileImage);


                        holder.binding.profileImage.setOnClickListener(v -> {
                            view = LayoutInflater.from(context).inflate(R.layout.sample_view_user_data, viewGroup, false);
                            viewGroup.removeView(view);
                            SampleViewUserDataBinding bd = SampleViewUserDataBinding.bind(view);
                            bd.name.setText(user.getName());
                            bd.profession.setText(user.getProfession());
                            Picasso.get()
                                    .load(user.getProfile_image())
                                    .placeholder(R.drawable.ic_user)
                                    .into(bd.profileImage);

                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("User Details");
                            builder.setView(bd.getRoot());
                            builder.setCancelable(true);
                            builder.create();
                            builder.show();
                        });
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

