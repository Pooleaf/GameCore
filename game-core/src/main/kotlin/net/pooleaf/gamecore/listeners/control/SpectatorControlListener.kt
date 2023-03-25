package net.pooleaf.gamecore.listeners.control

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.EntityDamageByPlayerEvent
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageByPlayerEvent
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerJoinEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*

/**
 * 관전 모드 플레이어를 컨트롤하는 Listener
 */
class SpectatorControlListener: Listener {

    private fun isSpectator(player: Player): Boolean {
        val gamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)

        return gamePlayer?.isSpectator == true
    }

    private fun isWaiting(): Boolean {
        return !GameCore.game.isTeleportedToMap
    }


    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerJoin(event: GamePlayerJoinEvent) {
        // 접속자가 관전자 안보이게하기
        GameCore.unsafe.playerManager.getOnlineSpectators().forEach { event.gamePlayer.player.hidePlayer(it.player) }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (isSpectator(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (isSpectator(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockDamage(event: BlockDamageEvent) {
        if (isSpectator(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBucketFill(event: PlayerBucketFillEvent) {
        if (isSpectator(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        if (isSpectator(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDamage(event: PlayerDamageEvent) {
        if (isSpectator(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onHit(event: EntityDamageByPlayerEvent) {
        if (isSpectator(event.damager)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPvp(event: PlayerDamageByPlayerEvent) {
        if (isSpectator(event.player) || isSpectator(event.damager)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onInteract(event: PlayerInteractEvent) {
        if (isSpectator(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDropItem(event: PlayerDropItemEvent) {
        if (isSpectator(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPickupItem(event: PlayerPickupItemEvent) {
        if (isSpectator(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDeath(event: PlayerDeathEvent) {
        if (isSpectator(event.entity)) {
            event.drops.clear()
            event.droppedExp = 0
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onFly(event: PlayerToggleFlightEvent) {
        if (isSpectator(event.player) && isWaiting() && !event.player.isOp) {
            event.player.isFlying = false
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        if (isSpectator(event.player) && !isWaiting()) {
            event.player.allowFlight = true
            event.player.isFlying = true
        }
    }

}