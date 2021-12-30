package com.myinnovation.socom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.myinnovation.socom.R;
import com.myinnovation.socom.fragments.AddPostFragment;
import com.myinnovation.socom.fragments.HomeFragment;
import com.myinnovation.socom.fragments.NotificationFragment;
import com.myinnovation.socom.fragments.ProfileFragment;
import com.myinnovation.socom.fragments.SearchFragment;
import com.myinnovation.socom.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        MainActivity.this.setTitle("My Profile");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        binding.toolbar.setVisibility(View.GONE);
        transaction.replace(R.id.container, new HomeFragment());
        transaction.commit();

        binding.readableBottomBar.setOnItemSelectListener(i -> {
            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();

            switch (i) {

                case 0:
                    binding.toolbar.setVisibility(View.GONE);

                    transaction1.replace(R.id.container, new HomeFragment());
                    break;

                case 1:
                    binding.toolbar.setVisibility(View.GONE);
                    transaction1.replace(R.id.container, new NotificationFragment());
                    break;

                case 2:
                    binding.toolbar.setVisibility(View.GONE);
                    transaction1.replace(R.id.container, new AddPostFragment());
                    break;

                case 3:
                    binding.toolbar.setVisibility(View.GONE);
                    transaction1.replace(R.id.container, new SearchFragment());
                    break;

                case 4:
                    binding.toolbar.setVisibility(View.VISIBLE);
                    transaction1.replace(R.id.container, new ProfileFragment());
                    break;
            }
            transaction1.commit();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.setting:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }
}