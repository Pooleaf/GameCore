package net.pooleaf.gamereplay.data.records.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerChatData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

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