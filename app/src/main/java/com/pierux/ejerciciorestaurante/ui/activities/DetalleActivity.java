package com.pierux.ejerciciorestaurante.ui.activities;

import static android.Manifest.permission.CALL_PHONE;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.pierux.ejerciciorestaurante.model.ItemsCard;
import com.pierux.ejerciciorestaurante.R;
import com.pierux.ejerciciorestaurante.data.DAORestaurante;

public class DetalleActivity extends AppCompatActivity {

    private TextView tvNombre, tvDireccion, tvTelefono, tvCategoria, tvComentario, tvPuntuacion;
    private ImageView imgRestaurante;
    private RatingBar rbPuntuacion;
    private Button btnSalir, btnLlamar;
    private DAORestaurante dao;
    private ItemsCard restaurante;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle);
        configurarVentana();

        dao = new DAORestaurante(this);

        tvNombre = findViewById(R.id.tvDetalleNombre);
        tvDireccion = findViewById(R.id.tvDetalleDireccion);
        tvTelefono = findViewById(R.id.tvDetalleTelefono);
        tvCategoria = findViewById(R.id.tvDetalleCategoria);
        tvComentario = findViewById(R.id.tvDetalleComentario);
        imgRestaurante = findViewById(R.id.imgDetalle);
        rbPuntuacion = findViewById(R.id.ratingBarDetalle);
        tvPuntuacion = findViewById(R.id.tvDetallePuntuacion);
        btnLlamar = findViewById(R.id.btnLlamar);
        btnSalir = findViewById(R.id.btnSalir);
        rbPuntuacion.setIsIndicator(true);


        // El -1 es un valor por defecto si no encuentra el ID, para saber que hubo un error.
        int restauranteId = getIntent().getIntExtra("RESTAURANTE_ID", -1);

        if (restauranteId != -1) {
            restaurante = dao.obtenerPorId(restauranteId);
            if (restaurante != null) {
                cargarDatos();

            }else{
                Toast.makeText(this, "Error: No se encontró el restaurante.", Toast.LENGTH_LONG).show();
                finish();

            }
        }else{
            Toast.makeText(this, "Error: ID no válido.", Toast.LENGTH_LONG).show();
            finish();
        }

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamar();
            }
        });


    }

    private void llamar() {
        String numero = restaurante.getTelefono();
        if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

            solicitarPermiso();
        }else {
            mostrarDialogoDeConfirmacion(numero);
        }

    }

    private void mostrarDialogoDeConfirmacion(String numero) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Llamada") // Título del diálogo
                .setMessage("¿Estás seguro de que quieres llamar a " + restaurante.getNombre() + "?")
                .setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_CALL);
                        i.setData(Uri.parse("tel:" + numero));
                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


    private void solicitarPermiso() {
        requestPermissions(new String[]{CALL_PHONE}, 100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Comprobamos que es nuestra petición y si hay permisos se intenta llamada de nuevo
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                llamar();
            } else {
                Toast.makeText(this, "Permiso de llamada denegado.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void cargarDatos() {
        tvNombre.setText(restaurante.getNombre());
        tvTelefono.setText(restaurante.getTelefono());
        tvDireccion.setText(restaurante.getDireccion());
        tvCategoria.setText("Categoria: " + restaurante.getCategoria());
        tvComentario.setText(restaurante.getComentario());
        tvPuntuacion.setText(String.format("%.1f", restaurante.getPuntuacion()));
        rbPuntuacion.setRating(restaurante.getPuntuacion());

        if ("Bar".equals(restaurante.getCategoria())) {
            imgRestaurante.setImageResource(R.drawable.bar);
        } else {
            imgRestaurante.setImageResource(R.drawable.img_restuarante1);
        }
    }

    private void configurarVentana(){
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        insetsController.hide(WindowInsetsCompat.Type.systemBars());

        insetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

    }
}