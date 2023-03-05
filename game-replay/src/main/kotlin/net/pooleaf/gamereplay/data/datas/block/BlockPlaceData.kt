package net.pooleaf.gamereplay.data.datas.block

import net.pooleaf.gamereplay.data.RecordData

/**
 * 블럭 설치 사운드 재생용
 * 블럭 변경은 [BlockChangeData]에서 담당
 */
data class BlockPlaceData(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var blockTypeId: Int = 0
) : RecordData {

    override val type: String = "blockPlace"

}