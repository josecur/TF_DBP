@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.mindshift.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
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

/** Ruta de detalle (no aparece en la barra inferior; se llega desde la lista de especialistas). */
private const val RUTA_DETALLE = "especialista/{id}"

/**
 * Contenedor principal: barra de navegación inferior + NavHost que conmuta entre secciones.
 * El TopAppBar muestra el título de la sección activa y un botón de volver en las subpantallas.
 */
@Composable
fun MindShiftApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val esDetalleEspecialista = rutaActual?.startsWith("especialista/") == true
    val esTopLevel = Destino.entries.any { it.ruta == rutaActual }
    val titulo = when (rutaActual) {
        "login" -> "Iniciar sesión"
        "registro" -> "Crear cuenta"
        "mis-citas" -> "Mis citas"
        "historial" -> "Mi historial"
        "panel-especialista" -> "Solicitudes de cita"
        else -> if (esDetalleEspecialista) "Perfil del especialista"
        else (Destino.entries.firstOrNull { it.ruta == rutaActual } ?: Destino.INICIO).titulo
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    if (!esTopLevel && rutaActual != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Text("←", fontSize = 22.sp)
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                Destino.entries.forEach { destino ->
                    val seleccionado = rutaActual == destino.ruta ||
                        (destino == Destino.ESPECIALISTAS && esDetalleEspecialista) ||
                        (destino == Destino.PERFIL && (rutaActual == "login" || rutaActual == "registro" || rutaActual == "mis-citas" || rutaActual == "historial" || rutaActual == "panel-especialista"))
                    NavigationBarItem(
                        selected = seleccionado,
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
            composable(Destino.ESPECIALISTAS.ruta) {
                EspecialistasScreen(onVerDetalle = { id -> navController.navigate("especialista/$id") })
            }
            composable(Destino.PERFIL.ruta) {
                PerfilScreen(
                    onIrLogin = { navController.navigate("login") },
                    onIrRegistro = { navController.navigate("registro") },
                    onIrMisCitas = { navController.navigate("mis-citas") },
                    onIrPanel = { navController.navigate("panel-especialista") },
                    onIrHistorial = { navController.navigate("historial") }
                )
            }
            composable("mis-citas") { MisCitasScreen() }
            composable("historial") { HistorialScreen() }
            composable("panel-especialista") { PanelEspecialistaScreen() }
            composable(RUTA_DETALLE) { EspecialistaDetalleScreen() }
            composable("login") {
                LoginScreen(
                    onExito = { navController.popBackStack(Destino.PERFIL.ruta, inclusive = false) },
                    onIrRegistro = { navController.navigate("registro") }
                )
            }
            composable("registro") {
                RegistroScreen(
                    onExito = { navController.popBackStack(Destino.PERFIL.ruta, inclusive = false) },
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}
