package com.pierux.ejerciciorestaurante.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
//Librerias para la creacion automatica del cluster
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import com.pierux.ejerciciorestaurante.R;
import com.pierux.ejerciciorestaurante.model.ItemsCard;

/**
 * Personaliza la apariencia de los marcadores en el mapa.
 * 1. Define iconos distintos para restaurantes "visitados" y "no visitados".
 * 2. Gestiona la creación de clústeres (de forma automática a través de su libreria) cuando muchos
 *    marcadores están juntos, para que sea más legible.
 */

class RenderizadorCluster extends DefaultClusterRenderer<ItemsCard> {

    private final BitmapDescriptor iconoVisitado;
    private final BitmapDescriptor iconoNoVisitado;

    /**
     * Constructor, carga y redimensiona los iconos
     * @param context El contexto de la aplicación
     * @param map mapa donde va a dibujar
     * @param clusterManager
     */
    public RenderizadorCluster(Context context, GoogleMap map, ClusterManager<ItemsCard> clusterManager) {
        super(context, map, clusterManager);

        int anchoIcono = 120;
        int altoIcono = 120;

        iconoNoVisitado = crearIconoRedimensionado(context, R.drawable.pin_no_visitado, anchoIcono, altoIcono);
        iconoVisitado = crearIconoRedimensionado(context, R.drawable.pin_visitado, anchoIcono, altoIcono);
    }

    /**
     * Método que personaliza cada marcador con su categoria
     */
    @Override
    protected void onBeforeClusterItemRendered(@NonNull ItemsCard item, @NonNull MarkerOptions markerOptions) {
        if (item.isVisitado()) {
            markerOptions.icon(iconoVisitado);
        } else {
            markerOptions.icon(iconoNoVisitado);
        }

        // Anclamos el icono para que la punta del pin señale la ubicación correcta
        markerOptions.anchor(0.5f, 1f);
    }

    /**
     * Convierte las imagenes a bitmap para que pueda ser interpetado por Google Maps
     * de forma correcta
     */
    private BitmapDescriptor crearIconoRedimensionado(Context context, int idRecurso, int ancho, int alto) {

        Drawable drawableIcono = ContextCompat.getDrawable(context, idRecurso);

        //Creamos el "lienzo" vacio con las dimensiones de nuestra imagen
        Bitmap bitmapIcono = Bitmap.createBitmap(drawableIcono.getIntrinsicWidth(), drawableIcono.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmapIcono);
        drawableIcono.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawableIcono.draw(canvas);

        //Se crea el bitMap redimensionado al formato de Google Maps y se devuelve
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapIcono, ancho, alto, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }
}
