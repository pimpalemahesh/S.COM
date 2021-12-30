package com.myinnovation.socom.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Activity.CommentActivity;
import com.myinnovation.socom.Model.Comment;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.SampleCommentBinding;
import com.myinnovation.socom.databinding.SampleViewUserDataBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewholder> {

    Activity activity;
    Context context;
    ArrayList<Comment> list;
    View view;
    ViewGroup viewGroup;

    public CommentAdapter(Context context, ArrayList<Comment> list, Activity activity) {
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
        return new viewholder(LayoutInflater.from(context).inflate(R.layout.sample_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Comment comment = list.get(position);
        String text = TimeAgo.using(comment.getCommentAt());
        holder.binding.time.setText(text);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(comment.getCommentedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserClass user = snapshot.getValue(UserClass.class);
                Picasso.get()
                        .load(user.getProfile_image())
                        .placeholder(R.drawable.ic_user)
                        .into(holder.binding.followProfileImage);

                holder.binding.comment.setText(Html.fromHtml("<b>" + user.getName() + "</b>  " + comment.getCommentBody()));

                holder.binding.followProfileImage.setOnClickListener(v -> {
                    view = LayoutInflater.from(context).inflate(R.layout.sample_view_user_data, viewGroup, false);
                    viewGroup.removeView(view);
                    SampleViewUserDataBinding bd = SampleViewUserDataBinding.bind(view);
                    bd.name.setText(user.getName());
                    bd.profession.setText(user.getProfession());
                    Picasso.get()
                            .load(user.getProfile_image())
                            .placeholder(R.drawable.ic_user)
                            .into(bd.profileImage);

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("User Details");
                    builder.setView(bd.getRoot());
                    builder.setCancelable(true);
                    builder.create();
                    builder.show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        SampleCommentBinding binding;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            binding = SampleCommentBinding.bind(itemView);
        }
    }
}
