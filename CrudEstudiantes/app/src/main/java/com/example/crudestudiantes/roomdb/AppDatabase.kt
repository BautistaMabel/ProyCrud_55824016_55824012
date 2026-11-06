package com.example.crudestudiantes.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * DATABASE = la base de datos completa.
 * Indica qué tablas tiene (entities) y la versión.
 * Room genera la implementación real (AppDatabase_Impl) al compilar.
 */
@Database(entities = [Estudiante::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Por aquí accedemos al DAO
    abstract fun estudianteDao(): EstudianteDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        // Patrón "singleton": se crea una sola base de datos para toda la app
        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "estudiantes_room.db"      // archivo distinto al de SQLite
                ).build().also { instancia = it }
            }
        }
    }
}
