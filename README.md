<div align="center">

# 🏟️ Club Deportivo
### Aplicación Android de gestión para clubes deportivos

![Android](https://img.shields.io/badge/Android-minSDK%2030-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-Local%20DB-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-3-757575?style=for-the-badge&logo=material-design&logoColor=white)

*Sistema de administración integral para la gestión de socios, actividades, horarios y pagos de un club deportivo.*

</div>

---

## 📋 Tabla de contenidos

- [Descripción](#-descripción)
- [Características](#-características)
- [Pantallas de la app](#-pantallas-de-la-app)
- [Arquitectura y tecnologías](#-arquitectura-y-tecnologías)
- [Base de datos](#-base-de-datos)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Configuración e instalación](#-configuración-e-instalación)
- [Credenciales de acceso](#-credenciales-de-acceso)
- [Datos de ejemplo](#-datos-de-ejemplo)
- [Decisiones técnicas destacadas](#-decisiones-técnicas-destacadas)
- [Mejoras futuras](#-mejoras-futuras)

---

## 📖 Descripción

**Club Deportivo** es una aplicación nativa Android desarrollada en **Kotlin** que permite al personal administrativo de un club gestionar de manera centralizada todo lo relacionado con socios, no socios, actividades deportivas, horarios, profesores y pagos, todo almacenado localmente mediante **SQLite**.

La app está pensada para uso interno del staff, con un flujo de trabajo orientado a las operaciones más frecuentes del día a día: cobrar cuotas, registrar asistencia a actividades, controlar vencimientos y obtener reportes mensuales de ingresos.

---

## ✨ Características

### 👤 Gestión de Personas
- Registro de nuevos clientes (socios y no socios)
- Edición y baja lógica de clientes
- Conversión de no-socio a socio con registro automático de primera cuota
- Búsqueda por DNI en tiempo real
- Visualización detallada del perfil del cliente

### 💰 Gestión de Pagos
- Cobro de cuotas mensuales para socios con cálculo automático de vencimiento
- Registro de pagos de actividades para no socios
- Soporte para múltiples formas de pago (Efectivo, Transferencia, Tarjeta)
- Control de cuotas vencidas y por vencer (alerta a 7 días)

### 🏃 Gestión de Actividades
- Catálogo de actividades con precio por clase
- Asignación de profesores a actividades
- Gestión de horarios por día de la semana
- Baja lógica de horarios con propagación automática a la relación actividad-profesor
- Búsqueda de actividades por nombre

### 📊 Reportes
- Resumen mensual de ingresos navegable (mes a mes)
- Listado de socios activos con último pago
- Listado de no socios con última actividad pagada
- Panel de cuotas vencidas diferenciado por estado de mora

### 🏠 Dashboard
- Vista de actividades programadas para el día actual
- Acceso rápido al cobro de cada clase del día
- Indicador de fecha y usuario logueado

---

## 📱 Pantallas de la app

| Pantalla | Descripción |
|---|---|
| **Login** | Autenticación de usuario con validación de credenciales |
| **Inicio** | Dashboard con actividades del día y acceso a nuevo registro |
| **Actividades** | Listado con búsqueda, edición y alta de horarios deportivos |
| **Nuevo Horario** | Formulario para asignar actividad, profesor, día y horario |
| **Listados** | Tres vistas en una: socios, no socios y cuotas vencidas |
| **Ver Más** | Perfil completo del cliente con opciones de editar y eliminar |
| **Editar Usuario** | Formulario de actualización de datos personales |
| **Pago de Cuota** | Formulario para cobrar cuota mensual o hacer socio a un no-socio |
| **Pago de Actividad** | Registro de pago de clase de un no socio mediante búsqueda por DNI |
| **Resumen Mensual** | Reporte de ingresos navegable mes a mes |
| **Configuración** | Gestión de cuenta y cierre de sesión |

---

## 🏗️ Arquitectura y tecnologías

### Stack técnico

```
Lenguaje       →  Kotlin
UI             →  XML Layouts + Material Design 3
Base de datos  →  SQLite (via SQLiteOpenHelper)
Navegación     →  Intents explícitos + BottomNavigationView
Mínimo SDK     →  API 30 (Android 11)
Target SDK     →  API 36
```

### Patrón arquitectónico

La app sigue un patrón **Activity-centric** donde cada pantalla es una `Activity` independiente:

```
Activity  ──────────────►  DBHelper  ──────────►  SQLite
   │                          │
   │                     (queries,
AppUtils               transacciones,
(helpers)               data classes)
```

- **`DBHelper`** centraliza toda la lógica de acceso a datos (CRUD + consultas complejas)
- **`AppUtils`** provee utilidades compartidas: navegación tipada (`goTo()`), toasts, diálogos de confirmación, validaciones de inputs y formateo de fechas
- Los **Adapters** (`SocioAdapter`, `NoSocioAdapter`, `VencimientoAdapter`, `ActividadCardAdapter`) conectan datos con `RecyclerView`

### Dependencias

```kotlin
// AndroidX
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.appcompat)
implementation(libs.androidx.constraintlayout)
implementation(libs.androidx.activity)

// Material Design
implementation(libs.material)
```

---

## 🗄️ Base de datos

### Esquema entidad-relación

```
actividades ──────────────── actividad_profesor ────────────── profesores
    (id_actividad)               (actividad_id)                   (dni)
                                  (profesor_dni)
                                       │
                                 dias_horarios
                               (actividad_profesor_id)
                               (dia, hora_inicio, hora_fin)
                                       │
                                pagos_actividad
                               (id_dia_horario) ◄──── clientes
                                                         (id)
                                                          │
                                                        cuotas
                                                      (idCliente)
```

### Tablas

#### `actividades`
| Campo | Tipo | Descripción |
|---|---|---|
| id_actividad | INTEGER PK AUTOINCREMENT | Identificador único |
| nombre | TEXT NOT NULL | Nombre de la actividad |
| precio | NUMERIC NOT NULL | Precio por clase |

#### `clientes`
| Campo | Tipo | Descripción |
|---|---|---|
| id | INTEGER PK AUTOINCREMENT | Identificador único |
| nombre, apellido | TEXT NOT NULL | Datos personales |
| dni | TEXT NOT NULL UNIQUE | Documento (clave de búsqueda) |
| fecha_nac, telefono, direccion, email | TEXT | Datos de contacto |
| fecha_inscripcion | TEXT NOT NULL | Fecha de alta |
| ficha_medica | BOOLEAN DEFAULT 1 | Estado ficha médica |
| esSocio | BOOLEAN DEFAULT 0 | Distingue socios de no-socios |
| activo | BOOLEAN DEFAULT 1 | Baja lógica |
| carnet | BOOLEAN DEFAULT 0 | Si posee carnet |

#### `profesores`
| Campo | Tipo | Descripción |
|---|---|---|
| dni | TEXT PK | DNI como clave primaria |
| nombre, apellido, ... | TEXT | Datos personales |
| titulo | TEXT | Título o certificación |
| activo | INTEGER DEFAULT 0 | Estado del profesor |

#### `cuotas`
| Campo | Tipo | Descripción |
|---|---|---|
| idCuota | INTEGER PK AUTOINCREMENT | Identificador |
| idCliente | INTEGER FK | Referencia al socio |
| monto | NUMERIC | Monto abonado |
| fechaPago | TEXT | Fecha del cobro |
| formaPago | TEXT | Efectivo/Transferencia/Tarjeta |
| estadoDelPago | INTEGER | Estado (1=pagado) |
| fechaVencimiento | TEXT | Calculada como fechaPago + 1 mes |

#### `pagos_actividad`
| Campo | Tipo | Descripción |
|---|---|---|
| id_pago | INTEGER PK | Identificador |
| idCliente | INTEGER FK | Cliente no-socio |
| id_dia_horario | INTEGER FK | Horario específico abonado |
| fecha_pago | TEXT | Fecha del pago |
| forma_pago | TEXT | Medio de pago |
| monto | NUMERIC | Monto abonado |

#### `actividad_profesor` (tabla intermedia con metadatos)
| Campo | Tipo | Descripción |
|---|---|---|
| id | INTEGER PK | Identificador |
| actividad_id | INTEGER FK | Referencia a actividades |
| profesor_dni | TEXT FK | Referencia a profesores |
| activo | INTEGER DEFAULT 1 | Estado activo |
| motivo_baja, fecha_baja | TEXT | Auditoría de bajas |

#### `dias_horarios`
| Campo | Tipo | Descripción |
|---|---|---|
| id | INTEGER PK | Identificador |
| actividad_profesor_id | INTEGER FK | Relación actividad-profesor |
| dia | INTEGER | 0=Dom, 1=Lun, ..., 6=Sáb |
| hora_inicio | INTEGER | Minutos desde medianoche |
| hora_fin | INTEGER | Minutos desde medianoche |
| activo | INTEGER DEFAULT 1 | Baja lógica |
| motivo_baja, fecha_baja | TEXT | Auditoría de bajas |

### Índices y trigger

```sql
-- Índices de performance
CREATE INDEX idx_ap_act_prof ON actividad_profesor(actividad_id, profesor_dni);
CREATE INDEX idx_dh_apid ON dias_horarios(actividad_profesor_id);
CREATE INDEX idx_clientes_dni ON clientes(dni);
CREATE INDEX idx_cuotas_idCliente_venc ON cuotas(idCliente, fechaVencimiento);
CREATE UNIQUE INDEX ux_dh_unico_activo ON dias_horarios(...) WHERE activo = 1;

-- Trigger de limpieza automática
CREATE TRIGGER tr_ap_autoclean
AFTER DELETE ON dias_horarios
BEGIN
  DELETE FROM actividad_profesor
  WHERE id = OLD.actividad_profesor_id
    AND NOT EXISTS (
      SELECT 1 FROM dias_horarios WHERE actividad_profesor_id = OLD.actividad_profesor_id
    );
END;
```

---

## 📁 Estructura del proyecto

```
app/
└── src/
    └── main/
        ├── java/com/example/clubdeportivo/
        │   │
        │   ├── 🗄️  DBHelper.kt               # Toda la lógica de BD (CRUD + queries)
        │   ├── 🛠️  AppUtils.kt               # Helpers compartidos (navegación, validación, UI)
        │   │
        │   ├── 📱  Activities
        │   │   ├── LoginActivity.kt           # Pantalla de autenticación
        │   │   ├── InicioActivity.kt          # Dashboard principal
        │   │   ├── ActividadesActivity.kt     # Gestión de actividades y horarios
        │   │   ├── NuevoHorarioActividadActivity.kt
        │   │   ├── EditarActividadActivity.kt
        │   │   ├── ListadosActivity.kt        # Socios / No socios / Vencimientos
        │   │   ├── VerMasActivity.kt          # Perfil de cliente
        │   │   ├── NuevoUsuarioActivity.kt    # Alta de cliente
        │   │   ├── EditarUsuarioActivity.kt   # Edición de cliente
        │   │   ├── PagoDeCuotaActivity.kt     # Cobro cuota / conversión a socio
        │   │   ├── PagoActividadActivity.kt   # Pago de clase (no socio)
        │   │   ├── ResumenMensualActivity.kt  # Reporte mensual
        │   │   └── ConfiguracionActivity.kt  # Configuración y logout
        │   │
        │   └── 📦  Adapters
        │       ├── ActividadCardAdapter.kt    # Lista de actividades
        │       ├── SocioAdapter.kt            # Lista de socios
        │       ├── NoSocioAdapter.kt          # Lista de no socios
        │       └── VencimientoAdapter.kt      # Lista de cuotas vencidas
        │
        └── res/
            ├── layout/                        # 14 layouts XML
            ├── drawable/                      # Íconos y fondos custom
            ├── values/                        # Colors, strings, themes
            └── menu/
                └── menu_bottom_nav.xml        # Barra de navegación inferior
```

---

## ⚙️ Configuración e instalación

### Requisitos

- Android Studio Hedgehog o superior
- JDK 11
- Android SDK con API 30 o superior

### Pasos

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/app_ClubDeportivo.git
   cd app_ClubDeportivo
   ```

2. **Abrir en Android Studio**
   ```
   File → Open → seleccionar la carpeta raíz del proyecto
   ```

3. **Sincronizar Gradle**
   ```
   Android Studio sincronizará las dependencias automáticamente.
   ```

4. **Ejecutar la app**
   ```
   Seleccionar un emulador (API 30+) o dispositivo físico y presionar ▶ Run
   ```

> **Nota:** La base de datos SQLite se crea automáticamente en el primer lanzamiento con todos los datos de ejemplo precargados.

---

## 🔐 Credenciales de acceso

La app utiliza autenticación hardcodeada para el entorno de desarrollo:

| Usuario | Contraseña |
|---|---|
| `admin` | `admin` |
| `charlie` | `charlie` |
| `sacha` | `sacha` |
| `javo` | `javo` |
| `heber` | `heber` |

> ⚠️ **Importante:** Estas credenciales son solo para desarrollo. En un entorno productivo deberían ser reemplazadas por un sistema de autenticación real con contraseñas hasheadas.

---

## 📦 Datos de ejemplo

Al instalar la app por primera vez, la base de datos se inicializa automáticamente con:

- **8 actividades** deportivas: Fútbol, Básquet, Vóley, Yoga, CrossFit, Funcional, GAP, Natación Adultos
- **6 profesores** con títulos y datos completos
- **7 socios** con historial de cuotas en distintos estados (al día, por vencer, vencido)
- **8 no socios** registrados
- **Horarios** distribuidos de Domingo a Sábado
- **Cuotas** con fechas relativas al día de instalación para reflejar estados reales

### Estados de cuotas precargados

| Cliente | Estado |
|---|---|
| Pablo Álvarez | Al día |
| Mariana Cabral | Por vencer (< 7 días) |
| Diego Ortiz | Vencida hace 10 días |
| Lucía Funes | Vencida hace 40 días |
| Hernán Molina | Vence hoy |

---

## 💡 Decisiones técnicas destacadas

### Horarios en minutos desde medianoche
Los horarios se almacenan como enteros (minutos desde las 00:00) en lugar de strings. Esto simplifica ordenamiento, cálculo de duración y comparaciones.
```kotlin
// Ejemplo: 18:00 → 1080 minutos
private fun hhmm(mins: Int) = String.format("%02d:%02d", mins / 60, mins % 60)
```

### Baja lógica en lugar de DELETE
Clientes, horarios y relaciones actividad-profesor nunca se eliminan físicamente. Se marcan con `activo = 0` para preservar la integridad referencial con el historial de pagos.

### Trigger de autolimpieza
Cuando se da de baja el último horario activo de una relación actividad-profesor, un trigger SQLite marca automáticamente esa relación como inactiva, evitando inconsistencias sin lógica adicional en Kotlin.

### AppUtils como helper centralizado
El método `goTo()` con vararg tipado elimina la repetición de código de navegación en todas las Activities, soportando múltiples tipos de extras de forma type-safe.

```kotlin
utils.goTo(
    PagoActividadActivity::class.java,
    finishCurrent = true,
    "usuario" to usuario,
    "idActividad" to act.id,
    "precio" to act.precio
)
```

### Transacciones para operaciones compuestas
Las operaciones que afectan múltiples tablas (hacerSocio, insertarHorario, darDeBajaHorario) usan transacciones explícitas para garantizar atomicidad.

---

## 🚀 Mejoras futuras

- [ ] **Autenticación segura** — Reemplazar credenciales hardcodeadas por un sistema con hash de contraseñas (bcrypt/Argon2)
- [ ] **Arquitectura MVVM** — Migrar a ViewModel + LiveData/Flow para desacoplar la UI de la lógica de negocio
- [ ] **Room Database** — Reemplazar SQLiteOpenHelper por Room para consultas tipadas y seguridad en tiempo de compilación
- [ ] **Exportación de reportes** — Generar PDFs o CSVs del resumen mensual para compartir o imprimir
- [ ] **Notificaciones** — Alertas automáticas para cuotas próximas a vencer
- [ ] **Múltiples admins** — Sistema de roles y gestión completa de usuarios administradores (ConfiguracionActivity tiene los botones preparados pero deshabilitados)
- [ ] **Fotografía de perfil** — Soporte para foto del socio en el carnet
- [ ] **Backup y sincronización** — Exportar/importar la base de datos o sincronizar con un backend remoto

---

## 👨‍💻 Autores

Desarrollado como proyecto integrador universitario.

---

<div align="center">
  <sub>Club Deportivo · Android · Kotlin · SQLite</sub>
</div>
