package net.pooleaf.gamecore.kit

import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryGui
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiClickEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiCloseEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiPlayerInventoryClickEvent
import org.bukkit.entity.Player

class KitEditGui(
    val kit: Kit,
    val player: Player
): InventoryGui(kit.name, 3) {

    init {
        clickDelayMillis = 0
        kit.items.forEach { inventory.addItem(it) }
    }

    override fun onClick(event: InventoryGuiClickEvent) {
        event.isCancelled = false
    }

    override fun onPlayerInventoryClick(event: InventoryGuiPlayerInventoryClickEvent) {
        event.isCancelled = false
    }

    override fun onClose(event: InventoryGuiCloseEvent) {
        kit.items.clear()
        inventory.filterNotNull().forEach { kit.items.add(it) }

        kit.saveKitConfig()
        player.sendMessage("${kit.name} §b킷을 저장했습니다.")
    }

}