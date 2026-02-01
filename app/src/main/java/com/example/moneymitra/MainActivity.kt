package com.example.moneymitra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.moneymitra.ui.navigation.AppNavHost
import com.example.moneymitra.ui.theme.MoneyMitraTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MoneyMitraTheme {
                AppNavHost(this)
            }
        }
    }
}
