package net.pooleaf.gamereplay.data.game

import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.events.game.GameEndEvent
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

/**
 * 게임 종료 데이터
 */
data class GameEndData(
    var winnerPlayerUuidss: List<UUID> = arrayListOf()
) : RecordData {

    override val type: String = "gameEnd"

}

class GameEndDataRecordListener : Listener {

    @EventHandler
    fun onGameEnd(event: GameEndEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val winnerPlayerUuids = event.winnerTeam?.players?.map { it.uuid } ?: arrayListOf()

        val recordData = GameEndData().apply {
            this.winnerPlayerUuidss = winnerPlayerUuids
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class GameEndDataReplayHandler : RecordDataReplayHandler<GameEndData> {

    override fun onPlay(recordData: GameEndData, viewer: Player) {
        val winnerPlayerNames = recordData.winnerPlayerUuidss.map { CommonSenderModule.getPlayer(it)?.displayName ?: it.toString() }.joinToString()

        // 우승 타이틀
        Broadcaster.broadcastTitle("§e우승", "§f${winnerPlayerNames}", 10 * 20)

        // 사운드
        Broadcaster.broadcastSound(XSound.ENTITY_PLAYER_LEVELUP, 0.4F, 0.5F)
    }

}