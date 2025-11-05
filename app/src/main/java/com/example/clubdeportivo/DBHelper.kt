package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate

class DBHelper(context: Context) : SQLiteOpenHelper(context, "app_clubDeportivo.db", null, 3) {

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
                email TEXT NOT NULL UNIQUE,
                direccion TEXT NOT NULL,
                fecha_inscripcion TEXT NOT NULL DEFAULT (date('now')),
                ficha_medica     INTEGER NOT NULL DEFAULT 1,     -- ← default 1
                activo           INTEGER NOT NULL DEFAULT 1     -- ← default 1
                );
            """.trimIndent())

        db.execSQL(
            """
            CREATE TABLE socios (
                idSocio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE,
                fecha_nac TEXT NOT NULL,
                telefono TEXT NOT NULL,
                direccion TEXT NOT NULL,
                fecha_inscripcion TEXT NOT NULL,
                ficha_medica INTEGER NOT NULL,
                email TEXT NOT NULL UNIQUE,
                activo INTEGER NOT NULL,
                carnet INTEGER NOT NULL
                );
            """.trimIndent())

        db.execSQL("""
            CREATE TABLE profesores (
              dni TEXT PRIMARY KEY,
              nombre TEXT NOT NULL,
              apellido TEXT NOT NULL,
              fecha_nac TEXT NOT NULL,
              telefono TEXT NOT NULL,
              direccion TEXT NOT NULL,
              fecha_inscripcion TEXT NOT NULL,
              ficha_medica INTEGER NOT NULL,
              email TEXT NOT NULL,
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

    fun eliminarProducto(nombre: String){
        val db = writableDatabase
        db.delete("prueba", "nombre = ?", arrayOf(nombre))
    }

    // ------- Querys -------
    // Listados
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

    // Data class que representa los datos de un "No Socio"
    // Modelos de datos
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
    data class NoSocioDTO(
        val dni: Int,
        val nombre: String,
        val apellido: String,
        val telefono: String,
        val direccion: String,
        val email: String,
        val fecha_nac: String,
        val fichaMedica: String
    )

    // DTO unificado (hacélo nullable donde tu esquema lo requiera)
    data class PersonaDTO(
        val dni: String,                // usá String para no perder ceros a la izquierda
        val nombre: String?,
        val apellido: String?,
        val telefono: String?,
        val direccion: String?,
        val email: String?,
        val fecha_nac: String?,         // o el tipo que uses
        val fichaMedica: String?,       // puede no existir en socios → null
        val esSocio: Boolean,           // true = vino de "socios", false = "no_socios"

    )

    // Helper para leer columnas opcionales sin crashear si no existen
    private fun Cursor.getStringOrNull(col: String): String? {
        val idx = getColumnIndex(col)
        return if (idx >= 0 && !isNull(idx)) getString(idx) else null
    }

    fun obtenerPersonaPorDni(dni: String): PersonaDTO? {
        val db = this.readableDatabase

        // 1) intentar en "socios"
        db.query(
            "socios",
            null,
            "dni = ?",
            arrayOf(dni), // si en tu tabla está como INTEGER igual funciona; SQLite compara por valor
            null, null, null
        ).use { c ->
            if (c.moveToFirst()) {
                return PersonaDTO(
                    dni          = c.getStringOrNull("dni") ?: dni,
                    nombre       = c.getStringOrNull("nombre"),
                    apellido     = c.getStringOrNull("apellido"),
                    telefono     = c.getStringOrNull("telefono"),
                    direccion    = c.getStringOrNull("direccion"),
                    email        = c.getStringOrNull("email"),
                    fecha_nac    = c.getStringOrNull("fecha_nac"),
                    fichaMedica  = c.getStringOrNull("ficha_medica"), // si no existe → null
                    esSocio      = true,
                )
            }
        }

        // 2) si no está en socios, intentar en "no_socios"
        db.query(
            "no_socios",
            null,
            "dni = ?",
            arrayOf(dni),
            null, null, null
        ).use { c ->
            if (c.moveToFirst()) {
                return PersonaDTO(
                    dni          = c.getStringOrNull("dni") ?: dni,
                    nombre       = c.getStringOrNull("nombre"),
                    apellido     = c.getStringOrNull("apellido"),
                    telefono     = c.getStringOrNull("telefono"),
                    direccion    = c.getStringOrNull("direccion"),
                    email        = c.getStringOrNull("email"),
                    fecha_nac    = c.getStringOrNull("fecha_nac"),
                    fichaMedica  = c.getStringOrNull("ficha_medica"),
                    esSocio      = false,
                )
            }
        }
        return null
    }

    fun obtenerNoSocioPorDni(dni: String): NoSocioDTO? {
        // Obtener la base de datos en modo lectura
        val db = this.readableDatabase

        // Consultar la tabla "no_socios" filtrando por DNI
        val cursor = db.query(
            "no_socios",
            null,                   // Seleccionar todas las columnas
            "dni = ?",              // Cláusula WHERE para el DNI
            arrayOf(dni),           // Argumentos de la cláusula WHERE
            null, null, null        // groupBy, having, orderBy (no aplican aquí)
        )

        var noSocio: NoSocioDTO? = null
        if (cursor.moveToFirst()) {
            // Extraer valores de cada columna del cursor
            val dniValue = cursor.getInt(cursor.getColumnIndexOrThrow("dni"))
            val nombreValue      = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val apellidoValue    = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
            val telefonoValue    = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
            val direccionValue   = cursor.getString(cursor.getColumnIndexOrThrow("direccion"))
            val emailValue       = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val fechaNacValue = cursor.getString(cursor.getColumnIndexOrThrow("fecha_nac"))
            val fichaMedicaValue = cursor.getString(cursor.getColumnIndexOrThrow("ficha_medica"))

            // Crear un objeto NoSocioDTO con los datos obtenidos
            noSocio = NoSocioDTO(
                dni       = dniValue,
                nombre    = nombreValue,
                apellido  = apellidoValue,
                telefono  = telefonoValue,
                direccion = direccionValue,
                email     = emailValue,
                fecha_nac = fechaNacValue,
                fichaMedica = fichaMedicaValue
            )
        }
        // Cerrar el cursor para liberar recursos
        cursor.close()

        return noSocio
    }

    fun hacerSocioDesdeNoSocio(
        dni: String,
        monto: Double,
        formaPago: String,
        fechaPago: String // "YYYY-MM-DD"
    ): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // 1) Traer datos del no socio
            val ns = obtenerNoSocioPorDni(dni) ?: throw IllegalArgumentException("No existe No Socio con ese DNI")

            // 2) Insertar en socios
            val cvSocio = ContentValues().apply {
                put("nombre", ns.nombre)
                put("apellido", ns.apellido)
                put("dni", ns.dni)
                put("fecha_nac", ns.fecha_nac)
                put("telefono", ns.telefono)
                put("direccion", ns.direccion)
                put("fecha_inscripcion", fechaPago) // alta hoy
                put("ficha_medica", ns.fichaMedica)
                put("email", ns.email)
                put("activo", 1)
                put("carnet", 1)  // o 0 si no corresponde
            }
            val idSocio = db.insertOrThrow("socios", null, cvSocio)

            // 3) Registrar cuota inicial pagada
            val fechaVenc = LocalDate.parse(fechaPago).plusMonths(1).toString()
            val cvCuota = ContentValues().apply {
                put("idSocio", idSocio)
                put("monto", monto)
                put("fechaPago", fechaPago)
                put("formaPago", formaPago)
                put("estadoDelPago", 1) // 1=pagado
                put("fechaVencimiento", fechaVenc)
            }
            db.insertOrThrow("cuotas", null, cvCuota)

            // 4) Eliminar del padrón de no socios
            db.delete("no_socios", "dni = ?", arrayOf(dni))

            db.setTransactionSuccessful()
            return idSocio
        } finally {
            db.endTransaction()
            db.close()
        }
    }
}
