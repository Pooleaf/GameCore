package net.pooleaf.gamereplay.data.datas.player

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 플레이어 퇴장 데이터
 */
data class PlayerQuitData(
    var playerUuid: UUID? = null,
) : RecordData {

    override val type: String = "playerQuit"

}