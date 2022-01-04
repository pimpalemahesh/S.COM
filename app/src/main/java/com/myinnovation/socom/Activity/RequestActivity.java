package com.myinnovation.socom.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.myinnovation.socom.databinding.ActivityRequestBinding;

public class RequestActivity extends AppCompatActivity {

    ActivityRequestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}