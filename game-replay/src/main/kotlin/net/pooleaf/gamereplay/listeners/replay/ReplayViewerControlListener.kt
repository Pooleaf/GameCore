package net.pooleaf.gamereplay.listeners.replay

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.EntityDamageByPlayerEvent
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageByPlayerEvent
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*

class ReplayViewerControlListener: Listener {

    private fun isReplayPlayServer(): Boolean {
        return GameReplayApi.unsafe.replayConfig.isReplayPlayServer
    }


    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!isReplayPlayServer()) return

        Bukkit.getOnlinePlayers().forEach {
            it.hidePlayer(event.player)
            event.player.hidePlayer(it)
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (isReplayPlayServer()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (isReplayPlayServer()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockDamage(event: BlockDamageEvent) {
        if (isReplayPlayServer()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBucketFill(event: PlayerBucketFillEvent) {
        if (isReplayPlayServer()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        if (isReplayPlayServer()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDamage(event: PlayerDamageEvent) {
        if (isReplayPlayServer()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onHit(event: EntityDamageByPlayerEvent) {
        if (isReplayPlayServer()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPvp(event: PlayerDamageByPlayerEvent) {
        if (isReplayPlayServer()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (isReplayPlayServer()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        if (isReplayPlayServer()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPickupItem(event: PlayerPickupItemEvent) {
        if (isReplayPlayServer()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        if (isReplayPlayServer()) {
            event.drops.clear()
            event.droppedExp = 0
        }
    }

}