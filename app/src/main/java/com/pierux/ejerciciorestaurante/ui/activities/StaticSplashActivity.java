package com.pierux.ejerciciorestaurante.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.pierux.ejerciciorestaurante.R;

public class StaticSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_splash);


        new Handler().postDelayed(() -> {
            startActivity(new Intent(StaticSplashActivity.this, MainActivity.class));
            finish();
        }, 2000);
    }
}
