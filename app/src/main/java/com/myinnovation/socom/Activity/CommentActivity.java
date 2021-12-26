package com.myinnovation.socom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Adapter.CommentAdapter;
import com.myinnovation.socom.Model.Comment;
import com.myinnovation.socom.Model.Post;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.ActivityCommentBinding;
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

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Picasso.get()
                        .load(post.getPostImage())
                        .placeholder(R.drawable.budhha)
                        .into(binding.postImage);

                binding.description.setText(post.getPostDescription());
                binding.like.setText(post.getPostLike() + "");
                binding.comment.setText(post.getCommentCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference()
                .child("Users")
                .child(postedBy).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserClass user = snapshot.getValue(UserClass.class);
                Picasso.get()
                        .load(user.getProfile_image())
                        .placeholder(R.drawable.user_logo)
                        .into(binding.profileImage);

                binding.name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(binding.commentText.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "You have not entered any comment yet!", Toast.LENGTH_LONG).show();
        } else {
            binding.commentPostBtn.setOnClickListener(v -> {
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
                                if(snapshot.exists()){
                                    commentCount = snapshot.getValue(Integer.class);
                                }
                                database.getReference()
                                        .child("posts")
                                        .child(postId)
                                        .child("commentCount")
                                        .setValue(commentCount + 1).addOnSuccessListener(unused1 -> {
                                    Toast.makeText(getApplicationContext(), "Commented Successfully", Toast.LENGTH_LONG).show();
                                    binding.commentText.setText("");
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }));

            });
        }


        CommentAdapter adapter = new CommentAdapter(getApplicationContext(), list);
        binding.commentRv.setLayoutManager(new LinearLayoutManager(this));
        binding.commentRv.setAdapter(adapter);

        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
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
}