package net.pooleaf.gamereplay.data.datas.block

import net.pooleaf.gamereplay.data.RecordData


data class ExplodeData(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var yield: Float = 0.0F,
    var blockInfos: List<BlockExplodeInfo> = arrayListOf()
) : RecordData {

    override val type: String = "explode"

}

data class BlockExplodeInfo(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0
) {
}