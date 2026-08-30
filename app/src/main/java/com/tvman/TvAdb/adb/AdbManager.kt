package com.tvman.TvADB.adb

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.cert.X509Certificate
import javax.net.ssl.SSLSocket

/**
 * Lightweight ADB client manager for TvADB.
 * Designed around Wireless Debugging (pair + connect).
 * In a production build you would wire this to libadb-android or Kadb.
 * This scaffold provides the public API and a simple TCP shell path
 * that works once the device is already authorized / in tcpip mode.
 */
class AdbManager(private val context: Context) {

    companion object {
        private const val TAG = "TvADB-AdbManager"
        private const val DEFAULT_PORT = 5555
    }

    data class ConnectionState(
        val isConnected: Boolean = false,
        val host: String? = null,
        val port: Int = DEFAULT_PORT,
        val lastError: String? = null
    )

    @Volatile
    private var state = ConnectionState()

    private var socket: Socket? = null

    fun getState(): ConnectionState = state

    /**
     * Pair with a device using the Wireless Debugging pairing code (Android 11+).
     * Real implementation should use the ADB pairing protocol (TLS + SPAKE2).
     * Here we expose the API so the UI can call it.
     */
    suspend fun pair(host: String, port: Int, pairingCode: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                // TODO: integrate real pairing from libadb-android
                // For now return a clear message so the UI can guide the user.
                Log.i(TAG, "Pair requested → $host:$port code=$pairingCode")
                Result.success("Pairing request sent. Accept the prompt on the Quest if shown. Then use Connect.")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Connect to an already-authorized device over TCP.
     * On Quest this works after Wireless Debugging is enabled and the key is trusted.
     */
    suspend fun connect(host: String, port: Int = DEFAULT_PORT): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                // Close previous
                disconnectInternal()

                // Simple TCP connect – real ADB needs the ADB protocol handshake.
                // This is a scaffold; replace the body with library calls.
                val s = Socket(host, port)
                s.soTimeout = 8000
                socket = s

                state = ConnectionState(isConnected = true, host = host, port = port)
                Log.i(TAG, "Connected to $host:$port")
                Result.success("Connected to $host:$port")
            } catch (e: Exception) {
                state = ConnectionState(isConnected = false, lastError = e.message)
                Log.e(TAG, "Connect failed", e)
                Result.failure(e)
            }
        }

    /**
     * Execute a shell command and return the output.
     * Real version uses the ADB shell: service.
     */
    suspend fun shell(command: String): Result<String> = withContext(Dispatchers.IO) {
        if (!state.isConnected) {
            return@withContext Result.failure(IllegalStateException("Not connected. Pair/Connect first."))
        }
        try {
            // Placeholder – in real implementation:
            // connection.shell(command) or openShell().write(command)
            Log.i(TAG, "Shell → $command")

            // For demo / scaffold we just echo what would be sent.
            // Replace with actual ADB protocol execution.
            val fakeOutput = "[TvADB] Executed: $command\n(Replace this with real adbd response)"
            Result.success(fakeOutput)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Convenience for applying a Quest mod.
     */
    suspend fun applyMod(command: String): Result<String> = shell(command)

    fun disconnect() {
        disconnectInternal()
        state = ConnectionState(isConnected = false)
    }

    private fun disconnectInternal() {
        try {
            socket?.close()
        } catch (_: Exception) {
        }
        socket = null
    }

    /**
     * Helper that tries common ports (5555 + the random Wireless Debugging port).
     */
    suspend fun smartConnect(host: String, preferredPort: Int? = null): Result<String> {
        val ports = listOfNotNull(preferredPort, 5555).distinct()
        var lastError: Exception? = null
        for (p in ports) {
            val r = connect(host, p)
            if (r.isSuccess) return r
            lastError = r.exceptionOrNull() as? Exception
        }
        return Result.failure(lastError ?: Exception("All ports failed"))
    }
}
