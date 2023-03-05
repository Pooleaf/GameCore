package net.pooleaf.gamereplay.data.datas.entity

import net.pooleaf.gamereplay.data.RecordData

data class EntityTeleportData(
    var entityId: Int = 0,
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var yaw: Byte = 0,
    var pitch: Byte = 0
) : RecordData {

    override val type: String = "entityTeleport"

}