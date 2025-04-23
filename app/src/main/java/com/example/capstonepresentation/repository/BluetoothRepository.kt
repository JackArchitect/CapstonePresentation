package com.example.capstonepresentation.repository

import android.util.Log
import com.example.capstonepresentation.model.BluetoothData
import com.example.capstonepresentation.util.UartDataParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import kotlin.math.abs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.suspend
import javax.inject.Inject
import javax.inject.Singleton
import com.example.capstonepresentation.bluetooth.NordicBleManager


class BluetoothRepository @Inject constructor(
    private val bleManager: NordicBleManager,
    private val uartParser: UartDataParser
) {
    private val _rawData = MutableSharedFlow<ByteArray>(
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val sensorData: Flow<BluetoothData> = _rawData.mapNotNull { rawBytes -> try {
            uartParser.parseToBluetoothData(rawBytes)?.takeIf { data ->
                data.battery in 0f..100f && abs(data.power) < 2000f
            }
        } catch (e: Exception) {
            Log.e("Bluetooth Repository", "Parse failed: ${e.message}")
            null
        }
    }
        .distinctUntilChanged()
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            started = SharingStarted.WhileSubscribed(5000),
            replay = 1
        )
    suspend fun handleBluetoothData(value: ByteArray) {
        _rawData.emit(value)
    }
}


//data class BluetoothData(
//    val timestamp: String,
//    val toque: Float,
//    val power: Float,
//    val cadence: Float,
//    val battery: Float,
//    val rawData: ByteArray
//) {
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//        other as BluetoothData
//        return rawData.contentEquals(other.rawData)
//    }
//
//    override fun hashCode(): Int = rawData.contentHashCode()
//}