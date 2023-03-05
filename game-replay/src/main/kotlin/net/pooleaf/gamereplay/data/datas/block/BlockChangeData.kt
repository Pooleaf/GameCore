package net.pooleaf.gamereplay.data.datas.block

import net.pooleaf.gamereplay.data.RecordData

data class BlockChangeData(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var blockTypeId: Int = 0,
    var blockData: Byte = 0
) : RecordData {

    override val type: String = "blockChange"

}