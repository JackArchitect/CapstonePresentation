package com.example.capstonepresentation.util

import com.example.capstonepresentation.model.BluetoothData
import javax.inject.Singleton

@Singleton
class UartDataParser {

    companion object {
        private const val TAG = "UartDataParser"
        private val LINE_DELIMITER = "\n"
        private val FIELD_PATTERN = """(.+?):\t+(.+)""".toRegex()
    }

    fun parseToBluetoothData(bleValue: ByteArray): BluetoothData? {
        return try {
            val textSection = bleValue.toString(Charsets.US_ASCII)

            val fields = textSection.split(LINE_DELIMITER).associate { line ->
                FIELD_PATTERN.find(line)?.let {
                    it.groupValues[1].trim() to it.groupValues[2].trim()
                } ?: ("" to "")
            }.filter { it.first.isNotEmpty() }
            BluetoothData(
                timestamp = fields["Time"] ?: "0:00:00.000",
                torque = fields["Torque"] ?.removeSuffix("mV")?.toFloatOrNull() ?: 0f,
                power = fields["Power"] ?.removeSuffix("W")?.toFloatOrNull() ?: 0f,
                cadence = fields["Cadence"] ?.removeSuffix("RPM")?.toFloatOrNull() ?: 0f,
                battery = fields["Battery"] ?.removeSuffix("%")?.toFloatOrNull() ?: 0f,
                rawData = bleValue
            ).takeIf { validataData(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Parse error: ${e.message}", e)
            null
        }
    }

    private fun validataData(data: BluetoothData): Boolean {
        return data.battery in 0f..100f &&
                abs(data.power) < 2000f &&
                data.rawData.isNotEmpty()
    }

//    companion object {
//        private const val HEADER = 0xAA.toByte()
//
//        fun parse(data: ByteArray): BluetoothData? {
//            if (data.isEmpty() || data[0] != HEADER) return null
//
//            // 简单校验（实际项目应该用CRC/Checksum）
//            if (data.size < 6) return null
//
//            val power = data[1].toInt() and 0xFF  // 转无符号字节
//            val isConnected = data[2] == 0x01.toByte()
//
//            val nameLength = data[3].toInt() and 0xFF
//            if (data.size < 4 + nameLength + 2) return null
//
//            val deviceName = String(data, 4, nameLength, Charsets.UTF_8)
//            val signalStrength = data[4 + nameLength].toInt() and 0xFF
//
//            return BluetoothData(
//                power = power,
//                isConnected = isConnected,
//                deviceName = deviceName,
//                signalStrength = signalStrength
//            )
//        }
    }
}