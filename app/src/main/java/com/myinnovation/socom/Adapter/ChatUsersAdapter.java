package com.myinnovation.socom.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Activity.ChatActivity;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.SampleChatUsersBinding;
import com.myinnovation.socom.databinding.SampleViewUserDataBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatUsersAdapter extends RecyclerView.Adapter<ChatUsersAdapter.viewholder> {

    ArrayList<UserClass> list;
    Activity activity;
    View view;
    ViewGroup viewGroup;

    public ChatUsersAdapter(ArrayList<UserClass> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        viewGroup = parent;
        return new viewholder(LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.sample_chat_users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        UserClass user = list.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderId + user.getUserId();

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            long time = snapshot.child("lastMsgTime").getValue(Long.class);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                            holder.binding.time.setText(dateFormat.format(new Date(time)));
                            holder.binding.lastmsg.setText(lastMsg);
                        } else {
                            holder.binding.lastmsg.setText("Tap to chat");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Picasso.get()
                .load(user.getProfile_image())
                .placeholder(R.drawable.ic_image)
                .into(holder.binding.followProfileImage);

        holder.binding.name.setText(user.getName());

        holder.binding.followProfileImage.setOnClickListener(v -> {
            view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.sample_view_user_data, viewGroup, false);
            viewGroup.removeView(view);
            SampleViewUserDataBinding bd = SampleViewUserDataBinding.bind(view);
            bd.name.setText(user.getName());
            Picasso.get()
                    .load(user.getProfile_image())
                    .placeholder(R.drawable.avatar)
                    .into(bd.profileImage);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("User Details");
            builder.setView(bd.getRoot());
            builder.setCancelable(true);
            builder.create();
            builder.show();
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity.getApplicationContext(), ChatActivity.class);
            intent.putExtra("name", user.getName());
            intent.putExtra("image", user.getProfile_image());
            intent.putExtra("uid", user.getUserId());
            intent.putExtra("token", user.getToken());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplicationContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{

        SampleChatUsersBinding binding;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            binding = SampleChatUsersBinding.bind(itemView);
        }
    }
}
