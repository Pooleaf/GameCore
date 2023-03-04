package net.pooleaf.gamereplay.listeners

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameBeforeResetEvent
import net.pooleaf.gamecore.events.game.GameStartedEvent
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class GameRecordListener : Listener {

    @EventHandler
    fun onGameStarted(event: GameStartedEvent) {
        if (!GameReplayApi.replayConfig.isReplayServer) return
        if (GameReplayApi.unsafe.recordManager.isRecording()) return

        val gameId = GameCore.game.gameId ?: return
        val targetPlayers = GameCore.unsafe.playerManager.getJoinedPlayers().map { it.uuid }

        GameReplayApi.unsafe.recordManager.startRecord(gameId, targetPlayers)
    }

    @EventHandler
    fun onGameBeforeReset(event: GameBeforeResetEvent) {
        if (!GameReplayApi.replayConfig.isReplayServer) return
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        BukkitAsyncScope.launch {
            GameReplayApi.unsafe.recordManager.endRecord()
        }
    }

}