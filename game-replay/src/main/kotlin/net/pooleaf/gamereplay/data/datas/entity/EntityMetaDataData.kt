package net.pooleaf.gamereplay.data.datas.entity

import net.pooleaf.gamereplay.data.RecordData

/**
 * https://wiki.vg/index.php?title=Entity_metadata&oldid=7415#Entity
 */
data class EntityMetaDataData(
    var entityId: Int = 0,
    var dataWatchables: List<DataWatchable> = listOf()
) : RecordData {

    override val type: String = "entityMetaData"

}

data class DataWatchable(
    var index: Int = 0,
    var value: Any = 0
) {
}