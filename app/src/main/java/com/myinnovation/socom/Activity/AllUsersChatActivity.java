package com.myinnovation.socom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.myinnovation.socom.Adapter.ChatUsersAdapter;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.databinding.ActivityAllUsersChatBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AllUsersChatActivity extends AppCompatActivity {

    ActivityAllUsersChatBinding binding;
    ChatUsersAdapter adapter;

    ArrayList<UserClass> list = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseDatabase mbase;
    String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllUsersChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar3);
        AllUsersChatActivity.this.setTitle(getIntent().getStringExtra("username"));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mbase = FirebaseDatabase.getInstance();
        if(mAuth.getUid() != null){
            currentId = mAuth.getUid();
        }
        binding.chatUserRv.showShimmerAdapter();

        adapter = new ChatUsersAdapter(list, AllUsersChatActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatUserRv.setLayoutManager(layoutManager);


            binding.searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });



        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(token -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("token", token);
                    mbase.getReference()
                            .child("Users")
                            .child(currentId)
                            .updateChildren(map);
                });



        binding.requests.setOnClickListener(v -> startActivity(new Intent(AllUsersChatActivity.this, RequestActivity.class)));

        mbase.getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserClass user = dataSnapshot.getValue(UserClass.class);
                            if(user != null && dataSnapshot.getKey() != null){
                                user.setUserId(dataSnapshot.getKey());
                                if (!dataSnapshot.getKey().equals(currentId)) {
                                    list.add(user);
                                }
                                binding.chatUserRv.setAdapter(adapter);
                                binding.chatUserRv.hideShimmerAdapter();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void search(String str) {
        ArrayList<UserClass> arrayList = new ArrayList<>();
        for(UserClass user : list){
            if(user.getName().toLowerCase().contains(str.toLowerCase())){
                arrayList.add(user);
            }
        }

        ChatUsersAdapter ad = new ChatUsersAdapter(arrayList, AllUsersChatActivity.this);
        binding.chatUserRv.setAdapter(ad);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mbase.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mbase.getReference().child("presence").child(currentId).setValue("Offline");
    }
}