package net.pooleaf.gamereplay.data.datas.entity

import net.pooleaf.gamereplay.data.RecordData
import org.bukkit.inventory.ItemStack

/**
 * 아이템 Entity Index 10
 * https://wiki.vg/index.php?title=Entity_metadata&oldid=7415#Entity
 */
data class ItemMetaDataData(
    var entityId: Int = 0,
    var value: ItemStack? = null
) : RecordData {

    override val type: String = "itemMetaData"

}