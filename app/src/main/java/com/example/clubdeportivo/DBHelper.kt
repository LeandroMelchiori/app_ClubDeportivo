package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class DBHelper(context: Context) : SQLiteOpenHelper(context, "app_clubDeportivo.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.execSQL("PRAGMA foreign_keys=ON")
        db.setForeignKeyConstraintsEnabled(true)
    }

    // ----------------------------------- CRUD -----------------------------------------
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
              FOREIGN KEY (id_actividad) REFERENCES dias_horarios(id)
            );
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS actividad_profesor (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              actividad_id INTEGER NOT NULL,
              profesor_dni TEXT NOT NULL,
              UNIQUE(actividad_id, profesor_dni),
              FOREIGN KEY (actividad_id) REFERENCES actividades(id_actividad) ON DELETE CASCADE,
              FOREIGN KEY (profesor_dni) REFERENCES profesores(dni) ON DELETE CASCADE);
              """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS dias_horarios (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              actividad_profesor_id INTEGER NOT NULL,
              dia INTEGER NOT NULL,            -- 0..6 (Dom..Sáb)
              hora_inicio INTEGER NOT NULL,    -- minutos
              hora_fin INTEGER NOT NULL,       -- minutos
              FOREIGN KEY(actividad_profesor_id) REFERENCES actividad_profesor(id) ON DELETE CASCADE,
              UNIQUE(actividad_profesor_id, dia, hora_inicio, hora_fin));
            """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS idx_ap_act_prof
            ON actividad_profesor(actividad_id, profesor_dni);
            """.trimIndent())

        db.execSQL("""
            CREATE INDEX IF NOT EXISTS idx_dh_apid
            ON dias_horarios(actividad_profesor_id);
            """.trimIndent())

        db.execSQL("""
            CREATE TRIGGER IF NOT EXISTS tr_ap_autoclean
            AFTER DELETE ON dias_horarios
            BEGIN
              DELETE FROM actividad_profesor
              WHERE id = OLD.actividad_profesor_id
                AND NOT EXISTS (
                  SELECT 1 FROM dias_horarios WHERE actividad_profesor_id = OLD.actividad_profesor_id
                );
            END;
            """.trimIndent())


        // CARGA INICIAL
        db.beginTransaction()
        try {
            // ACTIVIDADES
            db.execSQL("""
                INSERT OR IGNORE INTO actividades (nombre, precio) VALUES
                ('Fútbol', 8000.00),
                ('Básquet', 8500.00),
                ('Vóley', 7000.00),
                ('Yoga', 6500.00),
                ('CrossFit', 9500.00),
                ('Funcional', 6000.00),
                ('GAP', 5000.00),
                ('Natación Adultos', 9000.00);
                """.trimIndent())

            // PROFESORES
            db.execSQL("""
                INSERT OR IGNORE INTO profesores
                (dni, nombre, apellido, fecha_nac, telefono, direccion, fecha_inscripcion, ficha_medica, email, activo, titulo) VALUES
                ('20123456','Juan','Pérez','1988-04-12','3415551111','San Martín 123, Rosario','2025-01-10',1,'juan.perez@club.com',1,'Prof. Ed. Física'),
                ('22333444','María','Giménez','1990-09-02','3415552222','Mendoza 456, Rosario','2025-01-15',1,'maria.gimenez@club.com',1,'Instructora de Yoga'),
                ('27999888','Diego','Sosa','1985-07-22','3415553333','Sarmiento 789, Rosario','2025-01-20',1,'diego.sosa@club.com',1,'Entrenador de Fútbol'),
                ('25444777','Lucía','Benítez','1992-03-18','3415554444','Oroño 321, Rosario','2025-01-22',1,'lucia.benitez@club.com',1,'Entrenadora de Natación'),
                ('23111222','Agustín','Rossi','1987-11-05','3415555555','Italia 999, Rosario','2025-01-25',1,'agustin.rossi@club.com',1,'Coach CrossFit'),
                ('20888999','Sofía','Almada','1991-12-01','3415556666','Córdoba 1500, Rosario','2025-02-01',1,'sofia.almada@club.com',1,'Prof. Vóley');
                """.trimIndent()
            )

            // NO_SOCIOS
            db.execSQL("""
                INSERT OR IGNORE INTO no_socios
                (nombre, apellido, dni, fecha_nac, telefono, email, direccion, fecha_inscripcion, ficha_medica, activo) VALUES
                ('Carlos','Ruiz','33111222','1999-05-10','3416000001','carlos.ruiz@gmail.com','Mitre 120, Rosario','2025-03-01',1,1),
                ('Ana','Martínez','30999888','2001-11-23','3416000002','ana.martinez@gmail.com','Belgrano 450, Rosario','2025-03-02',1,1),
                ('Matías','Ojeda','28123456','1995-08-14','3416000003','matias.ojeda@gmail.com','Dorrego 980, Rosario','2025-03-03',1,1),
                ('Camila','Lopez','32123456','2000-02-28','3416000004','camila.lopez@gmail.com','Tucumán 2100, Rosario','2025-03-04',1,1),
                ('Bruno','Ferreyra','34123456','1998-07-07','3416000005','bruno.ferreyra@gmail.com','Paraguay 300, Rosario','2025-03-05',1,1),
                ('Valentina','Suárez','35123456','2002-09-19','3416000006','valentina.suarez@gmail.com','Catamarca 750, Rosario','2025-03-06',1,1),
                ('Ezequiel','Páez','36123456','1997-01-30','3416000007','eze.paez@gmail.com','Urquiza 210, Rosario','2025-03-07',1,1),
                ('Julieta','Bianchi','37123456','2003-04-22','3416000008','julieta.bianchi@gmail.com','Salta 1750, Rosario','2025-03-08',1,1);
                """.trimIndent()
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
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
    fun eliminarActividad(idActividad: Int): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val rows = db.delete(
                "actividades",
                "id_actividad = ?",
                arrayOf(idActividad.toString())
            )
            db.setTransactionSuccessful()
            rows > 0
        } finally {
            db.endTransaction()
        }
    }
    fun eliminarPersonaPorDni(dni: String): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // ¿Es socio?
            var idSocio: Long? = null
            db.rawQuery("SELECT idSocio FROM socios WHERE dni = ?", arrayOf(dni)).use { c ->
                if (c.moveToFirst()) idSocio = c.getLong(0)
            }

            if (idSocio != null) {
                // Si es socio, primero borro dependencias (ej.: cuotas) y luego al socio
                db.delete("cuotas", "idSocio = ?", arrayOf(idSocio.toString()))
                val rows = db.delete("socios", "idSocio = ?", arrayOf(idSocio.toString()))
                db.setTransactionSuccessful()
                return rows > 0
            } else {
                // No socio: borro directo por DNI
                val rows = db.delete("no_socios", "dni = ?", arrayOf(dni))
                db.setTransactionSuccessful()
                return rows > 0
            }
        } finally {
            db.endTransaction()
        }
    }

    // ----------------------------------------------------------------------------------

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
    data class ActividadCard(
        val id: Int,
        val nombre: String,
        val precio: Double,
        val profesores: String?, // puede ser null si no asignaste
        val horarios: String?    // puede ser null si no cargaste horarios
    )

    // Helper para leer columnas opcionales sin crashear si no existen
    private fun Cursor.getStringOrNull(col: String): String? {
        val idx = getColumnIndex(col)
        return if (idx >= 0 && !isNull(idx)) getString(idx) else null
    }

    // ----------------------------------- Querys -----------------------------------

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
        SELECT a.nombre, a.precio, dh.id, dh.dia, dh.hora_inicio, dh.hora_fin
        FROM dias_horarios dh
        JOIN actividad_profesor ap ON ap.id = dh.actividad_profesor_id
        JOIN actividades a         ON a.id_actividad = ap.actividad_id
        WHERE dh.dia = ?
        ORDER BY dh.hora_inicio
    """.trimIndent()

        fun hhmm(mins: Int) = String.format("%02d:%02d", mins / 60, mins % 60)

        db.rawQuery(sql, arrayOf(dia.toString())).use { c ->
            while (c.moveToNext()) {
                val id = c.getInt(2)
                val nombre = c.getString(0)
                val dia = c.getInt(3)
                val precio = c.getDouble(1)
                val hIni = hhmm(c.getInt(4))
                val hFin = hhmm(c.getInt(5))
                lista.add(InicioActivity.ActividadHoy(id, nombre, dia, hIni, hFin, precio))
            }
        }
        return lista
    }
    fun obtenerActividades(): List<ActividadCard> {
        val db = readableDatabase
        val sql = """
         SELECT
            a.id_actividad,
            a.nombre,
            a.precio,
            (SELECT GROUP_CONCAT(p.apellido || ' ' || p.nombre, ' / ')
             FROM actividad_profesor ap2
             JOIN profesores p ON p.dni = ap2.profesor_dni
             WHERE ap2.actividad_id = a.id_actividad
            ) AS profesores,
            (SELECT GROUP_CONCAT(
                    CASE dh.dia
                        WHEN 0 THEN 'Dom' WHEN 1 THEN 'Lun' WHEN 2 THEN 'Mar'
                        WHEN 3 THEN 'Mié' WHEN 4 THEN 'Jue' WHEN 5 THEN 'Vie'
                        WHEN 6 THEN 'Sáb' END || ' ' ||
                    printf('%02d:%02d', dh.hora_inicio/60, dh.hora_inicio%60) || '-' ||
                    printf('%02d:%02d', dh.hora_fin/60, dh.hora_fin%60)
                , ' / ')
             FROM actividad_profesor ap
             JOIN dias_horarios dh ON dh.actividad_profesor_id = ap.id
             WHERE ap.actividad_id = a.id_actividad
            ) AS horarios
        FROM actividades a
        WHERE EXISTS (
            SELECT 1
            FROM actividad_profesor ap
            JOIN dias_horarios dh ON dh.actividad_profesor_id = ap.id
            WHERE ap.actividad_id = a.id_actividad)
        ORDER BY a.nombre
    """.trimIndent()

        val lista = mutableListOf<ActividadCard>()
        db.rawQuery(sql, null).use { c ->
            while (c.moveToNext()) {
                lista += ActividadCard(
                    id = c.getInt(0),
                    nombre = c.getString(1),
                    precio = c.getDouble(2),
                    profesores = if (!c.isNull(3)) c.getString(3) else null,
                    horarios = if (!c.isNull(4)) c.getString(4) else null
                )
            }
        }
        return lista
    }

    // Busquedas
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
    fun buscarActividadesPorNombre(texto: String): List<ActividadCard> {
        val db = readableDatabase
        val like = "%${texto.trim()}%"
        val sql = """
        SELECT 
            a.id_actividad,
            a.nombre,
            a.precio,
            (SELECT GROUP_CONCAT(p.apellido || ' ' || p.nombre, ' / ')
             FROM actividad_profesor ap2
             JOIN profesores p ON p.dni = ap2.profesor_dni
             WHERE ap2.actividad_id = a.id_actividad
            ) AS profesores,
            (SELECT GROUP_CONCAT(
                    CASE dh.dia
                        WHEN 0 THEN 'Dom' WHEN 1 THEN 'Lun' WHEN 2 THEN 'Mar'
                        WHEN 3 THEN 'Mié' WHEN 4 THEN 'Jue' WHEN 5 THEN 'Vie'
                        WHEN 6 THEN 'Sáb' END || ' ' ||
                    printf('%02d:%02d', dh.hora_inicio/60, dh.hora_inicio%60) || '-' ||
                    printf('%02d:%02d', dh.hora_fin/60, dh.hora_fin%60)
                , ' / ')
             FROM actividad_profesor ap
             JOIN dias_horarios dh ON dh.actividad_profesor_id = ap.id
             WHERE ap.actividad_id = a.id_actividad
            ) AS horarios
        FROM actividades a
        WHERE a.nombre LIKE ?
          AND EXISTS (
              SELECT 1
              FROM actividad_profesor ap
              JOIN dias_horarios dh ON dh.actividad_profesor_id = ap.id
              WHERE ap.actividad_id = a.id_actividad
          )
        ORDER BY a.nombre
    """.trimIndent()

        val lista = mutableListOf<ActividadCard>()
        db.rawQuery(sql, arrayOf(like)).use { c ->
            while (c.moveToNext()) {
                lista += ActividadCard(
                    id         = c.getInt(0),
                    nombre     = c.getString(1),
                    precio     = c.getDouble(2),
                    profesores = if (!c.isNull(3)) c.getString(3) else null,
                    horarios   = if (!c.isNull(4)) c.getString(4) else null
                )
            }
        }
        return lista
    }

    // Registros
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

    fun insertarHorario(
        actividadId: Long,
        profesorDni: String,
        dia: Int,
        horaInicio: Int,
        horaFin: Int
    ): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // Verificar que exista el profesor por DNI
            db.rawQuery("SELECT 1 FROM profesores WHERE dni=?", arrayOf(profesorDni)).use { c ->
                if (!c.moveToFirst()) throw IllegalArgumentException("Profesor no encontrado")
            }

            // Obtener o crear la dupla (actividad, profesor)
            var apId = -1L
            db.rawQuery(
                "SELECT id FROM actividad_profesor WHERE actividad_id=? AND profesor_dni=?",
                arrayOf(actividadId.toString(), profesorDni)
            ).use { c -> if (c.moveToFirst()) apId = c.getLong(0) }

            if (apId == -1L) {
                val cvRel = ContentValues().apply {
                    put("actividad_id", actividadId)
                    put("profesor_dni", profesorDni)
                }
                val ins = db.insertWithOnConflict(
                    "actividad_profesor",
                    null,
                    cvRel,
                    SQLiteDatabase.CONFLICT_IGNORE
                )
                apId = if (ins != -1L) ins else db.rawQuery(
                    "SELECT id FROM actividad_profesor WHERE actividad_id=? AND profesor_dni=?",
                    arrayOf(actividadId.toString(), profesorDni)
                )
                    .use { c -> if (c.moveToFirst()) c.getLong(0) else throw IllegalStateException("No se pudo resolver actividad_profesor_id") }
            }

            // Insertar día/horario (en minutos)
            val cvHorario = ContentValues().apply {
                put("actividad_profesor_id", apId)
                put("dia", dia)
                put("hora_inicio", horaInicio)
                put("hora_fin", horaFin)
            }
            val rowId = db.insertWithOnConflict(
                "dias_horarios",
                null,
                cvHorario,
                SQLiteDatabase.CONFLICT_IGNORE
            )
            db.setTransactionSuccessful()
            return rowId
        } finally {
            db.endTransaction()
        }
    }

    fun registrarPagoActividadNoSocio(
        dni: String,
        horarioId: Int,          // FK a dias_horarios (o a tu relación actividad_profesor_horario)
        monto: Double,
        medioPago: String,
        fechaIso: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    ): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("dni_nosocio", dni)
            put("id_actividad", horarioId) // o "actividad_horario_id" según tu esquema
            put("monto", monto)
            put("forma_pago", medioPago)
            put("fecha_pago", fechaIso)
        }
        return db.insert("pagos_actividad", null, cv)
    }
}

