package net.pooleaf.gamereplay.data.records.player

import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.datas.player.PlayerJoinData
import net.pooleaf.gamereplay.data.datas.player.PlayerShowData
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

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