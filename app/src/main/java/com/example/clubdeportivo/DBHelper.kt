package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.deleteDatabase
import android.database.sqlite.SQLiteOpenHelper


// App_Clubdeportivo-Sacha/app/src/main/java/com/example/clubdeportivo/DBHelper.kt
class DBHelper(context: Context) : SQLiteOpenHelper(context, "app_clubDeportivo.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.execSQL("PRAGMA foreign_keys=ON")
    }

    // Crear tablas
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE actividades (
              id_actividad INTEGER PRIMARY KEY AUTOINCREMENT,
              nombre TEXT NOT NULL,
              precio NUMERIC NOT NULL
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE no_socios (
                idNoSocio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE,
                fecha_nac TEXT NOT NULL,
                telefono TEXT NOT NULL,
                direccion TEXT NOT NULL,
                fecha_inscripcion TEXT NOT NULL DEFAULT (date('now')),
                activo           INTEGER NOT NULL DEFAULT 1,     -- ← default 1
                ficha_medica     INTEGER NOT NULL DEFAULT 1,     -- ← default 1
                email TEXT
                );
            """.trimIndent())

        db.execSQL(
            """
            CREATE TABLE socios (
                idSocio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE,
                telefono TEXT NOT NULL,
                direccion TEXT NOT NULL,
                fecha_inscripcion TEXT NOT NULL,
                ficha_medica INTEGER NOT NULL,
                email TEXT,
                activo INTEGER NOT NULL,
                carnet INTEGER NOT NULL
                );
            """.trimIndent())

        db.execSQL("""
            CREATE TABLE profesores (
              dni TEXT PRIMARY KEY,
              nombre TEXT NOT NULL,
              apellido TEXT NOT NULL,
              telefono TEXT,
              direccion TEXT,
              fecha_inscripcion TEXT,
              ficha_medica INTEGER,
              email TEXT,
              activo INTEGER,
              titulo TEXT
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE cuotas (
              idCuota INTEGER PRIMARY KEY AUTOINCREMENT,
              idSocio INTEGER NOT NULL,
              monto NUMERIC,
              fechaPago TEXT NOT NULL,
              formaPago TEXT,
              estadoDelPago INTEGER NOT NULL,
              fechaVencimiento TEXT NOT NULL,
              FOREIGN KEY (idSocio) REFERENCES socios(idSocio)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE pagos_actividad (
              id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
              dni_nosocio TEXT NOT NULL,
              id_actividad INTEGER NOT NULL,
              fecha_pago TEXT NOT NULL,
              forma_pago TEXT NOT NULL,
              monto NUMERIC NOT NULL,
              FOREIGN KEY (dni_nosocio) REFERENCES no_socios(dni),
              FOREIGN KEY (id_actividad) REFERENCES actividades(id_actividad)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE actividad_profesores (
              id_actividad INTEGER NOT NULL,
              dni_profesor TEXT NOT NULL,
              PRIMARY KEY (id_actividad, dni_profesor),
              FOREIGN KEY (id_actividad) REFERENCES actividades(id_actividad) ON DELETE CASCADE,
              FOREIGN KEY (dni_profesor) REFERENCES profesores(dni) ON DELETE CASCADE
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE dias_horarios (
              id_dia_horario INTEGER PRIMARY KEY AUTOINCREMENT,
              id_actividad INTEGER,
              dia TEXT NOT NULL,
              hora_inicio TEXT NOT NULL,
              hora_fin TEXT NOT NULL,
              FOREIGN KEY (id_actividad) REFERENCES actividades(id_actividad) ON DELETE CASCADE
            );
        """.trimIndent())

        // Opcional: sembrar datos iniciales leyendo un .sql de assets (ver paso 2).
        // seedFromAsset(db, context, "sql/club_deportivo_inserts.sql")
    }

    // Eliminar producto
    fun eliminarProducto(nombre: String){
        val db = writableDatabase
        db.delete("prueba", "nombre = ?", arrayOf(nombre))
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

    // Actualizar tablas
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.beginTransaction()
        try {
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}