package net.pooleaf.gamereplay.data.records.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerHideData
import net.pooleaf.gamereplay.data.datas.player.PlayerQuitData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val player = event.player
        if (!GameReplayApi.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val recordData = PlayerQuitData().apply {
            playerUuid = player.uniqueId
        }
        val hideData = PlayerHideData().apply {
            playerUuid = player.uniqueId
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(hideData)
    }

}