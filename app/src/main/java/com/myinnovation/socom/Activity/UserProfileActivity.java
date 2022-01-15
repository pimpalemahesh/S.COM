package com.myinnovation.socom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Adapter.FollowersAdapter;
import com.myinnovation.socom.Model.Follow;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.ActivityUserProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {

    ActivityUserProfileBinding binding;
    ArrayList<Follow> list;

    String senderUid, receiverUid, senderRoom, receiverRoom;
    UserClass user;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar5);
        UserProfileActivity.this.setTitle("Profile");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            senderUid = getIntent().getStringExtra("senderUid");
        } else {
            senderUid = FirebaseAuth.getInstance().getUid();
        }
        receiverUid = FirebaseAuth.getInstance().getUid();
        senderRoom = receiverUid + senderUid;
        receiverRoom = senderUid + receiverUid;

        reference
                .child("Users")
                .child(senderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            user = snapshot.getValue(UserClass.class);
                            if (user != null) {
                                binding.UserName.setText(user.getName());
                                binding.Profession.setText(user.getProfession());
                                binding.followers.setText(String.valueOf(user.getFollowerCount()));
                                binding.friend.setText(String.valueOf(user.getFriendCount()));
                                binding.posts.setText(String.valueOf(user.getPostCount()));
                                Picasso.get()
                                        .load(user.getProfile_image())
                                        .placeholder(R.drawable.ic_user)
                                        .into(binding.profileImage);

                                Picasso.get()
                                        .load(user.getCoverPhoto())
                                        .placeholder(R.drawable.ic_image)
                                        .into(binding.coverphoto);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        String receiverUid = FirebaseAuth.getInstance().getUid();

        String receiverRoom = senderUid + receiverUid;

        reference.child("chats").child(senderRoom)
                .child("Status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String status = snapshot.getValue(String.class);
                            if (status != null && status.equals("Accept")) {
                                binding.Accept.setVisibility(View.GONE);
                                binding.Reject.setVisibility(View.GONE);
                            } else if (status != null && status.equals("Block")) {
                                binding.Block.setText(R.string.un_block);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.Accept.setOnClickListener(v -> {
            HashMap<String, Object> message = new HashMap<>();
            message.put("Status", "Accept");
            reference.child("chats").child(senderRoom).
                    updateChildren(message)
                    .addOnSuccessListener(unused -> reference.child("chats").child(receiverRoom).
                            updateChildren(message)
                            .addOnSuccessListener(unused1 -> {
                                binding.Accept.setVisibility(View.INVISIBLE);
                                binding.Reject.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Request Accepted successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), AllUsersChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
                                startActivity(intent);
                            }));
        });

        binding.Reject.setOnClickListener(v -> {
            HashMap<String, Object> message = new HashMap<>();
            message.put("Status", "Reject");
            reference.child("chats").child(senderRoom).
                    updateChildren(message)
                    .addOnSuccessListener(unused -> reference.child("chats").child(receiverRoom).
                            updateChildren(message)
                            .addOnSuccessListener(unused12 -> {
                                binding.Accept.setVisibility(View.INVISIBLE);
                                binding.Reject.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Request Rejected successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), AllUsersChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                finish();
                                startActivity(intent);
                            }));
        });

        binding.Block.setOnClickListener(v -> {
            if (binding.Block.getText().toString().equals("Block")) {
                Toast.makeText(getApplicationContext(), "User Blocked.", Toast.LENGTH_LONG).show();
                HashMap<String, Object> message = new HashMap<>();
                message.put("Status", "Block");
                reference.child("chats").child(senderRoom).
                        updateChildren(message)
                        .addOnSuccessListener(unused -> reference.child("chats").child(receiverRoom).
                                updateChildren(message)
                                .addOnSuccessListener(unused12 -> {
                                    reference.child("chats").child(receiverRoom).
                                            updateChildren(message)
                                            .addOnSuccessListener(unused13 -> {
                                                DatabaseReference databaseReference = reference.child("Requests").child(receiverRoom);
                                                databaseReference
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()) {
                                                                    databaseReference.removeValue().addOnSuccessListener(unused2 -> {
                                                                        binding.Accept.setVisibility(View.INVISIBLE);
                                                                        binding.Reject.setVisibility(View.INVISIBLE);
                                                                        Intent intent = new Intent(getApplicationContext(), AllUsersChatActivity.class);
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        finish();
                                                                        startActivity(intent);
                                                                    });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                            });
                                }));
            } else {

                Toast.makeText(getApplicationContext(), "User Unblocked", Toast.LENGTH_LONG).show();
                binding.Block.setText(R.string.block);
                HashMap<String, Object> message = new HashMap<>();
                message.put("Status", "Reject");
                reference.child("chats").child(senderRoom).
                        updateChildren(message)
                        .addOnSuccessListener(unused3 -> reference.child("chats").child(receiverRoom).
                                updateChildren(message)
                                .addOnSuccessListener(unused12 -> {
                                    reference.child("chats").child(receiverRoom).
                                            updateChildren(message)
                                            .addOnSuccessListener(unused13 -> {
                                                binding.Accept.setVisibility(View.INVISIBLE);
                                                binding.Reject.setVisibility(View.INVISIBLE);
                                                Intent intent = new Intent(getApplicationContext(), AllUsersChatActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                finish();
                                                startActivity(intent);
                                            });
                                }));

            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        startActivity(new Intent(this, AllUsersChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        return super.onOptionsItemSelected(item);
    }
}