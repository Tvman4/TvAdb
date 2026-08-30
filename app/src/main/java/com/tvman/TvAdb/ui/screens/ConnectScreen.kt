package com.tvman.TvAdb.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tvman.TvAdb.adb.AdbManager
import kotlinx.coroutines.launch

@Composable
fun ConnectScreen(
    adbManager: AdbManager,
    onConnected: () -> Unit = {}
) {
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("5555") }
    var pairingCode by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Not connected") }
    var isBusy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val state = adbManager.getState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Wireless Debugging",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Enable Wireless Debugging on your Quest. Use Pair once, then Connect. No Shizuku required.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = host,
            onValueChange = { host = it },
            label = { Text("Quest IP Address") },
            placeholder = { Text("192.168.x.x") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Wifi, contentDescription = null) }
        )

        OutlinedTextField(
            value = port,
            onValueChange = { port = it },
            label = { Text("Port") },
            placeholder = { Text("5555 or Wireless Debugging port") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = pairingCode,
            onValueChange = { pairingCode = it },
            label = { Text("Pairing Code (optional)") },
            placeholder = { Text("6-digit code from Quest") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        isBusy = true
                        val p = port.toIntOrNull() ?: 5555
                        val result = adbManager.pair(host, p, pairingCode)
                        status = result.getOrElse { it.message ?: "Pair failed" }
                        isBusy = false
                    }
                },
                enabled = !isBusy && host.isNotBlank() && pairingCode.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Pair")
            }

            Button(
                onClick = {
                    scope.launch {
                        isBusy = true
                        val p = port.toIntOrNull() ?: 5555
                        val result = adbManager.connect(host, p)
                        status = result.getOrElse { it.message ?: "Connect failed" }
                        if (result.isSuccess) onConnected()
                        isBusy = false
                    }
                },
                enabled = !isBusy && host.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Link, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Connect")
            }
        }

        OutlinedButton(
            onClick = {
                adbManager.disconnect()
                status = "Disconnected"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.LinkOff, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Disconnect")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (state.isConnected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (state.isConnected) "● Connected" else "○ Disconnected",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = status, style = MaterialTheme.typography.bodySmall)
                state.host?.let { h ->
                    Text(text = "$h:${state.port}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (isBusy) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        HorizontalDivider()

        Text(text = "Quick tips", style = MaterialTheme.typography.titleSmall)
        Text(
            text = "1. Enable Wireless Debugging on Quest\n" +
                    "2. Pair with the code once\n" +
                    "3. Connect with IP + port\n" +
                    "4. Go to Mods → Long Arms or Pull/Fly",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
