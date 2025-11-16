package com.pierux.ejerciciorestaurante.ui.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.pierux.ejerciciorestaurante.model.ItemsCard;
import com.pierux.ejerciciorestaurante.R;
import com.pierux.ejerciciorestaurante.adapters.RestauranteAdapter;
import com.pierux.ejerciciorestaurante.data.DAORestaurante;

import java.util.ArrayList;
import java.util.List;

public class VisitadosFragment extends Fragment {

    private RecyclerView recyclerView;
    private RestauranteAdapter adapter;
    private List<ItemsCard> listaRestaurantes;
    private DAORestaurante dao;

    private MaterialButton btnTodos, btnBar, btnRes;
    private RangeSlider slider;
    private EditText etFiltro;

    private String textoFiltro = "";
    private String categoriaSeleccionada = "Todas";
    private double minEstrellas = 0;
    private double maxEstrellas = 5;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitados, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewVisitados);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dao = new DAORestaurante(getContext());
        listaRestaurantes = new ArrayList<>();

        adapter = new RestauranteAdapter(getContext(), listaRestaurantes);
        recyclerView.setAdapter(adapter);

        btnTodos = view.findViewById(R.id.btnFiltroTodos);
        btnBar = view.findViewById(R.id.btnFiltroBar);
        btnRes = view.findViewById(R.id.btnFiltroRestaurante);
        slider = view.findViewById(R.id.filtroEstrellas);
        etFiltro = view.findViewById(R.id.etFiltroNombre);

        btnTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriaSeleccionada = "Todas";
                aplicarFiltrosCombinados();
                actualizarAparienciaBotones();

            }
        });

        btnBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriaSeleccionada = "Bar";
                aplicarFiltrosCombinados();
                actualizarAparienciaBotones();
            }
        });

        btnRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriaSeleccionada= "Restaurante";
                aplicarFiltrosCombinados();
                actualizarAparienciaBotones();
            }
        });

        slider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider rangeSlider, float value, boolean fromUser) {
                if(fromUser){
                    minEstrellas = slider.getValues().get(0);
                    maxEstrellas = slider.getValues().get(1);
                    aplicarFiltrosCombinados();

                }
            }
        });

        etFiltro.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textoFiltro = s.toString();
                aplicarFiltrosCombinados();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarVisitados();
    }




    private void aplicarFiltrosCombinados() {
        List<ItemsCard> soloVisitados = sacarVisitados();
        List<ItemsCard> filtradoFinal = new ArrayList<>();

        for (ItemsCard card: soloVisitados){

            if (card.getNombre().toLowerCase().contains(textoFiltro.toLowerCase())){
                boolean filtroCategoria = categoriaSeleccionada.equalsIgnoreCase("Todas")
                        || card.getCategoria().equalsIgnoreCase(categoriaSeleccionada);

                boolean filtroEstrellas = card.getPuntuacion() >= minEstrellas
                        && card.getPuntuacion() <= maxEstrellas;

                if (filtroCategoria && filtroEstrellas){
                    filtradoFinal.add(card);
                }
            }
        }

        listaRestaurantes.clear();
        listaRestaurantes.addAll(filtradoFinal);
        adapter.notifyDataSetChanged();

    }


    private void cargarVisitados() {
        listaRestaurantes.clear();
        List<ItemsCard> soloVisitados = sacarVisitados();

        listaRestaurantes.addAll(soloVisitados);
        adapter.notifyDataSetChanged();
    }

    @NonNull
    private List<ItemsCard> sacarVisitados() {
        List<ItemsCard> todosLosRestaurantes = dao.obtenerTodos();
        List<ItemsCard> soloVisitados = new ArrayList<>();

        for(ItemsCard restaurante : todosLosRestaurantes) {
            if(restaurante.isVisitado()) {
                soloVisitados.add(restaurante);
            }
        }
        return soloVisitados;
    }

    private void actualizarAparienciaBotones() {
        btnTodos.setActivated(false);
        btnBar.setActivated(false);
        btnRes.setActivated(false);

        switch (categoriaSeleccionada) {
            case "Todas" -> {
                btnTodos.setActivated(true);
            }
            case "Bar" -> {
                btnBar.setActivated(true);
            }
            case "Restaurante" ->{
                btnRes.setActivated(true);
            }

        }
    }
}
