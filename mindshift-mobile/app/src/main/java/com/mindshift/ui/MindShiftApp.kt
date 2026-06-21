@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.mindshift.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/** Secciones de la app (equivalen a las rutas del app.routes.ts de Angular). */
enum class Destino(
    val ruta: String,
    val label: String,
    val titulo: String,
    val emoji: String
) {
    INICIO("inicio", "Inicio", "MindShift", "🏠"),
    TEST("test", "Test", "Cuestionario", "🧠"),
    ESPECIALISTAS("especialistas", "Expertos", "Especialistas", "🩺"),
    PERFIL("perfil", "Perfil", "Mi perfil", "👤")
}

/**
 * Contenedor principal: barra de navegación inferior + NavHost que conmuta entre secciones.
 * El TopAppBar muestra el título de la sección activa.
 */
@Composable
fun MindShiftApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route
    val destinoActual = Destino.entries.firstOrNull { it.ruta == rutaActual } ?: Destino.INICIO

    Scaffold(
        topBar = { TopAppBar(title = { Text(destinoActual.titulo) }) },
        bottomBar = {
            NavigationBar {
                Destino.entries.forEach { destino ->
                    NavigationBarItem(
                        selected = rutaActual == destino.ruta,
                        onClick = {
                            navController.navigate(destino.ruta) {
                                // Evita apilar la misma pantalla y conserva el estado de cada pestaña
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(destino.emoji, fontSize = 20.sp) },
                        label = { Text(destino.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destino.INICIO.ruta,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destino.INICIO.ruta) {
                HomeScreen(
                    onComenzarTest = { navController.navigate(Destino.TEST.ruta) { launchSingleTop = true } },
                    onVerEspecialistas = { navController.navigate(Destino.ESPECIALISTAS.ruta) { launchSingleTop = true } }
                )
            }
            composable(Destino.TEST.ruta) { CuestionarioScreen() }
            composable(Destino.ESPECIALISTAS.ruta) { EspecialistasScreen() }
            composable(Destino.PERFIL.ruta) { PerfilScreen() }
        }
    }
}
