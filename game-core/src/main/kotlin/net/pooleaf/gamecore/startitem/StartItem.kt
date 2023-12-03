package net.pooleaf.gamecore.startitem

import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import org.bukkit.inventory.ItemStack

class StartItem {

    @ConfigName("갑옷.헬멧")
    var helmetItem: ItemStack? = null

    @ConfigName("갑옷.갑옷")
    var chestplatItem: ItemStack? = null

    @ConfigName("갑옷.레깅스")
    var leggingsItem: ItemStack? = null

    @ConfigName("갑옷.부츠")
    var bootsItem: ItemStack? = null

    @ConfigName("아이템")
    var items = arrayListOf<ItemStack>()

    @ConfigName("레벨")
    var level = 0

}