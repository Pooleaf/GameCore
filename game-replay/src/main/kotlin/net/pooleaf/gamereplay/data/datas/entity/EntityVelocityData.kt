package net.pooleaf.gamereplay.data.datas.entity

import net.pooleaf.gamereplay.data.RecordData

data class EntityVelocityData(
    var entityId: Int = 0,
    var velocityX: Int = 0,
    var velocityY: Int = 0,
    var velocityZ: Int = 0
) : RecordData {

    override val type: String = "entityVelocity"

}