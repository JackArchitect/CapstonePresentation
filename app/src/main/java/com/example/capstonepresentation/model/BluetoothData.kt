package com.example.capstonepresentation.model

data class BluetoothData(
    val timestamp: String,
    val torque: Float,
    val power: Float,
    val cadence: Float,
    val battery: Float,
    val rawData: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BluetoothData
        return rawData.contentEquals(other.rawData)
    }

    override fun hashCode(): Int = rawData.contentHashCode()
}