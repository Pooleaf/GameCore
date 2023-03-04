package net.pooleaf.gamereplay.data.player

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*

/**
 * 플레이어 접속 데이터
 */
data class PlayerJoinData(
    var playerUuid: UUID? = null,
) : RecordData {

    override val type: String = "playerJoin"

}

class PlayerJoinDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val player = event.player
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val recordData = PlayerJoinData().apply {
            playerUuid = player.uniqueId
        }
        val showData = PlayerShowData().apply {
            playerUuid = player.uniqueId
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(showData)
    }

}

class PlayerJoinDataReplayHandler : RecordDataReplayHandler<PlayerJoinData> {

    override fun onPlay(recordData: PlayerJoinData, viewer: Player) {
        val joinCommonPlayer = CommonSenderModule.getPlayer(recordData.playerUuid)
        val joinPlayerName = joinCommonPlayer?.displayName ?: recordData.playerUuid
        viewer.sendMessage("§7[리플레이] §f${joinPlayerName} §e님께서 접속했습니다.")
    }

}