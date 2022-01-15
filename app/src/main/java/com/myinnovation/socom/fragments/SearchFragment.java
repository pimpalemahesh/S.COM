package com.myinnovation.socom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Adapter.SearchAdapter;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.Objects;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        binding.userRv.showShimmerAdapter();

        SearchAdapter adapter = new SearchAdapter(list, getContext(), getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.userRv.setLayoutManager(layoutManager);

        mbase.getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserClass user = dataSnapshot.getValue(UserClass.class);
                            if(user != null){
                                user.setUserId(dataSnapshot.getKey());
                                if (!Objects.requireNonNull(dataSnapshot.getKey()).equals(FirebaseAuth.getInstance().getUid())) {
                                    list.add(user);
                                }
                            }
                            binding.userRv.setAdapter(adapter);
                            binding.userRv.hideShimmerAdapter();
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