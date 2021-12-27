package com.myinnovation.socom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Model.Story;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.Model.UserStories;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.SampleStoryBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.myviewholder> {

    ArrayList<Story> list;
    Context context;

    public StoryAdapter(ArrayList<Story> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_story, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Story story = list.get(position);

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
                            .placeholder(R.drawable.user_logo)
                            .into(holder.binding.profileImage);
                    holder.binding.name.setText(user.getName());

                    holder.binding.story.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                                        public void onDescriptionClickListener(int position) {
                                            //your action
                                        }

                                        @Override
                                        public void onTitleIconClickListener(int position) {
                                            //your action
                                        }
                                    }) // Optional Listeners
                                    .build() // Must be called before calling show method
                                    .show();
                        }
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
