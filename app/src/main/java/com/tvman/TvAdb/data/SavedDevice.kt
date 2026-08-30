package com.tvman.TvADB.data

data class SavedDevice(
    val name: String,
    val host: String,
    val port: Int = 5555,
    val lastConnected: Long = System.currentTimeMillis()
)
