package net.pooleaf.gamecore.kit

import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import net.pooleaf.gamecore.GameCore
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

class Kit {

    @ConfigName("이름")
    lateinit var name: String

    @ConfigName("아이템")
    val items = arrayListOf<ItemStack>()


    /**
     * 킷을 저장합니다.
     */
    fun saveKitConfig() {
        GameCore.unsafe.kitService.saveKitConfig(this)
    }

    /**
     * 킷을 불러옵니다.
     */
    fun loadKitConfig() {
        GameCore.unsafe.kitService.loadKitConfig(name)
    }

    /**
     * 킷을 삭제합니다.
     */
    fun deleteKitConfig() {
        GameCore.unsafe.kitService.deleteKitConfig(name)
    }

}