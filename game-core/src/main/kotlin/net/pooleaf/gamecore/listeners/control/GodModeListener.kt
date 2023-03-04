package net.pooleaf.gamecore.listeners.control

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class GodModeListener: Listener {

    @EventHandler
    fun onPlayerDamage(event: PlayerDamageEvent) {
        if (GameCore.game.isGodMode) {
            event.isCancelled = true
        }
    }

}