package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHelper(context: Context) : SQLiteOpenHelper(context, "prueba.db", null, 1) {

    // Create
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE productos("+
                    "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "nombre TEXT NOT NULL)"
        )
    }

    // Upgrade
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS productos")
        onCreate(db)
    }

    // Insertar producto
    fun insertarProducto(nombre: String){
        val db = writableDatabase
        val values = ContentValues()
        values.put("nombre", nombre)
        db.insert("productos", null, values)
    }

    // Listar productos
    fun obtenerProductos(): List<String> {
        val db = readableDatabase
        val lista = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT * FROM productos", null)
        if(cursor.moveToFirst()){
            do {
                val nombre = cursor.getString(1)
                lista.add(nombre)
            } while (cursor.moveToNext())
        }

        return lista
    }

    // Eliminar producto
    fun eliminarProducto(nombre: String){
        val db = writableDatabase
        db.delete("prueba", "nombre = ?", arrayOf(nombre))
    }
}