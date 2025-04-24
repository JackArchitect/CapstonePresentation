package com.example.capstonepresentation.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.suspend
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import com.example.capstonepresentation.bluetooth.BluetoothConstants
import java.util.Optional
import java.util.UUID

@Singleton
class NordicBleManager @Inject constructor(
    @ApplicationContext context: Context
) : BleManager(context) {

    private val targetServiceUuid: UUID = BluetoothConstants.UART_SERVICE_UUID
    private val targetCharUuid: UUID = BluetoothConstants.UART_RX_CHAR_UUID

    private val _rawData = MutableSharedFlow<ByteArray>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val rawData: SharedFlow<ByteArray> = _rawData

    override fun getGattCallback(): BleManagerGattCallback {
        return if (targetServiceUuid != null && targetCharUuid != null) {
            createStaticUuidCallback()
        } else {
            createDynamicDiscoveryCallback()
        }
//            override fun initialize() {
//
//                setNotificationCallback(uartCharacteristic)
//                    .with { _, data ->
//                        _rawData.tryEmit(data.value)
//                    }
//            }
    }

    private fun createStaticUuidCallback() : BleManagerGattCallback {
        return object : BleManagerGattCallback() {
            override fun initialize() {
                setNotificationCallback(targetCharUuid!!)
                    .with { _, data -> _rawData.tryEmit(data.value)}
            }
//            writeCharacteristic(
//                BluetoothConstants.UART_TX_CHAR_UUID,
//                "INIT".toByteArray()
//            ).enqueue()
        }
    }

    private fun createDynamicDiscoveryCallback() : BleManagerGattCallback {
        return object : BleManagerGattCallback() {
            override fun onServicesDiscovered(device: BluetoothDevice, optional: Boolean) {
                getService()
            }

            override fun onServicesInvalidated() {
                TODO("Not yet implemented")
            }

        }
    }


}