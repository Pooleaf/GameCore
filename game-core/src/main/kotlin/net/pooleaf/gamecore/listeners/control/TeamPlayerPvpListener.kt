package net.pooleaf.gamecore.listeners.control

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageByPlayerEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.utils.toGamePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TeamPlayerPvpListener : Listener {

    @EventHandler
    fun onHit(event: PlayerDamageByPlayerEvent) {
        if (event.isCancelled) return
        if (GameCore.teamConfig.allowPvp) return

        val gamePlayer = event.player.toGamePlayer()!!
        val damagerGamePlayer = event.damager.toGamePlayer()!!

        if (gamePlayer.team == damagerGamePlayer.team) {
            event.isCancelled = true
        }
    }

}