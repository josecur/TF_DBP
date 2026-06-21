@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CuestionarioScreen(
    modifier: Modifier = Modifier,
    viewModel: CuestionarioViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.cargando ->
                CircularProgressIndicator(Modifier.align(Alignment.Center))

            state.error != null ->
                ErrorView(state.error!!, onRetry = viewModel::cargar)

            else -> when (state.paso) {
                Paso.SELECCION -> SeleccionView(state.categorias, viewModel::seleccionarCategoria)
                Paso.CUESTIONARIO -> PreguntaView(state, viewModel::responder)
                Paso.RESULTADO -> ResultadoView(state, viewModel::reiniciar)
            }
        }
    }
}

@Composable
private fun SeleccionView(categorias: List<String>, onElegir: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Evaluación situacional",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Elige un área para comenzar tu escaneo cognitivo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(categorias) { cat ->
            ElevatedCard(onClick = { onElegir(cat) }, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        nombreBonito(cat),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text("→", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}

@Composable
private fun PreguntaView(state: CuestionarioState, onResponder: (Int) -> Unit) {
    val pregunta = state.preguntaActual ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Pregunta ${state.indice + 1} de ${state.totalPreguntas}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LinearProgressIndicator(
                progress = { state.progreso },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            pregunta.enunciado,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            pregunta.opciones.forEach { opcion ->
                OutlinedButton(
                    onClick = { onResponder(opcion.valor_puntos) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        opcion.contenido,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultadoView(state: CuestionarioState, onReiniciar: () -> Unit) {
    val nivel = state.nivelRiesgo
    val color = when (nivel) {
        "CRÍTICO" -> Color(0xFFD32F2F)
        "MODERADO" -> Color(0xFFF57C00)
        else -> Color(0xFF388E3C)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            "Resultado",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            nombreBonito(state.categoria.orEmpty()),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Surface(
            color = color.copy(alpha = 0.15f),
            contentColor = color,
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                "Nivel $nivel",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Text("Puntaje total: ${state.puntaje}", style = MaterialTheme.typography.bodyLarge)
        Text(
            mensajeNivel(nivel),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(onClick = onReiniciar) { Text("Hacer otra evaluación") }
    }
}

@Composable
private fun ErrorView(mensaje: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("No se pudo conectar al backend", style = MaterialTheme.typography.titleMedium)
        Text(
            mensaje,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}

private fun nombreBonito(categoria: String): String = when (categoria) {
    "ansiedad_social" -> "Ansiedad social"
    "tdah_burnout" -> "TDAH / Burnout"
    "depresion_apatia" -> "Depresión / Apatía"
    "perfeccionismo" -> "Perfeccionismo"
    else -> categoria.replace('_', ' ').replaceFirstChar { it.uppercase() }
}

private fun mensajeNivel(nivel: String): String = when (nivel) {
    "CRÍTICO" -> "Tu nivel de carga es alto. Te recomendamos contactar a un especialista."
    "MODERADO" -> "Hay señales de carga moderada. Cuida tus hábitos y considera buscar apoyo."
    else -> "Tu nivel de carga es bajo. ¡Sigue cuidándote!"
}
