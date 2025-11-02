package com.example.clubdeportivo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "app_clubDeportivo.db", null, 2) {

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
              dia            INTEGER NOT NULL CHECK(dia BETWEEN 0 AND 6), -- 0=Dom ... 6=Sáb
              hora_inicio INTEGER NOT NULL,  -- 18:30 -> 18*60+30 = 1110
              hora_fin    INTEGER NOT NULL,
              FOREIGN KEY (id_actividad) REFERENCES actividades(id_actividad) ON DELETE CASCADE,
              CONSTRAINT chk_rango CHECK (hora_fin > hora_inicio),
              CONSTRAINT unq_slot UNIQUE (id_actividad, dia, hora_inicio, hora_fin)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS idx_dh_dia_hora
            ON dias_horarios(dia, hora_inicio);
            """.trimIndent())


        // Opcional: sembrar datos iniciales leyendo un .sql de assets (ver paso 2).
        // seedFromAsset(db, context, "sql/club_deportivo_inserts.sql")
    }

    fun eliminarProducto(nombre: String){
        val db = writableDatabase
        db.delete("prueba", "nombre = ?", arrayOf(nombre))
    }



    // Funcion actividades del dia

    // Modelo para la tarjeta
    data class NoSocioCard(
        val nombre: String,
        val apellido: String,
        val dni: String,
        val ultimaPago: String? // puede ser null si nunca pagó
    )
    data class VencimientoCard(
        val nombre: String,
        val apellido: String,
        val dni: String,
        val fechaVenc: String,
        val ultimoPago: String?   // puede ser null
    )
    data class SocioCard(
        val nombre: String,
        val apellido: String,
        val dni: String,
        val ultimoPago: String?   // puede ser null
    )


    // ------- Querys -------

    // Vencimientos para una fecha (formato "YYYY-MM-DD")
    fun obtenerNoSocios(): List<NoSocioCard> {
        val lista = mutableListOf<NoSocioCard>()
        val db = readableDatabase

        val sql = """
        SELECT n.nombre, n.apellido, n.dni, MAX(p.fecha_pago) AS ultima_pago
        FROM no_socios n
        LEFT JOIN pagos_actividad p ON p.dni_nosocio = n.dni
        GROUP BY n.dni, n.nombre, n.apellido
        ORDER BY n.apellido, n.nombre
    """.trimIndent()

        val c = db.rawQuery(sql, null)
        if (c.moveToFirst()) {
            do {
                val nombre = c.getString(0)
                val apellido = c.getString(1)
                val dni = c.getString(2)
                val ultima = if (!c.isNull(3)) c.getString(3) else null
                lista.add(NoSocioCard(nombre, apellido, dni, ultima))
            } while (c.moveToNext())
        }
        c.close(); db.close()
        return lista
    }
    fun obtenerSocios(): List<SocioCard> {
        val lista = mutableListOf<SocioCard>()
        val db = readableDatabase
        val sql = """
        SELECT s.nombre, s.apellido, s.dni, MAX(c.fechaPago) AS ultimo_pago
        FROM socios s
        LEFT JOIN cuotas c ON c.idSocio = s.idSocio
        GROUP BY s.idSocio, s.nombre, s.apellido, s.dni
        ORDER BY s.apellido, s.nombre
    """.trimIndent()
        val c = db.rawQuery(sql, null)
        if (c.moveToFirst()) {
            do {
                val nombre = c.getString(0)
                val apellido = c.getString(1)
                val dni = c.getString(2)
                val ultimo = if (!c.isNull(3)) c.getString(3) else null
                lista.add(SocioCard(nombre, apellido, dni, ultimo))
            } while (c.moveToNext())
        }
        c.close(); db.close()
        return lista
    }
    fun obtenerVencimientos(fecha: String): List<VencimientoCard> {
        val lista = mutableListOf<VencimientoCard>()
        val db = readableDatabase
        val sql = """
        SELECT s.nombre, s.apellido, s.dni, c.fechaVencimiento,
               (SELECT MAX(c2.fechaPago) FROM cuotas c2 WHERE c2.idSocio = s.idSocio) AS ultimo_pago
        FROM cuotas c
        JOIN socios s ON s.idSocio = c.idSocio
        WHERE c.fechaVencimiento = ?
        ORDER BY s.apellido, s.nombre
    """.trimIndent()
        val c = db.rawQuery(sql, arrayOf(fecha))
        if (c.moveToFirst()) {
            do {
                val nombre = c.getString(0)
                val apellido = c.getString(1)
                val dni = c.getString(2)
                val fv = c.getString(3)
                val ultimo = if (!c.isNull(4)) c.getString(4) else null
                lista.add(VencimientoCard(nombre, apellido, dni, fv, ultimo))
            } while (c.moveToNext())
        }
        c.close(); db.close()
        return lista
    }
    fun obtenerActividadesDelDia(dia: Int): List<InicioActivity.ActividadHoy> {
        val lista = mutableListOf<InicioActivity.ActividadHoy>()
        val db = readableDatabase
        val sql = """
        SELECT a.nombre, a.precio, d.hora_inicio, d.hora_fin
        FROM actividades a
        JOIN dias_horarios d ON a.id_actividad = d.id_actividad
        WHERE d.dia = ?
        ORDER BY d.hora_inicio
    """.trimIndent()

        fun hhmm(mins: Int) = String.format("%02d:%02d", mins / 60, mins % 60)

        val c = db.rawQuery(sql, arrayOf(dia.toString()))
        if (c.moveToFirst()) {
            do {
                val nombre = c.getString(0)
                val precio = c.getDouble(1)
                val hIni = hhmm(c.getInt(2))
                val hFin = hhmm(c.getInt(3))
                lista.add(InicioActivity.ActividadHoy(nombre, hIni, hFin, precio))
            } while (c.moveToNext())
        }
        c.close(); db.close()
        return lista
    }

    // Actualizar tablas
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS dias_horarios")
        db.execSQL("DROP TABLE IF EXISTS actividad_profesores")
        db.execSQL("DROP TABLE IF EXISTS pagos_actividad")
        db.execSQL("DROP TABLE IF EXISTS cuotas")
        db.execSQL("DROP TABLE IF EXISTS profesores")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS no_socios")
        db.execSQL("DROP TABLE IF EXISTS actividades")
        // Vuelve a crear todo
        onCreate(db)
    }


}
