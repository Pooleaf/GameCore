package net.pooleaf.gamereplay.data.datas.block

import net.pooleaf.gamereplay.data.RecordData

data class BlockDamageData(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var state: Int = 0
) : RecordData {

    override val type: String = "blockDamage"

}