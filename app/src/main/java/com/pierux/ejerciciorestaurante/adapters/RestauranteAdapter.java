package com.pierux.ejerciciorestaurante.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.pierux.ejerciciorestaurante.model.ItemsCard;
import com.pierux.ejerciciorestaurante.R;
import com.pierux.ejerciciorestaurante.data.DAORestaurante;
import com.pierux.ejerciciorestaurante.ui.activities.DetalleActivity;
import com.pierux.ejerciciorestaurante.ui.activities.FormularioActivity;

import java.util.List;

public class RestauranteAdapter extends RecyclerView.Adapter<RestauranteAdapter.ViewHolder> {

    private Context context;
    private List<ItemsCard> restuaranteData;
    private DAORestaurante dao;

    public RestauranteAdapter(Context context, List<ItemsCard> mData) {
        this.context = context;
        this.restuaranteData = mData;
        dao = new DAORestaurante(context);
    }

    /**
     *  Método que infla el molde de la tarjeta y hace que el xml se convierta en objeto vista.
     * @param parent Vista padre donde se insertará la tarjeta(RecyclerView).
     * @param viewType Tipo de vista.
     * @return Devuelve el molde de la tarjeta.
     */

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_element, parent, false);
        return new ViewHolder(view);
    }

    /**
     *  Método que rellena los datos de cada tarjeta con la información del restaurante
     * @param holder  Molde de la tarjeta a rellenar.
     * @param position Indica la posición de la lista de restaurantes.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(restuaranteData.get(position));
    }

    /**
     * Método que devuelve el numero de elementos (restaurantes) que hay.
     * @return numero de elementos
     */
    @Override
    public int getItemCount() {
        return restuaranteData.size();
    }

    /**
     * Método que elimina una tarjeta del adaptador, notifica a recyclerView para que
     * actualice la vista y los indices de los elementos
     * @param position
     */
    public void eliminarItem(int position) {
        restuaranteData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, restuaranteData.size());
    }


    /**
     * Molde para cada tarjeta de restaurante.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, telefono, direccion, tvPuntuacion;
        ImageView img;
        RatingBar ratingBar;
        ImageButton btnEditar, btnEliminar;

        /**
         * Constructor del molde de la tarjeta
         * @param itemView Vista de la tarjeta.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgVCardView);
            nombre = itemView.findViewById(R.id.tvNombreCard);
            telefono = itemView.findViewById(R.id.tvTelefonoCard);
            direccion = itemView.findViewById(R.id.tvDireccionCard);
            ratingBar = itemView.findViewById(R.id.ratingBarCard);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);

            btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Posicion segura al pulsar
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        int restauranteId = restuaranteData.get(position).getId();

                        // Pasamos todos los datos a FormularioActivity desde el id del restaurante
                        Intent intent = new Intent(context, FormularioActivity.class);
                        intent.putExtra("idRestaurante", restauranteId);
                        context.startActivity(intent);

                    }

                }
            });

            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        //Accion para confirmar eliminación
                        DialogInterface.OnClickListener accion = new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int idEliminar = restuaranteData.get(position).getId();
                                dao.eliminarRestaurante(idEliminar);
                                eliminarItem(position);
                                Toast.makeText(context, "Restaurante eliminado", Toast.LENGTH_LONG).show();

                            }
                        };

                        //Creamos la Alerta para confirmar eliminación
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmar eliminación");
                        builder.setMessage("¿Estás seguro de que quieres eliminar '" + restuaranteData.get(position).getNombre() + "'?");
                        builder.setIcon(R.drawable.baseline_delete_24);
                        builder.setPositiveButton("Sí, eliminar", accion);
                        builder.setNegativeButton("No", null);

                        //Creamos y mostramos el diálogo.
                        AlertDialog mostrarAlerta = builder.create();
                        mostrarAlerta.show();

                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        int restauranteId = restuaranteData.get(position).getId();
                        Intent intent = new Intent(context, DetalleActivity.class);

                        intent.putExtra("RESTAURANTE_ID", restauranteId);
                        context.startActivity(intent);
                    }
                }
            });
        }


        /**
         * Rellena los datos de la tarjeta con la información del restaurante.
         * @param item Objeto con la información del restaurante.
         */
        public void bindData(ItemsCard item) {
            nombre.setText(item.getNombre());
            telefono.setText(item.getTelefono());
            direccion.setText(item.getDireccion());
            img.setImageResource(item.getImagenResId());
            ratingBar.setRating(item.getPuntuacion());
            ratingBar.setIsIndicator(true);
        }
    }
}

