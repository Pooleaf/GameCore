package net.pooleaf.gamereplay.listeners

import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class NpcHideListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        GameReplayApi.unsafe.replayPlayerManager.values().forEach { replayPlayer ->
            replayPlayer.virtualPlayerManager.npcRegistry.forEach { npc -> event.player.hidePlayer(npc.entity as Player?) }
        }
    }

}