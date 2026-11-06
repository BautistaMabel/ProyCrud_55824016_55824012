package com.example.crudestudiantes.sqlite

/**
 * Un estudiante en la versión SQLite.
 * Es una clase de datos normal de Kotlin: NO tiene anotaciones.
 * Nosotros mismos convertimos cada fila de la tabla a este objeto (ver EstudianteDbHelper).
 */
data class Estudiante(
    val id: Int = 0,          // 0 = todavía no tiene id (SQLite lo asigna al insertar)
    val nombre: String,
    val carrera: String,
    val nota: Double
)
