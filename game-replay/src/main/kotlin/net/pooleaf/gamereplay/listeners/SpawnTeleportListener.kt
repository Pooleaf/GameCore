package net.pooleaf.gamereplay.listeners

import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class SpawnTeleportListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (!GameReplayApi.replayConfig.isReplayPlayServer) return

        GameReplayApi.spawnConfig.spawnLocation?.let { TeleportUtil.teleport(event.player, it) }
    }

}