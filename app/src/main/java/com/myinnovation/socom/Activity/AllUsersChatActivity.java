package com.myinnovation.socom.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.myinnovation.socom.Adapter.ChatUsersAdapter;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.ActivityAllUsersChatBinding;
import com.myinnovation.socom.fragments.ChatsFragment;
import com.myinnovation.socom.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class AllUsersChatActivity extends AppCompatActivity {

    ActivityAllUsersChatBinding binding;

    ArrayList<UserClass> list = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseDatabase mbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllUsersChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar3);
        AllUsersChatActivity.this.setTitle(getIntent().getStringExtra("username"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mbase = FirebaseDatabase.getInstance();
        binding.chatUserRv.showShimmerAdapter();

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("token", token);
                        mbase.getReference()
                                .child("Users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .updateChildren(map);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        ChatUsersAdapter adapter = new ChatUsersAdapter(list, AllUsersChatActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatUserRv.setLayoutManager(layoutManager);

        binding.requests.setOnClickListener(v -> startActivity(new Intent(AllUsersChatActivity.this, RequestActivity.class)));

        mbase.getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserClass user = dataSnapshot.getValue(UserClass.class);
                            user.setUserId(dataSnapshot.getKey());
                            if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                                list.add(user);
                            }
                            binding.chatUserRv.setAdapter(adapter);
                            binding.chatUserRv.hideShimmerAdapter();
                            adapter.notifyDataSetChanged();
                        }
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

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        mbase.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        mbase.getReference().child("presence").child(currentId).setValue("Offline");
    }
}