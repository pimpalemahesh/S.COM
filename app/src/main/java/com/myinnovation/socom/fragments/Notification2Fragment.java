package com.myinnovation.socom.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myinnovation.socom.Adapter.NotificationAdapter;
import com.myinnovation.socom.Model.NotificationModel;
import com.myinnovation.socom.R;

import java.util.ArrayList;


public class Notification2Fragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<NotificationModel> list;

    public Notification2Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification2, container, false);

        recyclerView = view.findViewById(R.id.notificationRv);

        list = new ArrayList<>();
        list.add(new NotificationModel(R.drawable.profile, "You started to following <b>Bill</b>." , "10 minutes ago"));
        list.add(new NotificationModel(R.drawable.bill, "<b>Bill</b> replyed to your comment" , "50 minutes ago"));
        list.add(new NotificationModel(R.drawable.budhha, "<b>Rock</b> started to following you." , "1 hour ago"));
        list.add(new NotificationModel(R.drawable.profile, "You replayed to <b>Emily</b>." , "3 hours ago"));
        list.add(new NotificationModel(R.drawable.profile, "You started to following <b>Bill</b>." , "10 minutes ago"));
        list.add(new NotificationModel(R.drawable.bill, "<b>Bill</b> replyed to your comment" , "50 minutes ago"));
        list.add(new NotificationModel(R.drawable.budhha, "<b>Rock</b> started to following you." , "1 hour ago"));
        list.add(new NotificationModel(R.drawable.profile, "You replayed to <b>Emily</b>." , "3 hours ago"));
        list.add(new NotificationModel(R.drawable.profile, "You started to following <b>Bill</b>." , "10 minutes ago"));
        list.add(new NotificationModel(R.drawable.bill, "<b>Bill</b> replyed to your comment" , "50 minutes ago"));
        list.add(new NotificationModel(R.drawable.budhha, "<b>Rock</b> started to following you." , "1 hour ago"));
        list.add(new NotificationModel(R.drawable.profile, "You replayed to <b>Emily</b>." , "3 hours ago"));
        list.add(new NotificationModel(R.drawable.profile, "You started to following <b>Bill</b>." , "10 minutes ago"));
        list.add(new NotificationModel(R.drawable.bill, "<b>Bill</b> replyed to your comment" , "50 minutes ago"));
        list.add(new NotificationModel(R.drawable.budhha, "<b>Rock</b> started to following you." , "1 hour ago"));
        list.add(new NotificationModel(R.drawable.profile, "You replayed to <b>Emily</b>." , "3 hours ago"));

        NotificationAdapter adapter = new NotificationAdapter(list, getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

}