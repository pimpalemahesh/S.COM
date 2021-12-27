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
import com.myinnovation.socom.Adapter.NotificationAdapter;
import com.myinnovation.socom.Model.Notification;
import com.myinnovation.socom.databinding.FragmentNotificationBinding;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    FragmentNotificationBinding binding;

    ArrayList<Notification> list;
    NotificationAdapter adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        binding.notificationRV.showShimmerAdapter();

        list = new ArrayList<>();

        adapter = new NotificationAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.notificationRV.setLayoutManager(linearLayoutManager);
        binding.notificationRV.setNestedScrollingEnabled(false);

        database.getReference()
                .child("notification")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Notification notification = dataSnapshot.getValue(Notification.class);
                            notification.setNotificationId(dataSnapshot.getKey());
                            list.add(notification);
                        }
                        binding.notificationRV.setAdapter(adapter);
                        binding.notificationRV.hideShimmerAdapter();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return binding.getRoot();
    }

}