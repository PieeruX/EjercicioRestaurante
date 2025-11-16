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

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
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

        idRestaurante = getIntent().getIntExtra("idRestaurante", -1);
        esEdicion = (idRestaurante != -1);

        if (esEdicion) {
            cargarDatosParaEdicion();
        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }
        placesClient = Places.createClient(this);

        //ArrayAdapter para mostrar las sugerencias en una lista desplegable
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        etDireccion.setAdapter(adapter);

        //Esto pide sgurenecias a la API de Google mientras el usuario escribe
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

                    // Hacemos la peticion para la API Places
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setCountry(paisActual)
                            .setQuery(texto.toString())
                            .build();

                    // Llama a la API
                    placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                        // Cuando Google responde con éxito...
                        adapter.clear(); // Borramos las sugerencias viejas
                        // Y añadimos las nuevas que nos ha dado Google
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            adapter.add(prediction.getFullText(null).toString());
                        }
                        adapter.notifyDataSetChanged();

                    });
                }
            }



            @Override
            public void afterTextChanged(Editable s) {}
        });

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
                if (!etNombre.getText().toString().strip().isEmpty()){
                    if (esEdicion){
                        editarDatos();
                        Toast.makeText(FormularioActivity.this, "Restaurante actualizado correctamente", Toast.LENGTH_LONG).show();
                    }else{
                        guardarDatos();
                        Toast.makeText(FormularioActivity.this, "Restaurante guardado correctamente", Toast.LENGTH_LONG).show();
                    }
                    finish();

                }else{
                    Toast.makeText(FormularioActivity.this, "Nombre no puede estar vacío", Toast.LENGTH_LONG).show();
                }


                
            }
        });


    }

    private String getCategoriaSeleccionada() {
        String seleccionado = "Restuarante";

        if (rbtn_restaurante.isChecked()) {
            seleccionado = "Restaurante";

        } else if (rbtn_bar.isChecked()) {
            seleccionado = "Bar";
        }

        return seleccionado;
    }


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
