package net.pooleaf.gamereplay.data.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import java.util.*

/**
 * 플레이어 가리기 데이터
 */
data class PlayerHideData(
    var playerUuid: UUID? = null,
) : RecordData {

    override val type: String = "playerHide"

}

class PlayerHideDataReplayHandler : RecordDataReplayHandler<PlayerHideData> {

    override fun onPlay(recordData: PlayerHideData, viewer: Player) {
        // NPC 가리기
        val replayPlayer = GameReplayApi.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid).citizensNpc
        viewer.hidePlayer(citizensNpc.entity as Player?)
    }

}