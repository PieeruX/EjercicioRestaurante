package com.pierux.ejerciciorestaurante.ui.fragments;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.pierux.ejerciciorestaurante.R;

import android.location.Address;
import android.location.Geocoder;
import com.google.maps.android.clustering.ClusterManager;

import com.pierux.ejerciciorestaurante.data.DAORestaurante;
import com.pierux.ejerciciorestaurante.model.ItemsCard;
import java.io.IOException;
import java.util.List;



public class MapaFragment extends Fragment implements OnMapReadyCallback, ClusterManager.OnClusterItemClickListener<ItemsCard>  {
    private static final String TAG = "MapaFragment";
    private GoogleMap mMap;
    private ClusterManager<ItemsCard> clusterManager;
    private FusedLocationProviderClient localizacionCliente;
    private ActivityResultLauncher<String[]> solicitudPermisoLocalizacion;

    private CardView cardRestaurante;
    private ImageView imagenRestaurante;
    private TextView nombreRestaurante, direccionRestaurante, telefonoRestaurante;
    private RatingBar estrellas;
    private Button btnMaps;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardRestaurante = view.findViewById(R.id.cardRestauranteMap);
        imagenRestaurante = view.findViewById(R.id.imagenRestauranteMap);
        nombreRestaurante = view.findViewById(R.id.nombreRestauranteMap);
        direccionRestaurante = view.findViewById(R.id.direccionRestauranteMap);
        telefonoRestaurante = view.findViewById(R.id.telefonoRestauranteMap);
        estrellas = view.findViewById(R.id.ratingBarMap);
        btnMaps = view.findViewById(R.id.btnVerEnMaps);


        localizacionCliente = LocationServices.getFusedLocationProviderClient(requireActivity());

        solicitudPermisoLocalizacion = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            Boolean accesoFino = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
            Boolean accesoGrueso = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

            // Si se concede algún permiso, obtener la ubicación actual
            if (accesoFino || accesoGrueso) {
                getCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "Permiso denegado. Mostrando Madrid.", Toast.LENGTH_LONG).show();
                LatLng madrid = new LatLng(40.4168, -3.7038);
                if (mMap != null){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 12f));
                }
            }
        });

        // Inicializar el fragmento del mapa con diseño personalizado desde Map ID
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    /**
     * Función llamada cuando el mapa está listo para ser usado.
     * @param googleMap El objeto GoogleMap proporcionado.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        configurarCluster();
        checkPermisosLocalizacion();
    }
    /**
     * Configura el ClusterManager y añade los marcadores directamente.
     */
    private void configurarCluster() {
        if (mMap != null || getContext() != null){
            clusterManager = new ClusterManager<>(requireContext(), mMap);

            // Le dices al ClusterManager cómo debe dibujar los marcadores y clústeres.
            RenderizadorCluster renderizador = new RenderizadorCluster(getContext(), mMap, clusterManager);
            clusterManager.setRenderer(renderizador);

            //Cuando el mapa está quieto, se actualiza
            mMap.setOnCameraIdleListener(clusterManager);

            //Gestiona los clicks del usuario en los marcadores
            mMap.setOnMarkerClickListener(clusterManager);

            clusterManager.setOnClusterItemClickListener(this);

            //Si hacemos click en cualquier parte del mapa, oculta la tarjeta visible
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {
                    if (cardRestaurante.getVisibility() == View.VISIBLE) {
                        cardRestaurante.setVisibility(View.GONE);
                    }
                }
            });

            DAORestaurante dao = new DAORestaurante(requireContext());
            List<ItemsCard> restaurantes = dao.obtenerTodos();

            for (ItemsCard restaurante : restaurantes) {
                if (restaurante.getDireccion() != null && !restaurante.getDireccion().isEmpty()) {
                    LatLng posicion = getLatLngDeDireccion(restaurante.getDireccion());
                    if (posicion != null) {
                        restaurante.setPosicion(posicion);
                        clusterManager.addItem(restaurante);
                    } else {
                        Log.w(TAG, "No se encontraron coordenadas para: " + restaurante.getDireccion());
                    }
                }
            }


        }

    }

    /**
     * Convierte una dirección en coordenadas.
     */
    private LatLng getLatLngDeDireccion(String direccion) {
        // Obtenemos context con requireContext()
        Geocoder geocoder = new Geocoder(requireContext());
        LatLng latLng = null;
        try {
            // Pide al geocodificador que encuentre la dirección (máximo 1 resultado).
            List<Address> addresses = geocoder.getFromLocationName(direccion, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Si la encuentra, crea y devuelve un objeto LatLng con las coordenadas.
                latLng =  new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            // Mostramos en LogCat
            Log.e(TAG, "Error de geocodificación para la dirección: " + direccion, e);
        }

        // Si no se encuentra la dirección o hay un error, devuelve null.
        return latLng;
    }


    /**
     * Verifica si los permisos de localización están concedidos.
     * Si están concedidos, obtiene la ubicación actual.
     * Si no, solicita los permisos necesarios.
     */

    private void checkPermisosLocalizacion() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            solicitudPermisoLocalizacion.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    /**
     * Obtiene la ubicación actual del usuario y mueve la cámara del mapa a esa ubicación.
     * Si no se puede obtener la ubicación, muestra un mensaje y centra el mapa en Madrid.
     */
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Controles del mapa
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        localizacionCliente.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                LatLng ubicacionUsuario = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacionUsuario, 15f));
            } else {
                Toast.makeText(requireContext(), "No se pudo obtener la ubicación.", Toast.LENGTH_LONG).show();
                LatLng madrid = new LatLng(40.4168, -3.7038);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 12f));
            }
        });
    }


    /**
     * Método que muestra los detalles en la tarjeta de informacion en el mapa y
     * lo mueve para no ocultar el marcador
     * @param restaurante
     *
     * @return
     */
    @Override
    public boolean onClusterItemClick(ItemsCard restaurante) {

        nombreRestaurante.setText(restaurante.getNombre());
        direccionRestaurante.setText(restaurante.getDireccion());
        telefonoRestaurante.setText(restaurante.getTelefono());
        estrellas.setRating(restaurante.getPuntuacion());

        if (restaurante.getCategoria() != null && restaurante.getCategoria().equalsIgnoreCase("Bar")) {
            imagenRestaurante.setImageResource(R.drawable.bar);

        } else if (restaurante.getCategoria() != null && restaurante.getCategoria().equalsIgnoreCase("Restaurante")) {
            imagenRestaurante.setImageResource(R.drawable.img_restuarante1);
        }

        LatLng posicion = restaurante.getPosition();

        //Mandar la ubicacion a google Maps
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el URI de las coordenadas
                String uri = "geo:0,0?q=" + posicion.latitude + "," + posicion.longitude + "(" + Uri.encode(restaurante.getNombre()) + ")";
                Uri gmmIntentUri = Uri.parse(uri);
                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                startActivity(intent);
            }
        });

        //Mueve la camara para que cuando salga la card no tape el marcador pulsado
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                restaurante.getPosition().latitude + 0.0025,
                restaurante.getPosition().longitude
        )), 300, null);

        cardRestaurante.setVisibility(View.VISIBLE);

        return false;
    }
}


