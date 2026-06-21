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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindshift.data.model.ReservaDto

@Composable
fun MisCitasScreen(
    modifier: Modifier = Modifier,
    viewModel: MisCitasViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val s = estado) {
            is Resource.Loading ->
                CircularProgressIndicator(Modifier.align(Alignment.Center))

            is Resource.Error ->
                EstadoError(s.mensaje, onRetry = viewModel::cargar)

            is Resource.Success -> {
                if (s.data.isEmpty()) {
                    Text(
                        "Aún no has solicitado ninguna cita.",
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
                        items(s.data) { cita -> CitaCard(cita) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CitaCard(c: ReservaDto) {
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
                    c.medico_nombre,
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

            if (c.estado == "Aceptado") {
                val tel = c.medico_contacto?.get("telefono")
                val enlace = c.medico_contacto?.get("enlace")
                if (!tel.isNullOrBlank()) {
                    Text("Teléfono: $tel", style = MaterialTheme.typography.bodySmall)
                }
                if (!enlace.isNullOrBlank()) {
                    Text("Contacto: $enlace", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun EstadoChip(estado: String) {
    val color = when (estado) {
        "Aceptado" -> Color(0xFF2E7D32)
        "Pendiente" -> Color(0xFFF57C00)
        else -> MaterialTheme.colorScheme.outline
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            estado,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/** "2026-06-21T19:12:00Z" -> "2026-06-21 19:12". */
private fun formatoFecha(iso: String): String =
    iso.replace("T", " ").take(16)
