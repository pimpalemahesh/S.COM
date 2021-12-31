package com.myinnovation.socom.fragments;

import android.app.ProgressDialog;
import android.app.appsearch.AppSearchResult;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myinnovation.socom.Model.Post;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.R;
import com.myinnovation.socom.databinding.FragmentAddPostBinding;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Objects;

public class AddPostFragment extends Fragment {

    FragmentAddPostBinding binding;
    Uri uri;

    FirebaseAuth mAuth;
    FirebaseDatabase mbase;
    FirebaseStorage storage;

    public AddPostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mbase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(inflater, container, false);

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Post Uploading");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Uploaded : ");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        mbase.getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            UserClass user = snapshot.getValue(UserClass.class);

                            Picasso.get()
                                    .load(user.getProfile_image())
                                    .placeholder(R.drawable.ic_image)
                                    .into(binding.postProfileImage);

                            binding.name.setText(user.getName());
                            binding.profession.setText(user.getProfession());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.postDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String description = binding.postDescription.getText().toString();
                if (!description.isEmpty()) {
                    binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btn));
                    binding.postBtn.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postBtn.setEnabled(true);
                } else {
                    binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_active_btn));
                    binding.postBtn.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.addImg.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 103);
        });

        binding.postBtn.setOnClickListener(view -> {

            if (uri == null) {
                Toast.makeText(getContext(), "Please Select your post first", Toast.LENGTH_LONG).show();
            } else if (binding.postDescription.getText().toString().equals("") || binding.postDescription.getText().toString().length() == 0) {
                Toast.makeText(getContext(), "You have not added any Description!", Toast.LENGTH_LONG).show();
            } else {
                try {
                    final StorageReference reference = storage.getReference().child("posts")
                            .child(FirebaseAuth.getInstance().getUid())
                            .child(new Date().getTime() + "");

                    reference.putFile(uri)
                            .addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                Post post = new Post();
                                post.setPostImage(uri.toString());
                                post.setPostBy(FirebaseAuth.getInstance().getUid());
                                post.setPostDescription(binding.postDescription.getText().toString());
                                post.setPostAt(new Date().getTime());

                                mbase.getReference().child("posts")
                                        .push()
                                        .setValue(post).addOnSuccessListener(unused -> {
                                    progressDialog.dismiss();

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                            .child("Users");
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    if(dataSnapshot.getKey().equals(post.getPostBy())){
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("Users")
                                                                .child(post.getPostBy())
                                                                .child("postCount")
                                                                .setValue(dataSnapshot.child("postCount").getValue(Long.class) + 1);
                                                        break;
                                                    }
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    Toast.makeText(getContext(), "Successfully Posted.", Toast.LENGTH_LONG).show();
                                });
                            }))
                            .addOnProgressListener(snapshot -> {
                                int per = (int) ((100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded : " + per + "%");
                                progressDialog.show();
                            });
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        });
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 103  &&  data.getData() != null) {
            uri = data.getData();
            binding.postImage.setImageURI(uri);
            binding.postImage.setVisibility(View.VISIBLE);
            binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btn));
            binding.postBtn.setTextColor(getContext().getResources().getColor(R.color.white));
            binding.postBtn.setEnabled(true);
        }
    }
}