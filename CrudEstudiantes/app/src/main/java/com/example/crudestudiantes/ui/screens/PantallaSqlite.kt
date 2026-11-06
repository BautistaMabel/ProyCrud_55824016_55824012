package com.example.crudestudiantes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.crudestudiantes.sqlite.Estudiante
import com.example.crudestudiantes.sqlite.EstudianteDbHelper
import com.example.crudestudiantes.ui.components.Cabecera
import com.example.crudestudiantes.ui.components.ConfirmarEliminar
import com.example.crudestudiantes.ui.components.EstadoVacio
import com.example.crudestudiantes.ui.components.Formulario
import com.example.crudestudiantes.ui.components.TarjetaEstudiante
import com.example.crudestudiantes.ui.components.UltimaOperacion
import com.example.crudestudiantes.ui.components.errorDeFormulario
import com.example.crudestudiantes.ui.components.textoANota
import com.example.crudestudiantes.ui.theme.Indigo
import com.example.crudestudiantes.ui.theme.IndigoOscuro
import kotlinx.coroutines.launch

/**
 * PANTALLA 1: CRUD con SQLite directo.
 *
 * Aquí usamos EstudianteDbHelper (carpeta "sqlite").
 * IMPORTANTE: con SQLite, después de cada cambio tenemos que volver a
 * pedir la lista nosotros mismos (función refrescar()).
 */
@Composable
fun PantallaSqlite(snackbar: SnackbarHostState, margenInferior: PaddingValues) {

    // El helper abre (o crea) la base de datos
    val context = LocalContext.current
    val db = remember { EstudianteDbHelper(context.applicationContext) }
    val scope = rememberCoroutineScope()

    // ---------- READ: leemos la lista al abrir la pantalla ----------
    var estudiantes by remember { mutableStateOf(db.obtenerTodos()) }

    // Estado del formulario
    var nombre by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var idEditando by remember { mutableStateOf<Int?>(null) }   // null = estamos creando
    var error by remember { mutableStateOf<String?>(null) }
    var aEliminar by remember { mutableStateOf<Estudiante?>(null) }
    var operacion by remember { mutableStateOf("READ") }
    var detalle by remember { mutableStateOf("SELECT * FROM estudiantes") }

    // Vuelve a leer la tabla y refresca la pantalla
    fun refrescar() {
        estudiantes = db.obtenerTodos()          // READ
    }

    fun limpiarFormulario() {
        nombre = ""; carrera = ""; nota = ""; idEditando = null; error = null
    }

    fun avisar(mensaje: String) {
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            snackbar.showSnackbar(mensaje)
        }
    }

    // Se ejecuta al pulsar "Guardar" o "Actualizar"
    fun guardar() {
        error = errorDeFormulario(nombre, carrera, nota)
        if (error != null) return

        val id = idEditando
        if (id == null) {
            // ---------- CREATE ----------
            db.insertar(Estudiante(nombre = nombre.trim(), carrera = carrera.trim(), nota = textoANota(nota)))
            operacion = "CREATE"
            detalle = "INSERT INTO estudiantes (nombre, carrera, nota)"
            avisar("Estudiante creado")
        } else {
            // ---------- UPDATE ----------
            db.actualizar(Estudiante(id = id, nombre = nombre.trim(), carrera = carrera.trim(), nota = textoANota(nota)))
            operacion = "UPDATE"
            detalle = "UPDATE estudiantes SET ... WHERE id = $id"
            avisar("Estudiante actualizado")
        }
        limpiarFormulario()
        refrescar()
    }

    LazyColumn(
        modifier = Modifier.padding(bottom = margenInferior.calculateBottomPadding()),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Cabecera(
                titulo = "SQLite",
                subtitulo = "SQLiteOpenHelper · escribimos el SQL a mano",
                total = estudiantes.size,
                colorClaro = Indigo,
                colorOscuro = IndigoOscuro
            )
        }
        item {
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Formulario(
                    nombre = nombre, carrera = carrera, nota = nota,
                    onNombre = { nombre = it }, onCarrera = { carrera = it }, onNota = { nota = it },
                    editando = idEditando != null,
                    error = error,
                    colorAcento = Indigo,
                    onGuardar = { guardar() },
                    onCancelar = { limpiarFormulario() }
                )
                UltimaOperacion(operacion, detalle, Indigo)
            }
        }
        if (estudiantes.isEmpty()) {
            item { EstadoVacio() }
        }
        items(estudiantes, key = { it.id }) { e ->
            Column(Modifier.padding(horizontal = 16.dp)) {
                TarjetaEstudiante(
                    nombre = e.nombre, carrera = e.carrera, nota = e.nota,
                    colorAcento = Indigo,
                    onEditar = {
                        // Pasamos los datos al formulario para modificarlos (UPDATE)
                        idEditando = e.id
                        nombre = e.nombre; carrera = e.carrera; nota = e.nota.toString()
                        error = null
                    },
                    onEliminar = { aEliminar = e }
                )
            }
        }
    }

    // ---------- DELETE (después de confirmar) ----------
    val borrar = aEliminar
    if (borrar != null) {
        ConfirmarEliminar(
            nombre = borrar.nombre,
            onConfirmar = {
                db.eliminar(borrar.id)           // DELETE
                operacion = "DELETE"
                detalle = "DELETE FROM estudiantes WHERE id = ${borrar.id}"
                if (idEditando == borrar.id) limpiarFormulario()
                aEliminar = null
                refrescar()
                avisar("Estudiante eliminado")
            },
            onCancelar = { aEliminar = null }
        )
    }
}
