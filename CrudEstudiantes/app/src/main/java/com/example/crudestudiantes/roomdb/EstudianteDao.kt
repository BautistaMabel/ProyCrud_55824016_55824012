package com.example.crudestudiantes.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) = la lista de operaciones que se pueden hacer con la tabla.
 *
 * Solo escribimos la FIRMA de cada función con una anotación.
 * Room genera el código real por nosotros.
 *
 * Las funciones "suspend" se ejecutan en segundo plano (corrutinas),
 * así la pantalla nunca se congela.
 */
@Dao
interface EstudianteDao {

    // CREATE  ->  INSERT
    @Insert
    suspend fun insertar(estudiante: Estudiante)

    // READ  ->  SELECT
    // Devuelve un Flow: cada vez que la tabla cambia, la lista se actualiza SOLA en pantalla.
    @Query("SELECT * FROM estudiantes ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<Estudiante>>

    // UPDATE  ->  UPDATE ... WHERE id = ...
    // Room usa el @PrimaryKey del objeto para saber qué fila modificar.
    @Update
    suspend fun actualizar(estudiante: Estudiante)

    // DELETE  ->  DELETE ... WHERE id = ...
    @Delete
    suspend fun eliminar(estudiante: Estudiante)
}
