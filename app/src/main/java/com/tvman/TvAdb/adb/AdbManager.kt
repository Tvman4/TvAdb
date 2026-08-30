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

    private var manager: AbsAdbConnectionManager? = null

    private fun getManager(): AbsAdbConnectionManager {
        val existing = manager
        if (existing != null) return existing

        return try {
            val created = TvAdbConnectionManager.getInstance(context)
            manager = created
            created
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create ADB manager", e)
            throw e
        }
    }

    fun getState(): ConnectionState = state

    suspend fun pair(host: String, port: Int, pairingCode: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                if (host.isBlank() || pairingCode.isBlank()) {
                    return@withContext Result.failure(Exception("IP and pairing code are required"))
                }
                if (pairingCode.trim().length != 6) {
                    return@withContext Result.failure(Exception("Pairing code must be 6 digits"))
                }

                val mgr = getManager()
                mgr.hostAddress = host.trim()
                val ok = mgr.pair(host.trim(), port, pairingCode.trim())

                if (ok) {
                    Result.success("Paired OK. Now Connect with the normal Wireless Debugging IP + port.")
                } else {
                    Result.failure(Exception("Pairing returned false. Check IP, pairing port, and code."))
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Pair crashed", e)
                Result.failure(Exception("Pair crash: ${e.javaClass.simpleName}: ${e.message}"))
            }
        }

    suspend fun connect(host: String, port: Int = 5555): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                if (host.isBlank()) {
                    return@withContext Result.failure(Exception("IP is required"))
                }

                val mgr = getManager()
                mgr.hostAddress = host.trim()
                val ok = mgr.connect(host.trim(), port)

                if (ok) {
                    state = ConnectionState(isConnected = true, host = host.trim(), port = port)
                    Result.success("Connected to ${host.trim()}:$port")
                } else {
                    state = ConnectionState(isConnected = false, lastError = "connect returned false")
                    Result.failure(Exception("Connect failed. Pair first, then use the correct port."))
                }
            } catch (e: Throwable) {
                state = ConnectionState(isConnected = false, lastError = e.message)
                Log.e(TAG, "Connect crashed", e)
                Result.failure(Exception("Connect crash: ${e.javaClass.simpleName}: ${e.message}"))
            }
        }

    suspend fun shell(command: String): Result<String> = withContext(Dispatchers.IO) {
        if (!state.isConnected) {
            return@withContext Result.failure(IllegalStateException("Not connected. Pair then Connect first."))
        }
        try {
            val mgr = getManager()
            val stream: AdbStream = mgr.openStream("shell:$command")
            val reader = BufferedReader(
                InputStreamReader(stream.openInputStream(), StandardCharsets.UTF_8)
            )
            val output = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.appendLine(line)
            }
            try { stream.close() } catch (_: Exception) {}

            val result = output.toString().ifBlank { "(command sent – no output)" }
            Log.i(TAG, "Shell → $command\n$result")
            Result.success(result)
        } catch (e: Throwable) {
            Log.e(TAG, "Shell crashed: $command", e)
            Result.failure(Exception("Shell crash: ${e.javaClass.simpleName}: ${e.message}"))
        }
    }

    suspend fun applyMod(command: String): Result<String> = shell(command)

    fun disconnect() {
        try {
            manager?.close()
        } catch (_: Exception) {
        }
        manager = null
        state = ConnectionState(isConnected = false)
    }
}
