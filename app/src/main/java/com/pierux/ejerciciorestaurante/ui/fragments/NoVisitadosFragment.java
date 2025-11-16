package com.pierux.ejerciciorestaurante.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
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
import com.pierux.ejerciciorestaurante.R;
import com.pierux.ejerciciorestaurante.adapters.RestauranteAdapter;
import com.pierux.ejerciciorestaurante.data.DAORestaurante;
import com.pierux.ejerciciorestaurante.model.ItemsCard;

import java.util.ArrayList;
import java.util.List;


public class NoVisitadosFragment extends Fragment {

    private RecyclerView recyclerView;
    private RestauranteAdapter adapter;
    private List<ItemsCard> listaRestaurantes;
    private DAORestaurante dao;

    private MaterialButton btnTodos, btnBar, btnRes;
    private EditText etFiltro;
    private String textoFiltro = "";
    private String categoriaSeleccionada = "Todas";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_visitados, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNoVisitados);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dao = new DAORestaurante(getContext());
        listaRestaurantes = new ArrayList<>();

        adapter = new RestauranteAdapter(getContext(), listaRestaurantes);
        recyclerView.setAdapter(adapter);

        btnTodos = view.findViewById(R.id.btnFiltroTodosNoVisitados);
        btnBar = view.findViewById(R.id.btnFiltroBarNoVisitao);
        btnRes = view.findViewById(R.id.btnFiltroResNoVisitado);
        etFiltro = view.findViewById(R.id.etFiltroNombreNoVisitado);

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
        List<ItemsCard> soloVisitados = sacarNoVisitados();
        List<ItemsCard> filtradoFinal = new ArrayList<>();

        for (ItemsCard card: soloVisitados){

            if (card.getNombre().toLowerCase().contains(textoFiltro.toLowerCase())){
                if (categoriaSeleccionada.equalsIgnoreCase("Todas")
                        || card.getCategoria().equalsIgnoreCase(categoriaSeleccionada)){
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
        List<ItemsCard> soloVisitados = sacarNoVisitados();

        listaRestaurantes.addAll(soloVisitados);
        adapter.notifyDataSetChanged();
    }

    @NonNull
    private List<ItemsCard> sacarNoVisitados() {
        List<ItemsCard> todosLosRestaurantes = dao.obtenerTodos();
        List<ItemsCard> soloVisitados = new ArrayList<>();

        for(ItemsCard restaurante : todosLosRestaurantes) {
            if(!restaurante.isVisitado()) {
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