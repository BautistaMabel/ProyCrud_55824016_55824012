package com.example.crudestudiantes.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * IMPLEMENTACIÓN 1: SQLite "a mano".
 *
 * SQLiteOpenHelper es la clase de Android que:
 *  - crea la base de datos la primera vez (onCreate)
 *  - la actualiza cuando cambia la versión (onUpgrade)
 *  - nos entrega la base para leer (readableDatabase) o escribir (writableDatabase)
 *
 * Aquí nosotros escribimos el SQL y convertimos los datos manualmente.
 */
class EstudianteDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        const val NOMBRE_BD = "estudiantes_sqlite.db"   // archivo de la base de datos
        const val VERSION_BD = 1

        // Nombre de la tabla y de sus columnas
        const val TABLA = "estudiantes"
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_CARRERA = "carrera"
        const val COL_NOTA = "nota"
    }

    // ------------------------------------------------------------------
    // CREAR LA BASE DE DATOS Y LA TABLA (se ejecuta solo la primera vez)
    // ------------------------------------------------------------------
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLA (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CARRERA TEXT NOT NULL,
                $COL_NOTA REAL NOT NULL
            )
            """.trimIndent()
        )
    }

    // Si algún día cambias VERSION_BD, Android llama a esta función.
    // Para un ejemplo sencillo: borramos la tabla y la creamos de nuevo.
    override fun onUpgrade(db: SQLiteDatabase, versionVieja: Int, versionNueva: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA")
        onCreate(db)
    }

    // ==================================================================
    // CREATE  ->  INSERT INTO estudiantes (...) VALUES (...)
    // ==================================================================
    fun insertar(estudiante: Estudiante): Long {
        // ContentValues es como un "mapa" columna -> valor
        val valores = ContentValues().apply {
            put(COL_NOMBRE, estudiante.nombre)
            put(COL_CARRERA, estudiante.carrera)
            put(COL_NOTA, estudiante.nota)
        }
        // Devuelve el id de la fila creada (o -1 si hubo error)
        return writableDatabase.insert(TABLA, null, valores)
    }

    // ==================================================================
    // READ  ->  SELECT * FROM estudiantes
    // ==================================================================
    fun obtenerTodos(): List<Estudiante> {
        val lista = mutableListOf<Estudiante>()

        // rawQuery ejecuta un SELECT y nos devuelve un Cursor (una "tabla" que se recorre fila por fila)
        val cursor = readableDatabase.rawQuery(
            "SELECT $COL_ID, $COL_NOMBRE, $COL_CARRERA, $COL_NOTA FROM $TABLA ORDER BY $COL_ID DESC",
            null
        )
        // use { } cierra el cursor automáticamente al terminar
        cursor.use {
            while (it.moveToNext()) {
                // Convertimos MANUALMENTE cada fila en un objeto Estudiante
                lista.add(
                    Estudiante(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                        nombre = it.getString(it.getColumnIndexOrThrow(COL_NOMBRE)),
                        carrera = it.getString(it.getColumnIndexOrThrow(COL_CARRERA)),
                        nota = it.getDouble(it.getColumnIndexOrThrow(COL_NOTA))
                    )
                )
            }
        }
        return lista
    }

    // ==================================================================
    // UPDATE  ->  UPDATE estudiantes SET ... WHERE id = ?
    // ==================================================================
    fun actualizar(estudiante: Estudiante): Int {
        val valores = ContentValues().apply {
            put(COL_NOMBRE, estudiante.nombre)
            put(COL_CARRERA, estudiante.carrera)
            put(COL_NOTA, estudiante.nota)
        }
        // "id = ?" -> el ? se reemplaza por el id (así evitamos errores y SQL injection)
        // Devuelve cuántas filas se modificaron
        return writableDatabase.update(
            TABLA, valores, "$COL_ID = ?", arrayOf(estudiante.id.toString())
        )
    }

    // ==================================================================
    // DELETE  ->  DELETE FROM estudiantes WHERE id = ?
    // ==================================================================
    fun eliminar(id: Int): Int {
        // Devuelve cuántas filas se borraron
        return writableDatabase.delete(TABLA, "$COL_ID = ?", arrayOf(id.toString()))
    }
}
