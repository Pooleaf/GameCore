package net.pooleaf.gamereplay.data.datas.game

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 게임 종료 데이터
 */
data class GameEndData(
    var winnerPlayerUuidss: List<UUID> = arrayListOf()
) : RecordData {

    override val type: String = "gameEnd"

}