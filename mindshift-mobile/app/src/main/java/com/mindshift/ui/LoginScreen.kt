@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.mindshift.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onExito: () -> Unit,
    onIrRegistro: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var esEspecialista by remember { mutableStateOf(false) }
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    LaunchedEffect(estado.exito) { if (estado.exito) onExito() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Ingresa a tu cuenta",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        TabRow(selectedTabIndex = if (esEspecialista) 1 else 0) {
            Tab(selected = !esEspecialista, onClick = { esEspecialista = false }, text = { Text("Usuario") })
            Tab(selected = esEspecialista, onClick = { esEspecialista = true }, text = { Text("Especialista") })
        }

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Correo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (estado.error != null) {
            Text(
                estado.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = { viewModel.login(esEspecialista, usuario, clave) },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estado.cargando) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Ingresar")
            }
        }

        if (!esEspecialista) {
            TextButton(onClick = onIrRegistro, modifier = Modifier.fillMaxWidth()) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}
