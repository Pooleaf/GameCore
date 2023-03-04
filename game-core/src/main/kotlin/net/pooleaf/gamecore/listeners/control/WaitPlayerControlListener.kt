package net.pooleaf.gamecore.listeners.control

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageByPlayerEvent
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent

class WaitPlayerControlListener: Listener {

    private fun isWaiting(): Boolean {
        return !GameCore.game.isGameStarted
    }


    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (isWaiting()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (isWaiting()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBucketFill(event: PlayerBucketFillEvent) {
        if (isWaiting()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        if (isWaiting()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDamage(event: PlayerDamageEvent) {
        if (isWaiting()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPvp(event: PlayerDamageByPlayerEvent) {
        if (isWaiting()) {
            event.isCancelled = true
        }
    }

}