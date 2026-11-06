package com.example.crudestudiantes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.crudestudiantes.ui.theme.Ambar
import com.example.crudestudiantes.ui.theme.Esmeralda
import com.example.crudestudiantes.ui.theme.Rojo
import com.example.crudestudiantes.ui.theme.TextoSuave

/*
 * Componentes de PANTALLA que usan las dos versiones (SQLite y Room).
 * Aquí NO hay nada de bases de datos: solo diseño.
 * Así la única diferencia entre las dos pantallas es cómo guardan los datos.
 */

// ---------- Validación simple del formulario ----------

/** Devuelve un mensaje de error, o null si todo está bien. */
fun errorDeFormulario(nombre: String, carrera: String, nota: String): String? {
    if (nombre.isBlank()) return "Escribe el nombre del estudiante"
    if (carrera.isBlank()) return "Escribe la carrera"
    val n = nota.replace(',', '.').toDoubleOrNull()
    if (n == null || n < 0.0 || n > 5.0) return "La nota debe ser un número entre 0 y 5"
    return null
}

/** Convierte el texto "4,5" o "4.5" en el número 4.5 */
fun textoANota(nota: String): Double = nota.replace(',', '.').toDouble()

// ---------- Cabecera con degradado ----------

@Composable
fun Cabecera(
    titulo: String,
    subtitulo: String,
    total: Int,
    colorClaro: Color,
    colorOscuro: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Brush.linearGradient(listOf(colorOscuro, colorClaro)))
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
            }
            Spacer(Modifier.width(12.dp))
            // Contador de registros
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("$total", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text("registros", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}

// ---------- Formulario (sirve para CREATE y para UPDATE) ----------

@Composable
fun Formulario(
    nombre: String,
    carrera: String,
    nota: String,
    onNombre: (String) -> Unit,
    onCarrera: (String) -> Unit,
    onNota: (String) -> Unit,
    editando: Boolean,          // true = modo UPDATE, false = modo CREATE
    error: String?,
    colorAcento: Color,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                if (editando) "Editar estudiante" else "Nuevo estudiante",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = nombre, onValueChange = onNombre,
                label = { Text("Nombre") }, singleLine = true,
                shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = carrera, onValueChange = onCarrera,
                label = { Text("Carrera") }, singleLine = true,
                shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nota, onValueChange = onNota,
                label = { Text("Nota (0 a 5)") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()
            )
            if (error != null) {
                Text(error, color = Rojo, style = MaterialTheme.typography.labelMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onGuardar,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorAcento)
                ) {
                    Text(if (editando) "Actualizar" else "Guardar", fontWeight = FontWeight.Bold)
                }
                if (editando) {
                    OutlinedButton(
                        onClick = onCancelar,
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Cancelar") }
                }
            }
        }
    }
}

// ---------- Cuadro que muestra la última operación realizada ----------

@Composable
fun UltimaOperacion(operacion: String, detalle: String, colorAcento: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorAcento.copy(alpha = 0.10f))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            operacion,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colorAcento)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.width(10.dp))
        Text(detalle, style = MaterialTheme.typography.bodyMedium, color = colorAcento, modifier = Modifier.weight(1f))
    }
}

// ---------- Tarjeta de un estudiante (parte "Read" visual) ----------

@Composable
fun TarjetaEstudiante(
    nombre: String,
    carrera: String,
    nota: Double,
    colorAcento: Color,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo con la inicial del nombre
            Box(
                modifier = Modifier.size(46.dp).clip(CircleShape).background(colorAcento.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    nombre.take(1).uppercase(),
                    color = colorAcento,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(nombre, style = MaterialTheme.typography.titleMedium)
                Text(carrera, style = MaterialTheme.typography.bodyMedium, color = TextoSuave)
                Spacer(Modifier.height(6.dp))
                // Etiqueta con la nota (verde / amarillo / rojo)
                val colorNota = when {
                    nota >= 4.0 -> Esmeralda
                    nota >= 3.0 -> Ambar
                    else -> Rojo
                }
                Text(
                    "Nota: $nota",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorNota.copy(alpha = 0.14f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    color = colorNota,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = colorAcento)
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Rojo)
            }
        }
    }
}

// ---------- Mensaje cuando no hay datos ----------

@Composable
fun EstadoVacio() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📭", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Aún no hay estudiantes", style = MaterialTheme.typography.titleMedium)
        Text("Llena el formulario y pulsa Guardar", style = MaterialTheme.typography.bodyMedium, color = TextoSuave)
    }
}

// ---------- Ventana para confirmar el borrado ----------

@Composable
fun ConfirmarEliminar(nombre: String, onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("¿Eliminar estudiante?") },
        text = { Text("Se borrará a $nombre de la base de datos.") },
        confirmButton = {
            TextButton(onClick = onConfirmar) { Text("Eliminar", color = Rojo, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
