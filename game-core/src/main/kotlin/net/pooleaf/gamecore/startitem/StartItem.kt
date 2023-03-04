package net.pooleaf.gamecore.startitem

import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import net.pooleaf.gamecore.GameCore
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

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


    /**
     * 시작 아이템을 저장합니다.
     */
    fun saveStartItemConfig() {
        GameCore.unsafe.startItemService.saveStartItemConfig()
    }

    /**
     * 시작 아이템을 불러옵니다.
     */
    fun loadStartItemConfig() {
        GameCore.unsafe.startItemService.loadStartItemConfig()
    }

}