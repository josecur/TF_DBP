package com.mindshift.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Datos de la sesión activa (reemplaza el objeto guardado en localStorage del web). */
data class Session(
    val rol: String,            // "usuario" | "especialista"
    val id: Long,
    val nombre: String,
    val correo: String,
    val nivelRiesgo: String? = null,   // solo usuario
    val especialidad: String? = null   // solo especialista
)

private val Context.dataStore by preferencesDataStore(name = "sesion")

/**
 * Persiste la sesión con DataStore. Expone un Flow que la UI observa para reaccionar
 * a login / logout automáticamente.
 */
class SessionManager(private val context: Context) {

    private object Keys {
        val ROL = stringPreferencesKey("rol")
        val ID = longPreferencesKey("id")
        val NOMBRE = stringPreferencesKey("nombre")
        val CORREO = stringPreferencesKey("correo")
        val NIVEL = stringPreferencesKey("nivel")
        val ESPECIALIDAD = stringPreferencesKey("especialidad")
    }

    val sesion: Flow<Session?> = context.dataStore.data.map { p ->
        val rol = p[Keys.ROL] ?: return@map null
        Session(
            rol = rol,
            id = p[Keys.ID] ?: 0L,
            nombre = p[Keys.NOMBRE] ?: "",
            correo = p[Keys.CORREO] ?: "",
            nivelRiesgo = p[Keys.NIVEL],
            especialidad = p[Keys.ESPECIALIDAD]
        )
    }

    suspend fun guardar(s: Session) {
        context.dataStore.edit { p ->
            p[Keys.ROL] = s.rol
            p[Keys.ID] = s.id
            p[Keys.NOMBRE] = s.nombre
            p[Keys.CORREO] = s.correo
            if (s.nivelRiesgo != null) p[Keys.NIVEL] = s.nivelRiesgo else p.remove(Keys.NIVEL)
            if (s.especialidad != null) p[Keys.ESPECIALIDAD] = s.especialidad else p.remove(Keys.ESPECIALIDAD)
        }
    }

    suspend fun cerrar() {
        context.dataStore.edit { it.clear() }
    }
}
