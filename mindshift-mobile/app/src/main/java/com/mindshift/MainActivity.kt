package com.mindshift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.mindshift.ui.CuestionarioScreen
import com.mindshift.ui.theme.MindShiftTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindShiftTheme {
                CuestionarioScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
