package com.myinnovation.socom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Adapter.CommentAdapter;
import com.myinnovation.socom.Model.Comment;
import com.myinnovation.socom.Model.Notification;
import com.myinnovation.socom.Model.Post;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.ActivityCommentBinding;
import com.myinnovation.socom.databinding.SampleViewUserDataBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
    Intent intent;
    String postId, postedBy;
    ArrayList<Comment> list = new ArrayList<>();

    FirebaseAuth mAuth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar2);
        CommentActivity.this.setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        // Takin data of post from database
        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Picasso.get()
                        .load(post.getPostImage())
                        .placeholder(R.drawable.ic_image)
                        .into(binding.postImage);

                binding.description.setText(post.getPostDescription());
                binding.like.setText(post.getPostLike() + "");
                binding.comment.setText(post.getCommentCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Taking data of user from database
        database.getReference()
                .child("Users")
                .child(postedBy).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserClass user = snapshot.getValue(UserClass.class);
                Picasso.get()
                        .load(user.getProfile_image())
                        .placeholder(R.drawable.ic_user)
                        .into(binding.profileImage);

                binding.name.setText(user.getName());

                binding.profileImage.setOnClickListener(v -> {
                    ViewGroup viewGroup = findViewById(R.id.commentActivity);
                    View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.sample_view_user_data, viewGroup, false);
                    viewGroup.removeView(view);
                    SampleViewUserDataBinding bd = SampleViewUserDataBinding.bind(view);
                    bd.name.setText(user.getName());
                    bd.profession.setText(user.getProfession());
                    Picasso.get()
                            .load(user.getProfile_image())
                            .placeholder(R.drawable.ic_user)
                            .into(bd.profileImage);

                    AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
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


        // sending comment
        binding.commentPostBtn.setOnClickListener(v -> {
            if (binding.commentText.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "You have not entered any comment yet!", Toast.LENGTH_LONG).show();
            } else {
                Comment comment = new Comment();
                comment.setCommentBody(binding.commentText.getText().toString());
                comment.setCommentAt(new Date().getTime());
                comment.setCommentedBy(FirebaseAuth.getInstance().getUid());

                database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("comments")
                        .push()
                        .setValue(comment).addOnSuccessListener(unused -> database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int commentCount = 0;
                                if (snapshot.exists()) {
                                    commentCount = snapshot.getValue(Integer.class);
                                }
                                database.getReference()
                                        .child("posts")
                                        .child(postId)
                                        .child("commentCount")
                                        .setValue(commentCount + 1).addOnSuccessListener(unused1 -> {
                                    Toast.makeText(getApplicationContext(), "Commented Successfully", Toast.LENGTH_LONG).show();
                                    binding.commentText.setText("");

                                    Notification notification = new Notification();
                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                    notification.setNotificationAt(new Date().getTime());
                                    notification.setPostId(postId);
                                    notification.setPostedBy(postedBy);
                                    notification.setType("comment");

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("notification")
                                            .child(postedBy)
                                            .push()
                                            .setValue(notification);
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }));
            }
        });


        CommentAdapter adapter = new CommentAdapter(getApplicationContext(), list, CommentActivity.this);
        binding.commentRv.setLayoutManager(new LinearLayoutManager(this));
        binding.commentRv.setAdapter(adapter);

        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    list.add(comment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}