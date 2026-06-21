@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.mindshift.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onComenzarTest: () -> Unit,
    onVerEspecialistas: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "BIENESTAR Y RENDIMIENTO",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Equilibrio mental, éxito absoluto.",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Monitorea tus patrones de pensamiento y detecta tu nivel de carga mental con un escaneo rápido.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(onClick = onComenzarTest, modifier = Modifier.fillMaxWidth()) {
            Text("Comenzar escaneo cognitivo")
        }

        Spacer(Modifier.height(4.dp))

        // Áreas que evalúa el cuestionario
        Text(
            "¿Qué evaluamos?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        PilarCard("🧠", "Ansiedad social", "Cómo te afectan las situaciones sociales y la exposición.")
        PilarCard("⚡", "TDAH / Burnout", "Atención, procrastinación y agotamiento mental.")
        PilarCard("🌧️", "Depresión / Apatía", "Energía, motivación y estado de ánimo.")
        PilarCard("🎯", "Perfeccionismo", "Autoexigencia y miedo al error.")

        Spacer(Modifier.height(4.dp))

        // Acceso a especialistas
        ElevatedCard(onClick = onVerEspecialistas, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🩺", fontSize = 28.sp)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        "¿Buscas apoyo?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Conecta con un especialista.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text("→", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun PilarCard(emoji: String, titulo: String, descripcion: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(emoji, fontSize = 28.sp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
