package com.myinnovation.socom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.ActivityUserProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {

    ActivityUserProfileBinding binding;
    String senderUid, receiverUid, senderRoom, receiverRoom;
    UserClass user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(getIntent().getExtras() != null){
            senderUid = getIntent().getStringExtra("senderUid");
        } else{
            senderUid = FirebaseAuth.getInstance().getUid();
        }
        receiverUid = FirebaseAuth.getInstance().getUid();
        senderRoom = receiverUid + senderUid;
        receiverRoom = senderUid + receiverUid;

        Toast.makeText(getApplicationContext(), senderUid, Toast.LENGTH_LONG).show();
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(senderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            user = snapshot.getValue(UserClass.class);
                            if(user != null){
                                binding.userName.setText(user.getName());
                                binding.profession.setText(user.getProfession());
                                Picasso.get()
                                        .load(user.getProfile_image())
                                        .placeholder(R.drawable.ic_user)
                                        .into(binding.ProfileImage);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        String receiverUid = FirebaseAuth.getInstance().getUid();

        String receiverRoom = senderUid + receiverUid;

        FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom)
                .child("Status")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String status = snapshot.getValue(String.class);
                            if(status != null && status.equals("Accept")){
                                binding.accept.setVisibility(View.GONE);
                                binding.Reject.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.accept.setOnClickListener(v -> {
            HashMap<String, Object> message = new HashMap<>();
            message.put("Status", "Accept");
            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).
                    updateChildren(message)
                    .addOnSuccessListener(unused -> FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).
                            updateChildren(message)
                            .addOnSuccessListener(unused1 -> {
                                binding.accept.setVisibility(View.INVISIBLE);
                                binding.Reject.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Request Accepted successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), AllUsersChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }));
        });

        binding.Reject.setOnClickListener(v -> {
            HashMap<String, Object> message = new HashMap<>();
            message.put("Status", "Reject");
            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).
                    updateChildren(message)
                    .addOnSuccessListener(unused -> FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).
                            updateChildren(message)
                            .addOnSuccessListener(unused12 -> {
                                binding.accept.setVisibility(View.INVISIBLE);
                                binding.Reject.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Request Rejected successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }));
        });
    }
}