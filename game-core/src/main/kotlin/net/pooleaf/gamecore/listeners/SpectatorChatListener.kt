package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class SpectatorChatListener : Listener {

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val gamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)

        if (!gamePlayer.isSpectator) return
        event.format = "§7[관전] ${event.format}"
    }

}