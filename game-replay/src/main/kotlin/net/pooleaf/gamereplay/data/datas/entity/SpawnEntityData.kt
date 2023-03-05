package net.pooleaf.gamereplay.data.datas.entity

import net.pooleaf.gamereplay.data.RecordData

data class SpawnEntityData(
    var entityId: Int = 0,
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var optionalSpeedX: Int = 0,
    var optionalSpeedY: Int = 0,
    var optionalSpeedZ: Int = 0,
    var yaw: Int = 0,
    var pitch: Int = 0,
    var objectType: Int = 0,
    var objectData: Int = 0
) : RecordData {

    override val type: String = "spawnEntity"

}