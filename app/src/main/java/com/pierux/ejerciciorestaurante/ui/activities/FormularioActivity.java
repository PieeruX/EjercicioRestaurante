package com.pierux.ejerciciorestaurante.ui.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.pierux.ejerciciorestaurante.BuildConfig;
import com.pierux.ejerciciorestaurante.model.ItemsCard;
import com.pierux.ejerciciorestaurante.R;
import com.pierux.ejerciciorestaurante.data.DAORestaurante;

import java.util.Arrays;

public class FormularioActivity extends AppCompatActivity {

    private EditText etNombre, etTelefono, etComentario;
    private AutoCompleteTextView etDireccion;
    private RatingBar ratingBar;
    private Switch switchVisitado;
    private Button btnGuardar, btnCancelar;
    private RadioGroup radioGroup;
    private RadioButton rbtn_bar, rbtn_restaurante;
    private boolean esEdicion = false;
    private int idRestaurante = -1;
    private DAORestaurante dao;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario);
        configurarVentana();

        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        etComentario = findViewById(R.id.etComentario);
        ratingBar = findViewById(R.id.puntuacion);
        switchVisitado = findViewById(R.id.switchVisitado);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        dao = new DAORestaurante(this);
        radioGroup = findViewById(R.id.rg_escogerTipo);
        rbtn_bar = findViewById(R.id.rbtn_bar);
        rbtn_restaurante = findViewById(R.id.rbtn_restaurante);

        ratingBar.setIsIndicator(false);

        //Comprobacion de si viene de agregar o de editar
        // si no viene de editar el valor se queda por defecto en -1
        idRestaurante = getIntent().getIntExtra("idRestaurante", -1);
        esEdicion = (idRestaurante != -1);

        //Si es edicion (se recibe ID), cargamos los datos
        if (esEdicion) {
            cargarDatosParaEdicion();
        }

        //Usar PLACES para la sugerencias de direcciones
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }
        placesClient = Places.createClient(this);

        //ArrayAdapter para mostrar las sugerencias en una lista desplegable
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        etDireccion.setAdapter(adapter);

        //Listener soobrescrito, cuando el usuario escribe, places lanza sugerencias
        etDireccion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence texto, int start, int before, int count) {
                if (texto.toString().isEmpty()) {
                    adapter.clear();
                }else{
                    // Obtenemos pais en el que está el telefono
                    String paisActual = getResources().getConfiguration().getLocales().get(0).getCountry();

                    // Creamos la peticion para la API Places
                    FindAutocompletePredictionsRequest peticion = FindAutocompletePredictionsRequest.builder()
                            .setCountry(paisActual)
                            .setQuery(texto.toString())
                            .build();

                    // Llama a la API
                    placesClient.findAutocompletePredictions(peticion).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
                        @Override
                        public void onSuccess(FindAutocompletePredictionsResponse response) {
                            // Cuando Google responde, limpiamos sugerencias antiguas
                            adapter.clear();

                            // Se añaden las nuevas sugerencias
                            for (AutocompletePrediction prediccion : response.getAutocompletePredictions()) {
                                adapter.add(prediccion.getFullText(null).toString());
                            }

                            // Notificamos al adaptador que los datos han cambiado para que actualice la lista.
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Listener del Switch, si está en false bloquea el ratingBar y lo pone a 0,
        // si está en true deja usar el ratingBar
        switchVisitado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean estaVisitado = switchVisitado.isChecked();
            ratingBar.setIsIndicator(!estaVisitado);

            if (!estaVisitado) {
                ratingBar.setRating(0);
            }

        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean nombreVacio = etNombre.getText().toString().strip().isEmpty();
                boolean telefonoVacio = etTelefono.getText().toString().strip().isEmpty();
                boolean direccionVacio = etDireccion.getText().toString().strip().isEmpty();
                if (!nombreVacio && !telefonoVacio && !direccionVacio){
                    if (esEdicion){
                        editarDatos();
                        Toast.makeText(FormularioActivity.this, "Lugar actualizado correctamente", Toast.LENGTH_SHORT).show();
                    }else{
                        guardarDatos();
                        Toast.makeText(FormularioActivity.this, "Lugar guardado correctamente", Toast.LENGTH_SHORT).show();
                    }
                    finish();

                }else{
                    Toast.makeText(FormularioActivity.this, "Rellene los campos obligatorios(*)", Toast.LENGTH_LONG).show();
                }


                
            }
        });

    }
    /**
     * Método que devuelve la categoria seleccionada por el usuario
     * @return categoria seleccionada
     */
    private String getCategoriaSeleccionada() {
        String seleccionado = "Restuarante";

        if (rbtn_restaurante.isChecked()) {
            seleccionado = "Restaurante";

        } else if (rbtn_bar.isChecked()) {
            seleccionado = "Bar";
        }

        return seleccionado;
    }

    /**
     * Método que carga los datos desde la base de datos cuando estamos en modo edición
     * de algún restaurante ya existente
     */
    private void cargarDatosParaEdicion() {
        ItemsCard restaurante = dao.obtenerPorId(idRestaurante); // Necesitarás crear este método en tu DAO
        if (restaurante != null) {
            etNombre.setText(restaurante.getNombre());
            etTelefono.setText(restaurante.getTelefono());
            etDireccion.setText(restaurante.getDireccion());
            ratingBar.setRating(restaurante.getPuntuacion());
            switchVisitado.setChecked(restaurante.isVisitado());

            if ("Bar".equals(restaurante.getCategoria())) {
                rbtn_bar.setChecked(true);
            } else {
                rbtn_restaurante.setChecked(true);
            }

            etComentario.setText(restaurante.getComentario());

            setTitle("Editar Restaurante");
        } else {

            Toast.makeText(this, "Error: Restaurante no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Método que llama al dao para actualizar los datos cuando estamos en modo edición.
     */
    private void editarDatos() {
        dao.actualizarRestaurante(idRestaurante,
                etNombre.getText().toString().strip(),
                etTelefono.getText().toString().strip(),
                etDireccion.getText().toString().strip(),
                ratingBar.getRating(),
                switchVisitado.isChecked(),
                getCategoriaSeleccionada(),
                etComentario.getText().toString().strip());
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

    /**
     * Método que llama al dao para guardar los datos de un nuevo restuarante
     */
    private void guardarDatos() {
        dao.insertarRestaurante(etNombre.getText().toString().strip(),
                etTelefono.getText().toString().strip(),
                etDireccion.getText().toString().strip(),
                ratingBar.getRating(),
                switchVisitado.isChecked(),
                getCategoriaSeleccionada(),
                etComentario.getText().toString().strip());

        }
    }
