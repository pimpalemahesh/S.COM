package com.myinnovation.socom.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myinnovation.socom.R;
import com.myinnovation.socom.fragments.AddPostFragment;
import com.myinnovation.socom.fragments.HomeFragment;
import com.myinnovation.socom.fragments.NotificationFragment;
import com.myinnovation.socom.fragments.ProfileFragment;
import com.myinnovation.socom.fragments.SearchFragment;
import com.myinnovation.socom.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private long backPressedTime;
    private Toast backToast;
    ActivityMainBinding binding;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        MainActivity.this.setTitle("My Profile");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            // If permission denied then we request again for permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},1);

        }

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
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}