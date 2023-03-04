package net.pooleaf.gamecore.listeners.control

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerInteractEvent

/**
 * 항상 플레이어를 컨트롤하는 Listener
 */
class PlayerControlListener: Listener {

    /**
     * 배고픔 무한
     */
    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        (event.entity as Player).foodLevel = 20
        event.isCancelled = true
    }

    /**
     * 침대 사용 금지
     */
    @EventHandler
    fun onBedEnter(event: PlayerBedEnterEvent) {
        event.isCancelled = true
    }

    // TODO 탈것 모두 금지

    /**
     * 내구도 무한
     */
    @EventHandler
    fun onDamage(event: PlayerDamageEvent) {
        for (item in event.player.inventory.armorContents) {
            if (item != null && item.type.maxDurability.toInt() != 0) {
                item.durability = 0.toShort()
            }
        }
    }

    /**
     * 내구도 무한
     */
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val item = event.player.itemInHand
        if (item != null && item.type.maxDurability.toInt() != 0) {
            item.durability = 0.toShort()
        }
    }

}