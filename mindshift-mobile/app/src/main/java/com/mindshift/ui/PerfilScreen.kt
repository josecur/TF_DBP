package com.mindshift.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PerfilScreen(
    onIrLogin: () -> Unit,
    onIrRegistro: () -> Unit,
    onIrMisCitas: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PerfilViewModel = viewModel()
) {
    val sesion by viewModel.sesion.collectAsStateWithLifecycle()
    val s = sesion

    if (s == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            Text("👤", fontSize = 48.sp)
            Text("No has iniciado sesión", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "Inicia sesión o crea una cuenta para ver tu perfil.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(onClick = onIrLogin, modifier = Modifier.fillMaxWidth()) { Text("Iniciar sesión") }
            OutlinedButton(onClick = onIrRegistro, modifier = Modifier.fillMaxWidth()) { Text("Crear cuenta") }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Avatar(inicial = s.nombre.take(1).uppercase().ifBlank { "?" }, tamano = 64)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(s.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        if (s.rol == "especialista") "Especialista" else "Usuario",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider()

            if (s.correo.isNotBlank()) Fila("Correo", s.correo)
            if (s.rol == "usuario" && !s.nivelRiesgo.isNullOrBlank()) Fila("Nivel de carga", s.nivelRiesgo)
            if (s.rol == "especialista" && !s.especialidad.isNullOrBlank()) Fila("Especialidad", s.especialidad)

            if (s.rol == "usuario") {
                OutlinedButton(onClick = onIrMisCitas, modifier = Modifier.fillMaxWidth()) {
                    Text("Mis citas")
                }
            }

            Spacer(Modifier.weight(1f))

            Button(onClick = viewModel::cerrarSesion, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión")
            }
        }
    }
}

@Composable
private fun Fila(etiqueta: String, valor: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
