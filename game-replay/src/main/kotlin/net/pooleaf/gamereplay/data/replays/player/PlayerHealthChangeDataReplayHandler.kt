package net.pooleaf.gamereplay.data.replays.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerHealthChangeData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class PlayerHealthChangeDataReplayHandler : RecordDataReplayHandler<PlayerHealthChangeData> {

    override fun onPlay(recordData: PlayerHealthChangeData, viewer: Player) {
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val virtualPlayer = replayPlayer.virtualPlayerManager.get(recordData.playerUuid) ?: return
        virtualPlayer.health = recordData.health
    }

}