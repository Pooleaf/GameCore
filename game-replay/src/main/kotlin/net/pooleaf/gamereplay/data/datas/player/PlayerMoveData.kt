package net.pooleaf.gamereplay.data.datas.player

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

data class PlayerMoveData(
    var playerUuid: UUID? = null,
    var worldName: String? = null,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var yaw: Float = 0.0F,
    var pitch: Float = 0.0F
) : RecordData {

    override val type: String = "playerMove"

}