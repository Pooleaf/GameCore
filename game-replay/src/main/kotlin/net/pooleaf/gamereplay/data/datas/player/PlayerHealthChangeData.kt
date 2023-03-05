package net.pooleaf.gamereplay.data.datas.player

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

data class PlayerHealthChangeData(
    var playerUuid: UUID? = null,
    var health: Double = 0.0
) : RecordData {

    override val type: String = "healthChange"

}