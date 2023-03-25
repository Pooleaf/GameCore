package net.pooleaf.gamereplay.listeners.record

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameBeforeResetEvent
import net.pooleaf.gamecore.events.game.GameStartedEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.GameReplayPlugin
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class GameRecordListener : Listener {

    @EventHandler
    fun onGameStarted(event: GameStartedEvent) {
        Bukkit.getScheduler().runTaskLater(GameReplayPlugin.instance, {
            if (!GameReplayApi.replayConfig.isRecordServer) return@runTaskLater
            if (GameReplayApi.unsafe.recordManager.isRecording()) return@runTaskLater

            val gameId = GameCore.game.gameId ?: return@runTaskLater
            val targetPlayers = GameCore.unsafe.playerManager.getJoinedPlayers().map { it.uuid }

            val map = GameCore.currentMap ?: return@runTaskLater

            GameReplayApi.unsafe.recordManager.startRecord(gameId, targetPlayers, map.centerWorldName!!, map.centerX, map.centerY, map.centerZ, map.worldBorderSize)
        }, 1L)
    }

    @EventHandler
    fun onGameBeforeReset(event: GameBeforeResetEvent) {
        if (!GameReplayApi.replayConfig.isRecordServer) return
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        BukkitAsyncScope.launch {
            GameReplayApi.unsafe.recordManager.endRecord()
        }
    }

}