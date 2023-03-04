package net.pooleaf.gamecore.listeners

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageByPlayerEvent
import net.pooleaf.gamecore.utils.toGamePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class LastHitListener: Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerHit(event: PlayerDamageByPlayerEvent) {
        if (event.isCancelled) return

        val damagedGamePlayer = event.player.toGamePlayer()
        val damagerGamePlayer = event.damager?.toGamePlayer()

        damagerGamePlayer?.let { damagerGamePlayer ->
            damagedGamePlayer?.lastDamagers?.put(damagerGamePlayer, System.currentTimeMillis())
        }
    }

}