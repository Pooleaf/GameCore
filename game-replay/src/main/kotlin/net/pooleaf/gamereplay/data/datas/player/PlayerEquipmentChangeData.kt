package net.pooleaf.gamereplay.data.datas.player

import net.pooleaf.gamereplay.data.RecordData
import org.bukkit.inventory.ItemStack
import java.util.*

data class PlayerEquipmentChangeData(
    var playerUuid: UUID? = null,
    var equipmentType: Int = 0,
    var item: ItemStack? = null
) : RecordData {

    override val type: String = "playerEquipmentChange"

}