package com.mindshift.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pantalla aún por construir (próximo incremento): el perfil del usuario.
 * Por ahora muestra un marcador para que la navegación quede completa.
 */

@Composable
fun PerfilScreen(modifier: Modifier = Modifier) {
    Placeholder(
        modifier = modifier,
        emoji = "👤",
        titulo = "Mi perfil",
        mensaje = "Inicia sesión para ver tu historial de evaluaciones y tus citas. (Próximo incremento.)"
    )
}

@Composable
private fun Placeholder(modifier: Modifier, emoji: String, titulo: String, mensaje: String) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text(emoji, fontSize = 48.sp)
        Text(
            titulo,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
