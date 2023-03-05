package net.pooleaf.gamereplay.data.datas.player

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 플레이어 접속 데이터
 */
data class PlayerJoinData(
    var playerUuid: UUID? = null,
) : RecordData {

    override val type: String = "playerJoin"

}