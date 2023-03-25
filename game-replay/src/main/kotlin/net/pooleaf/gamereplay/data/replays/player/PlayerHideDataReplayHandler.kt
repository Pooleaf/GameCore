package net.pooleaf.gamereplay.data.replays.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerHideData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class PlayerHideDataReplayHandler : RecordDataReplayHandler<PlayerHideData> {

    override fun onPlay(recordData: PlayerHideData, viewer: Player) {
        // NPC 가리기
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val virtualPlayer = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)
        virtualPlayer.despawnNpc()
    }

}