package com.example.deco3801

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.deco3801.ui.theme.DECO3801Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DECO3801Theme {
                AppFunctionality()
            }
        }
    }
}