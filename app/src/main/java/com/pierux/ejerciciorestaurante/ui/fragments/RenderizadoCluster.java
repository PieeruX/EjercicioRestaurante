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
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.pierux.ejerciciorestaurante.R;
import com.pierux.ejerciciorestaurante.model.ItemsCard;

class RenderizadorCluster extends DefaultClusterRenderer<ItemsCard> {

    // 1. Declaramos los dos posibles iconos que usaremos.
    private final BitmapDescriptor iconoVisitado;
    private final BitmapDescriptor iconoNoVisitado;

    public RenderizadorCluster(Context context, GoogleMap map, ClusterManager<ItemsCard> clusterManager) {
        super(context, map, clusterManager);

        // 2. Pre-cargamos y redimensionamos ambos iconos UNA SOLA VEZ
        //    para que la app sea muy eficiente.
        int anchoIcono = 120; // Juega con este valor si son muy grandes o pequeños
        int altoIcono = 120;

        // Cargar y preparar el icono para "NO VISITADO"
        iconoNoVisitado = crearIconoRedimensionado(context, R.drawable.pin_no_visitado, anchoIcono, altoIcono);

        // Cargar y preparar el icono para "VISITADO"
        iconoVisitado = crearIconoRedimensionado(context, R.drawable.pin_visitado, anchoIcono, altoIcono);
    }

    /**
     * 3. ¡AQUÍ ESTÁ LA LÓGICA PRINCIPAL!
     *    Este método se llama para cada marcador y decide qué icono ponerle.
     */
    @Override
    protected void onBeforeClusterItemRendered(@NonNull ItemsCard item, @NonNull MarkerOptions markerOptions) {

        // Comprobamos el estado booleano del restaurante
        if (item.isVisitado()) {
            markerOptions.icon(iconoVisitado);
        } else {
            markerOptions.icon(iconoNoVisitado);
        }

        // Anclamos el icono para que la punta del pin señale la ubicación correcta
        markerOptions.anchor(0.5f, 1f);
    }

    /**
     * Método de utilidad para no repetir código.
     * Carga una imagen, la redimensiona y la devuelve lista para usar.
     */
    private BitmapDescriptor crearIconoRedimensionado(Context context, int idRecurso, int ancho, int alto) {
        Drawable drawableIcono = ContextCompat.getDrawable(context, idRecurso);
        Bitmap bitmapIcono = Bitmap.createBitmap(drawableIcono.getIntrinsicWidth(), drawableIcono.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmapIcono);
        drawableIcono.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawableIcono.draw(canvas);
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapIcono, ancho, alto, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }
}


