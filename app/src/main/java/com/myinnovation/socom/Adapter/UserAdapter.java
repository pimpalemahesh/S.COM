package com.myinnovation.socom.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Model.Follow;
import com.myinnovation.socom.Model.Notification;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.SampleUserBinding;
import com.myinnovation.socom.databinding.SampleViewUserDataBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.myviewholder> {

    ArrayList<UserClass> list;
    Context context;
    Activity activity;
    View view;
    ViewGroup viewGroup;

    public UserAdapter(ArrayList<UserClass> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
        View view = LayoutInflater.from(context).inflate(R.layout.sample_user, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        UserClass user = list.get(position);

        Picasso.get()
                .load(user.getProfile_image())
                .placeholder(R.drawable.ic_image)
                .into(holder.binding.followProfileImage);

        holder.binding.profession.setText(user.getProfession());
        holder.binding.name.setText(user.getName());

        holder.binding.followProfileImage.setOnClickListener(v -> {
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

        // checking follwers if exist already.
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getUserId())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn));
                    holder.binding.followBtn.setText("Following");
                    holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.grey));
                    holder.binding.followBtn.setEnabled(false);
                } else {
                    // adding new follwers
                    holder.binding.followBtn.setOnClickListener(view -> {
                        Follow follow = new Follow();
                        follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                        follow.setFollowedAt(new Date().getTime());

                        FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(user.getUserId())
                                .child("followers")
                                .child(FirebaseAuth.getInstance().getUid())
                                .setValue(follow)
                                .addOnSuccessListener(unused -> {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(user.getUserId())
                                            .child("followerCount")
                                            .setValue(user.getFollowerCount() + 1).addOnSuccessListener(unused1 -> {

                                        holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn));
                                        holder.binding.followBtn.setText("Following");
                                        holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.grey));
                                        holder.binding.followBtn.setEnabled(false);
                                        Toast.makeText(context, "You Followed " + user.getName(), Toast.LENGTH_LONG).show();



                                        Notification notification = new Notification();
                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                        notification.setNotificationAt(new Date().getTime());
                                        notification.setType("follow");

                                        FirebaseDatabase.getInstance().getReference()
                                                .child("notification")
                                                .child(user.getUserId())
                                                .push()
                                                .setValue(notification);
                                    });
                                });
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error Occured", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {

        SampleUserBinding binding;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            binding = SampleUserBinding.bind(itemView);
        }
    }
}
