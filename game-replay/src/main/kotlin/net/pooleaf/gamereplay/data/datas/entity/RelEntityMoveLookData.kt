package net.pooleaf.gamereplay.data.datas.entity

import net.pooleaf.gamereplay.data.RecordData

data class RelEntityMoveLookData(
    var entityId: Int = 0,
    var dx: Byte = 0,
    var dy: Byte = 0,
    var dz: Byte = 0,
    var yaw: Byte = 0,
    var pitch: Byte = 0,
    var onGround: Boolean = false
) : RecordData {

    override val type: String = "relEntityMoveLook"

}