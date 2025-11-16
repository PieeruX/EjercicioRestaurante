package com.pierux.ejerciciorestaurante.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pierux.ejerciciorestaurante.BuildConfig;
import com.pierux.ejerciciorestaurante.R;

import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView btnNavView;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Crear hilo para inicializar Places API en segundo plano
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                //Iniciar Places API
                if (!Places.isInitialized()) {
                    Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //Crear cliente de Places global
                        PlacesClient placesClient = Places.createClient(MainActivity.this);
                        configurarVentana();
                    }

                });
            }
        });

    }

    /**
     * Esta función hace que el boton de navBar funcione con el contenedor de fragmentos
     * y se pueda sincronizar la navegación entre ellos.
     */

    private void setUpNavegacion() {
        btnNavView = findViewById(R.id.btnNavView);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(btnNavView, navHostFragment.getNavController());
    }

    private void configurarVentana(){
        // Ventana en modo pantalla completa (inmersivo)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        //Obtenemos el controlador de las barras de sistema
        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // Ocultar las barras de sistema (barra de estado y barra de navegación)
        insetsController.hide(WindowInsetsCompat.Type.systemBars());

        // Que aparezcan las barras de sistema temporalmente al hacer swipe
        insetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

        setUpNavegacion();
    }
}

