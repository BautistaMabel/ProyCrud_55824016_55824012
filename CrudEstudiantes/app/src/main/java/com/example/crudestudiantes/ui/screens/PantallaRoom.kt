package com.example.crudestudiantes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.crudestudiantes.roomdb.AppDatabase
import com.example.crudestudiantes.roomdb.Estudiante
import com.example.crudestudiantes.ui.components.Cabecera
import com.example.crudestudiantes.ui.components.ConfirmarEliminar
import com.example.crudestudiantes.ui.components.EstadoVacio
import com.example.crudestudiantes.ui.components.Formulario
import com.example.crudestudiantes.ui.components.TarjetaEstudiante
import com.example.crudestudiantes.ui.components.UltimaOperacion
import com.example.crudestudiantes.ui.components.errorDeFormulario
import com.example.crudestudiantes.ui.components.textoANota
import com.example.crudestudiantes.ui.theme.Esmeralda
import com.example.crudestudiantes.ui.theme.EsmeraldaOscuro
import kotlinx.coroutines.launch

/**
 * PANTALLA 2: CRUD con Room.
 *
 * Aquí usamos el DAO (carpeta "roomdb").
 * IMPORTANTE: con Room NO llamamos a "refrescar()". La lista viene como un Flow
 * y la pantalla se actualiza sola cada vez que la tabla cambia.
 */
@Composable
fun PantallaRoom(snackbar: SnackbarHostState, margenInferior: PaddingValues) {

    // Obtenemos el DAO desde la base de datos de Room
    val context = LocalContext.current
    val dao = remember { AppDatabase.obtener(context).estudianteDao() }
    val scope = rememberCoroutineScope()

    // ---------- READ: la lista se mantiene sincronizada con la tabla ----------
    val estudiantes by remember { dao.obtenerTodos() }.collectAsState(initial = emptyList())

    // Estado del formulario
    var nombre by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var idEditando by remember { mutableStateOf<Int?>(null) }   // null = estamos creando
    var error by remember { mutableStateOf<String?>(null) }
    var aEliminar by remember { mutableStateOf<Estudiante?>(null) }
    var operacion by remember { mutableStateOf("READ") }
    var detalle by remember { mutableStateOf("@Query SELECT * FROM estudiantes") }

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
        val n = nombre.trim()
        val c = carrera.trim()
        val nt = textoANota(nota)

        // Las funciones del DAO son "suspend": se llaman dentro de scope.launch { }
        scope.launch {
            if (id == null) {
                // ---------- CREATE ----------
                dao.insertar(Estudiante(nombre = n, carrera = c, nota = nt))
                operacion = "CREATE"
                detalle = "@Insert  dao.insertar(estudiante)"
                avisar("Estudiante creado")
            } else {
                // ---------- UPDATE ----------
                dao.actualizar(Estudiante(id = id, nombre = n, carrera = c, nota = nt))
                operacion = "UPDATE"
                detalle = "@Update  dao.actualizar(estudiante) → id = $id"
                avisar("Estudiante actualizado")
            }
        }
        limpiarFormulario()
        // OJO: aquí NO hay que volver a leer la lista. El Flow lo hace solo.
    }

    LazyColumn(
        modifier = Modifier.padding(bottom = margenInferior.calculateBottomPadding()),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Cabecera(
                titulo = "Room",
                subtitulo = "Entity + DAO + Database · Room escribe el código por ti",
                total = estudiantes.size,
                colorClaro = Esmeralda,
                colorOscuro = EsmeraldaOscuro
            )
        }
        item {
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Formulario(
                    nombre = nombre, carrera = carrera, nota = nota,
                    onNombre = { nombre = it }, onCarrera = { carrera = it }, onNota = { nota = it },
                    editando = idEditando != null,
                    error = error,
                    colorAcento = Esmeralda,
                    onGuardar = { guardar() },
                    onCancelar = { limpiarFormulario() }
                )
                UltimaOperacion(operacion, detalle, Esmeralda)
            }
        }
        if (estudiantes.isEmpty()) {
            item { EstadoVacio() }
        }
        items(estudiantes, key = { it.id }) { e ->
            Column(Modifier.padding(horizontal = 16.dp)) {
                TarjetaEstudiante(
                    nombre = e.nombre, carrera = e.carrera, nota = e.nota,
                    colorAcento = Esmeralda,
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
                scope.launch {
                    dao.eliminar(borrar)                 // DELETE
                    operacion = "DELETE"
                    detalle = "@Delete  dao.eliminar(estudiante) → id = ${borrar.id}"
                    avisar("Estudiante eliminado")
                }
                if (idEditando == borrar.id) limpiarFormulario()
                aEliminar = null
            },
            onCancelar = { aEliminar = null }
        )
    }
}
