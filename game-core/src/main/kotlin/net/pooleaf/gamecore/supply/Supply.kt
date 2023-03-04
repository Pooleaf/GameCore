package net.pooleaf.gamecore.supply

import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import net.pooleaf.gamecore.GameCore
import org.bukkit.inventory.ItemStack

class Supply {

    @ConfigName("이름")
    lateinit var name: String

    @ConfigName("확률 비율")
    var probabilityRatio: Int = 0

    @ConfigName("아이템")
    val items = arrayListOf<ItemStack>()


    /**
     * 보급품을 저장합니다.
     */
    fun saveSupplyConfig() {
        GameCore.unsafe.supplyService.saveSupplyConfig(this)
    }

    /**
     * 보급품을 불러옵니다.
     */
    fun loadSupplyConfig() {
        GameCore.unsafe.supplyService.loadSupplyConfig(name)
    }

    /**
     * 보급품을 삭제합니다.
     */
    fun deleteSupplyConfig() {
        GameCore.unsafe.supplyService.deleteSupplyConfig(name)
    }

}