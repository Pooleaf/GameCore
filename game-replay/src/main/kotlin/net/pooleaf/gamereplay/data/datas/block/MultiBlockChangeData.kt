package net.pooleaf.gamereplay.data.datas.block

import net.pooleaf.gamereplay.data.RecordData

data class MultiBlockChangeData(
    var chunkX: Int = 0,
    var chunkZ: Int = 0,
    var blockChangeInfos: List<BlockChangeInfo> = arrayListOf()
) : RecordData {

    override val type: String = "multiBlockChange"

}

class BlockChangeInfo() {
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var blockTypeId: Int = 0
    var blockData: Int = 0
}