package net.pooleaf.gamereplay.data.datas

import net.pooleaf.gamereplay.data.RecordData

/**
 * 채팅 데이터
 * 관리자에게만 전송
 */
data class TpsData(
    var tps: Double = 0.0
) : RecordData {

    override val type: String = "tps"

}