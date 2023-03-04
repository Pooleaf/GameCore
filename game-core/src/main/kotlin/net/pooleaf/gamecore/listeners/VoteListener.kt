package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerQuitEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class VoteListener: Listener {

    @EventHandler
    fun onPlayerQuit(event: GamePlayerQuitEvent) {
        val gamePlayer = event.gamePlayer

        GameCore.unsafe.startVoteManager.unvote(gamePlayer)
        GameCore.unsafe.mapVoteManager.unvote(gamePlayer)
    }

}