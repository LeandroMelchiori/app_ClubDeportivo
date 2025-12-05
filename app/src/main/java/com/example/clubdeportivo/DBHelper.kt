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

    // ----------------------------------- Creacion DB -----------------------------------------
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.execSQL("PRAGMA foreign_keys=ON")
        db.setForeignKeyConstraintsEnabled(true)
    }
    // Crear tablas
    override fun onCreate(db: SQLiteDatabase) {
        // Create TABLES
        db.execSQL("""
            CREATE TABLE actividades (
              id_actividad INTEGER PRIMARY KEY AUTOINCREMENT,
              nombre TEXT NOT NULL,
              precio NUMERIC NOT NULL
            );
            """.trimIndent())
        db.execSQL("""
            CREATE TABLE clientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE,
                fecha_nac TEXT NOT NULL,
                telefono TEXT NOT NULL,
                direccion TEXT NOT NULL,
                fecha_inscripcion TEXT NOT NULL,
                ficha_medica BOOLEAN NOT NULL DEFAULT 1,
                email TEXT NOT NULL UNIQUE,
                esSocio BOOLEAN NOT NULL DEFAULT 0,
                activo BOOLEAN NOT NULL DEFAULT 1,
                carnet BOOLEAN NOT NULL DEFAULT 0
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
              activo INTEGER NOT NULL DEFAULT 0,
              titulo TEXT
            );
            """.trimIndent())
        db.execSQL("""
            CREATE TABLE cuotas (
              idCuota INTEGER PRIMARY KEY AUTOINCREMENT,
              idCliente INTEGER NOT NULL,
              monto NUMERIC,
              fechaPago TEXT NOT NULL,
              formaPago TEXT,
              estadoDelPago INTEGER NOT NULL,
              fechaVencimiento TEXT NOT NULL,
              FOREIGN KEY (idCliente) REFERENCES clientes(id)
            );
            """.trimIndent())
        db.execSQL("""
            CREATE TABLE pagos_actividad (
            id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
            idCliente INTEGER NOT NULL,              -- INTEGER
            id_dia_horario INTEGER NOT NULL,        -- mejor nombre
            fecha_pago TEXT NOT NULL,
            forma_pago TEXT NOT NULL,
            monto NUMERIC NOT NULL,
            FOREIGN KEY (idCliente) REFERENCES clientes(id),
            FOREIGN KEY (id_dia_horario) REFERENCES dias_horarios(id)
            );
            """.trimIndent())
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS actividad_profesor (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              actividad_id INTEGER NOT NULL,
              profesor_dni TEXT NOT NULL,
              activo INTEGER NOT NULL DEFAULT 1,
              motivo_baja TEXT,
              fecha_baja TEXT,
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
              activo INTEGER NOT NULL DEFAULT 1,
              motivo_baja TEXT,
              fecha_baja TEXT,
              FOREIGN KEY(actividad_profesor_id) REFERENCES actividad_profesor(id) ON DELETE CASCADE,
              UNIQUE(actividad_profesor_id, dia, hora_inicio, hora_fin));
            """.trimIndent())
        db.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS ux_dh_unico_activo
            ON dias_horarios(actividad_profesor_id, dia, hora_inicio, hora_fin)
            WHERE activo = 1;
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
            CREATE INDEX IF NOT EXISTS idx_clientes_dni
            ON clientes(dni);
        """.trimIndent())
        db.execSQL("""
            CREATE INDEX IF NOT EXISTS idx_cuotas_idCliente_venc
            ON cuotas(idCliente, fechaVencimiento);
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

        // ----------------------------------- Carga inicial DB -----------------------------------------
        db.beginTransaction()
        try {
            // --------- ACTIVIDADES ---------
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

            // --------- PROFESORES ---------
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
            // --------- CLIENTES (SOCIOS) ---------
            db.execSQL("""
                INSERT OR IGNORE INTO clientes
                (id, nombre, apellido, dni, fecha_nac, telefono, direccion, fecha_inscripcion, ficha_medica, email, activo, carnet, esSocio) VALUES
                (1,'Pablo','Álvarez','40111111','1993-02-15','3415557001','San Luis 101, Rosario',date('now','-4 months'),1,'p.alvarez@club.com',1,1,1),
                (2,'Mariana','Cabral','40222222','1991-07-09','3415557002','Santiago 220, Rosario',date('now','-2 months'),1,'m.cabral@club.com',1,1,1),
                (3,'Diego','Ortiz','40333333','1989-11-20','3415557003','Pellegrini 1500, Rosario',date('now','-6 months'),1,'d.ortiz@club.com',1,1,1),
                (4,'Lucía','Funes','40444444','1995-03-03','3415557004','Riobamba 800, Rosario',date('now','-8 months'),1,'l.funes@club.com',1,1,1),
                (6,'Carla','Vega','40666666','1992-12-12','3415557006','Mitre 200, Rosario',date('now','-3 months'),1,'c.vega@club.com',1,1,1),
                (7,'Sofía','Ramos','40777777','1990-09-17','3415557007','Salta 900, Rosario',date('now','-10 months'),1,'s.ramos@club.com',1,1,1),
                (8,'Hernán','Molina','40888888','1994-01-30','3415557008','España 1200, Rosario',date('now','-1 months'),1,'h.molina@club.com',1,1,1);
                """.trimIndent())

            // --------- CLIENTES (NO SOCIOS) ---------
            db.execSQL("""
                INSERT OR IGNORE INTO clientes
                (nombre, apellido, dni, fecha_nac, telefono, email, direccion, fecha_inscripcion, ficha_medica, activo, carnet, esSocio) VALUES
                ('Carlos','Ruiz','33111222','1999-05-10','3416000001','carlos.ruiz@gmail.com','Mitre 120, Rosario','2025-03-01',1,1,0,0),
                ('Ana','Martínez','30999888','2001-11-23','3416000002','ana.martinez@gmail.com','Belgrano 450, Rosario','2025-03-02',1,1,0,0),
                ('Matías','Ojeda','28123456','1995-08-14','3416000003','matias.ojeda@gmail.com','Dorrego 980, Rosario','2025-03-03',1,1,0,0),
                ('Camila','Lopez','32123456','2000-02-28','3416000004','camila.lopez@gmail.com','Tucumán 2100, Rosario','2025-03-04',1,1,0,0),
                ('Bruno','Ferreyra','34123456','1998-07-07','3416000005','bruno.ferreyra@gmail.com','Paraguay 300, Rosario','2025-03-05',1,1,0,0),
                ('Valentina','Suárez','35123456','2002-09-19','3416000006','valentina.suarez@gmail.com','Catamarca 750, Rosario','2025-03-06',1,1,0,0),
                ('Ezequiel','Páez','36123456','1997-01-30','3416000007','eze.paez@gmail.com','Urquiza 210, Rosario','2025-03-07',1,1,0,0),
                ('Julieta','Bianchi','37123456','2003-04-22','3416000008','julieta.bianchi@gmail.com','Salta 1750, Rosario','2025-03-08',1,1,0,0);
                 """.trimIndent())

            // --------- CUOTAS ---------
            // Cliente 1: AL DÍA (última cuota paga hoy, vence el mes que viene)
            db.execSQL("""
                INSERT OR IGNORE INTO cuotas (idCliente, monto, fechaPago, formaPago, estadoDelPago, fechaVencimiento) VALUES
                (1, 30000, date('now','-2 months'), 'Efectivo', 1, date('now','-1 months')),
                (1, 30000, date('now','-1 months'), 'Efectivo', 1, date('now')),
                (1, 30000, date('now'),            'Efectivo', 1, date('now','+1 months'));
                """.trimIndent())

            // Cliente 2: POR VENCER (faltan < 7 días)
            db.execSQL("""
                INSERT OR IGNORE INTO cuotas (idCliente, monto, fechaPago, formaPago, estadoDelPago, fechaVencimiento) VALUES
                (2, 30000, date('now','-25 days'), 'Transferencia', 1, date('now','+5 days'));
                """.trimIndent())

            // Cliente 3: VENCIDO hace 10 días
            db.execSQL("""
                INSERT OR IGNORE INTO cuotas (idCliente, monto, fechaPago, formaPago, estadoDelPago, fechaVencimiento) VALUES
                (3, 30000, date('now','-40 days'), 'Tarjeta', 1, date('now','-10 days'));
                """.trimIndent())

            // Cliente 4: VENCIDO hace 40 días
            db.execSQL("""
                INSERT OR IGNORE INTO cuotas (idCliente, monto, fechaPago, formaPago, estadoDelPago, fechaVencimiento) VALUES
                (4, 30000, date('now','-70 days'), 'Efectivo', 1, date('now','-40 days'));
                """.trimIndent())

            // Cliente 6: AL DÍA con historial
            db.execSQL("""
                INSERT OR IGNORE INTO cuotas (idCliente, monto, fechaPago, formaPago, estadoDelPago, fechaVencimiento) VALUES
                (6, 30000, date('now','-2 months'), 'Tarjeta', 1, date('now','-1 months')),
                (6, 30000, date('now','-1 months'), 'Tarjeta', 1, date('now'));
                """.trimIndent())

            // Cliente 7: Vencido hace 5 meses
            db.execSQL("""
                INSERT OR IGNORE INTO cuotas (idCliente, monto, fechaPago, formaPago, estadoDelPago, fechaVencimiento) VALUES
                (7, 30000, date('now','-6 months'), 'Efectivo', 1, date('now','-5 months'));
                """.trimIndent())

            // Cliente 8: VENCE HOY
            db.execSQL("""
                INSERT OR IGNORE INTO cuotas (idCliente, monto, fechaPago, formaPago, estadoDelPago, fechaVencimiento) VALUES
                (8, 30000, date('now','-30 days'), 'Transferencia', 1, date('now'));
                """.trimIndent())

            // --------- Actividad_Profesor ---------
            db.execSQL("""
                INSERT OR IGNORE INTO actividad_profesor (actividad_id, profesor_dni, activo) VALUES
                (1, '27999888', 1),  -- Fútbol - Diego Sosa
                (2, '20888999', 1),  -- Básquet - Sofía Almada
                (3, '20888999', 1),  -- Vóley  - Sofía Almada
                (4, '22333444', 1),  -- Yoga   - María Giménez
                (5, '23111222', 1),  -- CrossFit - Agustín Rossi
                (6, '20123456', 1),  -- Funcional - Juan Pérez
                (7, '20123456', 1),  -- GAP - Juan Pérez
                (8, '25444777', 1);  -- Natación Adultos - Lucía Benítez
                """.trimIndent())

            // --------- Horarios ---------

            // Lunes (1) – Fútbol y CrossFit
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 1, 1080, 1140   -- 18:00–19:00
                FROM actividad_profesor
                WHERE actividad_id = 1 AND profesor_dni = '27999888';
                """.trimIndent())
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 1, 1140, 1200   -- 19:00–20:00
                FROM actividad_profesor
                WHERE actividad_id = 5 AND profesor_dni = '23111222';
                """.trimIndent())

            // Martes (2) – Básquet y Vóley
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 2, 1080, 1170   -- 18:00–19:30
                FROM actividad_profesor
                WHERE actividad_id = 2 AND profesor_dni = '20888999';
                """.trimIndent())
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 2, 1170, 1260   -- 19:30–21:00
                FROM actividad_profesor
                WHERE actividad_id = 3 AND profesor_dni = '20888999';
                """.trimIndent())

            // Miércoles (3) – Yoga mañana, Funcional tarde
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 3, 540, 600     -- 09:00–10:00
                FROM actividad_profesor
                WHERE actividad_id = 4 AND profesor_dni = '22333444';
                """.trimIndent())
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 3, 1080, 1140   -- 18:00–19:00
                FROM actividad_profesor
                WHERE actividad_id = 6 AND profesor_dni = '20123456';
                """.trimIndent())

            // Jueves (4) – GAP y Natación
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 4, 1020, 1080   -- 17:00–18:00
                FROM actividad_profesor
                WHERE actividad_id = 7 AND profesor_dni = '20123456';
                """.trimIndent())
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 4, 1080, 1140   -- 18:00–19:00
                FROM actividad_profesor
                WHERE actividad_id = 8 AND profesor_dni = '25444777';
                """.trimIndent())

            // Viernes (5) – Fútbol y CrossFit
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 5, 1080, 1170   -- 18:00–19:30
                FROM actividad_profesor
                WHERE actividad_id = 1 AND profesor_dni = '27999888';
                """.trimIndent())
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 5, 1170, 1230   -- 19:30–20:30
                FROM actividad_profesor
                WHERE actividad_id = 5 AND profesor_dni = '23111222';
                """.trimIndent())

            // Sábado (6) – Natación mañana, Yoga tarde
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 6, 600, 660     -- 10:00–11:00
                FROM actividad_profesor
                WHERE actividad_id = 8 AND profesor_dni = '25444777';
                """.trimIndent())
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 6, 1080, 1140   -- 18:00–19:00
                FROM actividad_profesor
                WHERE actividad_id = 4 AND profesor_dni = '22333444';
                """.trimIndent())
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 0, 600, 660     -- 10:00–11:00
                FROM actividad_profesor
                WHERE actividad_id = 6 AND profesor_dni = '20123456';
                """.trimIndent())
            db.execSQL("""
                INSERT OR IGNORE INTO dias_horarios (actividad_profesor_id, dia, hora_inicio, hora_fin)
                SELECT id, 0, 1080, 1140   -- 18:00–19:00
                FROM actividad_profesor
                WHERE actividad_id = 3 AND profesor_dni = '20888999';
                """.trimIndent())

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
    // Actualizar tablas
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS pagos_actividad")
        db.execSQL("DROP TABLE IF EXISTS cuotas")
        db.execSQL("DROP TABLE IF EXISTS dias_horarios")
        db.execSQL("DROP TABLE IF EXISTS actividad_profesor")
        db.execSQL("DROP TABLE IF EXISTS profesores")
        db.execSQL("DROP TABLE IF EXISTS clientes")
        db.execSQL("DROP TABLE IF EXISTS actividades")
        onCreate(db)
    }

    // ----------------------------------------- READ -------------------------------------------

    // Listados
    fun obtenerNoSocios(): List<NoSocioCard> {
        val lista = mutableListOf<NoSocioCard>()
        val db = readableDatabase
        val sql = """
    WITH ultimos_pagos AS (
        SELECT
            p.idCliente,
            MAX(p.fecha_pago) AS ultima_fecha
        FROM pagos_actividad p
        GROUP BY p.idCliente
    )
    SELECT
        c.nombre,
        c.apellido,
        c.dni,
        up.ultima_fecha AS ultima_pago,
        a.nombre AS actividad_pagada
    FROM clientes c
    LEFT JOIN ultimos_pagos up
           ON up.idCliente = c.id
    LEFT JOIN pagos_actividad p
           ON p.idCliente = up.idCliente
          AND p.fecha_pago = up.ultima_fecha
    LEFT JOIN dias_horarios dh
           ON dh.id = p.id_dia_horario
    LEFT JOIN actividad_profesor ap
           ON ap.id = dh.actividad_profesor_id
    LEFT JOIN actividades a
           ON a.id_actividad = ap.actividad_id
    WHERE c.esSocio = 0
      AND c.activo = 1
    ORDER BY c.apellido, c.nombre;
""".trimIndent()
        val c = db.rawQuery(sql, null)
        if (c.moveToFirst()) {
            do {
                val nombre = c.getString(0)
                val apellido = c.getString(1)
                val dni = c.getString(2)
                val ultimoPago = if (!c.isNull(3)) c.getString(3) else null
                val nombreAct = if (!c.isNull(3)) c.getString(4) else null
                lista.add(NoSocioCard(nombre, apellido, dni, ultimoPago, nombreAct))
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
        FROM clientes s
        LEFT JOIN cuotas c ON c.idCliente = s.id
        WHERE s.activo = 1 AND esSocio = 1
        GROUP BY s.id, s.nombre, s.apellido, s.dni
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
                SELECT s.nombre,
                       s.apellido,
                       s.dni,
                       c.fechaVencimiento,
                       (SELECT MAX(c2.fechaPago)
                        FROM cuotas c2
                        WHERE c2.idCliente = s.id) AS ultimo_pago
                FROM cuotas c
                JOIN clientes s ON s.id = c.idCliente
                -- Nos quedamos solo con la ÚLTIMA cuota de cada socio
                JOIN (
                    SELECT idCliente, MAX(fechaVencimiento) AS maxVenc
                    FROM cuotas
                    GROUP BY idCliente
                ) ult ON ult.idCliente = c.idCliente
                     AND ult.maxVenc = c.fechaVencimiento
                -- Vence hoy o ya venció
                WHERE c.fechaVencimiento <= ?
                    AND s.activo = 1
                    AND esSocio = 1
                ORDER BY s.apellido, s.nombre
            """.trimIndent()

            val c = db.rawQuery(sql, arrayOf(fecha))
            if (c.moveToFirst()) {
                do {
                    val nombre  = c.getString(0)
                    val apellido = c.getString(1)
                    val dni      = c.getString(2)
                    val fv       = c.getString(3)   // fechaVencimiento (YYYY-MM-DD)
                    val ultimo   = if (!c.isNull(4)) c.getString(4) else null

                    lista.add(VencimientoCard(nombre, apellido, dni, fv, ultimo))
                } while (c.moveToNext())
            }
            c.close()
            db.close()
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
         WHERE dh.activo = 1
           AND dh.dia = ?
        ORDER BY dh.hora_inicio
    """.trimIndent()

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
    fun obtenerActividadesPorHorario(): List<ActividadCard> {
        val db = readableDatabase
        val sql = """
        SELECT
            a.id_actividad                  AS id_actividad,
            dh.id                           AS dh_id,
            a.nombre                        AS nombre,
            a.precio                        AS precio,
            (p.apellido || ' ' || p.nombre) AS profesor,
            dh.dia                          AS dia,
            dh.hora_inicio                  AS hora_inicio,
            dh.hora_fin                     AS hora_fin
        FROM dias_horarios dh
        JOIN actividad_profesor ap ON ap.id = dh.actividad_profesor_id
        JOIN actividades a         ON a.id_actividad = ap.actividad_id
        JOIN profesores p          ON p.dni = ap.profesor_dni
        WHERE dh.activo = 1 AND (ap.activo IS NULL OR ap.activo = 1)
        ORDER BY a.nombre, profesor, dh.dia, dh.hora_inicio
    """.trimIndent()

        val lista = mutableListOf<ActividadCard>()
        db.rawQuery(sql, null).use { c ->
            val idxAct   = c.getColumnIndexOrThrow("id_actividad")
            val idxDh    = c.getColumnIndexOrThrow("dh_id")
            val idxNom   = c.getColumnIndexOrThrow("nombre")
            val idxPrec  = c.getColumnIndexOrThrow("precio")
            val idxProf  = c.getColumnIndexOrThrow("profesor")
            val idxDia   = c.getColumnIndexOrThrow("dia")
            val idxIni   = c.getColumnIndexOrThrow("hora_inicio")
            val idxFin   = c.getColumnIndexOrThrow("hora_fin")

            while (c.moveToNext()) {
                val dia = c.getInt(idxDia)
                val ini = c.getInt(idxIni)
                val fin = c.getInt(idxFin)
                lista += ActividadCard(
                    idActividad    = c.getInt(idxAct),
                    idDiaHorario   = c.getInt(idxDh),
                    nombre         = c.getString(idxNom),
                    precio         = c.getDouble(idxPrec),
                    profesor       = c.getString(idxProf),
                    dia            = dia,
                    horaInicio     = ini,
                    horaFin        = fin,
                    etiquetaHorario= "${etiquetaDia(dia)} ${hhmm(ini)}-${hhmm(fin)}"
                )
            }
        }
        return lista
    }
    fun obtenerResumenPagosMes(anio: Int, mes: Int): ResumenPagosMes {
            val db = readableDatabase
            val anioStr = anio.toString()
            val mesStr = String.format("%02d", mes) // "01", "02", ..., "12"

            // ----- Cuotas de socios -----
            var cantSocios = 0
            var montoCuotas = 0.0
            db.rawQuery("""
                SELECT COUNT(DISTINCT idCliente) AS cant, IFNULL(SUM(monto),0) AS total
                FROM cuotas
                WHERE strftime('%Y', fechaPago) = ? 
                  AND strftime('%m', fechaPago) = ?
                """.trimIndent(),
                arrayOf(anioStr, mesStr)
            ).use { c ->
                if (c.moveToFirst()) {
                    cantSocios = c.getInt(0)
                    montoCuotas = c.getDouble(1)
                }
            }

            // ----- Pagos de actividades de NO socios -----
            var cantNoSocios = 0
            var montoActividades = 0.0
            db.rawQuery(
                """
                SELECT COUNT(DISTINCT idCliente) AS cant, IFNULL(SUM(monto),0) AS total
                FROM pagos_actividad
                WHERE strftime('%Y', fecha_pago) = ? 
                  AND strftime('%m', fecha_pago) = ?
                """.trimIndent(),
                arrayOf(anioStr, mesStr)
            ).use { c ->
                if (c.moveToFirst()) {
                    cantNoSocios = c.getInt(0)
                    montoActividades = c.getDouble(1)
                }
            }
            val totalClientes = cantSocios + cantNoSocios
            val ingresosTotales = montoCuotas + montoActividades

            return ResumenPagosMes(
                anio = anio,
                mes = mes,
                cantNoSocios = cantNoSocios,
                cantSocios = cantSocios,
                totalClientes = totalClientes,
                montoCuotas = montoCuotas,
                montoActividades = montoActividades,
                ingresosTotales = ingresosTotales
            )
        }

    // Busquedas
    fun buscarPersonaPorDni(dni: String): PersonaDTO? {
        val db = this.readableDatabase
        db.query(
            "clientes",
            null,
            "dni = ? AND activo = 1",
            arrayOf(dni),
            null, null, null
        ).use { c ->
            if (c.moveToFirst()) {
                return PersonaDTO(
                    id = c.getInt(c.getColumnIndexOrThrow("id")),
                    dni = c.getStringOrNull("dni") ?: dni,
                    nombre = c.getStringOrNull("nombre"),
                    apellido = c.getStringOrNull("apellido"),
                    telefono = c.getStringOrNull("telefono"),
                    direccion = c.getStringOrNull("direccion"),
                    email = c.getStringOrNull("email"),
                    fecha_nac = c.getStringOrNull("fecha_nac"),
                    fichaMedica = c.getStringOrNull("ficha_medica"),
                    esSocio = c.getInt(c.getColumnIndexOrThrow("esSocio")) == 1
                )
            }
        }
        return null
    }
    fun buscarActividadesPorNombre(texto: String): List<ActividadCard> {
        val db = readableDatabase
        val like = "%${texto.trim()}%"
        val sql = """
        SELECT
            a.id_actividad                  AS id_actividad,
            dh.id                           AS dh_id,
            a.nombre                        AS nombre,
            a.precio                        AS precio,
            (p.apellido || ' ' || p.nombre) AS profesor,
            dh.dia                          AS dia,
            dh.hora_inicio                  AS hora_inicio,
            dh.hora_fin                     AS hora_fin
        FROM dias_horarios dh
        JOIN actividad_profesor ap ON ap.id = dh.actividad_profesor_id
        JOIN actividades a         ON a.id_actividad = ap.actividad_id
        JOIN profesores p          ON p.dni = ap.profesor_dni
        WHERE dh.activo = 1
          AND a.nombre LIKE ?
        ORDER BY a.nombre, profesor, dh.dia, dh.hora_inicio
    """.trimIndent()

        val lista = mutableListOf<ActividadCard>()
        db.rawQuery(sql, arrayOf(like)).use { c ->
            val idxAct  = c.getColumnIndexOrThrow("id_actividad")
            val idxDh   = c.getColumnIndexOrThrow("dh_id")
            val idxNom  = c.getColumnIndexOrThrow("nombre")
            val idxPrec = c.getColumnIndexOrThrow("precio")
            val idxProf = c.getColumnIndexOrThrow("profesor")
            val idxDia  = c.getColumnIndexOrThrow("dia")
            val idxIni  = c.getColumnIndexOrThrow("hora_inicio")
            val idxFin  = c.getColumnIndexOrThrow("hora_fin")

            while (c.moveToNext()) {
                val dia = c.getInt(idxDia)
                val ini = c.getInt(idxIni)
                val fin = c.getInt(idxFin)
                lista += ActividadCard(
                    idActividad    = c.getInt(idxAct),
                    idDiaHorario   = c.getInt(idxDh),
                    nombre         = c.getString(idxNom),
                    precio         = c.getDouble(idxPrec),
                    profesor       = c.getString(idxProf),
                    dia            = dia,
                    horaInicio     = ini,
                    horaFin        = fin,
                    etiquetaHorario= "${etiquetaDia(dia)} ${hhmm(ini)}-${hhmm(fin)}"
                )
            }
        }
        return lista
    }

    // ----------------------------------------- CREATE -----------------------------------------
    fun hacerSocioDesdeNoSocio(
        dni: Int,
        monto: Double,
        formaPago: String,
        fechaPago: String // "YYYY-MM-DD"
    ): Int? {

        val db = writableDatabase
        db.beginTransaction()

        try {
            // 1) Traer cliente por DNI
            val cliente = buscarPersonaPorDni(dni.toString())
                ?: throw IllegalArgumentException("No existe un cliente con ese DNI")

            // Si ya es socio, no corresponde hacer el alta
            if (cliente.esSocio) {
                throw IllegalStateException("El cliente ya es socio")
            }

            // 2) Actualizar tabla clientes: pasar a socio
            val cvUpdate = ContentValues().apply {
                put("esSocio", 1)
                put("activo", 1)
                put("carnet", 1)
            }

            db.update(
                "clientes",
                cvUpdate,
                "dni = ?",
                arrayOf(dni.toString())
            )

            val idCliente = cliente.id  // tu clase Persona debería tener este id

            // 3) Registrar cuota inicial pagada
            val fechaVenc = LocalDate.parse(fechaPago).plusMonths(1).toString()

            val cvCuota = ContentValues().apply {
                put("idCliente", idCliente)
                put("monto", monto)
                put("fechaPago", fechaPago)
                put("formaPago", formaPago)
                put("estadoDelPago", 1)  // pagado
                put("fechaVencimiento", fechaVenc)
            }
            db.insertOrThrow("cuotas", null, cvCuota)
            db.setTransactionSuccessful()
            return idCliente
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
        horaFin: Int,
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
            var apActivo = 1
            db.rawQuery(
                "SELECT id, COALESCE(activo,1) AS activo FROM actividad_profesor WHERE actividad_id=? AND profesor_dni=?",
                arrayOf(actividadId.toString(), profesorDni)
            ).use { c ->
                if (c.moveToFirst()) {
                    apId = c.getLong(0)
                    apActivo = c.getInt(1)
                }
            }

            // Si existe, y la columnaactivo esta en 0, convertir a 1
            if (apId != -1L) {
                // Existe: si estaba inactiva, reactivarla (y limpiar bajas si las tenés)
                if ( apActivo ==0) {
                    val cv = ContentValues().apply {
                        put("activo", 1)
                        putNull("fecha_baja")
                        putNull("motivo_baja")
                    }
                    db.update("actividad_profesor", cv, "id=?", arrayOf(apId.toString()))
                }
            }

            // Si no existe, crearlo
            if (apId == -1L) {
                val cvRel = ContentValues().apply {
                    put("actividad_id", actividadId)
                    put("profesor_dni", profesorDni)
                    put("activo", 1)
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

            // Insertar día/horario
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
    fun registrarPagoCuota(
        dni: String,
        monto: Double,
        formaPago: String,
        ultimoPago: String,
        fechaPago: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    ): Long {
        val db = writableDatabase
        val fechaVenc = LocalDate.parse(ultimoPago).plusMonths(1).toString()
        val s = buscarPersonaPorDni(dni)
        val cv = ContentValues().apply {
            put("idCliente", s!!.id)
            put("monto", monto)
            put("fechaPago", fechaPago)
            put("formaPago", formaPago)
            put("estadoDelPago", 1)
            put("fechaVencimiento", fechaVenc)
        }
        return db.insert("cuotas", null, cv)
    }
    fun registrarPagoActividadNoSocio(
        idCliente: Int,      // ← mejor como Int, es FK a clientes.id
        horarioId: Int,      // ← FK a dias_horarios.id
        monto: Double,
        medioPago: String,
        fechaIso: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    ): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put("idCliente", idCliente)
            put("id_dia_horario", horarioId)
            put("monto", monto)
            put("forma_pago", medioPago)
            put("fecha_pago", fechaIso)
        }
        return db.insert("pagos_actividad", null, cv)
    }


    // ----------------------------------------- Delete -----------------------------------------
    // Borrado logico del padrón para evitar conflicto con tabla de pagos
    fun darDeBajaHorario(dhId: Int, motivo: String? = null): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            // Traer apId por si luego marcamos la relación inactiva
            val apId = android.database.DatabaseUtils.longForQuery(
                db, "SELECT actividad_profesor_id FROM dias_horarios WHERE id=?",
                arrayOf(dhId.toString())
            )

            val hoy = java.time.LocalDate.now().toString() // "YYYY-MM-DD"
            val cv = ContentValues().apply {
                put("activo", 0)
                put("fecha_baja", hoy)
                if (motivo != null) put("motivo_baja", motivo)
            }

            val rows = db.update("dias_horarios", cv, "id=?", arrayOf(dhId.toString()))
            if (rows == 0) return false

            // Si esa relación ya no tiene horarios activos, marcamos la relación como inactiva (no se borra)
            val quedanActivos = android.database.DatabaseUtils.longForQuery(
                db,
                "SELECT COUNT(*) FROM dias_horarios WHERE actividad_profesor_id=? AND activo=1",
                arrayOf(apId.toString())
            )
            if (quedanActivos == 0L) {
                val cvAp = ContentValues().apply {
                    put("activo", 0)
                    put("fecha_baja", hoy)
                    if (motivo != null) put("motivo_baja", motivo)
                }
                db.update("actividad_profesor", cvAp, "id=?", arrayOf(apId.toString()))
            }
            db.setTransactionSuccessful()
            true
        } finally { db.endTransaction() }
    }
    fun eliminarPersonaPorId(id: String): Boolean {
            val db = this.writableDatabase
            db.beginTransaction()
            try {
                // Verificar si existe el cliente
                val idCliente = db.rawQuery(
                    "SELECT id FROM clientes WHERE id = ?",
                    arrayOf(id)
                ).use { c ->
                    if (c.moveToFirst()) c.getLong(0) else null
                }

                if (idCliente == null) return false

                // Borrado lógico
                val cv = ContentValues().apply {
                    put("activo", 0)
                    put("carnet", 0)
                    put("esSocio", 0)
                }

                val rows = db.update(
                    "clientes",
                    cv,
                    "id = ?",
                    arrayOf(idCliente.toString())
                )

                db.setTransactionSuccessful()
                return rows > 0

            } finally {
                db.endTransaction()
                db.close()
            }
        }

    // ----------------------------------------- Update -----------------------------------------
    // Horarios
    fun actualizarHorarioPorId(
        idDiaHorario: Int,
        dia: Int,
        horaInicio: Int,
        horaFin: Int,
    ): Boolean {
        val cv = ContentValues().apply {
            put("dia", dia)
            put("hora_inicio", horaInicio)
            put("hora_fin", horaFin)
        }
        val rows = writableDatabase.update("dias_horarios", cv, "id = ?", arrayOf(idDiaHorario.toString()))
        return rows > 0
    }

    // Clientes
    fun actualizarClientePorId(
        id: Int,
        dni: String,
        nombre: String,
        apellido: String,
        fechaNac: String,
        telefono: String?,
        direccion: String?,
        email: String?
    ): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // Opcional: verificar que exista el cliente
            val existe = db.rawQuery(
                "SELECT id FROM clientes WHERE id = ?",
                arrayOf(id.toString())
            ).use { c ->
                c.moveToFirst()
            }

            if (!existe) return false

            val cv = ContentValues().apply {
                put("nombre", nombre)
                put("apellido", apellido)
                put("dni", dni)
                put("fecha_nac", fechaNac)
                put("telefono", telefono)
                put("direccion", direccion)
                put("email", email)
            }

            val rows = db.update(
                "clientes",
                cv,
                "id = ?",
                arrayOf(id.toString())
            )

            db.setTransactionSuccessful()
            return rows > 0
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // ----------------------------------------- Utilidades -----------------------------------------
    // Modelos de datos
    data class NoSocioCard(
        val nombre: String,
        val apellido: String,
        val dni: String,
        val ultimaPago: String?,
        val nombreAct: String?
    )
    data class VencimientoCard(
        val nombre: String,
        val apellido: String,
        val dni: String,
        val fechaVenc: String,
        val ultimoPago: String?
    )
    data class SocioCard(
        val nombre: String,
        val apellido: String,
        val dni: String,
        val ultimoPago: String?
    )
    data class ActividadCard(
            val idActividad: Int,
            val idDiaHorario: Int,   // ← dh.id para eliminar
            val nombre: String,
            val precio: Double,
            val profesor: String,
            val dia: Int,
            val horaInicio: Int,     // en minutos
            val horaFin: Int,        // en minutos
            val etiquetaHorario: String // "Lun 08:00-09:00"
    )
    data class PersonaDTO(
        val id: Int,
        val dni: String,
        val nombre: String?,
        val apellido: String?,
        val telefono: String?,
        val direccion: String?,
        val email: String?,
        val fecha_nac: String?,
        val fichaMedica: String?,
        val esSocio: Boolean,
        )
    data class ResumenPagosMes(
        val anio: Int,
        val mes: Int,
        val cantNoSocios: Int,
        val cantSocios: Int,
        val totalClientes: Int,
        val montoCuotas: Double,
        val montoActividades: Double,
        val ingresosTotales: Double
    )

    // Herramientas
    private fun Cursor.getStringOrNull(col: String): String? {
        val idx = getColumnIndex(col)
        return if (idx >= 0 && !isNull(idx)) getString(idx) else null
    }
    private fun ContentValues.putOrNull(key: String, value: String?) {
        if (value == null) putNull(key) else put(key, value)
    }
    private fun existeConDni(table: String, dni: String): Boolean =
        readableDatabase.query(table, arrayOf("dni"), "dni = ?", arrayOf(dni), null, null, null)
            .use { it.moveToFirst() }
    private fun etiquetaDia(dia: Int) = when (dia) {
        0 -> "Dom"; 1 -> "Lun"; 2 -> "Mar"; 3 -> "Mié"; 4 -> "Jue"; 5 -> "Vie"; else -> "Sáb"
    }
    private fun hhmm(mins: Int) = String.format("%02d:%02d", mins / 60, mins % 60)
}
