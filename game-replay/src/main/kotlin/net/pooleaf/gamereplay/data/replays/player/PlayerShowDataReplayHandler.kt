package net.pooleaf.gamereplay.data.replays.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerShowData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class PlayerShowDataReplayHandler : RecordDataReplayHandler<PlayerShowData> {

    override fun onPlay(recordData: PlayerShowData, viewer: Player) {
        // NPC 보이기
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid).citizensNpc
//        viewer.showPlayer(citizensNpc.entity as Player?)
//        citizensNpc.despawn()
        citizensNpc.spawn(citizensNpc.storedLocation)
    }

}