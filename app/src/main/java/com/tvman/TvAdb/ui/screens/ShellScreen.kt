package com.tvman.TvAdb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.tvman.TvAdb.adb.AdbManager
import kotlinx.coroutines.launch

@Composable
fun ShellScreen(adbManager: AdbManager) {
    var command by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("TvAdb Shell\nType any ADB shell command and press Send.\nExample: getprop debug.oculus.headlock\n") }
    var isBusy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("ADB Shell", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = command,
            onValueChange = { command = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Command") },
            placeholder = { Text("setprop debug.oculus.headlock 3") },
            singleLine = true,
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (command.isBlank()) return@IconButton
                        scope.launch {
                            isBusy = true
                            val r = adbManager.shell(command.trim())
                            val result = r.getOrElse { "Error: ${it.message}" }
                            output += "\n$ $command\n$result\n"
                            command = ""
                            isBusy = false
                        }
                    },
                    enabled = !isBusy
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        )

        if (isBusy) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = output,
                modifier = Modifier
                    .padding(12.dp)
                    .verticalScroll(scroll),
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = { command = "setprop debug.oculus.headlock 3" },
                label = { Text("Long Arms") }
            )
            AssistChip(
                onClick = { command = "getprop debug.oculus.headlock" },
                label = { Text("Check Headlock") }
            )
            AssistChip(
                onClick = { command = "getprop | grep debug.oculus" },
                label = { Text("List Props") }
            )
        }
    }
}
