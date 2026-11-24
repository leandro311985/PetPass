package com.leandro.petpass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.leandro.petpass.navigation.PetPassAppNavHost
import com.leandro.petpass.ui.theme.PetPassTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetPassTheme {
                PetPassAppNavHost(
                    container = (application as PetPassApplication).container
                )
            }
        }
    }
}
