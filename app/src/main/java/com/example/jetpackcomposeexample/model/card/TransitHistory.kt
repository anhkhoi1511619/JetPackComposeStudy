package com.example.jetpackcomposeexample.model.card

data class TransitHistory(
    val date: String,
    val type: Int,
    val lineCode: Int,
    val stationCode: Int,
    val balance: Int
) {
    override fun toString(): String {
        return "Ngày: $date | Giao dịch: $type | Line: $lineCode | Station: $stationCode | Số dư: ¥$balance"
    }
}
