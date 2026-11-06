package com.example.crudestudiantes

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.crudestudiantes.ui.screens.PantallaRoom
import com.example.crudestudiantes.ui.screens.PantallaSqlite
import com.example.crudestudiantes.ui.theme.CrudTheme
import com.example.crudestudiantes.ui.theme.Esmeralda
import com.example.crudestudiantes.ui.theme.Indigo

/**
 * Una sola app con DOS pantallas (pestañas abajo):
 *   0 -> SQLite   (CRUD con SQLiteOpenHelper)
 *   1 -> Room     (CRUD con Entity + DAO + Database)
 * Cada una usa su propia base de datos, así no se mezclan.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La cabecera de color llega hasta arriba, por eso los íconos de la barra de estado son claros
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        )
        setContent {
            CrudTheme {
                var pestana by remember { mutableIntStateOf(0) }
                val snackbar = remember { SnackbarHostState() }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    snackbarHost = { SnackbarHost(snackbar) },
                    bottomBar = {
                        NavigationBar(containerColor = Color.White) {
                            NavigationBarItem(
                                selected = pestana == 0,
                                onClick = { pestana = 0 },
                                icon = { Icon(Icons.Default.Build, contentDescription = null) },
                                label = { Text("SQLite") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Indigo,
                                    indicatorColor = Indigo
                                )
                            )
                            NavigationBarItem(
                                selected = pestana == 1,
                                onClick = { pestana = 1 },
                                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                                label = { Text("Room") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Esmeralda,
                                    indicatorColor = Esmeralda
                                )
                            )
                        }
                    }
                ) { padding ->
                    // Solo usamos el margen de abajo (la cabecera ocupa la parte de arriba)
                    val margen = PaddingValues(bottom = padding.calculateBottomPadding())
                    Box(Modifier.fillMaxSize()) {
                        if (pestana == 0) PantallaSqlite(snackbar, margen) else PantallaRoom(snackbar, margen)
                    }
                }
            }
        }
    }
}
