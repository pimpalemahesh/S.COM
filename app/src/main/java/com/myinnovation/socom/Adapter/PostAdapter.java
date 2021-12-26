package com.myinnovation.socom.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Activity.CommentActivity;
import com.myinnovation.socom.Model.Post;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.DashboardRvSampleBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.myviewholder> {

    ArrayList<Post> list;
    Context context;

    public PostAdapter(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv_sample, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {

        // Posted image.
        Post model = list.get(position);
        Picasso.get()
                .load(model.getPostImage())
                .placeholder(R.drawable.ic_image)
                .into(holder.binding.postImage);

        holder.binding.like.setText(model.getPostLike() + "");
        holder.binding.comment.setText(model.getCommentCount() + "");
        String description = model.getPostDescription();
        if(description.equals("")){
            holder.binding.postDescription.setVisibility(View.GONE);
        }
        else{
            holder.binding.postDescription.setVisibility(View.VISIBLE);
            holder.binding.postDescription.setText(model.getPostDescription());
        }


        // getting posted image detail from child Users and using model UserClass.

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(model.getPostBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserClass user = snapshot.getValue(UserClass.class);

                Picasso.get()
                        .load(user.getProfile_image())
                        .placeholder(R.drawable.ic_image)
                        .into(holder.binding.postProfileImage);

                holder.binding.userName.setText(user.getName());
                holder.binding.profession.setText(user.getProfession());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // for checking whether used liked respective post or not.
        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(model.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_userliked, 0, 0, 0);
                        }
                        else{
                            // if user liked post then first create like node then set its value to true then create postLike node increase count
                            // and change drawable left to with new image.
                            holder.binding.like.setOnClickListener(view -> FirebaseDatabase.getInstance().getReference()
                                    .child("posts")
                                    .child(model.getPostId())
                                    .child("likes")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(true).addOnSuccessListener(unused -> FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(model.getPostId())
                                            .child("postLike")
                                            .setValue(model.getPostLike() + 1).addOnSuccessListener(unused1 -> holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_userliked, 0, 0, 0))));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.comment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", model.getPostId());
            intent.putExtra("postedBy", model.getPostBy());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myviewholder extends RecyclerView.ViewHolder{

        DashboardRvSampleBinding binding;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            binding = DashboardRvSampleBinding.bind(itemView);
        }
    }
}
