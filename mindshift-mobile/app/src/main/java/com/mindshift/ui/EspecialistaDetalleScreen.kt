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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindshift.data.model.Profesional

@Composable
fun EspecialistaDetalleScreen(
    modifier: Modifier = Modifier,
    viewModel: EspecialistaDetalleViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val s = estado) {
            is Resource.Loading ->
                CircularProgressIndicator(Modifier.align(Alignment.Center))

            is Resource.Error ->
                EstadoError(s.mensaje, onRetry = viewModel::cargar)

            is Resource.Success ->
                DetalleContent(s.data)
        }
    }
}

@Composable
private fun DetalleContent(p: Profesional) {
    val uriHandler = LocalUriHandler.current

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

        if (!p.enlace_agenda.isNullOrBlank()) {
            Button(
                onClick = { uriHandler.openUri(p.enlace_agenda) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Contactar / Agendar cita")
            }
        }

        Text(
            "Las reservas dentro de la app llegan en el próximo incremento.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
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
