package com.pierux.ejerciciorestaurante.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.pierux.ejerciciorestaurante.R;


public class RestaurantesFragment extends Fragment {

    private MaterialButtonToggleGroup toggleGroup;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restuarantes, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toggleGroup = view.findViewById(R.id.toggle_button_group);

        // Cargar el fragmento inicial que se muestra por defecto ("Visitados")
        if (savedInstanceState == null) {
            cambiarFragment(new VisitadosFragment());
        }

        // Añadimos un "oyente" que se activará cada vez que se seleccione un botón.
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {

            if (isChecked) {
                if (checkedId == R.id.btnVisitados) {
                    cambiarFragment(new VisitadosFragment());

                } else if (checkedId == R.id.btnNoVisitados) {
                    cambiarFragment(new NoVisitadosFragment());
                }
            }
        });
    }

    private void cambiarFragment(Fragment fragment) {
        // Usamos getChildFragmentManager() porque estamos manejando fragmentos DENTRO de otro fragmento.
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        // Reemplaza lo que haya en el contenedor 'fragment_container' con el nuevo fragmento.
        transaction.replace(R.id.fragment_container, fragment);

        // Confirma la transacción para que el cambio sea visible.
        transaction.commit();
    }



}