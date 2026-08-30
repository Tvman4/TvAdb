package com.tvman.TvADB

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.tvman.TvADB.adb.AdbManager
import com.tvman.TvADB.ui.screens.ConnectScreen
import com.tvman.TvADB.ui.screens.ModsScreen
import com.tvman.TvADB.ui.screens.ShellScreen
import com.tvman.TvADB.ui.theme.TvADBTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val adbManager = AdbManager(applicationContext)

        setContent {
            TvADBTheme {
                var selectedTab by remember { mutableIntStateOf(0) }

                val tabs = listOf(
                    TabItem("Connect", Icons.Default.Link),
                    TabItem("Mods", Icons.Default.Build),
                    TabItem("Shell", Icons.Default.Terminal)
                )

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            tabs.forEachIndexed { index, tab ->
                                NavigationBarItem(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    icon = { Icon(tab.icon, contentDescription = tab.title) },
                                    label = { Text(tab.title) }
                                )
                            }
                        }
                    }
                ) { padding ->
                    Surface(modifier = Modifier.padding(padding)) {
                        when (selectedTab) {
                            0 -> ConnectScreen(adbManager)
                            1 -> ModsScreen(adbManager)
                            2 -> ShellScreen(adbManager)
                        }
                    }
                }
            }
        }
    }
}

private data class TabItem(val title: String, val icon: ImageVector)
