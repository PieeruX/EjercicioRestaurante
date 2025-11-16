package com.pierux.ejerciciorestaurante.ui.activities;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import androidx.activity.ComponentActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.pierux.ejerciciorestaurante.R;

public class SplashActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        LottieAnimationView lottie = findViewById(R.id.lottieSplash);

        // Listener para detectar el final de la animación
        lottie.addAnimatorListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Temporizador de 2 segundos antes de pasar al logo estático
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mostrarLogoEstatico();
                    }
                }, 2000);

            }

        });
    }

    private void mostrarLogoEstatico() {
        setContentView(R.layout.activity_static_splash);

        //Fundido de entrada al logo estático
        LinearLayout root = findViewById(R.id.staticRoot);
        if (root != null) {
            AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);// primer parámetro opacidad inicial, segundo parámetro opacidad final
            fadeIn.setDuration(1000); // duración del fundido de entrada
            root.startAnimation(fadeIn);
        }

        // Iniciar MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 1500);

    }
}





