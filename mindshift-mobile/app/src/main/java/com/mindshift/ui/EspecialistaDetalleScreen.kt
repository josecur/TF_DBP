package com.mindshift.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindshift.data.model.Profesional

@Composable
fun EspecialistaDetalleScreen(
    modifier: Modifier = Modifier,
    viewModel: EspecialistaDetalleViewModel = viewModel(),
    reservaViewModel: ReservaViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val s = estado) {
            is Resource.Loading ->
                CircularProgressIndicator(Modifier.align(Alignment.Center))

            is Resource.Error ->
                EstadoError(s.mensaje, onRetry = viewModel::cargar)

            is Resource.Success ->
                DetalleContent(s.data, reservaViewModel)
        }
    }
}

@Composable
private fun DetalleContent(p: Profesional, reservaViewModel: ReservaViewModel) {
    val uriHandler = LocalUriHandler.current
    val sesion by reservaViewModel.sesion.collectAsStateWithLifecycle()
    val reserva by reservaViewModel.estado.collectAsStateWithLifecycle()
    var mostrarDialog by remember { mutableStateOf(false) }

    LaunchedEffect(reserva.exito) { if (reserva.exito) mostrarDialog = false }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Avatar(inicial = p.nombreProfesional.take(1).uppercase(), tamano = 64)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "${tratamiento(p)} ${p.nombreProfesional} ${p.apellidoProfesional}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    p.especialidad,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        HorizontalDivider()

        Campo("Universidad", p.universidad)

        if (!p.descripcion_trayectoria.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Trayectoria", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    p.descripcion_trayectoria,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider()

        // --- Solicitar cita ---
        when {
            reserva.exito -> Text(
                "✓ ¡Solicitud enviada! El especialista la revisará.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            sesion?.rol == "usuario" -> Button(
                onClick = { mostrarDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Solicitar cita")
            }
            sesion?.rol == "especialista" -> Text(
                "Inicia sesión como usuario (no especialista) para solicitar una cita.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> Text(
                "Inicia sesión como usuario para solicitar una cita.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (reserva.error != null) {
            Text(
                reserva.error!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (!p.enlace_agenda.isNullOrBlank()) {
            OutlinedButton(
                onClick = { uriHandler.openUri(p.enlace_agenda) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Contactar por WhatsApp / agenda")
            }
        }
    }

    if (mostrarDialog) {
        ReservaDialog(
            cargando = reserva.cargando,
            onConfirmar = { motivo -> reservaViewModel.solicitarCita(p.id, motivo) },
            onCerrar = { mostrarDialog = false }
        )
    }
}

@Composable
private fun ReservaDialog(
    cargando: Boolean,
    onConfirmar: (String) -> Unit,
    onCerrar: () -> Unit
) {
    var motivo by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { if (!cargando) onCerrar() },
        title = { Text("Solicitar cita") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Cuéntale brevemente al especialista el motivo de tu consulta:")
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    label = { Text("Motivo") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(motivo) }, enabled = !cargando) {
                Text(if (cargando) "Enviando..." else "Enviar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar, enabled = !cargando) { Text("Cancelar") }
        }
    )
}

@Composable
private fun Campo(etiqueta: String, valor: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}
