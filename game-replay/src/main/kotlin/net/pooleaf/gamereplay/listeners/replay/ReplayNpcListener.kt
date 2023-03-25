package net.pooleaf.gamereplay.listeners.replay

import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTeleportEvent

class ReplayNpcListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        // NPC 가리기
        GameReplayApi.unsafe.replayPlayerManager.values().forEach { replayPlayer ->
            replayPlayer.virtualPlayerManager.npcRegistry.forEach { npc -> event.player.hidePlayer(npc.entity as Player?) }
        }
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        val player = event.player

        // NPC가 안보이는 문제가 있어 리스폰
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(player.uniqueId) ?: return
        replayPlayer.virtualPlayerManager.values()
            .filter { it.isSpawned() }
            .forEach {
            it.despawnNpc()
            it.spawnNpc(player)
        }
    }

}