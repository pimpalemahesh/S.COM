package com.myinnovation.socom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Adapter.FollowersAdapter;
import com.myinnovation.socom.Adapter.UserAdapter;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.FragmentSearchBinding;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;

    ArrayList<UserClass> list = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseDatabase mbase;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mbase = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        UserAdapter adapter = new UserAdapter(list, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.userRv.setLayoutManager(layoutManager);
        binding.userRv.setAdapter(adapter);

        mbase.getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserClass user = dataSnapshot.getValue(UserClass.class);
                            user.setUserId(dataSnapshot.getKey());
                            if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                                list.add(user);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return binding.getRoot();
    }
}