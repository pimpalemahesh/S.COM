package com.myinnovation.socom.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myinnovation.socom.Adapter.PostAdapter;
import com.myinnovation.socom.Adapter.StoryAdapter;
import com.myinnovation.socom.Model.Post;
import com.myinnovation.socom.Model.StoryModel;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {

    ArrayList<StoryModel> storylist;
    ArrayList<Post> postlist;
    ImageView addStory;
    FirebaseDatabase mbase;
    FirebaseAuth mAuth;
    FragmentHomeBinding binding;

    public HomeFragment() {
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        mbase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mbase.getReference().child("Users").child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postlist.clear();
                        if(snapshot.exists()){
                            UserClass user = snapshot.getValue(UserClass.class);
                            Picasso.get()
                                    .load(user.getProfile_image())
                                    .placeholder(R.drawable.profile)
                                    .into(binding.profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        storylist = new ArrayList<>();

        storylist.add(new StoryModel(R.drawable.bill, R.drawable.ic_user, R.drawable.profile, "Bill"));
        storylist.add(new StoryModel(R.drawable.budhha, R.drawable.ic_add, R.drawable.budhha, "Bill"));
        storylist.add(new StoryModel(R.drawable.profile, R.drawable.ic_home, R.drawable.bill, "Bill"));
        storylist.add(new StoryModel(R.drawable.profile, R.drawable.ic_user, R.drawable.profile, "Bill"));

        StoryAdapter adapter = new StoryAdapter(storylist, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.storyRV.setLayoutManager(linearLayoutManager);
        binding.storyRV.setNestedScrollingEnabled(false);
        binding.storyRV.setAdapter(adapter);

        postlist = new ArrayList<>();

        PostAdapter postAdapter = new PostAdapter(postlist, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.dashboardRv.setLayoutManager(layoutManager);
        binding.dashboardRv.addItemDecoration(new DividerItemDecoration(binding.dashboardRv.getContext(), DividerItemDecoration.VERTICAL));
        binding.dashboardRv.setNestedScrollingEnabled(false);
        binding.dashboardRv.setAdapter(postAdapter);

        mbase.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlist.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    Objects.requireNonNull(post).setPostId(dataSnapshot.getKey());
                    postlist.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return binding.getRoot();
    }
}