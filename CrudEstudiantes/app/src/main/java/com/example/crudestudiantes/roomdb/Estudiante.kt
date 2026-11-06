package com.example.crudestudiantes.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ENTITY (Entidad) = la TABLA de la base de datos, escrita como una clase de Kotlin.
 *
 * Room lee estas anotaciones y crea la tabla por nosotros.
 * (Comparar con SQLite: allá escribíamos el CREATE TABLE a mano.)
 */
@Entity(tableName = "estudiantes")
data class Estudiante(
    @PrimaryKey(autoGenerate = true)   // el id se genera solo (1, 2, 3...)
    val id: Int = 0,
    val nombre: String,
    val carrera: String,
    val nota: Double
)
