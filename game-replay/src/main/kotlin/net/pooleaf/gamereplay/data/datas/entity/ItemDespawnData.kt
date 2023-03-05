package net.pooleaf.gamereplay.data.datas.entity

import net.pooleaf.gamereplay.data.RecordData

data class ItemDespawnData(
    var entityId: Int = 0
) : RecordData {

    override val type: String = "itemDespawn"

}