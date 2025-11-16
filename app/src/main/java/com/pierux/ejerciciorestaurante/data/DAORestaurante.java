package com.pierux.ejerciciorestaurante.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.pierux.ejerciciorestaurante.model.ItemsCard;
import com.pierux.ejerciciorestaurante.R;

import java.util.ArrayList;
import java.util.List;

public class DAORestaurante {

    private AdminSQLiteOpenHelper admin;

    public DAORestaurante(Context context) {
        admin = new AdminSQLiteOpenHelper(context, "bd_restaurantes", null, 2);
    }

    // Insertar nuevo restaurante
    public long insertarRestaurante(String nombre, String telefono, String direccion, float estrellas, boolean visitado, String categoria, String comentario) {
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("nombre", nombre);
        registro.put("telefono", telefono);
        registro.put("direccion", direccion);
        registro.put("estrellas", estrellas);
        registro.put("visitado", visitado ? 1 : 0);
        registro.put("categoria", categoria);
        registro.put("comentario", comentario);
        long id = db.insert("restaurantes", null, registro);
        db.close();
        return id;
    }

    // Actualizar restaurante existente
    public int actualizarRestaurante(int id, String nombre, String telefono, String direccion, float estrellas, boolean visitado, String categoria, String comentario) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String idText = String.valueOf(id);

        ContentValues registro = new ContentValues();

        registro.put("nombre", nombre);
        registro.put("telefono", telefono);
        registro.put("direccion", direccion);
        registro.put("estrellas", estrellas);
        registro.put("visitado", visitado ? 1 : 0);
        registro.put("categoria", categoria);
        registro.put("comentario", comentario);

        int filas = db.update("restaurantes", registro, "id=" + idText, null);

        db.close();
        return filas;
    }

     private int obtenerImagenPorCategoria(Cursor puntero) {
        String categoria = puntero.getString(puntero.getColumnIndexOrThrow("categoria"));
        return "Bar".equals(categoria) ? R.drawable.bar : R.drawable.img_restuarante1;
    }


    // Obtener restaurante por ID
    public ItemsCard obtenerPorId(int id) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String idText = String.valueOf(id);

        Cursor fila = db.rawQuery("SELECT * FROM restaurantes WHERE id =" + idText, null);

        ItemsCard restaurante = null;

        if (fila.moveToFirst()) {
            int imagenResId =obtenerImagenPorCategoria(fila);

            restaurante = new ItemsCard(
                    fila.getInt(fila.getColumnIndexOrThrow("id")),
                    fila.getString(fila.getColumnIndexOrThrow("nombre")),
                    fila.getString(fila.getColumnIndexOrThrow("telefono")),
                    fila.getString(fila.getColumnIndexOrThrow("direccion")),
                    imagenResId,
                    fila.getFloat(fila.getColumnIndexOrThrow("estrellas")),
                    fila.getInt(fila.getColumnIndexOrThrow("visitado")) == 1,
                    fila.getString(fila.getColumnIndexOrThrow("categoria")),
                    fila.getString(fila.getColumnIndexOrThrow("comentario"))
            );
        }
        fila.close();
        db.close();
        return restaurante;
    }

    // Obtener todos los restaurantes
    public List<ItemsCard> obtenerTodos() {
        List<ItemsCard> lista = new ArrayList<>();
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM restaurantes", null);

        while (cursor.moveToNext()) {
            int imagenResId = obtenerImagenPorCategoria(cursor);

            lista.add(new ItemsCard(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                    cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                    imagenResId,
                    cursor.getFloat(cursor.getColumnIndexOrThrow("estrellas")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("visitado")) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
                    cursor.getString(cursor.getColumnIndexOrThrow("comentario"))
            ));
        }
        cursor.close();
        db.close();
        return lista;
    }

    public int eliminarRestaurante(int id) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String sId = String.valueOf(id);
        int filasAfectadas = db.delete("restaurantes", "id = " + sId, null);

        db.close();
        return filasAfectadas;
    }
}

