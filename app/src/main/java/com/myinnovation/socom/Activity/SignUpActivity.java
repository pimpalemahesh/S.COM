package com.myinnovation.socom.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.myinnovation.socom.Model.UserClass;
import com.myinnovation.socom.databinding.ActivitySignUpBinding;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase mbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding.username.requestFocus();

        mAuth = FirebaseAuth.getInstance();
        mbase = FirebaseDatabase.getInstance();

        binding.gotosignin.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));
        binding.signup.setOnClickListener(view -> {
            binding.bar.setVisibility(View.VISIBLE);

            try {
                mAuth.createUserWithEmailAndPassword(Objects.requireNonNull(binding.emailSignup.getEditText()).getText().toString(), Objects.requireNonNull(binding.passwordSignup.getEditText()).getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                binding.bar.setVisibility(View.INVISIBLE);

                                UserClass user = new UserClass(Objects.requireNonNull(binding.username.getEditText()).getText().toString(), Objects.requireNonNull(binding.userProfession.getEditText()).getText().toString(), binding.emailSignup.getEditText().getText().toString(), binding.passwordSignup.getEditText().getText().toString());
                                String id = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();

                                mbase.getReference().child("Users").child(id).setValue(user);

                                Toast.makeText(getApplicationContext(), "Successfully Registered.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(e -> {

                            binding.bar.setVisibility(View.INVISIBLE);
                            Objects.requireNonNull(binding.username.getEditText()).setText("");
                            Objects.requireNonNull(binding.userProfession.getEditText()).setText("");
                            binding.emailSignup.getEditText().setText("");
                            binding.passwordSignup.getEditText().setText("");

                            Toast.makeText(getApplicationContext(), "Sign up Error : " + e.getMessage(), Toast.LENGTH_LONG).show();


                        });
            } catch (Exception e) {
                binding.bar.setVisibility(View.INVISIBLE);
                Objects.requireNonNull(binding.username.getEditText()).setText("");
                Objects.requireNonNull(binding.userProfession.getEditText()).setText("");
                Objects.requireNonNull(binding.emailSignup.getEditText()).setText("");
                Objects.requireNonNull(binding.passwordSignup.getEditText()).setText("");
                Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onBackPressed() {

    }
}