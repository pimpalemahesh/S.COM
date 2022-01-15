package com.myinnovation.socom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Adapter.RequestAdapter;
import com.myinnovation.socom.Model.Request;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.databinding.ActivityRequestBinding;

import java.util.ArrayList;
import java.util.Objects;

public class RequestActivity extends AppCompatActivity {

    ActivityRequestBinding binding;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    RequestAdapter adapter;
    ArrayList<UserClass> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        adapter = new RequestAdapter(list, RequestActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.requestRv.setLayoutManager(layoutManager);
        binding.requestRv.showShimmerAdapter();

        setSupportActionBar(binding.requestToolbar);
        RequestActivity.this.setTitle("Request Messages");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase.getInstance().getReference().child("Requests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                Request request = dataSnapshot.getValue(Request.class);
                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists()){
                                                    list.clear();
                                                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                                                        UserClass user = snapshot1.getValue(UserClass.class);
                                                        if (request != null && user != null && request.getRequestId() != null) {
                                                            user.setUserId(request.getRequestId());
                                                            if (request.getRequestId().equals(snapshot1.getKey())) {
                                                                if (!request.getRequestId().equals(FirebaseAuth.getInstance().getUid())) {
                                                                    list.add(user);
                                                                }
                                                                binding.requestRv.setAdapter(adapter);
                                                                binding.requestRv.hideShimmerAdapter();
                                                                adapter.notifyDataSetChanged();
                                                            }
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
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }
}