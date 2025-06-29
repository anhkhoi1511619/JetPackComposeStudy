package com.example.jetpackcomposeexample.utils

import android.nfc.tech.NfcF
import com.example.jetpackcomposeexample.model.card.TransitHistory

fun readTransitHistory(nfcF: NfcF, idm: ByteArray): List<TransitHistory> {
    val serviceCode = byteArrayOf(0x0f, 0x09) // 0x090f → lịch sử
    val blockCount = 10

    val blockList = ByteArray(blockCount * 2)
    for (i in 0 until blockCount) {
        blockList[i * 2] = 0x80.toByte()
        blockList[i * 2 + 1] = i.toByte()
    }

    val cmdLen = 1 + 8 + 1 + 2 + 1 + blockList.size
    val cmd = ByteArray(cmdLen + 1)
    cmd[0] = (cmdLen + 1).toByte()
    cmd[1] = 0x06 // READ WITHOUT ENCRYPTION
    System.arraycopy(idm, 0, cmd, 2, 8)
    var pos = 10
    cmd[pos++] = 1
    cmd[pos++] = serviceCode[0]
    cmd[pos++] = serviceCode[1]
    cmd[pos++] = blockCount.toByte()
    System.arraycopy(blockList, 0, cmd, pos, blockList.size)

    val response = nfcF.transceive(cmd)

    val histories = mutableListOf<TransitHistory>()

    var offset = 13
    while (offset + 16 <= response.size) {
        val block = response.copyOfRange(offset, offset + 16)
        parseHistoryBlock(block)?.let { histories.add(it) }
        offset += 16
    }

    return histories
}

fun parseHistoryBlock(block: ByteArray): TransitHistory? {
    if (block.size != 16) return null

    val type = block[0].toInt() and 0xFF

    val dateRaw = ((block[4].toInt() and 0xFF) shl 8) or (block[5].toInt() and 0xFF)
    val year = 2000 + (dateRaw shr 9)
    val month = (dateRaw shr 5) and 0x0F
    val day = dateRaw and 0x1F
    val dateStr = "%04d-%02d-%02d".format(year, month, day)

    val lineCode = block[6].toInt() and 0xFF
    val stationCode = block[7].toInt() and 0xFF

    val balance = ((block[11].toInt() and 0xFF) shl 8) or (block[10].toInt() and 0xFF)

    return TransitHistory(dateStr, type, lineCode, stationCode, balance)
}

