# CRUD de Estudiantes: SQLite vs Room

Una sola app con **dos pantallas** (pestañas abajo). Las dos hacen exactamente lo mismo
(guardar, listar, editar y borrar estudiantes con *nombre, carrera y nota*),
pero cada una guarda los datos de forma distinta y en su propio archivo de base de datos.

| Pestaña | Tecnología | Archivo de la BD |
|---|---|---|
| SQLite (azul) | `SQLiteOpenHelper` | `estudiantes_sqlite.db` |
| Room (verde)  | `Entity + DAO + Database` | `estudiantes_room.db` |

## Estructura de archivos
```
app/src/main/java/com/example/crudestudiantes/
├── MainActivity.kt                 ← las dos pestañas
├── sqlite/                         ← SOLO SQLite
│   ├── Estudiante.kt               (clase normal de Kotlin)
│   └── EstudianteDbHelper.kt       (crear BD + tabla + CREATE/READ/UPDATE/DELETE)
├── roomdb/                         ← SOLO Room
│   ├── Estudiante.kt               (ENTITY = la tabla)
│   ├── EstudianteDao.kt            (DAO = las operaciones)
│   └── AppDatabase.kt              (DATABASE = la base de datos)
└── ui/
    ├── components/Componentes.kt   (diseño compartido: cabecera, formulario, tarjetas)
    ├── screens/PantallaSqlite.kt   (pantalla que usa sqlite/)
    ├── screens/PantallaRoom.kt     (pantalla que usa roomdb/)
    └── theme/                      (colores y tipografía)
```
Busca en el código los comentarios `CREATE`, `READ`, `UPDATE` y `DELETE`.

## Cómo probar cada operación (en las dos pestañas)
1. **CREATE**: escribe Nombre, Carrera y Nota (0 a 5) → **Guardar**. Aparece una tarjeta nueva.
2. **READ**: la lista se muestra sola. Cierra la app por completo y ábrela: los datos siguen ahí.
3. **UPDATE**: pulsa el lápiz de una tarjeta, cambia un dato → **Actualizar**.
4. **DELETE**: pulsa el basurero → **Eliminar**.

El cuadro de color debajo del formulario muestra la última operación y su equivalente en SQL / Room.
Prueba con los mismos datos en ambas pestañas para compararlas, por ejemplo:

| Nombre | Carrera | Nota |
|---|---|---|
| Ana Gómez | Ingeniería de Sistemas | 4.5 |
| Luis Pérez | Diseño Gráfico | 3.2 |
| Sofía Ruiz | Medicina | 4.8 |

## Diferencias entre SQLite y Room

| | SQLite directo | Room |
|---|---|---|
| Crear la tabla | Escribimos `CREATE TABLE ...` a mano | Anotamos una clase con `@Entity` |
| Insertar | `ContentValues` + `db.insert()` | `@Insert` (una línea) |
| Leer | `rawQuery` + `Cursor` + convertir fila por fila | `@Query("SELECT ...")` y Room entrega los objetos |
| Actualizar | `db.update(..., "id = ?", ...)` | `@Update` |
| Eliminar | `db.delete(..., "id = ?", ...)` | `@Delete` |
| Errores en el SQL | Se descubren al **ejecutar** la app | Se descubren al **compilar** |
| Actualizar la pantalla | Volver a llamar `obtenerTodos()` a mano | Automático con `Flow` |
| Hilos | Nosotros debemos cuidar no bloquear la pantalla | Room obliga a usar corrutinas (`suspend`) |
| Cantidad de código | Más | Menos |
| Configuración | Ninguna (viene con Android) | Añadir librería + KSP en Gradle |

**Idea clave:** Room no reemplaza a SQLite. Por debajo Room *usa* SQLite;
solo escribe por ti el código repetitivo y te avisa de errores antes de ejecutar.
