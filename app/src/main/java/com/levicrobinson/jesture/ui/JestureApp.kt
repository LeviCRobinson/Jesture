package com.levicrobinson.jesture.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.levicrobinson.jesture.ui.navigation.NavigationRoot

@Composable
fun JestureApp() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavigationRoot(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}