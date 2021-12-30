package com.myinnovation.socom.Adapter;

import static com.google.firebase.storage.FirebaseStorage.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myinnovation.socom.Activity.CommentActivity;
import com.myinnovation.socom.Model.Notification;
import com.myinnovation.socom.Model.Post;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.SampleDashboardBinding;
import com.myinnovation.socom.databinding.SampleViewUserDataBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.myviewholder> {

    ArrayList<Post> list;
    Context context;
    ViewGroup viewGroup;
    Activity activity;
    View view;

    public PostAdapter(ArrayList<Post> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        viewGroup = parent;

        View view = LayoutInflater.from(context).inflate(R.layout.sample_dashboard, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {

        // Posted image.
        Post model = list.get(position);

        long Timedef = System.currentTimeMillis() - model.getPostAt();
        long hour = TimeUnit.MILLISECONDS.toMinutes(Timedef);

        if(hour > 1){
            Toast.makeText(context, "Inside method", Toast.LENGTH_LONG).show();
            Uri imageUri = Uri.parse(model.getPostImage());

            FirebaseDatabase.getInstance().getReference()
                    .child("posts")
                    .child(model.getPostId())
                    .removeValue().addOnSuccessListener(unused -> {
                        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(imageUri));
                        photoRef.delete().addOnSuccessListener(unused12 -> {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("notification")
                                    .child(model.getPostBy())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                if(snapshot.exists() && (dataSnapshot.getKey() == model.getPostId())){

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("notification")
                                                            .child(model.getPostBy())
                                                            .child(dataSnapshot.getKey())
                                                            .addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if(snapshot.exists()){
                                                                        for(DataSnapshot nof : snapshot.getChildren()){
                                                                            if(nof.child("type").getValue(String.class).equals("comment")){
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("notification")
                                                                                        .child(model.getPostBy())
                                                                                        .child(dataSnapshot.getKey())
                                                                                        .child(nof.getKey())
                                                                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Toast.makeText(context, "Value removed", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                }
                                            }
                                            notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        });
                    });

        }


        Picasso.get()
                .load(model.getPostImage())
                .placeholder(R.drawable.ic_image)
                .into(holder.binding.postImage);

        holder.binding.like.setText(model.getPostLike() + "");
        holder.binding.comment.setText(model.getCommentCount() + "");
        String description = model.getPostDescription();
        if (description.equals("")) {
            holder.binding.postDescription.setVisibility(View.GONE);
        } else {
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

                holder.binding.postProfileImage.setOnClickListener(v -> {
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

        // for checking whether used liked respective post or not.
        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(model.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_userliked, 0, 0, 0);
                        } else {
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
                                            .setValue(model.getPostLike() + 1).addOnSuccessListener(unused1 -> {
                                                holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_userliked, 0, 0, 0);

                                                Notification notification = new Notification();
                                                notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                notification.setNotificationAt(new Date().getTime());
                                                notification.setPostId(model.getPostId());
                                                notification.setPostedBy(model.getPostBy());
                                                notification.setType("like");

                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("notification")
                                                        .child(model.getPostBy())
                                                        .push()
                                                        .setValue(notification);

                                            })));
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

    class myviewholder extends RecyclerView.ViewHolder {

        SampleDashboardBinding binding;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            binding = SampleDashboardBinding.bind(itemView);
        }
    }
}
