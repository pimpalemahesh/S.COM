package com.myinnovation.socom.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.myinnovation.socom.R;

public class NoInternetActivity extends AppCompatActivity {

    Button refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(v -> {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo wifi = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo datac = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((wifi != null & datac != null)
                    && (wifi.isConnected() | datac.isConnected())) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }else{
                Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });

    }
}