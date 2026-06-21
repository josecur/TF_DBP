package com.mindshift.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindshift.data.model.ReservaDto

@Composable
fun PanelEspecialistaScreen(
    modifier: Modifier = Modifier,
    viewModel: PanelEspecialistaViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val aceptando by viewModel.aceptando.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val s = estado) {
            is Resource.Loading ->
                CircularProgressIndicator(Modifier.align(Alignment.Center))

            is Resource.Error ->
                EstadoError(s.mensaje, onRetry = viewModel::cargar)

            is Resource.Success -> {
                if (s.data.isEmpty()) {
                    Text(
                        "No tienes solicitudes de cita por ahora.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(s.data) { solicitud ->
                            SolicitudCard(solicitud, aceptando == solicitud.id, viewModel::aceptar)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SolicitudCard(c: ReservaDto, aceptando: Boolean, onAceptar: (Long) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    c.alumno_nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                EstadoChip(c.estado)
            }

            if (!c.fecha.isNullOrBlank()) {
                Text(
                    "Solicitada: ${formatoFecha(c.fecha)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(c.motivo, style = MaterialTheme.typography.bodyMedium)

            when (c.estado) {
                "Pendiente" -> Button(
                    onClick = { onAceptar(c.id) },
                    enabled = !aceptando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (aceptando) "Aceptando..." else "Aceptar")
                }

                "Aceptado" -> Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "Contacto del alumno",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(c.alumno_correo, style = MaterialTheme.typography.bodyMedium)
                    if (!c.nivel_riesgo.isNullOrBlank()) {
                        Text(
                            "Nivel de carga: ${c.nivel_riesgo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
