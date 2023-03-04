package net.pooleaf.gamereplay.data.game

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamecore.events.player.GamePlayerDefeatEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.data.player.PlayerHideData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

/**
 * 채팅 데이터
 * 관리자에게만 전송
 */
data class GamePlayerDefeatData(
    var defeatPlayerUuid: UUID? = null,
    var killerPlayerUuid: UUID? = null
) : RecordData {

    override val type: String = "gamePlayerDefeat"

}

class GamePlayerDefeatDataRecordListener : Listener {

    @EventHandler
    fun onGameEnd(event: GamePlayerDefeatEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = GamePlayerDefeatData().apply {
            defeatPlayerUuid = event.gamePlayer.uuid
            killerPlayerUuid = event.killerGamePlayer?.uuid
        }
        val hideData = PlayerHideData().apply {
            playerUuid = event.gamePlayer.uuid
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(hideData)
    }

}

class GamePlayerDefeatDataReplayHandler : RecordDataReplayHandler<GamePlayerDefeatData> {

    override fun onPlay(recordData: GamePlayerDefeatData, viewer: Player) {
        val playerName = CommonSenderModule.getPlayer(recordData.defeatPlayerUuid)?.displayName ?: recordData.defeatPlayerUuid

        if (recordData.killerPlayerUuid == null) {
            viewer.sendMessage("§c${playerName} §c님이 탈락했습니다.")
        } else {
            val killerPlayerName = CommonSenderModule.getPlayer(recordData.killerPlayerUuid)?.displayName ?: recordData.killerPlayerUuid
            viewer.sendMessage("§c${killerPlayerName} §c님이 §c${playerName} §c님을 탈락시켰습니다.")
        }
    }

}