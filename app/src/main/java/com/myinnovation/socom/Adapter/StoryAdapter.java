package com.myinnovation.socom.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myinnovation.socom.Model.Story;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.Model.UserStories;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.SampleStoryBinding;
import com.myinnovation.socom.databinding.SampleViewUserDataBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.myviewholder> {

    ArrayList<Story> list;
    Context context;
    View view;
    ViewGroup viewGroup;
    Activity activity;

    public StoryAdapter(ArrayList<Story> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewGroup = parent;
        View view = LayoutInflater.from(context).inflate(R.layout.sample_story, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Story story = list.get(position);

//        long Timedef = System.currentTimeMillis() - story.getStories().get(position).getStoryAt();
//        long hour = TimeUnit.MILLISECONDS.toHours(Timedef);
//
//        if (hour > 48) {
//            Toast.makeText(context, "Inside method", Toast.LENGTH_LONG).show();
//
//
//            FirebaseDatabase.getInstance().getReference()
//                    .child("stories")
//                    .child(story.getStoryBy())
//                    .child("userStories")
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                UserStories stories = dataSnapshot.getValue(UserStories.class);
//
//                                long Timedef = System.currentTimeMillis() - stories.getStoryAt();
//                                long h = TimeUnit.MILLISECONDS.toHours(Timedef);
//
//                                if (h > 48) {
//                                    Uri imageUri = Uri.parse(stories.getImage());
//
//                                    FirebaseDatabase.getInstance().getReference()
//                                            .child("stories")
//                                            .child(story.getStoryBy())
//                                            .child("userStories")
//                                            .child(snapshot.getKey())
//                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(imageUri));
//                                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//                                                    Toast.makeText(context, "Value removed successfully", Toast.LENGTH_LONG).show();
//                                                }
//                                            });
//                                        }
//                                    });
//
//                                }
//                            }
//                            notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//
//        }

//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
//                .child(story.getStoryBy())
//                .child("userStories");
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    Toast.makeText(context, snapshot.getKey() + "", Toast.LENGTH_LONG).show();
//                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        long time = dataSnapshot.child("storyAt").getValue(Long.class);
//                        Toast.makeText(context, dataSnapshot.getKey() + "", Toast.LENGTH_LONG).show();
//                        long tdef = System.currentTimeMillis() - time;
//                        long minute = TimeUnit.MILLISECONDS.toMinutes(tdef);
//                        if(minute > 10){
//                            Toast.makeText(context, dataSnapshot.getKey() + "", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                }
//                else{
//                    Toast.makeText(context, "not exist", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        if (story.getStories().size() > 0) {
            UserStories lastStory = story.getStories().get(story.getStories().size() - 1);
            Picasso.get()
                    .load(lastStory.getImage())
                    .placeholder(R.drawable.ic_image)
                    .into(holder.binding.story);

            holder.binding.statusCircle.setPortionsCount(story.getStories().size());

            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(story.getStoryBy()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserClass user = snapshot.getValue(UserClass.class);
                    Picasso.get()
                            .load(user.getProfile_image())
                            .placeholder(R.drawable.ic_user)
                            .into(holder.binding.profileImage);
                    holder.binding.name.setText(user.getName());

                    holder.binding.profileImage.setOnClickListener(v -> {
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

                    holder.binding.story.setOnClickListener(v -> {
                        ArrayList<MyStory> myStories = new ArrayList<>();

                        for (UserStories stories : story.getStories()) {
                            myStories.add(new MyStory(
                                    stories.getImage()
                            ));
                        }

                        new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                .setStoriesList(myStories) // Required
                                .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                .setTitleText(user.getName()) // Default is Hidden
                                .setSubtitleText("") // Default is Hidden
                                .setTitleLogoUrl(user.getProfile_image()) // Default is Hidden
                                .setStoryClickListeners(new StoryClickListeners() {
                                    @Override
                                    public void onDescriptionClickListener(int position1) {
                                        //your action
                                    }

                                    @Override
                                    public void onTitleIconClickListener(int position1) {
                                        //your action
                                    }
                                }) // Optional Listeners
                                .build() // Must be called before calling show method
                                .show();
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {

        SampleStoryBinding binding;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            binding = SampleStoryBinding.bind(itemView);
        }
    }
}
