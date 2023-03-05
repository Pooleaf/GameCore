package net.pooleaf.gamereplay.data.datas.player

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

data class PlayerAnimationData(
    var playerUuid: UUID? = null,
    var animationType: String? = null
) : RecordData {

    override val type: String = "playerAnimation"

}