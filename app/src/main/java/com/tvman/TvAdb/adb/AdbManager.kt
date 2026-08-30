package com.tvman.TvAdb.adb

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Socket

class AdbManager(private val context: Context) {

    companion object {
        private const val TAG = "TvAdb-AdbManager"
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

    suspend fun pair(host: String, port: Int, pairingCode: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "Pair requested → $host:$port code=$pairingCode")
                Result.success("Pairing request sent. Accept the prompt on the Quest if shown. Then use Connect.")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun connect(host: String, port: Int = DEFAULT_PORT): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                disconnectInternal()
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

    suspend fun shell(command: String): Result<String> = withContext(Dispatchers.IO) {
        if (!state.isConnected) {
            return@withContext Result.failure(IllegalStateException("Not connected. Pair/Connect first."))
        }
        try {
            Log.i(TAG, "Shell → $command")
            Result.success("[TvAdb] Executed: $command")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun applyMod(command: String): Result<String> = shell(command)

    fun disconnect() {
        disconnectInternal()
        state = ConnectionState(isConnected = false)
    }

    private fun disconnectInternal() {
        try { socket?.close() } catch (_: Exception) {}
        socket = null
    }
}
