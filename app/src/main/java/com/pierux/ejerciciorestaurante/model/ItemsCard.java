package com.pierux.ejerciciorestaurante.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ItemsCard implements ClusterItem {
    private int id;
    private String nombre;
    private String telefono;
    private String direccion;
    private int imagenResId;
    private float puntuacion;
    private boolean visitado;
    private String categoria;
    private String comentario;
    private LatLng posicion;

    public ItemsCard(int id, String nombre, String telefono, String direccion, int imagenResId, float puntuacion, boolean visitado, String categoria, String comentario) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.imagenResId = imagenResId;
        this.puntuacion = puntuacion;
        this.visitado = visitado;
        this.categoria = categoria;
        this.comentario = comentario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getImagenResId() {
        return imagenResId;
    }

    public void setImagenResId(int imagenResId) {
        this.imagenResId = imagenResId;
    }

    public float getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(float puntuacion) {
        this.puntuacion = puntuacion;
    }

    public boolean isVisitado() {
        return visitado;
    }

    public void setVisitado(boolean visitado) {
        this.visitado = visitado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LatLng getPosicion() {
        return posicion;
    }

    public void setPosicion(LatLng posicion) {
        this.posicion = posicion;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return posicion;
    }

    @Nullable
    @Override
    public String getTitle() {
        return getNombre();
    }

    @Nullable
    @Override
    public String getSnippet() {
        return getDireccion();
    }

    @Nullable
    @Override
    public Float getZIndex() {
        return 0f;
    }
}

