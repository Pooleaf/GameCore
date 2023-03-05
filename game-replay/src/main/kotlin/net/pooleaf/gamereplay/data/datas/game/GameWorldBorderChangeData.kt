package net.pooleaf.gamereplay.data.datas.game

import net.pooleaf.gamereplay.data.RecordData

/**
 * 팀 탈락 데이터
 */
data class GameWorldBorderChangeData(
    var tick: Long = 0,
    var centerX: Int = 0,
    var centerZ: Int = 0,
    var beforeSize: Int = 0,
    var newSize: Int = 0,
    var updateDurationSeconds: Int = 0
) : RecordData {

    override val type: String = "gameWorldBorderChange"

}