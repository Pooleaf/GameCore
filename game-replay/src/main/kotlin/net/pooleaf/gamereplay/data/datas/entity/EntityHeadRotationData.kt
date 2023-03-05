package net.pooleaf.gamereplay.data.datas.entity

import net.pooleaf.gamereplay.data.RecordData

data class EntityHeadRotationData(
    var entityId: Int = 0,
    var headYaw: Byte = 0
) : RecordData {

    override val type: String = "entityHeadRotation"

}