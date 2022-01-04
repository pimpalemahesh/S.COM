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
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.databinding.ActivityRequestBinding;

import java.util.ArrayList;

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

        setSupportActionBar(binding.requestToolbar);
        RequestActivity.this.setTitle("Request Messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference.child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            UserClass user = dataSnapshot.getValue(UserClass.class);
                            user.setUserId(dataSnapshot.getKey());

                            databaseReference.child("Request")
                                    .child(firebaseAuth.getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                                    if(dataSnapshot.getKey().equals(dataSnapshot1.getKey())){
                                                        list.add(user);
                                                    }
                                                }
                                                binding.requestRv.setAdapter(adapter);
                                                binding.requestRv.hideShimmerAdapter();
                                                adapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
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
}