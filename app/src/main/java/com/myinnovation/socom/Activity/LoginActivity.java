package com.myinnovation.socom.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myinnovation.socom.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;

    ActivityLoginBinding binding;

    FirebaseAuth mAuth;
    FirebaseUser currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding.gotosignup.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();

        binding.bar.setVisibility(View.INVISIBLE);

        binding.login.setOnClickListener(view -> {

            binding.bar.setVisibility(View.VISIBLE);

            try {
                mAuth.signInWithEmailAndPassword(Objects.requireNonNull(binding.emailLogin.getEditText()).getText().toString(), Objects.requireNonNull(binding.passwordLogin.getEditText()).getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                binding.bar.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                        })
                        .addOnFailureListener(e -> {
                            binding.bar.setVisibility(View.INVISIBLE);
                            binding.emailLogin.getEditText().setText("");
                            binding.passwordLogin.getEditText().setText("");
                            Toast.makeText(getApplicationContext(), "Failed to log in: " + e.getMessage(), +Toast.LENGTH_LONG).show();
                        });
            } catch (Exception e) {
                binding.bar.setVisibility(View.INVISIBLE);
                Objects.requireNonNull(binding.emailLogin.getEditText()).setText("");
                Objects.requireNonNull(binding.passwordLogin.getEditText()).setText("");
                Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentuser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            finishAffinity();
            return;
        } else {
            backToast = Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}