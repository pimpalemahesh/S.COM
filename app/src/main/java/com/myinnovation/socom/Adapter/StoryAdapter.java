package com.myinnovation.socom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myinnovation.socom.Model.StoryModel;
import com.myinnovation.socom.R;

import java.util.ArrayList;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.myviewholder> {

    ArrayList<StoryModel> list;
    Context context;

    public StoryAdapter(ArrayList<StoryModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_rv_design, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        StoryModel model = list.get(position);
        holder.story.setImageResource(model.getStory());
        holder.storyType.setImageResource(model.getStoryType());
        holder.profile.setImageResource(model.getProfile());
        holder.name.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myviewholder extends RecyclerView.ViewHolder{

        ImageView story, storyType, profile;
        TextView name;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            story = itemView.findViewById(R.id.story);
            storyType = itemView.findViewById(R.id.storyType);
            profile = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.name);
        }
    }
}
