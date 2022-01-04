package com.myinnovation.socom.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myinnovation.socom.Activity.AllUsersChatActivity;
import com.myinnovation.socom.Activity.CommentActivity;
import com.myinnovation.socom.Adapter.PostAdapter;
import com.myinnovation.socom.Adapter.StoryAdapter;
import com.myinnovation.socom.Model.Post;
import com.myinnovation.socom.Model.Story;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.Model.UserStories;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.FragmentHomeBinding;
import com.myinnovation.socom.databinding.SampleViewUserDataBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class HomeFragment extends Fragment {

    ArrayList<Story> storylist;
    ArrayList<Post> postlist;
    FirebaseDatabase mbase;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    FragmentHomeBinding binding;
    ActivityResultLauncher<String> galleryLauncher;

    ProgressDialog dialog;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);


        binding.dashboardRv.showShimmerAdapter();
        binding.storyRV.showShimmerAdapter();

        mbase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        mbase.getReference().child("Users").child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postlist.clear();
                        if (snapshot.exists()) {
                            UserClass user = snapshot.getValue(UserClass.class);
                            Picasso.get()
                                    .load(user.getProfile_image())
                                    .placeholder(R.drawable.ic_user)
                                    .into(binding.profileImage);

                            binding.openChatSection.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(getContext(), AllUsersChatActivity.class).putExtra("username", user.getName()));
                                }
                            });

                            binding.profileImage.setOnClickListener(v -> {
                                ViewGroup viewGroup = container;
                                View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.sample_view_user_data, viewGroup, false);
                                viewGroup.removeView(view);
                                SampleViewUserDataBinding bd = SampleViewUserDataBinding.bind(view);
                                bd.name.setText(user.getName());
                                bd.profession.setText(user.getProfession());
                                Picasso.get()
                                        .load(user.getProfile_image())
                                        .placeholder(R.drawable.ic_user)
                                        .into(bd.profileImage);

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("User Details");
                                builder.setView(bd.getRoot());
                                builder.setCancelable(true);
                                builder.create();
                                builder.show();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        storylist = new ArrayList<>();


        StoryAdapter adapter = new StoryAdapter(storylist, getContext(), getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.storyRV.setLayoutManager(linearLayoutManager);
        binding.storyRV.setNestedScrollingEnabled(false);

        mbase.getReference()
                .child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    storylist.clear();
                    for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                        Story story = new Story();
                        story.setStoryBy(storySnapshot.getKey());
                        story.setStoryAt(storySnapshot.child("postBy").getValue(Long.class));

                        ArrayList<UserStories> stories = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : storySnapshot.child("userStories").getChildren()) {
                            UserStories userStories = dataSnapshot.getValue(UserStories.class);
                            stories.add(userStories);
                        }
                        story.setStories(stories);
                        storylist.add(story);
                    }
                    binding.storyRV.setAdapter(adapter);
                    binding.storyRV.hideShimmerAdapter();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postlist = new ArrayList<>();

        PostAdapter postAdapter = new PostAdapter(postlist, getContext(), getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.dashboardRv.setLayoutManager(layoutManager);
        binding.dashboardRv.addItemDecoration(new DividerItemDecoration(binding.dashboardRv.getContext(), DividerItemDecoration.VERTICAL));
        binding.dashboardRv.setNestedScrollingEnabled(false);


        mbase.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    Objects.requireNonNull(post).setPostId(dataSnapshot.getKey());
                    postlist.add(post);
                }
                binding.dashboardRv.setAdapter(postAdapter);
                binding.dashboardRv.hideShimmerAdapter();
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.addStoryImg.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            binding.story.setImageURI(result);
            dialog.show();

            final StorageReference storageReference = storage.getReference()
                    .child("stories")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(new Date().getTime() + "");

            storageReference.putFile(result).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Story story = new Story();
                    story.setStoryAt(new Date().getTime());
                    mbase.getReference()
                            .child("stories")
                            .child(FirebaseAuth.getInstance().getUid())
                            .child("postBy")
                            .setValue(story.getStoryAt()).addOnSuccessListener(unused -> {
                                UserStories stories = new UserStories(uri.toString(), story.getStoryAt());

                                mbase.getReference()
                                        .child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("userStories")
                                        .push()
                                        .setValue(stories).addOnSuccessListener(unused1 -> dialog.dismiss());
                            });
                }
            }));

        });
        return binding.getRoot();
    }
}