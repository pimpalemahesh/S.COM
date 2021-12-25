package com.myinnovation.socom.Adapter;

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
import com.myinnovation.socom.Model.FollowModel;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.UserSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.myviewholder>{

    ArrayList<UserClass> list;
    Context context;

    public UserAdapter(ArrayList<UserClass> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sample, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        UserClass user = list.get(position);

        Picasso.get()
                .load(user.getProfile_image())
                .placeholder(R.drawable.imgicon)
                .into(holder.binding.followProfileImage);

        holder.binding.profession.setText(user.getProfession());
        holder.binding.name.setText(user.getName());

        // checking follwers if exist already.
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getUserId())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.binding.followBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.follow_active_btn));
                    holder.binding.followBtn.setText("Following");
                    holder.binding.followBtn.setTextColor(context.getResources().getColor(R.color.grey));
                    holder.binding.followBtn.setEnabled(false);
                }
                else{
                    // adding new follwers
                    holder.binding.followBtn.setOnClickListener(view -> {
                        FollowModel follow = new FollowModel();
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

    class myviewholder extends RecyclerView.ViewHolder{

        UserSampleBinding binding;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            binding = UserSampleBinding.bind(itemView);
        }
    }
}
