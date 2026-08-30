package com.tvman.TvAdb.adb

import android.content.Context
import android.util.Log
import io.github.muntashirakon.adb.AbsAdbConnectionManager
import io.github.muntashirakon.adb.AdbStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class AdbManager(private val context: Context) {

    companion object {
        private const val TAG = "TvAdb-AdbManager"
    }

    data class ConnectionState(
        val isConnected: Boolean = false,
        val host: String? = null,
        val port: Int = 5555,
        val lastError: String? = null
    )

    @Volatile
    private var state = ConnectionState()

    private val manager: AbsAdbConnectionManager by lazy {
        TvAdbConnectionManager.getInstance(context)
    }

    fun getState(): ConnectionState = state

    suspend fun pair(host: String, port: Int, pairingCode: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                manager.hostAddress = host
                val ok = manager.pair(host, port, pairingCode)
                if (ok) {
                    Result.success("Paired OK. Now Connect with the normal Wireless Debugging IP + port.")
                } else {
                    Result.failure(Exception("Pairing failed. Check IP, pairing port, and 6-digit code."))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Pair failed", e)
                Result.failure(e)
            }
        }

    suspend fun connect(host: String, port: Int = 5555): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                manager.hostAddress = host
                val ok = manager.connect(host, port)
                if (ok) {
                    state = ConnectionState(isConnected = true, host = host, port = port)
                    Result.success("Connected to $host:$port")
                } else {
                    state = ConnectionState(isConnected = false, lastError = "connect returned false")
                    Result.failure(Exception("Connect failed. Pair first, then use the correct port."))
                }
            } catch (e: Exception) {
                state = ConnectionState(isConnected = false, lastError = e.message)
                Log.e(TAG, "Connect failed", e)
                Result.failure(e)
            }
        }

    suspend fun shell(command: String): Result<String> = withContext(Dispatchers.IO) {
        if (!state.isConnected) {
            return@withContext Result.failure(IllegalStateException("Not connected. Pair then Connect first."))
        }
        try {
            val stream: AdbStream = manager.openStream("shell:$command")
            val reader = BufferedReader(InputStreamReader(stream.openInputStream(), StandardCharsets.UTF_8))
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.appendLine(line)
            }
            try { stream.close() } catch (_: Exception) {}
            val result = output.toString().ifBlank { "(command sent – no output)" }
            Log.i(TAG, "Shell → $command\n$result")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Shell failed: $command", e)
            Result.failure(e)
        }
    }

    suspend fun applyMod(command: String): Result<String> = shell(command)

    fun disconnect() {
        try { manager.close() } catch (_: Exception) {}
        state = ConnectionState(isConnected = false)
    }
}
