package net.pooleaf.gamereplay.data.player

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

/**
 * 채팅 데이터
 * 관리자에게만 전송
 */
data class PlayerChatData(
    var playerUuid: UUID? = null,
    var message: String? = null
) : RecordData {

    override val type: String = "playerChat"

}

class PlayerChatDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val player = event.player
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val recordData = PlayerChatData().apply {
            playerUuid = player.uniqueId
            message = event.message
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class PlayerChatDataReplayHandler : RecordDataReplayHandler<PlayerChatData> {

    override fun onPlay(recordData: PlayerChatData, viewer: Player) {
        if (!viewer.isOp) return

        val chatCommonPlayer = CommonSenderModule.getPlayer(recordData.playerUuid)
        val chatPlayerName = chatCommonPlayer?.displayName ?: recordData.playerUuid
        viewer.sendMessage("§7[리플레이] §f${chatPlayerName}§f: ${recordData.message}")
    }

}