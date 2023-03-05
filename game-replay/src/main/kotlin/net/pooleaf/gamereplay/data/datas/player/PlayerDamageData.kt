package net.pooleaf.gamereplay.data.datas.player

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

data class PlayerDamageData(
    var playerUuid: UUID? = null
) : RecordData {

    override val type: String = "playerDamage"

}