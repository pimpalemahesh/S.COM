package com.myinnovation.socom.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.myinnovation.socom.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(() -> {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo wifi = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo datac = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((wifi != null & datac != null)
                    && (wifi.isConnected() | datac.isConnected())) {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }else{
                finish();
                startActivity(new Intent(getApplicationContext(), NoInternetActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }, 3000);

    }
}