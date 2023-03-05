package net.pooleaf.gamereplay.data.datas.player

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 플레이어 보기 데이터
 */
data class PlayerShowData(
    var playerUuid: UUID? = null,
) : RecordData {

    override val type: String = "playerShow"

}