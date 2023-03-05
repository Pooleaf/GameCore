package net.pooleaf.gamereplay.data.datas.game

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 채팅 데이터
 * 관리자에게만 전송
 */
data class GamePlayerDefeatData(
    var defeatPlayerUuid: UUID? = null,
    var killerPlayerUuid: UUID? = null
) : RecordData {

    override val type: String = "gamePlayerDefeat"

}