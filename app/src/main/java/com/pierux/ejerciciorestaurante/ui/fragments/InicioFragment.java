package com.pierux.ejerciciorestaurante.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pierux.ejerciciorestaurante.R;
import com.pierux.ejerciciorestaurante.ui.activities.FormularioActivity;


public class InicioFragment extends Fragment {
    Button btnAddRestaurante;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAddRestaurante = view.findViewById(R.id.btnAddRestaurante);

        btnAddRestaurante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(requireActivity(), FormularioActivity.class);
                startActivity(i);
            }
        });
    }
}