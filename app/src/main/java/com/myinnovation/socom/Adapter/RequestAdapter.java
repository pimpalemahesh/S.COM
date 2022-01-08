package com.myinnovation.socom.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Activity.ChatActivity;
import com.myinnovation.socom.Activity.UserProfileActivity;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.SampleChatUsersBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.viewHolder>{

    ArrayList<UserClass> list;
    Activity activity;
    View view;
    ViewGroup viewGroup;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public RequestAdapter(ArrayList<UserClass> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.sample_chat_users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        UserClass user = list.get(position);

        Picasso.get()
                .load(user.getProfile_image())
                .placeholder(R.drawable.ic_image)
                .into(holder.binding.followProfileImage);

        holder.binding.name.setText(user.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity.getApplicationContext(), UserProfileActivity.class);
            intent.putExtra("senderUid", user.getUserId());
            intent.putExtra("senderImg", user.getProfile_image());
            intent.putExtra("senderName", user.getName());
            intent.putExtra("senderProfession", user.getProfession());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.getApplicationContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        SampleChatUsersBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleChatUsersBinding.bind(itemView);
        }
    }
}
