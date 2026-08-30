package com.tvman.TvADB.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tvman.TvADB.adb.AdbManager
import com.tvman.TvADB.mods.QuestMods
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModsScreen(adbManager: AdbManager) {
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var lastResult by remember { mutableStateOf("") }
    var isBusy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val categories = listOf("All") + QuestMods.byCategory().keys.sorted()
    val filtered = QuestMods.allMods.filter { mod ->
        (selectedCategory == "All" || mod.category == selectedCategory) &&
                (search.isBlank() ||
                        mod.title.contains(search, true) ||
                        mod.description.contains(search, true) ||
                        mod.command.contains(search, true))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header + Long Arms highlight
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Quest Mods",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${QuestMods.allMods.size} mods ready • Wireless ADB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))

                // Big flagship buttons
                val longArms = QuestMods.findById("long_arms")!!
                val pullFly = QuestMods.findById("pull_fly")!!

                Button(
                    onClick = {
                        scope.launch {
                            isBusy = true
                            val r = adbManager.applyMod(longArms.command)
                            lastResult = r.getOrElse { "Error: ${it.message}" }
                            isBusy = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("LONG ARMS", fontWeight = FontWeight.Bold)
                        Text(
                            "setprop debug.oculus.headlock 3",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isBusy = true
                            val r = adbManager.applyMod(pullFly.command)
                            lastResult = r.getOrElse { "Error: ${it.message}" }
                            isBusy = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("PULL / FLY", fontWeight = FontWeight.Bold)
                        Text(
                            "Ctrlpredmax 5 + Right.ctrlr.vel 5",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        // Search + category chips
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search mods…") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true
        )

        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
            edgePadding = 16.dp
        ) {
            categories.forEach { cat ->
                Tab(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    text = { Text(cat) }
                )
            }
        }

        if (lastResult.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    lastResult,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (isBusy) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered, key = { it.id }) { mod ->
                ModCard(
                    mod = mod,
                    onApply = {
                        scope.launch {
                            isBusy = true
                            val r = adbManager.applyMod(mod.command)
                            lastResult = r.getOrElse { "Error: ${it.message}" }
                            isBusy = false
                        }
                    },
                    onReset = mod.resetCommand?.let { resetCmd ->
                        {
                            scope.launch {
                                isBusy = true
                                val r = adbManager.applyMod(resetCmd)
                                lastResult = r.getOrElse { "Error: ${it.message}" }
                                isBusy = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ModCard(
    mod: QuestMods.Mod,
    onApply: () -> Unit,
    onReset: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        mod.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        mod.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                FilledTonalButton(onClick = onApply) {
                    Text("Apply")
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                mod.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                mod.command,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            if (onReset != null) {
                TextButton(onClick = onReset) {
                    Text("Reset")
                }
            }
        }
    }
}
