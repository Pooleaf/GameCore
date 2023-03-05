package net.pooleaf.gamereplay.data.datas.entity

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

data class CollectData(
    var collectedEntityId: Int = 0,
    var collectorPlayerUuid: UUID? = null
) : RecordData {

    override val type: String = "collect"

}