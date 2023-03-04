package net.pooleaf.gamereplay.data.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import java.util.*

/**
 * 플레이어 보기 데이터
 */
data class PlayerShowData(
    var playerUuid: UUID? = null,
) : RecordData {

    override val type: String = "playerShow"

}

class PlayerShowDataReplayHandler : RecordDataReplayHandler<PlayerShowData> {

    override fun onPlay(recordData: PlayerShowData, viewer: Player) {
        // NPC 보이기
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid).citizensNpc
        viewer.showPlayer(citizensNpc.entity as Player?)
        citizensNpc.despawn()
        citizensNpc.spawn(citizensNpc.storedLocation)
    }

}